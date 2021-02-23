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
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorAsBackgroundResource;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.AbortedException;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnimplementedException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Preconditions;
import com.google.protobuf.Int64Value;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.concurrent.GuardedBy;
import org.threeten.bp.Duration;

/**
 * A BigQuery Stream Writer that can be used to write data into BigQuery Table.
 *
 * <p>This is to be used to managed streaming write when you are working with PENDING streams or
 * want to explicitly manage offset. In that most common cases when writing with COMMITTED stream
 * without offset, please use a simpler writer {@code DirectWriter}.
 *
 * <p>A {@link StreamWrier} provides built-in capabilities to: handle batching of messages;
 * controlling memory utilization (through flow control) and request cleanup (only keeps write
 * schema on first request in the stream).
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
public class StreamWriter implements AutoCloseable {
  private static final Logger LOG = Logger.getLogger(StreamWriter.class.getName());

  private static String streamPatternString =
      "(projects/[^/]+/datasets/[^/]+/tables/[^/]+)/streams/[^/]+";
  private static String tablePatternString = "(projects/[^/]+/datasets/[^/]+/tables/[^/]+)";

  private static Pattern streamPattern = Pattern.compile(streamPatternString);
  private static Pattern tablePattern = Pattern.compile(tablePatternString);

  private final String streamName;
  private final String tableName;

  private final String traceId;

  private final BatchingSettings batchingSettings;
  private final RetrySettings retrySettings;
  private BigQueryWriteSettings stubSettings;

  private final Lock messagesBatchLock;
  private final Lock appendAndRefreshAppendLock;

  @GuardedBy("appendAndRefreshAppendLock")
  private final MessagesBatch messagesBatch;

  // Indicates if a stream has some non recoverable exception happened.
  private AtomicReference<Throwable> streamException;

  private BackgroundResource backgroundResources;
  private List<BackgroundResource> backgroundResourceList;

  private BigQueryWriteClient stub;
  BidiStreamingCallable<AppendRowsRequest, AppendRowsResponse> bidiStreamingCallable;

  @GuardedBy("appendAndRefreshAppendLock")
  ClientStream<AppendRowsRequest> clientStream;

  private final AppendResponseObserver responseObserver;

  private final ScheduledExecutorService executor;

  @GuardedBy("appendAndRefreshAppendLock")
  private boolean shutdown;

  private final Waiter messagesWaiter;

  @GuardedBy("appendAndRefreshAppendLock")
  private boolean activeAlarm;

  private ScheduledFuture<?> currentAlarmFuture;

  private Integer currentRetries = 0;

  // Used for schema updates
  private OnSchemaUpdateRunnable onSchemaUpdateRunnable;

  /** The maximum size of one request. Defined by the API. */
  public static long getApiMaxRequestBytes() {
    return 10L * 1000L * 1000L; // 10 megabytes (https://en.wikipedia.org/wiki/Megabyte)
  }

  /** The maximum size of in flight requests. Defined by the API. */
  public static long getApiMaxInflightRequests() {
    return 5000L;
  }

  private StreamWriter(Builder builder)
      throws IllegalArgumentException, IOException, InterruptedException {
    if (builder.createDefaultStream) {
      Matcher matcher = tablePattern.matcher(builder.streamOrTableName);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid table name: " + builder.streamOrTableName);
      }
      streamName = builder.streamOrTableName + "/_default";
      tableName = builder.streamOrTableName;
    } else {
      Matcher matcher = streamPattern.matcher(builder.streamOrTableName);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid stream name: " + builder.streamOrTableName);
      }
      streamName = builder.streamOrTableName;
      tableName = matcher.group(1);
    }

    this.traceId = builder.traceId;
    this.batchingSettings = builder.batchingSettings;
    this.retrySettings = builder.retrySettings;
    this.messagesBatch = new MessagesBatch(batchingSettings, this.streamName, this);
    messagesBatchLock = new ReentrantLock();
    appendAndRefreshAppendLock = new ReentrantLock();
    activeAlarm = false;
    this.streamException = new AtomicReference<Throwable>(null);

    executor = builder.executorProvider.getExecutor();
    backgroundResourceList = new ArrayList<>();
    if (builder.executorProvider.shouldAutoClose()) {
      backgroundResourceList.add(new ExecutorAsBackgroundResource(executor));
    }
    messagesWaiter = new Waiter(this.batchingSettings.getFlowControlSettings());
    responseObserver = new AppendResponseObserver(this);

    if (builder.client == null) {
      stubSettings =
          BigQueryWriteSettings.newBuilder()
              .setCredentialsProvider(builder.credentialsProvider)
              .setTransportChannelProvider(builder.channelProvider)
              .setEndpoint(builder.endpoint)
              .build();
      stub = BigQueryWriteClient.create(stubSettings);
      backgroundResourceList.add(stub);
    } else {
      stub = builder.client;
    }
    backgroundResources = new BackgroundResourceAggregation(backgroundResourceList);
    shutdown = false;
    if (builder.onSchemaUpdateRunnable != null) {
      this.onSchemaUpdateRunnable = builder.onSchemaUpdateRunnable;
      this.onSchemaUpdateRunnable.setStreamWriter(this);
    }

    bidiStreamingCallable = stub.appendRowsCallable();
    clientStream = bidiStreamingCallable.splitCall(responseObserver);
    try {
      while (!clientStream.isSendReady()) {
        Thread.sleep(10);
      }
    } catch (InterruptedException e) {
    }
  }

  /** Stream name we are writing to. */
  public String getStreamNameString() {
    return streamName;
  }

  /** Table name we are writing to. */
  public String getTableNameString() {
    return tableName;
  }

  /** OnSchemaUpdateRunnable for this streamWriter. */
  OnSchemaUpdateRunnable getOnSchemaUpdateRunnable() {
    return this.onSchemaUpdateRunnable;
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
    appendAndRefreshAppendLock.lock();
    try {
      Preconditions.checkState(!shutdown, "Cannot append on a shut-down writer.");
      Preconditions.checkNotNull(message, "Message is null.");
      Preconditions.checkState(streamException.get() == null, "Stream already failed.");
      final AppendRequestAndFutureResponse outstandingAppend =
          new AppendRequestAndFutureResponse(message);
      List<InflightBatch> batchesToSend;
      batchesToSend = messagesBatch.add(outstandingAppend);
      // Setup the next duration based delivery alarm if there are messages batched.
      setupAlarm();
      if (!batchesToSend.isEmpty()) {
        for (final InflightBatch batch : batchesToSend) {
          LOG.fine("Scheduling a batch for immediate sending");
          writeBatch(batch);
        }
      }
      return outstandingAppend.appendResult;
    } finally {
      appendAndRefreshAppendLock.unlock();
    }
  }

  /**
   * Re-establishes a stream connection.
   *
   * @throws InterruptedException
   */
  public void refreshAppend() throws InterruptedException {
    throw new UnimplementedException(null, GrpcStatusCode.of(Status.Code.UNIMPLEMENTED), false);
  }

  @GuardedBy("appendAndRefreshAppendLock")
  private void setupAlarm() {
    if (!messagesBatch.isEmpty()) {
      if (!activeAlarm) {
        long delayThresholdMs = getBatchingSettings().getDelayThreshold().toMillis();
        LOG.log(Level.FINE, "Setting up alarm for the next {0} ms.", delayThresholdMs);
        currentAlarmFuture =
            executor.schedule(
                new Runnable() {
                  @Override
                  public void run() {
                    LOG.fine("Sending messages based on schedule");
                    appendAndRefreshAppendLock.lock();
                    activeAlarm = false;
                    try {
                      writeBatch(messagesBatch.popBatch());
                    } finally {
                      appendAndRefreshAppendLock.unlock();
                    }
                  }
                },
                delayThresholdMs,
                TimeUnit.MILLISECONDS);
      }
    } else if (currentAlarmFuture != null) {
      LOG.log(Level.FINER, "Cancelling alarm, no more messages");
      currentAlarmFuture.cancel(false);
      activeAlarm = false;
    }
  }

  /**
   * Write any outstanding batches if non-empty. This method sends buffered messages, but does not
   * wait for the send operations to complete. To wait for messages to send, call {@code get} on the
   * futures returned from {@code append}.
   */
  @GuardedBy("appendAndRefreshAppendLock")
  public void writeAllOutstanding() {
    InflightBatch unorderedOutstandingBatch = null;
    if (!messagesBatch.isEmpty()) {
      writeBatch(messagesBatch.popBatch());
    }
    messagesBatch.reset();
  }

  @GuardedBy("appendAndRefreshAppendLock")
  private void writeBatch(final InflightBatch inflightBatch) {
    if (inflightBatch != null) {
      AppendRowsRequest request = inflightBatch.getMergedRequest();
      try {
        appendAndRefreshAppendLock.unlock();
        messagesWaiter.acquire(inflightBatch.getByteSize());
        appendAndRefreshAppendLock.lock();
        if (streamException.get() != null) {
          appendAndRefreshAppendLock.unlock();
          messagesWaiter.release(inflightBatch.getByteSize());
          appendAndRefreshAppendLock.lock();
          inflightBatch.onFailure(
              new AbortedException(
                  "Stream has previous errors, abort append",
                  null,
                  GrpcStatusCode.of(Status.Code.ABORTED),
                  true));
          return;
        }
        responseObserver.addInflightBatch(inflightBatch);
        clientStream.send(request);
      } catch (FlowController.FlowControlException ex) {
        appendAndRefreshAppendLock.lock();
        inflightBatch.onFailure(ex);
      }
    }
  }

  /** Close the stream writer. Shut down all resources. */
  @Override
  public void close() {
    LOG.info("Closing stream writer:" + streamName);
    shutdown();
    try {
      awaitTermination(1, TimeUnit.MINUTES);
    } catch (InterruptedException ignored) {
    }
  }

  // The batch of messages that is being sent/processed.
  private static final class InflightBatch {
    // List of requests that is going to be batched.
    final List<AppendRequestAndFutureResponse> inflightRequests;
    // A list tracks expected offset for each AppendRequest. Used to reconstruct the Response
    // future.
    private final ArrayList<Long> offsetList;
    private final long creationTime;
    private int attempt;
    private long batchSizeBytes;
    private long expectedOffset;
    private Boolean attachSchema;
    private String streamName;
    private final AtomicBoolean failed;
    private final StreamWriter streamWriter;

    InflightBatch(
        List<AppendRequestAndFutureResponse> inflightRequests,
        long batchSizeBytes,
        String streamName,
        Boolean attachSchema,
        StreamWriter streamWriter) {
      this.inflightRequests = inflightRequests;
      this.offsetList = new ArrayList<Long>(inflightRequests.size());
      for (AppendRequestAndFutureResponse request : inflightRequests) {
        if (request.message.hasOffset()) {
          offsetList.add(new Long(request.message.getOffset().getValue()));
        } else {
          offsetList.add(new Long(-1));
        }
      }
      this.expectedOffset = offsetList.get(0).longValue();
      attempt = 1;
      creationTime = System.currentTimeMillis();
      this.batchSizeBytes = batchSizeBytes;
      this.attachSchema = attachSchema;
      this.streamName = streamName;
      this.failed = new AtomicBoolean(false);
      this.streamWriter = streamWriter;
    }

    int count() {
      return inflightRequests.size();
    }

    long getByteSize() {
      return this.batchSizeBytes;
    }

    long getExpectedOffset() {
      return expectedOffset;
    }

    private AppendRowsRequest getMergedRequest() throws IllegalStateException {
      if (inflightRequests.size() == 0) {
        throw new IllegalStateException("Unexpected empty message batch");
      }
      ProtoRows.Builder rowsBuilder =
          inflightRequests.get(0).message.getProtoRows().getRows().toBuilder();
      for (int i = 1; i < inflightRequests.size(); i++) {
        rowsBuilder.addAllSerializedRows(
            inflightRequests.get(i).message.getProtoRows().getRows().getSerializedRowsList());
      }
      AppendRowsRequest.ProtoData.Builder data =
          inflightRequests.get(0).message.getProtoRows().toBuilder().setRows(rowsBuilder.build());
      AppendRowsRequest.Builder requestBuilder = inflightRequests.get(0).message.toBuilder();
      if (!attachSchema) {
        data.clearWriterSchema();
        requestBuilder.clearWriteStream();
      } else {
        if (!data.hasWriterSchema()) {
          throw new IllegalStateException(
              "The first message on the connection must have writer schema set");
        }
        requestBuilder.setWriteStream(streamName);
        if (!inflightRequests.get(0).message.getTraceId().isEmpty()) {
          requestBuilder.setTraceId(inflightRequests.get(0).message.getTraceId());
        } else if (streamWriter.traceId != null) {
          requestBuilder.setTraceId(streamWriter.traceId);
        }
      }
      return requestBuilder.setProtoRows(data.build()).build();
    }

    private void onFailure(Throwable t) {
      if (failed.getAndSet(true)) {
        // Error has been set already.
        LOG.warning("Ignore " + t.toString() + " since error has already been set");
        return;
      }

      for (AppendRequestAndFutureResponse request : inflightRequests) {
        request.appendResult.setException(t);
      }
    }

    // Disassemble the batched response and sets the furture on individual request.
    private void onSuccess(AppendRowsResponse response) {
      for (int i = 0; i < inflightRequests.size(); i++) {
        AppendRowsResponse.Builder singleResponse = response.toBuilder();
        if (response.getAppendResult().hasOffset()) {
          long actualOffset = response.getAppendResult().getOffset().getValue();
          for (int j = 0; j < i; j++) {
            actualOffset +=
                inflightRequests.get(j).message.getProtoRows().getRows().getSerializedRowsCount();
          }
          singleResponse.setAppendResult(
              AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(actualOffset)));
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
      if (this.messageSize > getApiMaxRequestBytes()) {
        throw new StatusRuntimeException(
            Status.fromCode(Status.Code.FAILED_PRECONDITION)
                .withDescription("Message exceeded max size limit: " + getApiMaxRequestBytes()));
      }
    }
  }

  /** The batching settings configured on this {@code StreamWriter}. */
  public BatchingSettings getBatchingSettings() {
    return batchingSettings;
  }

  /** The retry settings configured on this {@code StreamWriter}. */
  public RetrySettings getRetrySettings() {
    return retrySettings;
  }

  /**
   * Schedules immediate flush of any outstanding messages and waits until all are processed.
   *
   * <p>Sends remaining outstanding messages and prevents future calls to publish. This method
   * should be invoked prior to deleting the {@link WriteStream} object in order to ensure that no
   * pending messages are lost.
   */
  protected void shutdown() {
    appendAndRefreshAppendLock.lock();
    try {
      if (shutdown) {
        LOG.fine("Already shutdown.");
        return;
      }
      shutdown = true;
      LOG.info("Shutdown called on writer: " + streamName);
      if (currentAlarmFuture != null && activeAlarm) {
        currentAlarmFuture.cancel(false);
        activeAlarm = false;
      }
      writeAllOutstanding();
      if (streamException.get() != null) {
        this.responseObserver.abortInflightRequests(
            new AbortedException(
                "Request aborted due to previous failures",
                streamException.get(),
                GrpcStatusCode.of(Status.Code.ABORTED),
                true));
      }
      try {
        appendAndRefreshAppendLock.unlock();
        messagesWaiter.waitComplete(0);
      } catch (InterruptedException e) {
        LOG.warning("Failed to wait for messages to return " + e.toString());
      }
      appendAndRefreshAppendLock.lock();
      if (clientStream.isSendReady()) {
        clientStream.closeSend();
      }
      backgroundResources.shutdown();
    } finally {
      appendAndRefreshAppendLock.unlock();
    }
  }

  /**
   * Wait for all work has completed execution after a {@link #shutdown()} request, or the timeout
   * occurs, or the current thread is interrupted.
   *
   * <p>Call this method to make sure all resources are freed properly.
   */
  protected boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return backgroundResources.awaitTermination(duration, unit);
  }

  /**
   * Constructs a new {@link Builder} using the given stream. If builder has createDefaultStream set
   * to true, then user should pass in a table name here.
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
   * try (WriteStream writer = WriteStream.newBuilder(stream).build()) {
   *   //...
   * }
   * }</pre>
   *
   * <p>Example of creating a default {@code WriteStream}, which is COMMIT only and doesn't support
   * offset. But it will support higher thoughput per stream and not subject to CreateWriteStream
   * quotas.
   *
   * <pre>{@code
   * String table = "projects/my_project/datasets/my_dataset/tables/my_table";
   * try (WriteStream writer = WriteStream.newBuilder(table).createDefaultStream().build()) {
   *   //...
   * }
   * }</pre>
   */
  public static Builder newBuilder(String streamOrTableName) {
    Preconditions.checkNotNull(streamOrTableName, "streamOrTableName is null.");
    return new Builder(streamOrTableName, null);
  }

  /**
   * Constructs a new {@link Builder} using the given stream and an existing BigQueryWriteClient.
   */
  public static Builder newBuilder(String streamOrTableName, BigQueryWriteClient client) {
    Preconditions.checkNotNull(streamOrTableName, "streamOrTableName is null.");
    Preconditions.checkNotNull(client, "Client is null.");
    return new Builder(streamOrTableName, client);
  }

  /** A builder of {@link StreamWriter}s. */
  public static final class Builder {
    static final Duration MIN_TOTAL_TIMEOUT = Duration.ofSeconds(10);
    static final Duration MIN_RPC_TIMEOUT = Duration.ofMillis(10);

    // Meaningful defaults.
    static final FlowControlSettings DEFAULT_FLOW_CONTROL_SETTINGS =
        FlowControlSettings.newBuilder()
            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
            .setMaxOutstandingElementCount(1000L)
            .setMaxOutstandingRequestBytes(100 * 1024 * 1024L) // 100 Mb
            .build();
    public static final BatchingSettings DEFAULT_BATCHING_SETTINGS =
        BatchingSettings.newBuilder()
            .setDelayThreshold(Duration.ofMillis(10))
            .setRequestByteThreshold(100 * 1024L) // 100 kb
            .setElementCountThreshold(100L)
            .setFlowControlSettings(DEFAULT_FLOW_CONTROL_SETTINGS)
            .build();
    public static final RetrySettings DEFAULT_RETRY_SETTINGS =
        RetrySettings.newBuilder()
            .setMaxRetryDelay(Duration.ofSeconds(60))
            .setInitialRetryDelay(Duration.ofMillis(100))
            .setMaxAttempts(3)
            .build();
    private static final int THREADS_PER_CPU = 5;
    static final ExecutorProvider DEFAULT_EXECUTOR_PROVIDER =
        InstantiatingExecutorProvider.newBuilder()
            .setExecutorThreadCount(THREADS_PER_CPU * Runtime.getRuntime().availableProcessors())
            .build();

    private String streamOrTableName;
    private String endpoint = BigQueryWriteSettings.getDefaultEndpoint();

    private String traceId;

    private BigQueryWriteClient client = null;

    // Batching options
    BatchingSettings batchingSettings = DEFAULT_BATCHING_SETTINGS;

    RetrySettings retrySettings = DEFAULT_RETRY_SETTINGS;

    private TransportChannelProvider channelProvider =
        BigQueryWriteSettings.defaultGrpcTransportProviderBuilder().setChannelsPerCpu(1).build();

    ExecutorProvider executorProvider = DEFAULT_EXECUTOR_PROVIDER;
    private CredentialsProvider credentialsProvider =
        BigQueryWriteSettings.defaultCredentialsProviderBuilder().build();

    private OnSchemaUpdateRunnable onSchemaUpdateRunnable;

    private boolean createDefaultStream = false;

    private Builder(String streamOrTableName, BigQueryWriteClient client) {
      this.streamOrTableName = Preconditions.checkNotNull(streamOrTableName);
      this.client = client;
    }

    /**
     * {@code ChannelProvider} to use to create Channels, which must point at Cloud BigQuery Storage
     * API endpoint.
     *
     * <p>For performance, this client benefits from having multiple underlying connections. See
     * {@link com.google.api.gax.grpc.InstantiatingGrpcChannelProvider.Builder#setPoolSize(int)}.
     */
    public Builder setChannelProvider(TransportChannelProvider channelProvider) {
      this.channelProvider =
          Preconditions.checkNotNull(channelProvider, "ChannelProvider is null.");
      return this;
    }

    /** {@code CredentialsProvider} to use to create Credentials to authenticate calls. */
    public Builder setCredentialsProvider(CredentialsProvider credentialsProvider) {
      this.credentialsProvider =
          Preconditions.checkNotNull(credentialsProvider, "CredentialsProvider is null.");
      return this;
    }

    /**
     * Sets the {@code BatchSettings} on the writer.
     *
     * @param batchingSettings
     * @return
     */
    public Builder setBatchingSettings(BatchingSettings batchingSettings) {
      Preconditions.checkNotNull(batchingSettings, "BatchingSettings is null.");

      BatchingSettings.Builder builder = batchingSettings.toBuilder();
      Preconditions.checkNotNull(batchingSettings.getElementCountThreshold());
      Preconditions.checkArgument(batchingSettings.getElementCountThreshold() > 0);
      Preconditions.checkNotNull(batchingSettings.getRequestByteThreshold());
      Preconditions.checkArgument(batchingSettings.getRequestByteThreshold() > 0);
      if (batchingSettings.getRequestByteThreshold() > getApiMaxRequestBytes()) {
        builder.setRequestByteThreshold(getApiMaxRequestBytes());
      }
      Preconditions.checkNotNull(batchingSettings.getDelayThreshold());
      Preconditions.checkArgument(batchingSettings.getDelayThreshold().toMillis() > 0);
      if (batchingSettings.getFlowControlSettings() == null) {
        builder.setFlowControlSettings(DEFAULT_FLOW_CONTROL_SETTINGS);
      } else {

        if (batchingSettings.getFlowControlSettings().getMaxOutstandingElementCount() == null) {
          builder.setFlowControlSettings(
              batchingSettings
                  .getFlowControlSettings()
                  .toBuilder()
                  .setMaxOutstandingElementCount(
                      DEFAULT_FLOW_CONTROL_SETTINGS.getMaxOutstandingElementCount())
                  .build());
        } else {
          Preconditions.checkArgument(
              batchingSettings.getFlowControlSettings().getMaxOutstandingElementCount() > 0);
          if (batchingSettings.getFlowControlSettings().getMaxOutstandingElementCount()
              > getApiMaxInflightRequests()) {
            builder.setFlowControlSettings(
                batchingSettings
                    .getFlowControlSettings()
                    .toBuilder()
                    .setMaxOutstandingElementCount(getApiMaxInflightRequests())
                    .build());
          }
        }
        if (batchingSettings.getFlowControlSettings().getMaxOutstandingRequestBytes() == null) {
          builder.setFlowControlSettings(
              batchingSettings
                  .getFlowControlSettings()
                  .toBuilder()
                  .setMaxOutstandingRequestBytes(
                      DEFAULT_FLOW_CONTROL_SETTINGS.getMaxOutstandingRequestBytes())
                  .build());
        } else {
          Preconditions.checkArgument(
              batchingSettings.getFlowControlSettings().getMaxOutstandingRequestBytes() > 0);
        }
        if (batchingSettings.getFlowControlSettings().getLimitExceededBehavior() == null) {
          builder.setFlowControlSettings(
              batchingSettings
                  .getFlowControlSettings()
                  .toBuilder()
                  .setLimitExceededBehavior(
                      DEFAULT_FLOW_CONTROL_SETTINGS.getLimitExceededBehavior())
                  .build());
        } else {
          Preconditions.checkArgument(
              batchingSettings.getFlowControlSettings().getLimitExceededBehavior()
                  != FlowController.LimitExceededBehavior.Ignore);
        }
      }
      this.batchingSettings = builder.build();
      return this;
    }

    /**
     * Sets the {@code RetrySettings} on the writer.
     *
     * @param retrySettings
     * @return
     */
    public Builder setRetrySettings(RetrySettings retrySettings) {
      this.retrySettings = Preconditions.checkNotNull(retrySettings, "RetrySettings is null.");
      return this;
    }

    /** Gives the ability to set a custom executor to be used by the library. */
    public Builder setExecutorProvider(ExecutorProvider executorProvider) {
      this.executorProvider =
          Preconditions.checkNotNull(executorProvider, "ExecutorProvider is null.");
      return this;
    }

    /** Gives the ability to override the gRPC endpoint. */
    public Builder setEndpoint(String endpoint) {
      this.endpoint = Preconditions.checkNotNull(endpoint, "Endpoint is null.");
      return this;
    }

    /** Gives the ability to set action on schema update. */
    public Builder setOnSchemaUpdateRunnable(OnSchemaUpdateRunnable onSchemaUpdateRunnable) {
      this.onSchemaUpdateRunnable =
          Preconditions.checkNotNull(onSchemaUpdateRunnable, "onSchemaUpdateRunnable is null.");
      return this;
    }

    /** If the stream is a default stream. */
    public Builder createDefaultStream() {
      this.createDefaultStream = true;
      return this;
    }

    /** Mark the request as coming from Dataflow. */
    public Builder setDataflowTraceId() {
      this.traceId = "Dataflow";
      return this;
    }

    /** Builds the {@code StreamWriter}. */
    public StreamWriter build() throws IllegalArgumentException, IOException, InterruptedException {
      return new StreamWriter(this);
    }
  }

  private static final class AppendResponseObserver
      implements ResponseObserver<AppendRowsResponse> {
    private Queue<InflightBatch> inflightBatches = new LinkedList<InflightBatch>();
    private StreamWriter streamWriter;

    public void addInflightBatch(InflightBatch batch) {
      synchronized (this.inflightBatches) {
        this.inflightBatches.add(batch);
      }
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

    private void abortInflightRequests(Throwable t) {
      LOG.fine("Aborting all inflight requests");
      synchronized (this.inflightBatches) {
        boolean first_error = true;
        while (!this.inflightBatches.isEmpty()) {
          InflightBatch inflightBatch = this.inflightBatches.poll();
          if (first_error || t.getCause().getClass() == AbortedException.class) {
            inflightBatch.onFailure(t);
            first_error = false;
          } else {
            inflightBatch.onFailure(
                new AbortedException(
                    "Request aborted due to previous failures",
                    t,
                    GrpcStatusCode.of(Status.Code.ABORTED),
                    true));
          }
          streamWriter.messagesWaiter.release(inflightBatch.getByteSize());
        }
      }
    }

    @Override
    public void onResponse(AppendRowsResponse response) {
      InflightBatch inflightBatch = null;
      synchronized (this.inflightBatches) {
        inflightBatch = this.inflightBatches.poll();
      }
      try {
        streamWriter.currentRetries = 0;
        if (response == null) {
          inflightBatch.onFailure(new IllegalStateException("Response is null"));
        }
        if (response.hasUpdatedSchema()) {
          if (streamWriter.getOnSchemaUpdateRunnable() != null) {
            streamWriter.getOnSchemaUpdateRunnable().setUpdatedSchema(response.getUpdatedSchema());
            streamWriter.executor.schedule(
                streamWriter.getOnSchemaUpdateRunnable(), 0L, TimeUnit.MILLISECONDS);
          }
        }
        // Currently there is nothing retryable. If the error is already exists, then ignore it.
        if (response.hasError()) {
          StatusRuntimeException exception =
              new StatusRuntimeException(
                  Status.fromCodeValue(response.getError().getCode())
                      .withDescription(response.getError().getMessage()));
          inflightBatch.onFailure(exception);
        } else {
          if (inflightBatch.getExpectedOffset() > 0
              && (response.getAppendResult().hasOffset()
                  && response.getAppendResult().getOffset().getValue()
                      != inflightBatch.getExpectedOffset())) {
            IllegalStateException exception =
                new IllegalStateException(
                    String.format(
                        "The append result offset %s does not match " + "the expected offset %s.",
                        response.getAppendResult().getOffset().getValue(),
                        inflightBatch.getExpectedOffset()));
            inflightBatch.onFailure(exception);
            abortInflightRequests(
                new AbortedException(
                    "Request aborted due to previous failures",
                    exception,
                    GrpcStatusCode.of(Status.Code.ABORTED),
                    true));
          } else {
            inflightBatch.onSuccess(response);
          }
        }
      } finally {
        streamWriter.messagesWaiter.release(inflightBatch.getByteSize());
      }
    }

    @Override
    public void onComplete() {
      LOG.info("OnComplete called");
    }

    @Override
    public void onError(Throwable t) {
      LOG.info("OnError called: " + t.toString());
      streamWriter.streamException.set(t);
      abortInflightRequests(t);
    }
  };

  // This class controls how many messages are going to be sent out in a batch.
  private static class MessagesBatch {
    private List<AppendRequestAndFutureResponse> messages;
    private long batchedBytes;
    private final BatchingSettings batchingSettings;
    private Boolean attachSchema = true;
    private final String streamName;
    private final StreamWriter streamWriter;

    private MessagesBatch(
        BatchingSettings batchingSettings, String streamName, StreamWriter streamWriter) {
      this.batchingSettings = batchingSettings;
      this.streamName = streamName;
      this.streamWriter = streamWriter;
      reset();
    }

    // Get all the messages out in a batch.
    @GuardedBy("appendAndRefreshAppendLock")
    private InflightBatch popBatch() {
      InflightBatch batch =
          new InflightBatch(
              messages, batchedBytes, this.streamName, this.attachSchema, this.streamWriter);
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

    private long getBatchedBytes() {
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
    @GuardedBy("appendAndRefreshAppendLock")
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
