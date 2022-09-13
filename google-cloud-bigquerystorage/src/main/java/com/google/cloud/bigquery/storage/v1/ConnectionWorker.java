/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery.storage.v1;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.batching.FlowController;
import com.google.cloud.bigquery.storage.util.Errors;
import com.google.cloud.bigquery.storage.v1.AppendRowsRequest.ProtoData;
import com.google.cloud.bigquery.storage.v1.StreamConnection.DoneCallback;
import com.google.cloud.bigquery.storage.v1.StreamConnection.RequestCallback;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Int64Value;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

/**
 * A BigQuery Stream Writer that can be used to write data into BigQuery Table.
 *
 * <p>TODO: Support batching.
 */
public class ConnectionWorker implements AutoCloseable {
  private static final Logger log = Logger.getLogger(StreamWriter.class.getName());

  private Lock lock;
  private Condition hasMessageInWaitingQueue;
  private Condition inflightReduced;

  /*
   * The identifier of stream to write to.
   */
  private final String streamName;

  /*
   * The proto schema of rows to write.
   */
  private final ProtoSchema writerSchema;

  /*
   * Max allowed inflight requests in the stream. Method append is blocked at this.
   */
  private final long maxInflightRequests;

  /*
   * Max allowed inflight bytes in the stream. Method append is blocked at this.
   */
  private final long maxInflightBytes;

  /*
   * Behavior when inflight queue is exceeded. Only supports Block or Throw, default is Block.
   */
  private final FlowController.LimitExceededBehavior limitExceededBehavior;

  /*
   * TraceId for debugging purpose.
   */
  private final String traceId;

  /*
   * Tracks current inflight requests in the stream.
   */
  @GuardedBy("lock")
  private long inflightRequests = 0;

  /*
   * Tracks current inflight bytes in the stream.
   */
  @GuardedBy("lock")
  private long inflightBytes = 0;

  /*
   * Tracks how often the stream was closed due to a retriable error. Streaming will stop when the
   * count hits a threshold. Streaming should only be halted, if it isn't possible to establish a
   * connection. Keep track of the number of reconnections in succession. This will be reset if
   * a row is successfully called back.
   */
  @GuardedBy("lock")
  private long conectionRetryCountWithoutCallback = 0;

  /*
   * If false, streamConnection needs to be reset.
   */
  @GuardedBy("lock")
  private boolean streamConnectionIsConnected = false;

  /*
   * A boolean to track if we cleaned up inflight queue.
   */
  @GuardedBy("lock")
  private boolean inflightCleanuped = false;

  /*
   * Indicates whether user has called Close() or not.
   */
  @GuardedBy("lock")
  private boolean userClosed = false;

  /*
   * The final status of connection. Set to nonnull when connection is permanently closed.
   */
  @GuardedBy("lock")
  private Throwable connectionFinalStatus = null;

  /*
   * Contains requests buffered in the client and not yet sent to server.
   */
  @GuardedBy("lock")
  private final Deque<AppendRequestAndResponse> waitingRequestQueue;

  /*
   * Contains sent append requests waiting for response from server.
   */
  @GuardedBy("lock")
  private final Deque<AppendRequestAndResponse> inflightRequestQueue;

  /*
   * Contains the updated TableSchema.
   */
  @GuardedBy("lock")
  private TableSchema updatedSchema;

  /*
   * A client used to interact with BigQuery.
   */
  private BigQueryWriteClient client;

  /*
   * If true, the client above is created by this writer and should be closed.
   */
  private boolean ownsBigQueryWriteClient = false;

  /*
   * Wraps the underlying bi-directional stream connection with server.
   */
  private StreamConnection streamConnection;

  /*
   * A separate thread to handle actual communication with server.
   */
  private Thread appendThread;

  /*
   * The inflight wait time for the previous sent request.
   */
  private final AtomicLong inflightWaitSec = new AtomicLong(0);

  /*
   * A String that uniquely identifies this writer.
   */
  private final String writerId = UUID.randomUUID().toString();

  /** The maximum size of one request. Defined by the API. */
  public static long getApiMaxRequestBytes() {
    return 10L * 1000L * 1000L; // 10 megabytes (https://en.wikipedia.org/wiki/Megabyte)
  }

