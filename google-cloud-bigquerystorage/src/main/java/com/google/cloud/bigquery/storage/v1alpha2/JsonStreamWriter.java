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

import com.google.api.core.*;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.*;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoRows;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.protobuf.Int64Value;

/**
 * A StreamWriter that can write JSON data (JSONObjects) to BigQuery tables. The JsonStreamWriter is
 * built on top of a StreamWriter, and it simply converts all JSON data to protobuf messages then
 * calls StreamWriter's append() method to write to BigQuery tables. It maintains all StreamWriter
 * functions, but also provides an additional feature: schema update support, where if the BigQuery
 * table schema is updated, users will be able to ingest data on the new schema after some time (in
 * order of minutes).
 */
public class JsonStreamWriter {
  private static String streamPatternString =
      "projects/[^/]+/datasets/[^/]+/tables/[^/]+/streams/[^/]+";
  private static Pattern streamPattern = Pattern.compile(streamPatternString);
  private static final Logger LOG = Logger.getLogger(JsonStreamWriter.class.getName());

  private BigQueryWriteClient client;
  private String streamName;
  private StreamWriter streamWriter;
  private Descriptor descriptor;
  private Table.TableSchema tableSchema;

  /**
   * Constructs the JsonStreamWriter
   *
   * @param builder The Builder object for the JsonStreamWriter
   */
  private JsonStreamWriter(Builder builder)
      throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException,
          InterruptedException {
    Matcher matcher = streamPattern.matcher(builder.streamName);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid stream name: " + builder.streamName);
    }

    this.streamName = builder.streamName;
    this.client = builder.client;
    this.tableSchema = builder.tableSchema;
    this.descriptor =
        BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(builder.tableSchema);

