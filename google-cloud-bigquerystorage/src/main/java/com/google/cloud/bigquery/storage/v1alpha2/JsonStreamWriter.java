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
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A StreamWriter that can write JSON data (JSONObjects) to BigQuery tables. The JsonStreamWriter is
 * built on top of a StreamWriter, and it simply converts all JSON data to protobuf messages then
 * calls StreamWriter's append() method to write to BigQuery tables. It maintains all StreamWriter
 * functions, but also provides an additional feature: schema update support, where if the BigQuery
 * table schema is updated, the JsonStreamWriter is guaranteed to be updated after some time.
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
  private Table.TableSchema BQTableSchema;

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
    this.BQTableSchema = builder.BQTableSchema;
    this.descriptor =
        BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(builder.BQTableSchema);

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

  // For testing, use proto first.
  // public <T extends Message> ApiFuture<AppendRowsResponse> append(JSONArray jsonArr) {
  //   for (int i = 0; i < jsonArr.size(); i++) {
  //     JSONObject json = jsonArr.getJSONObject(i);
  //   }
  //
  // //   ProtoRows.Builder rowsBuilder = ProtoRows.newBuilder();
  // //   Descriptors.Descriptor descriptor = null;
  // //   for (Message protoRow : protoRows) {
  // //     rowsBuilder.addSerializedRows(protoRow.toByteString());
  // //   }
  // //   AppendRowsRequest.ProtoData.Builder data = AppendRowsRequest.ProtoData.newBuilder();
  // //
  // data.setWriterSchema(ProtoSchemaConverter.convert(protoRows.get(0).getDescriptorForType()));
  // //   data.setRows(rowsBuilder.build());
  // //
  // //   ApiFuture<AppendRowsResponse> appendResponseFuture =
  // // this.streamWriter.append(AppendRowsRequest.newBuilder().setProtoRows(data.build()).build());
  // //   // AppendRowsResponse appendResponse = appendResponseFuture.get();
  // //   // if (appendResponse.hasUpdatedSchema()) {
  // //   //
  // //   //   // this.descriptor =
  // // BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(BQTableSchema);
  // //   // }
  //   // return appendResponseFuture;
  // }

  /**
   * Updates the descriptor and BQTable schema. This function is used to support schema updates, and
   * is called whenever AppendRowsResponse includes an updated schema. The function is synchronized
   * since it is called through a callback (which is in another thread). If the main thread is
   * calling append while the callback thread calls refreshAppend(), this might cause some issues.
   *
   * @param BQTableSchema The updated table schema.
   */
  private synchronized void updateDescriptor(Table.TableSchema updatedSchema)
      throws Descriptors.DescriptorValidationException, IOException, InterruptedException {
    if (!this.BQTableSchema.equals(updatedSchema)) {
      this.descriptor =
          BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(updatedSchema);
      this.BQTableSchema = updatedSchema;
      streamWriter.refreshAppend();
    }
  }

  /** Placeholder. Will update to include JsonToProtoMessage conversion. */
  public synchronized ApiFuture<AppendRowsResponse> append(AppendRowsRequest message) {
    ApiFuture<AppendRowsResponse> appendResponseFuture = this.streamWriter.append(message);
    ApiFutures.<AppendRowsResponse>addCallback(
        appendResponseFuture,
        new ApiFutureCallback<AppendRowsResponse>() {
          @Override
          public void onSuccess(AppendRowsResponse response) {
            if (response.hasUpdatedSchema()) {
              try {
                updateDescriptor(response.getUpdatedSchema());
              } catch (Exception e) {
                // Deal with exceptions
              }
            }
          }

          @Override
          public void onFailure(Throwable t) {}
        });
    return appendResponseFuture;
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
   * Gets current BQTableSchema
   *
   * @return Table.TableSchema
   */
  public Table.TableSchema getBQTableSchema() {
    return this.BQTableSchema;
  }

  /** Sets all StreamWriter settings. */
  private void setStreamWriterSettings(
      StreamWriter.Builder builder,
      TransportChannelProvider channelProvider,
      CredentialsProvider credentialsProvider,
      BatchingSettings batchingSettings,
      RetrySettings retrySettings,
      ExecutorProvider executorProvider,
      String endpoint) {
    if (channelProvider != null) {
      builder.setChannelProvider(channelProvider);
    }
    if (credentialsProvider != null) {
      builder.setCredentialsProvider(credentialsProvider);
    }
    if (batchingSettings != null) {
      builder.setBatchingSettings(batchingSettings);
    }
    if (retrySettings != null) {
      builder.setRetrySettings(retrySettings);
    }
    if (executorProvider != null) {
      builder.setExecutorProvider(executorProvider);
    }
    if (endpoint != null) {
      builder.setEndpoint(endpoint);
    }
  }

  /**
   * newBuilder that constructs a JsonStreamWriter builder with BigQuery client being initialized by
   * StreamWriter by default.
   *
   * @param streamName name of the stream that must follow
   *     "projects/[^/]+/datasets/[^/]+/tables/[^/]+/streams/[^/]+"
   * @param BQTableSchema schema used to convert Json to proto messages.
   * @return Builder
   */
  public static Builder newBuilder(String streamName, Table.TableSchema BQTableSchema) {
    Preconditions.checkNotNull(streamName, "StreamName is null.");
    Preconditions.checkNotNull(BQTableSchema, "BQTableSchema is null.");
    return new Builder(streamName, BQTableSchema, null);
  }

  /**
   * newBuilder that constructs a JsonStreamWriter builder.
   *
   * @param streamName name of the stream that must follow
   *     "projects/[^/]+/datasets/[^/]+/tables/[^/]+/streams/[^/]+"
   * @param BQTableSchema schema used to convert Json to proto messages.
   * @param client
   * @return Builder
   */
  public static Builder newBuilder(
      String streamName, Table.TableSchema BQTableSchema, BigQueryWriteClient client) {
    Preconditions.checkNotNull(streamName, "StreamName is null.");
    Preconditions.checkNotNull(BQTableSchema, "BQTableSchema is null.");
    Preconditions.checkNotNull(client, "BigQuery client is null.");
    return new Builder(streamName, BQTableSchema, client);
  }

  /** Closes the underlying StreamWriter. */
  public void close() {
    this.streamWriter.close();
  }

  public static final class Builder {
    private String streamName;
    private BigQueryWriteClient client;
    private Table.TableSchema BQTableSchema;

    private TransportChannelProvider channelProvider;
    private CredentialsProvider credentialsProvider;
    private BatchingSettings batchingSettings;
    private RetrySettings retrySettings;
    private ExecutorProvider executorProvider;
    private String endpoint;

    /**
     * Constructor for JsonStreamWriter's Builder
     *
     * @param streamName name of the stream that must follow
     *     "projects/[^/]+/datasets/[^/]+/tables/[^/]+/streams/[^/]+"
     * @param BQTableSchema schema used to convert Json to proto messages.
     * @param client
     */
    private Builder(
        String streamName, Table.TableSchema BQTableSchema, BigQueryWriteClient client) {
      this.streamName = streamName;
      this.BQTableSchema = BQTableSchema;
      this.client = client;
    }

    /**
     * Setter for the underlying StreamWriter's TransportChannelProvider.
     *
     * @param channelProvider
     * @return Builder
     */
    public Builder setChannelProvider(TransportChannelProvider channelProvider) {
      this.channelProvider =
          Preconditions.checkNotNull(channelProvider, "ChannelProvider is null.");
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's CredentialsProvider.
     *
     * @param credentialsProvider
     * @return Builder
     */
    public Builder setCredentialsProvider(CredentialsProvider credentialsProvider) {
      this.credentialsProvider =
          Preconditions.checkNotNull(credentialsProvider, "CredentialsProvider is null.");
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's BatchingSettings.
     *
     * @param batchingSettings
     * @return Builder
     */
    public Builder setBatchingSettings(BatchingSettings batchingSettings) {
      this.batchingSettings =
          Preconditions.checkNotNull(batchingSettings, "BatchingSettings is null.");
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's RetrySettings.
     *
     * @param retrySettings
     * @return Builder
     */
    public Builder setRetrySettings(RetrySettings retrySettings) {
      this.retrySettings = Preconditions.checkNotNull(retrySettings, "RetrySettings is null.");
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's ExecutorProvider.
     *
     * @param executorProvider
     * @return Builder
     */
    public Builder setExecutorProvider(ExecutorProvider executorProvider) {
      this.executorProvider =
          Preconditions.checkNotNull(executorProvider, "ExecutorProvider is null.");
      return this;
    }

    /**
     * Setter for the underlying StreamWriter's Endpoint.
     *
     * @param endpoint
     * @return Builder
     */
    public Builder setEndpoint(String endpoint) {
      this.endpoint = Preconditions.checkNotNull(endpoint, "Endpoint is null.");
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
