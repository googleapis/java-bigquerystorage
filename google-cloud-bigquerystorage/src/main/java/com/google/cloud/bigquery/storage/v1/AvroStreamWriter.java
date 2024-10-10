package com.google.cloud.bigquery.storage.v1;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import org.apache.avro.generic.GenericRecord;

/**
 * This is 99% exact copy of cloud-bigquerystorage {@link JsonStreamWriter} but using Avro's
 * GenericRecord instead of JSONArray/JSONObject. Must be in this package to access
 * ConnectionWorker.MAXIMUM_REQUEST_CALLBACK_WAIT_TIME
 */
public class AvroStreamWriter {

  private final SchemaAwareStreamWriter<GenericRecord> schemaAwareStreamWriter;
  private static final String CLIENT_ID = "java-avrowriter";

  /**
   * Constructs the AvroStreamWriter
   *
   * @param builder The Builder object for the AvroStreamWriter
   */
  private AvroStreamWriter(SchemaAwareStreamWriter.Builder<GenericRecord> builder)
      throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException,
          InterruptedException {
    this.schemaAwareStreamWriter = builder.build();
  }

  public ApiFuture<AppendRowsResponse> append(Iterable<GenericRecord> records)
      throws IOException, Descriptors.DescriptorValidationException {
    return this.schemaAwareStreamWriter.append(records);
  }

  public ApiFuture<AppendRowsResponse> append(Iterable<GenericRecord> records, long offset)
      throws IOException, Descriptors.DescriptorValidationException {
    return this.schemaAwareStreamWriter.append(records, offset);
  }

  public String getStreamName() {
    return this.schemaAwareStreamWriter.getStreamName();
  }

  public String getWriterId() {
    return this.schemaAwareStreamWriter.getWriterId();
  }

  public Descriptors.Descriptor getDescriptor() {
    return this.schemaAwareStreamWriter.getDescriptor();
  }

  public String getLocation() {
    return this.schemaAwareStreamWriter.getLocation();
  }

  public long getInflightWaitSeconds() {
    return this.schemaAwareStreamWriter.getInflightWaitSeconds();
  }

  public Map<String, AppendRowsRequest.MissingValueInterpretation>
      getMissingValueInterpretationMap() {
    return this.schemaAwareStreamWriter.getMissingValueInterpretationMap();
  }

  public static Builder newBuilder(String streamOrTableName, TableSchema tableSchema) {
    return new Builder(
        SchemaAwareStreamWriter.newBuilder(
            streamOrTableName, tableSchema, AvroToProtoConverter.INSTANCE));
  }

  public static Builder newBuilder(
      String streamOrTableName, TableSchema tableSchema, BigQueryWriteClient client) {
    return new Builder(
        SchemaAwareStreamWriter.newBuilder(
            streamOrTableName, tableSchema, client, AvroToProtoConverter.INSTANCE));
  }

  public static Builder newBuilder(String streamOrTableName, BigQueryWriteClient client) {
    return new Builder(
        SchemaAwareStreamWriter.newBuilder(
            streamOrTableName, client, AvroToProtoConverter.INSTANCE));
  }

  public static void setMaxRequestCallbackWaitTime(Duration waitTime) {
    ConnectionWorker.MAXIMUM_REQUEST_CALLBACK_WAIT_TIME = waitTime;
  }

  public void close() {
    this.schemaAwareStreamWriter.close();
  }

  public boolean isClosed() {
    return this.schemaAwareStreamWriter.isClosed();
  }

  public boolean isUserClosed() {
    return this.schemaAwareStreamWriter.isUserClosed();
  }

  public static final class Builder {
    private final SchemaAwareStreamWriter.Builder<GenericRecord> schemaAwareStreamWriterBuilder;

    private Builder(SchemaAwareStreamWriter.Builder<GenericRecord> schemaAwareStreamWriterBuilder) {
      this.schemaAwareStreamWriterBuilder = schemaAwareStreamWriterBuilder.setClientId(CLIENT_ID);
    }

