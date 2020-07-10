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
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoRows;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import io.grpc.Status;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
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
import com.google.api.gax.rpc.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.common.base.Preconditions;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

public class JsonStreamWriter {
    BigQueryWriteClient client;
    StreamWriter streamWriter;
    Descriptor descriptor;

    private static String streamPatternString =
        "(projects/[^/]+/datasets/[^/]+/tables/[^/]+)/streams/[^/]+";

    private static Pattern streamPattern = Pattern.compile(streamPatternString);

    private JsonStreamWriter(Builder builder) throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException, InterruptedException {
      Matcher matcher = streamPattern.matcher(builder.streamName);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid stream name: " + builder.streamName);
      }
      String streamName = builder.streamName;
      String tableName = matcher.group(1);

      this.client = builder.client;
      Stream.WriteStream writeStream = Stream.WriteStream.newBuilder().setType(builder.streamType).setName(builder.streamName).build();
      Stream.WriteStream response = client.createWriteStream(tableName, writeStream);
      this.descriptor = BQTableSchemaToProtoDescriptor.ConvertBQTableSchemaToProtoDescriptor(response.getTableSchema());
      System.out.println(writeStream.getName());
      // Stream.WriteStream stream =
      //     client.getWriteStream(writeStream.getName());
      // Stream.WriteStream stream =
      //     client.getWriteStream(Storage.GetWriteStreamRequest.newBuilder().setName(streamName).build());
      // this.streamWriter = builder.streamWriterBuilder.build();
    }

    public Descriptor getDescriptor() {
      return this.descriptor;
    }

    public static Builder newBuilder(String streamName) {
      return new Builder(streamName, null);
    }

    public static Builder newBuilder(String streamName, BigQueryWriteClient client) {
      Preconditions.checkArgument(client != null);
      return new Builder(streamName, client);
    }

    public static final class Builder {
      private String streamName;
      private BigQueryWriteClient client;
      private Stream.WriteStream.Type streamType;
      private StreamWriter.Builder streamWriterBuilder;

      private Builder(String streamName, BigQueryWriteClient client) {
        this.streamName = streamName;
        this.client = client;
        this.streamWriterBuilder = StreamWriter.newBuilder(streamName, client);
      }

      public Builder setStreamType(Stream.WriteStream.Type streamType) {
        this.streamType = streamType;
        return this;
      }

      public Builder setChannelProvider(TransportChannelProvider channelProvider) {
        this.streamWriterBuilder.setChannelProvider(channelProvider);
        return this;
      }
      public Builder setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.streamWriterBuilder.setCredentialsProvider(credentialsProvider);
        return this;
      }
      public Builder setBatchingSettings(BatchingSettings batchingSettings) {
        this.streamWriterBuilder.setBatchingSettings(batchingSettings);
        return this;
      }
      public Builder setRetrySettings(RetrySettings retrySettings) {
        this.streamWriterBuilder.setRetrySettings(retrySettings);
        return this;
      }
      public Builder setExecutorProvider(ExecutorProvider executorProvider) {
        this.streamWriterBuilder.setExecutorProvider(executorProvider);
        return this;
      }
      public Builder setEndpoint(String endpoint) {
        this.streamWriterBuilder.setEndpoint(endpoint);
        return this;
      }

      public JsonStreamWriter build() throws Descriptors.DescriptorValidationException, IllegalArgumentException, IOException, InterruptedException  {
        return new JsonStreamWriter(this);
      }
    }

}
