/*
 * Copyright 2020 Google LLC
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
package com.google.cloud.bigquery.storage.v1beta2;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.Duration;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

/**
 * A BigQuery Stream Writer that can be used to write data into BigQuery Table.
 *
 * <p>TODO: Add credential support.
 *
 * <p>TODO: Attach schema.
 *
 * <p>TODO: Add max size check.
 *
 * <p>TODO: Add inflight control.
 *
 * <p>TODO: Attach traceId.
 *
 * <p>TODO: Support batching.
 *
 * <p>TODO: Support schema change.
 */
public class StreamWriterV2 implements AutoCloseable {
  private static final Logger log = Logger.getLogger(StreamWriterV2.class.getName());

  private static final Duration DONE_CALLBACK_WAIT_TIMEOUT = Duration.ofMinutes(10);

  private Lock lock;
  private Condition hasMessageInWaitingQueue;

  /*
   * The identifier of stream to write to.
   */
  private final String streamName;

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
   * Wraps the underlying bi-directional stream connection with server.
   */
  private StreamConnection streamConnection;

  /*
   * A separate thread to handle actual communication with server.
   */
  private Thread appendThread;

  private StreamWriterV2(Builder builder) {
    this.lock = new ReentrantLock();
    this.hasMessageInWaitingQueue = lock.newCondition();
    this.streamName = builder.streamName;
    this.waitingRequestQueue = new LinkedList<AppendRequestAndResponse>();
    this.inflightRequestQueue = new LinkedList<AppendRequestAndResponse>();
    this.streamConnection =
        new StreamConnection(builder.client, this::requestCallback, this::doneCallback);
    this.appendThread = new Thread(this::appendLoop);
    this.appendThread.start();
  }

  /**
   * Schedules the writing of a message.
   *
   * <p>Example of writing a message.
   *
   * <pre>{@code
   * AppendRowsRequest message;
   * ApiFuture<AppendRowsResponse> messageIdFuture = writer.append(message);
   * ApiFutures.addCallback(messageIdFuture, new ApiFutureCallback<AppendRowsResponse>() {
   *   public void onSuccess(AppendRowsResponse response) {
   *     if (response.hasOffset()) {
   *       System.out.println("written with offset: " + response.getOffset());
   *     } else {
   *       System.out.println("received an in stream error: " + response.error().toString());
   *     }
   *   }
   *
   *   public void onFailure(Throwable t) {
   *     System.out.println("failed to write: " + t);
   *   }
   * }, MoreExecutors.directExecutor());
   * }</pre>
   *
   * @param message the message in serialized format to write to BigQuery.
   * @return the message ID wrapped in a future.
   */
  public ApiFuture<AppendRowsResponse> append(AppendRowsRequest message) {
    AppendRequestAndResponse requestWrapper = new AppendRequestAndResponse(message);
    this.lock.lock();
    try {
      if (userClosed) {
        requestWrapper.appendResult.setException(
            new StatusRuntimeException(
                Status.fromCode(Status.Code.FAILED_PRECONDITION)
                    .withDescription("Stream is already closed")));
        return requestWrapper.appendResult;
      }
      if (connectionFinalStatus != null) {
        requestWrapper.appendResult.setException(
            new StatusRuntimeException(
                Status.fromCode(Status.Code.FAILED_PRECONDITION)
                    .withDescription(
                        "Stream is closed due to " + connectionFinalStatus.toString())));
        return requestWrapper.appendResult;
      }
      waitingRequestQueue.addLast(requestWrapper);
      hasMessageInWaitingQueue.signal();
      return requestWrapper.appendResult;
    } finally {
      this.lock.unlock();
    }
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
    log.info("Waiting for append thread to finish. Stream: " + streamName);
    try {
      appendThread.join();
      log.info("User close complete. Stream: " + streamName);
    } catch (InterruptedException e) {
      // Unexpected. Just swallow the exception with logging.
      log.warning(
          "Append handler join is interrupted. Stream: " + streamName + " Error: " + e.toString());
    }
  }