    /**
     * Setter for the underlying StreamWriter's TransportChannelProvider.
     *
     * @param channelProvider
     * @return Builder
     */
    public Builder setChannelProvider(TransportChannelProvider channelProvider) {
      this.schemaAwareStreamWriterBuilder.setChannelProvider(channelProvider);
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's CredentialsProvider.
     *
     * @param credentialsProvider
     * @return Builder
     */
    public Builder setCredentialsProvider(CredentialsProvider credentialsProvider) {
      this.schemaAwareStreamWriterBuilder.setCredentialsProvider(credentialsProvider);
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's ExecutorProvider.
     *
     * @param executorProvider
     * @return
     */
    public Builder setExecutorProvider(ExecutorProvider executorProvider) {
      this.schemaAwareStreamWriterBuilder.setExecutorProvider(executorProvider);
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's FlowControlSettings.
     *
     * @param flowControlSettings
     * @return Builder
     */
    public Builder setFlowControlSettings(FlowControlSettings flowControlSettings) {
      this.schemaAwareStreamWriterBuilder.setFlowControlSettings(flowControlSettings);
      return this;
    }

    /**
     * Stream name on the builder.
     *
     * @return Builder
     */
    public String getStreamName() {
      return this.schemaAwareStreamWriterBuilder.getStreamName();
    }

    /**
     * Setter for the underlying StreamWriter's Endpoint.
     *
     * @param endpoint
     * @return Builder
     */
    public Builder setEndpoint(String endpoint) {
      this.schemaAwareStreamWriterBuilder.setEndpoint(endpoint);
      return this;
    }

    /**
     * Setter for a traceId to help identify traffic origin.
     *
     * @param traceId
     * @return Builder
     */
    public Builder setTraceId(String traceId) {
      this.schemaAwareStreamWriterBuilder.setTraceId(traceId);
      return this;
    }

    /**
     * Setter for a ignoreUnkownFields, if true, unknown Json fields to BigQuery will be ignored
     * instead of error out.
     *
     * @param ignoreUnknownFields
     * @return Builder
     */
    public Builder setIgnoreUnknownFields(boolean ignoreUnknownFields) {
      this.schemaAwareStreamWriterBuilder.setIgnoreUnknownFields(ignoreUnknownFields);
      return this;
    }

    /** This parameter is not used. It will be removed soon. */
    public Builder setReconnectAfter10M(boolean reconnectAfter10M) {
      return this;
    }

    /**
     * Enable multiplexing for this writer. In multiplexing mode tables will share the same
     * connection if possible until the connection is overwhelmed.
     *
     * @param enableConnectionPool
     * @return Builder
     */
    public Builder setEnableConnectionPool(boolean enableConnectionPool) {
      this.schemaAwareStreamWriterBuilder.setEnableConnectionPool(enableConnectionPool);
      return this;
    }

    /**
     * Location of the table this stream writer is targeting. Connection pools are shared by
     * location.
     *
     * @param location
     * @return Builder
     */
    public Builder setLocation(String location) {
      this.schemaAwareStreamWriterBuilder.setLocation(location);
      return this;
    }

    /**
     * Sets the compression to use for the calls. The compressor must be of type gzip.
     *
     * @param compressorName
     * @return Builder
     */
    public Builder setCompressorName(String compressorName) {
      this.schemaAwareStreamWriterBuilder.setCompressorName(compressorName);
      return this;
    }

    /**
     * Enable client lib automatic retries on request level errors.
     *
     * <pre>
     * Immediate Retry code:
     * ABORTED, UNAVAILABLE, CANCELLED, INTERNAL, DEADLINE_EXCEEDED
     * Backoff Retry code:
     * RESOURCE_EXHAUSTED
     *
     * Example:
     * RetrySettings retrySettings = RetrySettings.newBuilder()
     *      .setInitialRetryDelay(Duration.ofMillis(500)) // applies to backoff retry
     *      .setRetryDelayMultiplier(1.1) // applies to backoff retry
     *      .setMaxAttempts(5) // applies to both retries
     *      .setMaxRetryDelay(Duration.ofMinutes(1)) // applies to backoff retry .build();
     * </pre>
     *
     * @param retrySettings
     * @return
     */
    public Builder setRetrySettings(RetrySettings retrySettings) {
      this.schemaAwareStreamWriterBuilder.setRetrySettings(retrySettings);
      return this;
    }

    /**
     * Enable a latency profiler that would periodically generate a detailed latency report for the
     * top latency requests. This is currently an experimental API.
     */
    public Builder setEnableLatencyProfiler(boolean enableLatencyProfiler) {
      this.schemaAwareStreamWriterBuilder.setEnableLatencyProfiler(enableLatencyProfiler);
      return this;
    }

    /** Enable generation of metrics for OpenTelemetry. */
    public Builder setEnableOpenTelemetry(boolean enableOpenTelemetry) {
      this.schemaAwareStreamWriterBuilder.setEnableOpenTelemetry(enableOpenTelemetry);
      return this;
    }

    /**
     * Sets the default missing value interpretation value if the column is not presented in the
     * missing_value_interpretations map.
     *
     * <p>If this value is set to `DEFAULT_VALUE`, we will always populate default value if the
     * field is missing from json and default value is defined in the column.
     *
     * <p>If this value is set to `NULL_VALUE`, we will always not populate default value.
     */
    public Builder setDefaultMissingValueInterpretation(
        AppendRowsRequest.MissingValueInterpretation defaultMissingValueInterpretation) {
      this.schemaAwareStreamWriterBuilder.setDefaultMissingValueInterpretation(
          defaultMissingValueInterpretation);
      return this;
    }

    /**
     * Builds AvroStreamWriter
     *
     * @return AvroStreamWriter
     */
    public AvroStreamWriter build()
        throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException,
            InterruptedException {
      return new AvroStreamWriter(this.schemaAwareStreamWriterBuilder);
    }
  }
}
