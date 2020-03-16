/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.bigquery.storage.v1alpha2;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorAsBackgroundResource;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.common.base.Preconditions;
import io.grpc.Status;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.threeten.bp.Duration;

/**
 * A BigQuery Stream Writer that can be used to write data into BigQuery Table.
 *
 * <p>A {@link StreamWrier} provides built-in capabilities to: - handle batching of messages -
 * controlling memory utilization (outstanding requests management) - automatic connection
 * re-establishment - request cleanup
 *
 * <p>With customizable options that control:
 *
 * <ul>
 *   <li>Message batching: such as number of messages or max batch byte size, and batching deadline
 *   <li>Inflight message control: such as number of messages or max batch byte size
 * </ul>
 *
 * <p>{@link StreamWriter} will use the credentials set on the channel, which uses application
 * default credentials through {@link GoogleCredentials#getApplicationDefault} by default.
 */
public class StreamWriter {
  private static final Logger LOG = Logger.getLogger(StreamWriter.class.getName());

  private final String streamName;

  private final BatchingSettings batchingSettings;
  private final RetrySettings retrySettings;
  private final BigQueryWriteSettings stubSettings;

  private final Lock messagesBatchLock;
  private final MessagesBatch messagesBatch;

  private BackgroundResource backgroundResources;
  private List<BackgroundResource> backgroundResourceList;

  private BigQueryWriteClient stub;

  private final ScheduledExecutorService executor;

  private final AtomicBoolean shutdown;
  private final Waiter messagesWaiter;
  private final AtomicBoolean activeAlarm;
  private ScheduledFuture<?> currentAlarmFuture;
  BidiStreamingCallable<AppendRowsRequest, AppendRowsResponse> bidiStreamingCallable;
  ClientStream<AppendRowsRequest> clientStream;
  private final AppendResponseObserver responseObserver;

  /** The maximum size of one request. Defined by the API. */
  public static long getApiMaxRequestBytes() {
    return 10L * 1000L * 1000L; // 10 megabytes (https://en.wikipedia.org/wiki/Megabyte)
  }

  /** The maximum size of in flight requests. Defined by the API. */
  public static long getApiMaxInflightRequests() {
    return 5000L;
  }

  private StreamWriter(Builder builder) throws IOException {
    streamName = builder.streamName;

    this.batchingSettings = builder.batchingSettings;
    this.retrySettings = builder.retrySettings;
    this.messagesBatch = new MessagesBatch(batchingSettings);
    messagesBatchLock = new ReentrantLock();
    activeAlarm = new AtomicBoolean(false);
    executor = builder.executorProvider.getExecutor();
    backgroundResourceList = new ArrayList<>();
    if (builder.executorProvider.shouldAutoClose()) {
      backgroundResourceList.add(new ExecutorAsBackgroundResource(executor));
    }
    messagesWaiter = new Waiter(this.batchingSettings.getFlowControlSettings());
    responseObserver = new AppendResponseObserver(this);

    stubSettings =
        BigQueryWriteSettings.newBuilder()
            .setCredentialsProvider(builder.credentialsProvider)
            .setTransportChannelProvider(builder.channelProvider)
            .setEndpoint(builder.endpoint)
            .build();
    shutdown = new AtomicBoolean(false);
    refreshAppend();
  }

  /** Stream which we are writing to. */
  public String getStreamNameString() {
    return streamName;
  }

