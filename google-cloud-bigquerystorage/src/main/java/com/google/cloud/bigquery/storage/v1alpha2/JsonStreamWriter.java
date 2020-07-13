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
import com.google.common.base.Preconditions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonStreamWriter {
  BigQueryWriteClient client;
  StreamWriter streamWriter;
  Descriptor descriptor;

  private static String streamPatternString = "(projects/[^/]+/datasets/[^/]+/tables/[^/]+)";

  private static Pattern streamPattern = Pattern.compile(streamPatternString);

  private JsonStreamWriter(Builder builder)
      throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException,
          InterruptedException {
    Matcher matcher = streamPattern.matcher(builder.tableName);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid table name: " + builder.tableName);
    }

    this.client = builder.client;
    Stream.WriteStream request =
        Stream.WriteStream.newBuilder().setType(builder.streamType).build();
    Stream.WriteStream response =
        client.createWriteStream(
            Storage.CreateWriteStreamRequest.newBuilder()
                .setParent(builder.tableName)
                .setWriteStream(request)
                .build());
    this.descriptor =
        BQTableSchemaToProtoDescriptor.ConvertBQTableSchemaToProtoDescriptor(
            response.getTableSchema());
    StreamWriter.Builder streamWriterBuilder =
        StreamWriter.newBuilder(response.getName(), this.client);
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

  public static Builder newBuilder(String tableName) {
    return new Builder(tableName, null);
  }

  public static Builder newBuilder(String tableName, BigQueryWriteClient client) {
    Preconditions.checkArgument(client != null);
    return new Builder(tableName, client);
  }

  public static final class Builder {
    private String tableName;
    private BigQueryWriteClient client;
    private Stream.WriteStream.Type streamType;

    private TransportChannelProvider channelProvider;
    private CredentialsProvider credentialsProvider;
    private BatchingSettings batchingSettings;
    private RetrySettings retrySettings;
    private ExecutorProvider executorProvider;
    private String endpoint;

    private Builder(String tableName, BigQueryWriteClient client) {
      this.tableName = tableName;
      this.client = client;
    }

    public Builder setStreamType(Stream.WriteStream.Type streamType) {
      this.streamType = streamType;
      return this;
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