    StreamWriter.Builder streamWriterBuilder;
    if (this.client == null) {
      streamWriterBuilder = StreamWriter.newBuilder(builder.streamName);
    } else {
      streamWriterBuilder = StreamWriter.newBuilder(builder.streamName, builder.client);
    }
    setStreamWriterSettings(
        streamWriterBuilder,
        builder.channelProvider,
        builder.credentialsProvider,
        builder.batchingSettings,
        builder.retrySettings,
        builder.executorProvider,
        builder.endpoint);
    this.streamWriter = streamWriterBuilder.build();
  }

  /**
   * Writes a JSONArray that contains JSONObjects to the BigQuery table by first converting the JSON
   * data to protobuf messages, then using StreamWriter's append() to write the data. This will
   * return a ApiFuture<AppendRowsResponse>, which will be processed by a callback to determine if a
   * schema update is required.
   *
   * @param jsonArr The JSON array that contains JSONObjects to be written
   * @param allowUnknownFields if true, json data can have fields unknown to the BigQuery table.
   * @param offset  Offset for deduplication
   * @return ApiFuture<AppendRowsResponse>
   */
  public synchronized ApiFuture<AppendRowsResponse> append(JSONArray jsonArr, long offset, boolean allowUnknownFields) {
    ProtoRows.Builder rowsBuilder = ProtoRows.newBuilder();
    // Any error in convertJsonToProtoMessage will throw an IllegalArgumentException/IllegalStateException/NullPointerException and will halt processing of JSON data.
    for (int i = 0; i < jsonArr.length(); i++) {
      JSONObject json = jsonArr.getJSONObject(i);
      Message protoMessage =
          JsonToProtoMessage.convertJsonToProtoMessage(this.descriptor, json, allowUnknownFields);
      rowsBuilder.addSerializedRows(protoMessage.toByteString());
    }
    AppendRowsRequest.ProtoData.Builder data = AppendRowsRequest.ProtoData.newBuilder();
    data.setWriterSchema(ProtoSchemaConverter.convert(this.descriptor));
    data.setRows(rowsBuilder.build());

    ApiFuture<AppendRowsResponse> appendResponseFuture =
        this.streamWriter.append(AppendRowsRequest.newBuilder().setProtoRows(data.build()).setOffset(Int64Value.of(offset)).build());
    ApiFutures.<AppendRowsResponse>addCallback(
        appendResponseFuture,
        new ApiFutureCallback<AppendRowsResponse>() {
          @Override
          public void onSuccess(AppendRowsResponse response) {
            if (response.hasUpdatedSchema()) {
              updateDescriptor(response.getUpdatedSchema());
            }
          }

          @Override
          public void onFailure(Throwable t) {
            LOG.severe("AppendRowsResponse error: " + t.getCause() + ".");
          }
        });
    return appendResponseFuture;
  }

  /**
   * Updates the descriptor and BQTable schema. This function is used to support schema updates, and
   * is called whenever AppendRowsResponse includes an updated schema. The function is synchronized
   * since it is called through a callback (which is in another thread). If the main thread is
   * calling append while the callback thread calls refreshAppend(), this might cause some issues.
   *
   * @param updatedSchema The updated table schema.
   */
  private synchronized void updateDescriptor(Table.TableSchema updatedSchema) {
    if (!this.tableSchema.equals(updatedSchema)) {
      try {
        this.descriptor =
            BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(updatedSchema);
      } catch (Descriptors.DescriptorValidationException e) {
        LOG.severe("Schema updated error: Failed to convert updatedSchema that was returned by AppendRowsResponse to a descriptor.");
        return;
      }
      this.tableSchema = updatedSchema;
      try {
        streamWriter.refreshAppend();
      } catch (IOException | InterruptedException e) {
        LOG.severe("Schema updated error: Got exception while reestablishing connection for schema update.");
        return;
      }

      LOG.info("Successfully updated schema.");
    }
  }

  /**
   * Gets streamName
   *
   * @return String
   */
  public String getStreamName() {
    return this.streamName;
  }

  /**
   * Gets current descriptor
   *
   * @return Descriptor
   */
  public Descriptor getDescriptor() {
    return this.descriptor;
  }

  /**
   * Gets current tableSchema
   *
   * @return Table.TableSchema
   */
  public Table.TableSchema getTableSchema() {
    return this.tableSchema;
  }

  /** Sets all StreamWriter settings. */
  private void setStreamWriterSettings(
      StreamWriter.Builder builder,
      Optional<TransportChannelProvider> channelProvider,
      Optional<CredentialsProvider> credentialsProvider,
      Optional<BatchingSettings> batchingSettings,
      Optional<RetrySettings> retrySettings,
      Optional<ExecutorProvider> executorProvider,
      Optional<String> endpoint) {
    if (channelProvider.isPresent()) {
      builder.setChannelProvider(channelProvider.get());
    }
    if (credentialsProvider.isPresent()) {
      builder.setCredentialsProvider(credentialsProvider.get());
    }
    if (batchingSettings.isPresent()) {
      builder.setBatchingSettings(batchingSettings.get());
    }
    if (retrySettings.isPresent()) {
      builder.setRetrySettings(retrySettings.get());
    }
    if (executorProvider.isPresent()) {
      builder.setExecutorProvider(executorProvider.get());
    }
    if (endpoint.isPresent()) {
      builder.setEndpoint(endpoint.get());
    }
  }

  /**
   * newBuilder that constructs a JsonStreamWriter builder with BigQuery client being initialized by
   * StreamWriter by default.
   *
   * @param streamName name of the stream that must follow
   *     "projects/[^/]+/datasets/[^/]+/tables/[^/]+/streams/[^/]+"
   * @param tableSchema The schema of the table when the stream was created, which is passed back
   *     through {@code WriteStream}
   * @return Builder
   */
  public static Builder newBuilder(String streamName, Table.TableSchema tableSchema) {
    Preconditions.checkNotNull(streamName, "StreamName is null.");
    Preconditions.checkNotNull(tableSchema, "TableSchema is null.");
    return new Builder(streamName, tableSchema, null);
  }

  /**
   * newBuilder that constructs a JsonStreamWriter builder.
   *
   * @param streamName name of the stream that must follow
   *     "projects/[^/]+/datasets/[^/]+/tables/[^/]+/streams/[^/]+"
   * @param tableSchema The schema of the table when the stream was created, which is passed back
   *     through {@code WriteStream}
   * @param client
   * @return Builder
   */
  public static Builder newBuilder(
      String streamName, Table.TableSchema tableSchema, BigQueryWriteClient client) {
    Preconditions.checkNotNull(streamName, "StreamName is null.");
    Preconditions.checkNotNull(tableSchema, "TableSchema is null.");
    Preconditions.checkNotNull(client, "BigQuery client is null.");
    return new Builder(streamName, tableSchema, client);
  }

  /** Closes the underlying StreamWriter. */
  public void close() {
    this.streamWriter.close();
  }

  public static final class Builder {
    private String streamName;
    private BigQueryWriteClient client;
    private Table.TableSchema tableSchema;

    private Optional<TransportChannelProvider> channelProvider = Optional.empty();
    private Optional<CredentialsProvider> credentialsProvider = Optional.empty();
    private Optional<BatchingSettings> batchingSettings = Optional.empty();
    private Optional<RetrySettings> retrySettings = Optional.empty();
    private Optional<ExecutorProvider> executorProvider = Optional.empty();
    private Optional<String> endpoint = Optional.empty();

    /**
     * Constructor for JsonStreamWriter's Builder
     *
     * @param streamName name of the stream that must follow
     *     "projects/[^/]+/datasets/[^/]+/tables/[^/]+/streams/[^/]+"
     * @param tableSchema schema used to convert Json to proto messages.
     * @param client
     */
    private Builder(String streamName, Table.TableSchema tableSchema, BigQueryWriteClient client) {
      this.streamName = streamName;
      this.tableSchema = tableSchema;
      this.client = client;
    }

    /**
     * Setter for the underlying StreamWriter's TransportChannelProvider.
     *
     * @param channelProvider
     * @return Builder
     */
    public Builder setChannelProvider(TransportChannelProvider channelProvider) {
      this.channelProvider = Optional.of(
          Preconditions.checkNotNull(channelProvider, "ChannelProvider is null."));
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's CredentialsProvider.
     *
     * @param credentialsProvider
     * @return Builder
     */
    public Builder setCredentialsProvider(CredentialsProvider credentialsProvider) {
      this.credentialsProvider =Optional.of(
          Preconditions.checkNotNull(credentialsProvider, "CredentialsProvider is null."));
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's BatchingSettings.
     *
     * @param batchingSettings
     * @return Builder
     */
    public Builder setBatchingSettings(BatchingSettings batchingSettings) {
      this.batchingSettings =Optional.of(
          Preconditions.checkNotNull(batchingSettings, "BatchingSettings is null."));
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's RetrySettings.
     *
     * @param retrySettings
     * @return Builder
     */
    public Builder setRetrySettings(RetrySettings retrySettings) {
      this.retrySettings = Optional.of(Preconditions.checkNotNull(retrySettings, "RetrySettings is null."));
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's ExecutorProvider.
     *
     * @param executorProvider
     * @return Builder
     */
    public Builder setExecutorProvider(ExecutorProvider executorProvider) {
      this.executorProvider =Optional.of(
          Preconditions.checkNotNull(executorProvider, "ExecutorProvider is null."));
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's Endpoint.
     *
     * @param endpoint
     * @return Builder
     */
    public Builder setEndpoint(String endpoint) {
      this.endpoint = Optional.of(Preconditions.checkNotNull(endpoint, "Endpoint is null."));
      return this;
    }

    /**
     * Builds JsonStreamWriter
     *
     * @return JsonStreamWriter
     */
    public JsonStreamWriter build()
        throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException,
            InterruptedException {
      return new JsonStreamWriter(this);
    }
  }
}