  /**
   * Schedules the writing of a message. The write of the message may occur immediately or be
   * delayed based on the writer batching options.
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
    Preconditions.checkState(!shutdown.get(), "Cannot append on a shut-down writer.");

    final AppendRequestAndFutureResponse outstandingAppend =
        new AppendRequestAndFutureResponse(message);
    List<InflightBatch> batchesToSend;
    messagesBatchLock.lock();
    try {
      batchesToSend = messagesBatch.add(outstandingAppend);
      // Setup the next duration based delivery alarm if there are messages batched.
      setupAlarm();
      if (!batchesToSend.isEmpty()) {
        for (final InflightBatch batch : batchesToSend) {
          LOG.fine("Scheduling a batch for immediate sending.");
          writeBatch(batch);
        }
      }
    } finally {
      messagesBatchLock.unlock();
    }

    return outstandingAppend.appendResult;
  }

  public void refreshAppend() throws IOException {
    Preconditions.checkState(!shutdown.get(), "Cannot append on a shut-down writer.");
    if (stub != null) {
      stub.shutdown();
    }
    stub = BigQueryWriteClient.create(stubSettings);
    backgroundResourceList.add(stub);
    backgroundResources = new BackgroundResourceAggregation(backgroundResourceList);
    bidiStreamingCallable = stub.appendRowsCallable();
    clientStream = bidiStreamingCallable.splitCall(responseObserver);
    messagesBatch.resetAttachSchema();
  }

  private void setupAlarm() {
    if (!messagesBatch.isEmpty()) {
      if (!activeAlarm.getAndSet(true)) {
        long delayThresholdMs = getBatchingSettings().getDelayThreshold().toMillis();
        LOG.log(Level.FINE, "Setting up alarm for the next {0} ms.", delayThresholdMs);
        currentAlarmFuture =
            executor.schedule(
                new Runnable() {
                  @Override
                  public void run() {
                    LOG.log(Level.FINE, "Sending messages based on schedule");
                    activeAlarm.getAndSet(false);
                    messagesBatchLock.lock();
                    try {
                      writeBatch(messagesBatch.popBatch());
                    } finally {
                      messagesBatchLock.unlock();
                    }
                  }
                },
                delayThresholdMs,
                TimeUnit.MILLISECONDS);
      }
    } else if (currentAlarmFuture != null) {
      LOG.log(Level.FINER, "Cancelling alarm, no more messages");
      if (activeAlarm.getAndSet(false)) {
        currentAlarmFuture.cancel(false);
      }
    }
  }

  /**
   * Write any outstanding batches if non-empty. This method sends buffered messages, but does not
   * wait for the send operations to complete. To wait for messages to send, call {@code get} on the
   * futures returned from {@code append}.
   */
  public void writeAllOutstanding() {
    InflightBatch unorderedOutstandingBatch = null;
    messagesBatchLock.lock();
    try {
      if (!messagesBatch.isEmpty()) {
        writeBatch(messagesBatch.popBatch());
      }
      messagesBatch.reset();
    } finally {
      messagesBatchLock.unlock();
    }
  }

  private void writeBatch(final InflightBatch inflightBatch) {
    AppendRowsRequest request = inflightBatch.getMergedRequest();
    messagesWaiter.waitOnElementCount();
    messagesWaiter.waitOnSizeLimit(inflightBatch.getByteSize());
    responseObserver.setInflightBatch(inflightBatch);
    LOG.finer("Sending message: " + request.toString());
    clientStream.send(request);
    messagesWaiter.incrementPendingCount(1);
    messagesWaiter.incrementPendingSize(inflightBatch.getByteSize());
  }

  // The batch of messages that is being sent/processed.
  private static final class InflightBatch {
    // List of requests that is going to be batched.
    final List<AppendRequestAndFutureResponse> inflightRequests;
    // A list tracks expected offset for each AppendRequest. Used to reconstruct the Response
    // future.
    final ArrayList<Long> offsetList;
    final long creationTime;
    int attempt;
    int batchSizeBytes;
    long expectedOffset;
    Boolean attachSchema;

    InflightBatch(
        List<AppendRequestAndFutureResponse> inflightRequests,
        int batchSizeBytes,
        Boolean attachSchema) {
      this.inflightRequests = inflightRequests;
      this.offsetList = new ArrayList<Long>(inflightRequests.size());
      for (AppendRequestAndFutureResponse request : inflightRequests) {
        if (request.message.getOffset().getValue() > 0) {
          offsetList.add(new Long(request.message.getOffset().getValue()));
        } else {
          offsetList.add(new Long(-1));
        }
      }
      this.expectedOffset = offsetList.get(offsetList.size() - 1).longValue();
      attempt = 1;
      creationTime = System.currentTimeMillis();
      this.batchSizeBytes = batchSizeBytes;
      this.attachSchema = attachSchema;
    }

    int count() {
      return inflightRequests.size();
    }

    int getByteSize() {
      return this.batchSizeBytes;
    }

    long getExpectedOffset() {
      return expectedOffset;
    }

    private AppendRowsRequest getMergedRequest() throws IllegalStateException {
      if (inflightRequests.size() == 0) {
        throw new IllegalStateException("Unexpected empty message batch");
      }
      ProtoBufProto.ProtoRows.Builder rowsBuilder =
          inflightRequests.get(0).message.getProtoRows().getRows().toBuilder();
      for (int i = 1; i < inflightRequests.size(); i++) {
        rowsBuilder.addAllSerializedRows(
            inflightRequests.get(i).message.getProtoRows().getRows().getSerializedRowsList());
      }
      AppendRowsRequest.ProtoData.Builder data =
          inflightRequests.get(0).message.getProtoRows().toBuilder().setRows(rowsBuilder.build());
      if (!attachSchema) {
        data.clearWriterSchema();
      }
      return inflightRequests.get(0).message.toBuilder().setProtoRows(data.build()).build();
    }

