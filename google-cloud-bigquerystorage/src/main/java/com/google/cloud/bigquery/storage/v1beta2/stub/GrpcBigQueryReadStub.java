/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.bigquery.storage.v1beta2.stub;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.RequestParamsBuilder;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.bigquery.storage.v1beta2.CreateReadSessionRequest;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.ReadSession;
import com.google.cloud.bigquery.storage.v1beta2.SplitReadStreamRequest;
import com.google.cloud.bigquery.storage.v1beta2.SplitReadStreamResponse;
import com.google.longrunning.stub.GrpcOperationsStub;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * gRPC stub implementation for the BigQueryRead service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@BetaApi
@Generated("by gapic-generator-java")
public class GrpcBigQueryReadStub extends BigQueryReadStub {
  private static final MethodDescriptor<CreateReadSessionRequest, ReadSession>
      createReadSessionMethodDescriptor =
          MethodDescriptor.<CreateReadSessionRequest, ReadSession>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(
                  "google.cloud.bigquery.storage.v1beta2.BigQueryRead/CreateReadSession")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(CreateReadSessionRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(ReadSession.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<ReadRowsRequest, ReadRowsResponse>
      readRowsMethodDescriptor =
          MethodDescriptor.<ReadRowsRequest, ReadRowsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName("google.cloud.bigquery.storage.v1beta2.BigQueryRead/ReadRows")
              .setRequestMarshaller(ProtoUtils.marshaller(ReadRowsRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(ReadRowsResponse.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<SplitReadStreamRequest, SplitReadStreamResponse>
      splitReadStreamMethodDescriptor =
          MethodDescriptor.<SplitReadStreamRequest, SplitReadStreamResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(
                  "google.cloud.bigquery.storage.v1beta2.BigQueryRead/SplitReadStream")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(SplitReadStreamRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(SplitReadStreamResponse.getDefaultInstance()))
              .build();

  private final UnaryCallable<CreateReadSessionRequest, ReadSession> createReadSessionCallable;
  private final ServerStreamingCallable<ReadRowsRequest, ReadRowsResponse> readRowsCallable;
  private final UnaryCallable<SplitReadStreamRequest, SplitReadStreamResponse>
      splitReadStreamCallable;

  private final BackgroundResource backgroundResources;
  private final GrpcOperationsStub operationsStub;
  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcBigQueryReadStub create(BigQueryReadStubSettings settings)
      throws IOException {
    return new GrpcBigQueryReadStub(settings, ClientContext.create(settings));
  }

  public static final GrpcBigQueryReadStub create(ClientContext clientContext) throws IOException {
    return new GrpcBigQueryReadStub(BigQueryReadStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcBigQueryReadStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcBigQueryReadStub(
        BigQueryReadStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcBigQueryReadStub, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcBigQueryReadStub(BigQueryReadStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcBigQueryReadCallableFactory());
  }

  /**
   * Constructs an instance of GrpcBigQueryReadStub, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcBigQueryReadStub(
      BigQueryReadStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;
    this.operationsStub = GrpcOperationsStub.create(clientContext, callableFactory);

    GrpcCallSettings<CreateReadSessionRequest, ReadSession> createReadSessionTransportSettings =
        GrpcCallSettings.<CreateReadSessionRequest, ReadSession>newBuilder()
            .setMethodDescriptor(createReadSessionMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(
                      "read_session.table", String.valueOf(request.getReadSession().getTable()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<ReadRowsRequest, ReadRowsResponse> readRowsTransportSettings =
        GrpcCallSettings.<ReadRowsRequest, ReadRowsResponse>newBuilder()
            .setMethodDescriptor(readRowsMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("read_stream", String.valueOf(request.getReadStream()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<SplitReadStreamRequest, SplitReadStreamResponse>
        splitReadStreamTransportSettings =
            GrpcCallSettings.<SplitReadStreamRequest, SplitReadStreamResponse>newBuilder()
                .setMethodDescriptor(splitReadStreamMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("name", String.valueOf(request.getName()));
                      return builder.build();
                    })
                .build();

    this.createReadSessionCallable =
        callableFactory.createUnaryCallable(
            createReadSessionTransportSettings,
            settings.createReadSessionSettings(),
            clientContext);
    this.readRowsCallable =
        callableFactory.createServerStreamingCallable(
            readRowsTransportSettings, settings.readRowsSettings(), clientContext);
    this.splitReadStreamCallable =
        callableFactory.createUnaryCallable(
            splitReadStreamTransportSettings, settings.splitReadStreamSettings(), clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public GrpcOperationsStub getOperationsStub() {
    return operationsStub;
  }

  @Override
  public UnaryCallable<CreateReadSessionRequest, ReadSession> createReadSessionCallable() {
    return createReadSessionCallable;
  }

  @Override
  public ServerStreamingCallable<ReadRowsRequest, ReadRowsResponse> readRowsCallable() {
    return readRowsCallable;
  }

  @Override
  public UnaryCallable<SplitReadStreamRequest, SplitReadStreamResponse> splitReadStreamCallable() {
    return splitReadStreamCallable;
  }

  @Override
  public final void close() {
    try {
      backgroundResources.close();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to close resource", e);
    }
  }

  @Override
  public void shutdown() {
    backgroundResources.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return backgroundResources.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return backgroundResources.isTerminated();
  }

  @Override
  public void shutdownNow() {
    backgroundResources.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return backgroundResources.awaitTermination(duration, unit);
  }
}
