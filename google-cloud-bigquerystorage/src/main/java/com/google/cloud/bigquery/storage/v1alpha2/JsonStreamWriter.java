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
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoRows;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.*;
import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import java.util.regex.Pattern;
import com.google.protobuf.Message;

public class JsonStreamWriter {
  private static String streamPatternString =
      "(projects/[^/]+/datasets/[^/]+/tables/[^/]+)/streams/[^/]+";
  private static Pattern streamPattern = Pattern.compile(streamPatternString);

  BigQueryWriteClient client;
  StreamWriter streamWriter;
  Descriptor descriptor;

  private JsonStreamWriter(Builder builder)
      throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException,
          InterruptedException {
    Matcher matcher = streamPattern.matcher(builder.streamName);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid stream name: " + builder.streamName);
    }

    this.client = builder.client;
    this.descriptor =
        BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(builder.BQTableSchema);

    StreamWriter.Builder streamWriterBuilder =
        StreamWriter.newBuilder(builder.streamName, this.client);
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
  public <T extends Message> ApiFuture<AppendRowsResponse> append(List<T> protoRows) {
    ProtoRows.Builder rowsBuilder = ProtoRows.newBuilder();
    Descriptors.Descriptor descriptor = null;
    for (Message protoRow : protoRows) {
      rowsBuilder.addSerializedRows(protoRow.toByteString());
    }
    AppendRowsRequest.ProtoData.Builder data = AppendRowsRequest.ProtoData.newBuilder();
    data.setWriterSchema(ProtoSchemaConverter.convert(protoRows.get(0).getDescriptorForType()));
    data.setRows(rowsBuilder.build());

    ApiFuture<AppendRowsResponse> appendResponseFuture = this.streamWriter.append(AppendRowsRequest.newBuilder().setProtoRows(data.build()).build());
    // AppendRowsResponse appendResponse = appendResponseFuture.get();
    // if (appendResponse.hasUpdatedSchema()) {
    //
    //   // this.descriptor = BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(BQTableSchema);
    // }
    return appendResponseFuture;
  }

  public Descriptor getDescriptor() {
    return this.descriptor;
  }

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

  public static Builder newBuilder(String streamName, Table.TableSchema BQTableSchema) {
    return new Builder(streamName, BQTableSchema, null);
  }

  public static Builder newBuilder(String streamName, Table.TableSchema BQTableSchema, BigQueryWriteClient client) throws IllegalArgumentException{
    if (client == null) {
      throw new IllegalArgumentException("BigQuery Write Client is null.");
    }
    return new Builder(streamName, BQTableSchema, client);
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

    private Builder(String streamName, Table.TableSchema BQTableSchema, BigQueryWriteClient client) {
      this.streamName = streamName;
      this.BQTableSchema = BQTableSchema;
      this.client = client;
    }

    public Builder setChannelProvider(TransportChannelProvider channelProvider)
        throws IllegalArgumentException {
      if (channelProvider == null) {
        throw new IllegalArgumentException("Channel provider cannot be set to null.");
      }
      this.channelProvider = channelProvider;
      return this;
    }

    public Builder setCredentialsProvider(CredentialsProvider credentialsProvider)
        throws IllegalArgumentException {
      if (credentialsProvider == null) {
        throw new IllegalArgumentException("Credentials provider cannot be set to null.");
      }
      this.credentialsProvider = credentialsProvider;
      return this;
    }

    public Builder setBatchingSettings(BatchingSettings batchingSettings)
        throws IllegalArgumentException {
      if (batchingSettings == null) {
        throw new IllegalArgumentException("Batching settings cannot be set to null.");
      }
      this.batchingSettings = batchingSettings;
      return this;
    }

    public Builder setRetrySettings(RetrySettings retrySettings) throws IllegalArgumentException {
      if (retrySettings == null) {
        throw new IllegalArgumentException("Retry settings cannot be set to null.");
      }
      this.retrySettings = retrySettings;
      return this;
    }

    public Builder setExecutorProvider(ExecutorProvider executorProvider)
        throws IllegalArgumentException {
      if (executorProvider == null) {
        throw new IllegalArgumentException("Executor provider cannot be set to null.");
      }
      this.executorProvider = executorProvider;
      return this;
    }

    public Builder setEndpoint(String endpoint) throws IllegalArgumentException {
      this.endpoint = endpoint;
      return this;
    }

    public JsonStreamWriter build()
        throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException,
            InterruptedException {
      return new JsonStreamWriter(this);
    }
  }
}