    private void onFailure(Throwable t) {
      for (AppendRequestAndFutureResponse request : inflightRequests) {
        request.appendResult.setException(t);
      }
    }

    // Disassemble the batched response and sets the furture on individual request.
    private void onSuccess(AppendRowsResponse response) {
      for (int i = 0; i < inflightRequests.size(); i++) {
        AppendRowsResponse.Builder singleResponse = response.toBuilder();
        if (offsetList.get(i) > 0) {
          singleResponse.setOffset(offsetList.get(i));
        } else {
          long actualOffset = response.getOffset();
          for (int j = 0; j < i; j++) {
            actualOffset +=
                inflightRequests.get(j).message.getProtoRows().getRows().getSerializedRowsCount();
          }
          singleResponse.setOffset(actualOffset);
        }
        inflightRequests.get(i).appendResult.set(singleResponse.build());
      }
    }
  }

  // Class that wraps AppendRowsRequest and its cooresponding Response future.
  private static final class AppendRequestAndFutureResponse {
    final SettableApiFuture<AppendRowsResponse> appendResult;
    final AppendRowsRequest message;
    final int messageSize;

    AppendRequestAndFutureResponse(AppendRowsRequest message) {
      this.appendResult = SettableApiFuture.create();
      this.message = message;
      this.messageSize = message.getProtoRows().getSerializedSize();
    }
  }

  /** The batching settings configured on this {@code StreamWriter}. */
  public BatchingSettings getBatchingSettings() {
    return batchingSettings;
  }

  /**
   * Schedules immediate flush of any outstanding messages and waits until all are processed.
   *
   * <p>Sends remaining outstanding messages and prevents future calls to publish. This method
   * should be invoked prior to deleting the {@link WriteStream} object in order to ensure that no
   * pending messages are lost.
   */
  public void shutdown() {
    Preconditions.checkState(
        !shutdown.getAndSet(true), "Cannot shut down a publisher already shut-down.");
    if (currentAlarmFuture != null && activeAlarm.getAndSet(false)) {
      currentAlarmFuture.cancel(false);
    }
    writeAllOutstanding();
    messagesWaiter.waitComplete();
    backgroundResources.shutdown();
  }