  public ConnectionWorker(
      String streamName,
      ProtoSchema writerSchema,
      long maxInflightRequests,
      long maxInflightBytes,
      FlowController.LimitExceededBehavior limitExceededBehavior,
      String traceId,
      BigQueryWriteClient client,
      boolean ownsBigQueryWriteClient)
      throws IOException {
    this.lock = new ReentrantLock();
    this.hasMessageInWaitingQueue = lock.newCondition();
    this.inflightReduced = lock.newCondition();
    this.streamName = streamName;
    if (writerSchema == null) {
      throw new StatusRuntimeException(
          Status.fromCode(Code.INVALID_ARGUMENT)
              .withDescription("Writer schema must be provided when building this writer."));
    }
    this.writerSchema = writerSchema;
    this.maxInflightRequests = maxInflightRequests;
    this.maxInflightBytes = maxInflightBytes;
    this.limitExceededBehavior = limitExceededBehavior;
    this.traceId = traceId;
    this.waitingRequestQueue = new LinkedList<AppendRequestAndResponse>();
    this.inflightRequestQueue = new LinkedList<AppendRequestAndResponse>();
    this.client = client;
    this.ownsBigQueryWriteClient = ownsBigQueryWriteClient;

    this.appendThread =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                appendLoop();
              }
            });
    this.appendThread.start();
  }

  private void resetConnection() {
    this.streamConnection =
        new StreamConnection(
            this.client,
            new RequestCallback() {
              @Override
              public void run(AppendRowsResponse response) {
                requestCallback(response);
              }
            },
            new DoneCallback() {
              @Override
              public void run(Throwable finalStatus) {
                doneCallback(finalStatus);
              }
            });
  }

  /** Schedules the writing of rows at the end of current stream. */
  public ApiFuture<AppendRowsResponse> append(ProtoRows rows) {
    return append(rows, -1);
  }

  /** Schedules the writing of rows at given offset. */
  public ApiFuture<AppendRowsResponse> append(ProtoRows rows, long offset) {
    AppendRowsRequest.Builder requestBuilder = AppendRowsRequest.newBuilder();
    requestBuilder.setProtoRows(ProtoData.newBuilder().setRows(rows).build());
    if (offset >= 0) {
      requestBuilder.setOffset(Int64Value.of(offset));
    }
    return appendInternal(requestBuilder.build());
  }

  private ApiFuture<AppendRowsResponse> appendInternal(AppendRowsRequest message) {
    AppendRequestAndResponse requestWrapper = new AppendRequestAndResponse(message);
    if (requestWrapper.messageSize > getApiMaxRequestBytes()) {
      requestWrapper.appendResult.setException(
          new StatusRuntimeException(
              Status.fromCode(Code.INVALID_ARGUMENT)
                  .withDescription(
                      "MessageSize is too large. Max allow: "
                          + getApiMaxRequestBytes()
                          + " Actual: "
                          + requestWrapper.messageSize)));
      return requestWrapper.appendResult;
    }
    this.lock.lock();
    try {
      if (userClosed) {
        requestWrapper.appendResult.setException(
            new Exceptions.StreamWriterClosedException(
                Status.fromCode(Status.Code.FAILED_PRECONDITION)
                    .withDescription("Connection is already closed"),
                streamName,
                writerId));
        return requestWrapper.appendResult;
      }
      // Check if queue is going to be full before adding the request.
      if (this.limitExceededBehavior == FlowController.LimitExceededBehavior.ThrowException) {
        if (this.inflightRequests + 1 >= this.maxInflightRequests) {
          throw new Exceptions.InflightRequestsLimitExceededException(
              writerId, this.maxInflightRequests);
        }
        if (this.inflightBytes + requestWrapper.messageSize >= this.maxInflightBytes) {
          throw new Exceptions.InflightBytesLimitExceededException(writerId, this.maxInflightBytes);
        }
      }

      if (connectionFinalStatus != null) {
        requestWrapper.appendResult.setException(
            new Exceptions.StreamWriterClosedException(
                Status.fromCode(Status.Code.FAILED_PRECONDITION)
                    .withDescription(
                        "Connection is closed due to " + connectionFinalStatus.toString()),
                streamName,
                writerId));
        return requestWrapper.appendResult;
      }

      ++this.inflightRequests;
      this.inflightBytes += requestWrapper.messageSize;
      waitingRequestQueue.addLast(requestWrapper);
      hasMessageInWaitingQueue.signal();
      maybeWaitForInflightQuota();
      return requestWrapper.appendResult;
    } finally {
      this.lock.unlock();
    }
  }

  @GuardedBy("lock")
  private void maybeWaitForInflightQuota() {
    long start_time = System.currentTimeMillis();
    while (this.inflightRequests >= this.maxInflightRequests
        || this.inflightBytes >= this.maxInflightBytes) {
      try {
        inflightReduced.await(100, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        log.warning(
            "Interrupted while waiting for inflight quota. Stream: "
                + streamName
                + " Error: "
                + e.toString());
        throw new StatusRuntimeException(
            Status.fromCode(Code.CANCELLED)
                .withCause(e)
                .withDescription("Interrupted while waiting for quota."));
      }
    }
    inflightWaitSec.set((System.currentTimeMillis() - start_time) / 1000);
  }

  public long getInflightWaitSeconds() {
    return inflightWaitSec.longValue();
  }

  /** @return a unique Id for the writer. */
  public String getWriterId() {
    return writerId;
  }

  /** Close the stream writer. Shut down all resources. */
  @Override
  public void close() {
    log.info("User closing stream: " + streamName);
    this.lock.lock();
    try {
      this.userClosed = true;
    } finally {
      this.lock.unlock();
    }
    log.fine("Waiting for append thread to finish. Stream: " + streamName);
    try {
      appendThread.join();
      log.info("User close complete. Stream: " + streamName);
    } catch (InterruptedException e) {
      // Unexpected. Just swallow the exception with logging.
      log.warning(
          "Append handler join is interrupted. Stream: " + streamName + " Error: " + e.toString());
    }
    if (this.ownsBigQueryWriteClient) {
      this.client.close();
      try {
        // Backend request has a 2 minute timeout, so wait a little longer than that.
        this.client.awaitTermination(150, TimeUnit.SECONDS);
      } catch (InterruptedException ignored) {
      }
    }
  }

  /*
   * This loop is executed in a separate thread.
   *
   * It takes requests from waiting queue and sends them to server.
   */
  private void appendLoop() {
    Deque<AppendRequestAndResponse> localQueue = new LinkedList<AppendRequestAndResponse>();
    boolean streamNeedsConnecting = false;
    // Set firstRequestInConnection to true immediately after connecting the steam,
    // indicates then next row sent, needs the schema and other metadata.
    boolean isFirstRequestInConnection = true;
    while (!waitingQueueDrained()) {
      this.lock.lock();
      try {
        hasMessageInWaitingQueue.await(100, TimeUnit.MILLISECONDS);
        // Copy the streamConnectionIsConnected guarded by lock to a local variable.
        // In addition, only reconnect if there is a retriable error.
        streamNeedsConnecting = !streamConnectionIsConnected && connectionFinalStatus == null;
        if (streamNeedsConnecting) {
          // If the stream connection is broken, any requests on inflightRequestQueue will need
          // to be resent, as the new connection has no knowledge of the requests. Copy the requests
          // from inflightRequestQueue and prepent them onto the waitinRequestQueue. They need to be
          // prepended as they need to be sent before new requests.
          while (!inflightRequestQueue.isEmpty()) {
            waitingRequestQueue.addFirst(inflightRequestQueue.pollLast());
          }
        }
        while (!this.waitingRequestQueue.isEmpty()) {
          AppendRequestAndResponse requestWrapper = this.waitingRequestQueue.pollFirst();
          this.inflightRequestQueue.addLast(requestWrapper);
          localQueue.addLast(requestWrapper);
        }
      } catch (InterruptedException e) {
        log.warning(
            "Interrupted while waiting for message. Stream: "
                + streamName
                + " Error: "
                + e.toString());
      } finally {
        this.lock.unlock();
      }

      if (localQueue.isEmpty()) {
        continue;
      }
      if (streamNeedsConnecting) {
        // Set streamConnectionIsConnected to true, to indicate the stream has been connected. This
        // should happen before the call to resetConnection. As it is unknown when the connection
        // could be closed and the doneCallback called, and thus clearing the flag.
        lock.lock();
        try {
          this.streamConnectionIsConnected = true;
        } finally {
          lock.unlock();
        }
        resetConnection();
        // Set firstRequestInConnection to indicate the next request to be sent should include
        // metedata.
        isFirstRequestInConnection = true;
      }
      while (!localQueue.isEmpty()) {
        AppendRowsRequest preparedRequest =
            prepareRequestBasedOnPosition(
                localQueue.pollFirst().message, isFirstRequestInConnection);
        // Send should only throw an exception if there is a problem with the request. The catch
        // block will handle this case, and return the exception with the result.
        // Otherwise send will return:
        //   SUCCESS: Message was sent, wait for the callback.
        //   STREAM_CLOSED: Stream was closed, normally or due to en error
        //   NOT_ENOUGH_QUOTA: Message wasn't sent due to not enough quota.
        // TODO: Handle NOT_ENOUGH_QUOTA.
        // In the close case, the request is in the inflight queue, and will either be returned
        // to the user with an error, or will be resent.
        this.streamConnection.send(preparedRequest);
        isFirstRequestInConnection = false;
      }
    }

    log.fine("Cleanup starts. Stream: " + streamName);
    // At this point, the waiting queue is drained, so no more requests.
    // We can close the stream connection and handle the remaining inflight requests.
    if (streamConnection != null) {
      this.streamConnection.close();
      waitForDoneCallback(3, TimeUnit.MINUTES);
    }

    // At this point, there cannot be more callback. It is safe to clean up all inflight requests.
    log.fine(
        "Stream connection is fully closed. Cleaning up inflight requests. Stream: " + streamName);
    cleanupInflightRequests();
    log.fine("Append thread is done. Stream: " + streamName);
  }

  /*
   * Returns true if waiting queue is drain, a.k.a. no more requests in the waiting queue.
   *
   * It serves as a signal to append thread that there cannot be any more requests in the waiting
   * queue and it can prepare to stop.
   */
  private boolean waitingQueueDrained() {
    this.lock.lock();
    try {
      return (this.userClosed || this.connectionFinalStatus != null)
          && this.waitingRequestQueue.isEmpty();
    } finally {
      this.lock.unlock();
    }
  }

  private void waitForDoneCallback(long duration, TimeUnit timeUnit) {
    log.fine("Waiting for done callback from stream connection. Stream: " + streamName);
    long deadline = System.nanoTime() + timeUnit.toNanos(duration);
    while (System.nanoTime() <= deadline) {
      this.lock.lock();
      try {
        if (!this.streamConnectionIsConnected) {
          // Done callback is received, return.
          return;
        }
      } finally {
        this.lock.unlock();
      }
      Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
    }
    this.lock.lock();
    try {
      if (connectionFinalStatus == null) {
        connectionFinalStatus =
            new StatusRuntimeException(
                Status.fromCode(Code.CANCELLED)
                    .withDescription("Timeout waiting for DoneCallback."));
      }
    } finally {
      this.lock.unlock();
    }

    return;
  }

  private AppendRowsRequest prepareRequestBasedOnPosition(
      AppendRowsRequest original, boolean isFirstRequest) {
    AppendRowsRequest.Builder requestBuilder = original.toBuilder();
    if (isFirstRequest) {
      if (this.writerSchema != null) {
        requestBuilder.getProtoRowsBuilder().setWriterSchema(this.writerSchema);
      }
      requestBuilder.setWriteStream(this.streamName);
      if (this.traceId != null) {
        requestBuilder.setTraceId(this.traceId);
      }
    } else {
      requestBuilder.clearWriteStream();
      requestBuilder.getProtoRowsBuilder().clearWriterSchema();
    }
    return requestBuilder.build();
  }

  private void cleanupInflightRequests() {
    Throwable finalStatus =
        new Exceptions.StreamWriterClosedException(
            Status.fromCode(Status.Code.FAILED_PRECONDITION)
                .withDescription("Connection is already closed, cleanup inflight request"),
            streamName,
            writerId);
    Deque<AppendRequestAndResponse> localQueue = new LinkedList<AppendRequestAndResponse>();
    this.lock.lock();
    try {
      if (this.connectionFinalStatus != null) {
        finalStatus = this.connectionFinalStatus;
      }
      while (!this.inflightRequestQueue.isEmpty()) {
        localQueue.addLast(pollInflightRequestQueue());
      }
      this.inflightCleanuped = true;
    } finally {
      this.lock.unlock();
    }
    log.fine("Cleaning " + localQueue.size() + " inflight requests with error: " + finalStatus);
    while (!localQueue.isEmpty()) {
      localQueue.pollFirst().appendResult.setException(finalStatus);
    }
  }

  private void requestCallback(AppendRowsResponse response) {
    AppendRequestAndResponse requestWrapper;
    this.lock.lock();
    if (response.hasUpdatedSchema()) {
      this.updatedSchema = response.getUpdatedSchema();
    }
    try {
      // Had a successful connection with at least one result, reset retries.
      // conectionRetryCountWithoutCallback is reset so that only multiple retries, without
      // successful records sent, will cause the stream to fail.
      if (conectionRetryCountWithoutCallback != 0) {
        conectionRetryCountWithoutCallback = 0;
      }
      if (!this.inflightRequestQueue.isEmpty()) {
        requestWrapper = pollInflightRequestQueue();
      } else if (inflightCleanuped) {
        // It is possible when requestCallback is called, the inflight queue is already drained
        // because we timed out waiting for done.
        return;
      } else {
        // This is something not expected, we shouldn't have an empty inflight queue otherwise.
        log.log(Level.WARNING, "Unexpected: request callback called on an empty inflight queue.");
        connectionFinalStatus =
            new StatusRuntimeException(
                Status.fromCode(Code.FAILED_PRECONDITION)
                    .withDescription("Request callback called on an empty inflight queue."));
        return;
      }
    } finally {
      this.lock.unlock();
    }
    if (response.hasError()) {
      Exceptions.StorageException storageException =
          Exceptions.toStorageException(response.getError(), null);
      if (storageException != null) {
        requestWrapper.appendResult.setException(storageException);
      } else {
        StatusRuntimeException exception =
            new StatusRuntimeException(
                Status.fromCodeValue(response.getError().getCode())
                    .withDescription(response.getError().getMessage()));
        requestWrapper.appendResult.setException(exception);
      }
    } else {
      requestWrapper.appendResult.set(response);
    }
  }

  private boolean isRetriableError(Throwable t) {
    Status status = Status.fromThrowable(t);
    if (Errors.isRetryableInternalStatus(status)) {
      return true;
    }
    return status.getCode() == Code.ABORTED
        || status.getCode() == Code.UNAVAILABLE
        || status.getCode() == Code.CANCELLED;
  }

  private void doneCallback(Throwable finalStatus) {
    log.fine(
        "Received done callback. Stream: "
            + streamName
            + " Final status: "
            + finalStatus.toString());
    this.lock.lock();
    try {
      this.streamConnectionIsConnected = false;
      if (connectionFinalStatus == null) {
        // If the error can be retried, don't set it here, let it try to retry later on.
        if (isRetriableError(finalStatus) && !userClosed) {
          this.conectionRetryCountWithoutCallback++;
          log.fine(
              "Retriable error "
                  + finalStatus.toString()
                  + " received, retry count "
                  + conectionRetryCountWithoutCallback
                  + " for stream "
                  + streamName);
        } else {
          Exceptions.StorageException storageException = Exceptions.toStorageException(finalStatus);
          this.connectionFinalStatus = storageException != null ? storageException : finalStatus;
          log.info(
              "Connection finished with error "
                  + finalStatus.toString()
                  + " for stream "
                  + streamName);
        }
      }
    } finally {
      this.lock.unlock();
    }
  }

  @GuardedBy("lock")
  private AppendRequestAndResponse pollInflightRequestQueue() {
    AppendRequestAndResponse requestWrapper = this.inflightRequestQueue.pollFirst();
    --this.inflightRequests;
    this.inflightBytes -= requestWrapper.messageSize;
    this.inflightReduced.signal();
    return requestWrapper;
  }

  /** Thread-safe getter of updated TableSchema */
  public synchronized TableSchema getUpdatedSchema() {
    return this.updatedSchema;
  }

  // Class that wraps AppendRowsRequest and its corresponding Response future.
  private static final class AppendRequestAndResponse {
    final SettableApiFuture<AppendRowsResponse> appendResult;
    final AppendRowsRequest message;
    final long messageSize;

    AppendRequestAndResponse(AppendRowsRequest message) {
      this.appendResult = SettableApiFuture.create();
      this.message = message;
      this.messageSize = message.getProtoRows().getSerializedSize();
    }
  }
}