  /*
   * This loop is executed in a separate thread.
   *
   * It takes requests from waiting queue and sends them to server.
   */
  private void appendLoop() {
    Deque<AppendRequestAndResponse> localQueue = new LinkedList<AppendRequestAndResponse>();
    while (!waitingQueueDrained()) {
      this.lock.lock();
      try {
        hasMessageInWaitingQueue.await(100, TimeUnit.MILLISECONDS);
        while (!this.waitingRequestQueue.isEmpty()) {
          localQueue.addLast(this.waitingRequestQueue.pollFirst());
        }
      } catch (InterruptedException e) {
        log.warning("Interrupted while waiting for message. Error: " + e.toString());
      } finally {
        this.lock.unlock();
      }

      if (localQueue.isEmpty()) {
        continue;
      }

      // TODO: Add reconnection here.

      this.lock.lock();
      try {
        while (!localQueue.isEmpty()) {
          AppendRequestAndResponse requestWrapper = localQueue.pollFirst();
          this.inflightRequestQueue.addLast(requestWrapper);
          this.streamConnection.send(requestWrapper.message);
        }
      } finally {
        this.lock.unlock();
      }
    }

    log.info("Cleanup starts. Stream: " + streamName);
    // At this point, the waiting queue is drained, so no more requests.
    // We can close the stream connection and handle the remaining inflight requests.
    this.streamConnection.close();

    log.info("Waiting for done callback from stream connection. Stream: " + streamName);
    long waitDeadlineMs = System.currentTimeMillis() + DONE_CALLBACK_WAIT_TIMEOUT.toMillis();
    while (true) {
      if (System.currentTimeMillis() > waitDeadlineMs) {
        log.warning(
            "Timeout waiting for done wallback. Skip inflight cleanup. Stream: " + streamName);
        return;
      }
      this.lock.lock();
      try {
        if (connectionFinalStatus != null) {
          // Done callback is received, break.
          break;
        }
      } finally {
        this.lock.unlock();
      }
      Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
    }

    // At this point, there cannot be more callback. It is safe to clean up all inflight requests.
    log.info(
        "Stream connection is fully closed. Cleaning up inflight requests. Stream: " + streamName);
    cleanupInflightRequests();
    log.info("Append thread is done. Stream: " + streamName);
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

  private void cleanupInflightRequests() {
    Throwable finalStatus;
    Deque<AppendRequestAndResponse> localQueue = new LinkedList<AppendRequestAndResponse>();
    this.lock.lock();
    try {
      finalStatus = this.connectionFinalStatus;
      while (!this.inflightRequestQueue.isEmpty()) {
        localQueue.addLast(this.inflightRequestQueue.pollFirst());
      }
    } finally {
      this.lock.unlock();
    }
    log.info(
        "Cleaning "
            + localQueue.size()
            + " inflight requests with error: "
            + finalStatus.toString());
    while (!localQueue.isEmpty()) {
      localQueue.pollFirst().appendResult.setException(finalStatus);
    }
  }

  private void requestCallback(AppendRowsResponse response) {
    AppendRequestAndResponse requestWrapper;
    this.lock.lock();
    try {
      requestWrapper = this.inflightRequestQueue.pollFirst();
    } finally {
      this.lock.unlock();
    }
    requestWrapper.appendResult.set(response);
  }

  private void doneCallback(Throwable finalStatus) {
    log.info(
        "Received done callback. Stream: "
            + streamName
            + " Final status: "
            + finalStatus.toString());
    this.lock.lock();
    try {
      this.connectionFinalStatus = finalStatus;
    } finally {
      this.lock.unlock();
    }
  }

  /** Constructs a new {@link StreamWriterV2.Builder} using the given stream and client. */
  public static StreamWriterV2.Builder newBuilder(String streamName, BigQueryWriteClient client) {
    return new StreamWriterV2.Builder(streamName, client);
  }

  /** A builder of {@link StreamWriterV2}s. */
  public static final class Builder {

    private String streamName;

    private BigQueryWriteClient client;

    private Builder(String streamName, BigQueryWriteClient client) {
      this.streamName = Preconditions.checkNotNull(streamName);
      ;
      this.client = Preconditions.checkNotNull(client);
      ;
    }

    /** Builds the {@code StreamWriterV2}. */
    public StreamWriterV2 build() {
      return new StreamWriterV2(this);
    }
  }

  // Class that wraps AppendRowsRequest and its corresponding Response future.
  private static final class AppendRequestAndResponse {
    final SettableApiFuture<AppendRowsResponse> appendResult;
    final AppendRowsRequest message;

    AppendRequestAndResponse(AppendRowsRequest message) {
      this.appendResult = SettableApiFuture.create();
      this.message = message;
    }
  }
}