  /**
   * Wait for all work has completed execution after a {@link #shutdown()} request, or the timeout
   * occurs, or the current thread is interrupted.
   *
   * <p>Call this method to make sure all resources are freed properly.
   */
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return backgroundResources.awaitTermination(duration, unit);
  }

  /**
   * Constructs a new {@link Builder} using the given topic.
   *
   * <p>Example of creating a {@code WriteStream}.
   *
   * <pre>{@code
   * String table = "projects/my_project/datasets/my_dataset/tables/my_table";
   * String stream;
   * try (BigQueryWriteClient bigqueryWriteClient = BigQueryWriteClient.create()) {
   *     CreateWriteStreamRequest request = CreateWriteStreamRequest.newBuilder().setParent(table).build();
   *     WriteStream response = bigQueryWriteClient.createWriteStream(request);
   *     stream = response.getName();
   * }
   * WriteStream writer = WriteStream.newBuilder(stream).withSchema(LogProto.GetDescriptor()).build();
   * try {
   *   // ...
   * } finally {
   *   // When finished with the writer, make sure to shutdown to free up resources.
   *   writer.shutdown();
   *   writer.awaitTermination(1, TimeUnit.MINUTES);
   * }
   * }</pre>
   */
  public static Builder newBuilder(String streamName) {
    return new Builder(streamName);
  }

  /** A builder of {@link Publisher}s. */
  public static final class Builder {
    static final Duration MIN_TOTAL_TIMEOUT = Duration.ofSeconds(10);
    static final Duration MIN_RPC_TIMEOUT = Duration.ofMillis(10);

    // Meaningful defaults.
    static final long DEFAULT_ELEMENT_COUNT_THRESHOLD = 100L;
    static final long DEFAULT_REQUEST_BYTES_THRESHOLD = 100 * 1024L; // 100 kB
    static final Duration DEFAULT_DELAY_THRESHOLD = Duration.ofMillis(1);
    private static final Duration DEFAULT_INITIAL_RPC_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration DEFAULT_MAX_RPC_TIMEOUT = Duration.ofSeconds(600);
    private static final Duration DEFAULT_TOTAL_TIMEOUT = Duration.ofSeconds(600);
    static final FlowControlSettings DEFAULT_FLOW_CONTROL_SETTINGS =
        FlowControlSettings.newBuilder()
            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
            .setMaxOutstandingElementCount(1000L)
            .setMaxOutstandingRequestBytes(100 * 1024 * 1024L) // 100 Mb
            .build();
    static final BatchingSettings DEFAULT_BATCHING_SETTINGS =
        BatchingSettings.newBuilder()
            .setDelayThreshold(DEFAULT_DELAY_THRESHOLD)
            .setRequestByteThreshold(DEFAULT_REQUEST_BYTES_THRESHOLD)
            .setElementCountThreshold(DEFAULT_ELEMENT_COUNT_THRESHOLD)
            .setFlowControlSettings(DEFAULT_FLOW_CONTROL_SETTINGS)
            .build();
    static final RetrySettings DEFAULT_RETRY_SETTINGS =
        RetrySettings.newBuilder()
            .setTotalTimeout(DEFAULT_TOTAL_TIMEOUT)
            .setInitialRetryDelay(Duration.ofMillis(100))
            .setRetryDelayMultiplier(1.3)
            .setMaxRetryDelay(Duration.ofSeconds(60))
            .setMaxAttempts(3)
            .build();
    static final boolean DEFAULT_ENABLE_MESSAGE_ORDERING = false;
    private static final int THREADS_PER_CPU = 5;
    static final ExecutorProvider DEFAULT_EXECUTOR_PROVIDER =
        InstantiatingExecutorProvider.newBuilder()
            .setExecutorThreadCount(THREADS_PER_CPU * Runtime.getRuntime().availableProcessors())
            .build();

    String streamName;
    private String endpoint = BigQueryWriteSettings.getDefaultEndpoint();

    // Batching options
    BatchingSettings batchingSettings = DEFAULT_BATCHING_SETTINGS;

    RetrySettings retrySettings = DEFAULT_RETRY_SETTINGS;

    private boolean enableMessageOrdering = DEFAULT_ENABLE_MESSAGE_ORDERING;

    private TransportChannelProvider channelProvider =
        BigQueryWriteSettings.defaultGrpcTransportProviderBuilder().setChannelsPerCpu(1).build();

    private HeaderProvider headerProvider = new NoHeaderProvider();
    private HeaderProvider internalHeaderProvider =
        BigQueryWriteSettings.defaultApiClientHeaderProviderBuilder().build();
    ExecutorProvider executorProvider = DEFAULT_EXECUTOR_PROVIDER;
    private CredentialsProvider credentialsProvider =
        BigQueryWriteSettings.defaultCredentialsProviderBuilder().build();

    private Builder(String stream) {
      this.streamName = Preconditions.checkNotNull(stream);
    }

    /**
     * {@code ChannelProvider} to use to create Channels, which must point at Cloud BigQuery Storage
     * API endpoint.
     *
     * <p>For performance, this client benefits from having multiple underlying connections. See
     * {@link com.google.api.gax.grpc.InstantiatingGrpcChannelProvider.Builder#setPoolSize(int)}.
     */
    public Builder setChannelProvider(TransportChannelProvider channelProvider) {
      this.channelProvider = Preconditions.checkNotNull(channelProvider);
      return this;
    }

    /** {@code CredentialsProvider} to use to create Credentials to authenticate calls. */
    public Builder setCredentialsProvider(CredentialsProvider credentialsProvider) {
      this.credentialsProvider = Preconditions.checkNotNull(credentialsProvider);
      return this;
    }

    // Batching options
    public Builder setBatchingSettings(BatchingSettings batchingSettings) {
      Preconditions.checkNotNull(batchingSettings);
      Preconditions.checkNotNull(batchingSettings.getElementCountThreshold());
      Preconditions.checkArgument(batchingSettings.getElementCountThreshold() > 0);
      Preconditions.checkNotNull(batchingSettings.getRequestByteThreshold());
      Preconditions.checkArgument(batchingSettings.getRequestByteThreshold() > 0);
      Preconditions.checkNotNull(batchingSettings.getDelayThreshold());
      Preconditions.checkArgument(batchingSettings.getDelayThreshold().toMillis() > 0);
      this.batchingSettings = batchingSettings;
      return this;
    }

    /** Gives the ability to set a custom executor to be used by the library. */
    public Builder setExecutorProvider(ExecutorProvider executorProvider) {
      this.executorProvider = Preconditions.checkNotNull(executorProvider);
      return this;
    }

    /** Gives the ability to override the gRPC endpoint. */
    public Builder setEndpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public StreamWriter build() throws IOException {
      return new StreamWriter(this);
    }
  }

  private static final class AppendResponseObserver
      implements ResponseObserver<AppendRowsResponse> {
    private InflightBatch inflightBatch;
    private StreamWriter streamWriter;

    public void setInflightBatch(InflightBatch batch) {
      this.inflightBatch = batch;
    }

    public AppendResponseObserver(StreamWriter streamWriter) {
      this.streamWriter = streamWriter;
    }

    private boolean isRecoverableError(Throwable t) {
      Status status = Status.fromThrowable(t);
      return status.getCode() == Status.Code.UNAVAILABLE;
    }

    @Override
    public void onStart(StreamController controller) {
      // no-op
    }

    @Override
    public void onResponse(AppendRowsResponse response) {
      try {
      	if (response == null) {
	        inflightBatch.onFailure(
			        new IllegalStateException("Response is null"));
        }
      	// TODO: Deal with in stream errors.
      	if (response.hasError()) {
      		throw new RuntimeException("Stream had a failed response: " + response.getError().getMessage());
        }
      	if (inflightBatch.getExpectedOffset() > 0
			        && response.getOffset() != inflightBatch.getExpectedOffset()) {
          inflightBatch.onFailure(
              new IllegalStateException(
                  String.format(
                      "The append result offset %s does not match " + "the expected offset %s.",
                      response.getOffset(), inflightBatch.getExpectedOffset())));
        } else {
          inflightBatch.onSuccess(response);
        }
      } finally {
        streamWriter.messagesWaiter.incrementPendingCount(-1);
        streamWriter.messagesWaiter.incrementPendingSize(0 - inflightBatch.getByteSize());
      }
    }

    @Override
    public void onComplete() {}

    @Override
    public void onError(Throwable t) {
      if (isRecoverableError(t)) {
        try {
          streamWriter.refreshAppend();
        } catch (IOException e) {
          inflightBatch.onFailure(e);
          streamWriter.messagesWaiter.incrementPendingCount(-1);
          streamWriter.messagesWaiter.incrementPendingSize(0 - inflightBatch.getByteSize());
        }
      } else {
        try {
          inflightBatch.onFailure(t);
        } finally {
          streamWriter.messagesWaiter.incrementPendingCount(-1);
          streamWriter.messagesWaiter.incrementPendingSize(0 - inflightBatch.getByteSize());
        }
      }
    }
  };

  // This class controls how many messages are going to be sent out in a batch.
  private static class MessagesBatch {
    private List<AppendRequestAndFutureResponse> messages;
    private int batchedBytes;
    private final BatchingSettings batchingSettings;
    private Boolean attachSchema = true;

    private MessagesBatch(BatchingSettings batchingSettings) {
      this.batchingSettings = batchingSettings;
      reset();
    }

    // Get all the messages out in a batch.
    private InflightBatch popBatch() {
      InflightBatch batch = new InflightBatch(messages, batchedBytes, this.attachSchema);
      this.attachSchema = false;
      reset();
      return batch;
    }

    private void reset() {
      messages = new LinkedList<>();
      batchedBytes = 0;
    }

    private void resetAttachSchema() {
      attachSchema = true;
    }

    private boolean isEmpty() {
      return messages.isEmpty();
    }

    private int getBatchedBytes() {
      return batchedBytes;
    }

    private int getMessagesCount() {
      return messages.size();
    }

    private boolean hasBatchingBytes() {
      return getMaxBatchBytes() > 0;
    }

    private long getMaxBatchBytes() {
      return batchingSettings.getRequestByteThreshold();
    }

    // The message batch returned could contain the previous batch of messages plus the current
    // message.
    // if the message is too large.
    private List<InflightBatch> add(AppendRequestAndFutureResponse outstandingAppend) {
      List<InflightBatch> batchesToSend = new ArrayList<>();
      // Check if the next message makes the current batch exceed the max batch byte size.
      if (!isEmpty()
          && hasBatchingBytes()
          && getBatchedBytes() + outstandingAppend.messageSize >= getMaxBatchBytes()) {
        batchesToSend.add(popBatch());
      }

      messages.add(outstandingAppend);
      batchedBytes += outstandingAppend.messageSize;

      // Border case: If the message to send is greater or equals to the max batch size then send it
      // immediately.
      // Alternatively if after adding the message we have reached the batch max messages then we
      // have a batch to send.
      if ((hasBatchingBytes() && outstandingAppend.messageSize >= getMaxBatchBytes())
          || getMessagesCount() == batchingSettings.getElementCountThreshold()) {
        batchesToSend.add(popBatch());
      }

      return batchesToSend;
    }
  }
}
