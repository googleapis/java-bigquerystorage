/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.bigquery.storage.v1alpha.stub;

import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.httpjson.ApiMethodDescriptor;
import com.google.api.gax.httpjson.HttpJsonCallSettings;
import com.google.api.gax.httpjson.HttpJsonStubCallableFactory;
import com.google.api.gax.httpjson.ProtoMessageRequestFormatter;
import com.google.api.gax.httpjson.ProtoMessageResponseParser;
import com.google.api.gax.httpjson.ProtoRestSerializer;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.RequestParamsBuilder;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.bigquery.storage.v1alpha.BatchCreateMetastorePartitionsRequest;
import com.google.cloud.bigquery.storage.v1alpha.BatchCreateMetastorePartitionsResponse;
import com.google.cloud.bigquery.storage.v1alpha.BatchDeleteMetastorePartitionsRequest;
import com.google.cloud.bigquery.storage.v1alpha.BatchUpdateMetastorePartitionsRequest;
import com.google.cloud.bigquery.storage.v1alpha.BatchUpdateMetastorePartitionsResponse;
import com.google.cloud.bigquery.storage.v1alpha.ListMetastorePartitionsRequest;
import com.google.cloud.bigquery.storage.v1alpha.ListMetastorePartitionsResponse;
import com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsRequest;
import com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse;
import com.google.protobuf.Empty;
import com.google.protobuf.TypeRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * REST stub implementation for the MetastorePartitionService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@BetaApi
@Generated("by gapic-generator-java")
public class HttpJsonMetastorePartitionServiceStub extends MetastorePartitionServiceStub {
  private static final TypeRegistry typeRegistry = TypeRegistry.newBuilder().build();

  private static final ApiMethodDescriptor<
          BatchCreateMetastorePartitionsRequest, BatchCreateMetastorePartitionsResponse>
      batchCreateMetastorePartitionsMethodDescriptor =
          ApiMethodDescriptor
              .<BatchCreateMetastorePartitionsRequest, BatchCreateMetastorePartitionsResponse>
                  newBuilder()
              .setFullMethodName(
                  "google.cloud.bigquery.storage.v1alpha.MetastorePartitionService/BatchCreateMetastorePartitions")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<BatchCreateMetastorePartitionsRequest>newBuilder()
                      .setPath(
                          "/v1alpha/{parent=projects/*/locations/*/datasets/*/tables/*}/partitions:batchCreate",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<BatchCreateMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "parent", request.getParent());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<BatchCreateMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putQueryParam(fields, "$alt", "json;enum-encoding=int");
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearParent().build(), true))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<BatchCreateMetastorePartitionsResponse>newBuilder()
                      .setDefaultInstance(
                          BatchCreateMetastorePartitionsResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private static final ApiMethodDescriptor<BatchDeleteMetastorePartitionsRequest, Empty>
      batchDeleteMetastorePartitionsMethodDescriptor =
          ApiMethodDescriptor.<BatchDeleteMetastorePartitionsRequest, Empty>newBuilder()
              .setFullMethodName(
                  "google.cloud.bigquery.storage.v1alpha.MetastorePartitionService/BatchDeleteMetastorePartitions")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<BatchDeleteMetastorePartitionsRequest>newBuilder()
                      .setPath(
                          "/v1alpha/{parent=projects/*/locations/*/datasets/*/tables/*}/partitions:batchDelete",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<BatchDeleteMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "parent", request.getParent());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<BatchDeleteMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putQueryParam(fields, "$alt", "json;enum-encoding=int");
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearParent().build(), true))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<Empty>newBuilder()
                      .setDefaultInstance(Empty.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private static final ApiMethodDescriptor<
          BatchUpdateMetastorePartitionsRequest, BatchUpdateMetastorePartitionsResponse>
      batchUpdateMetastorePartitionsMethodDescriptor =
          ApiMethodDescriptor
              .<BatchUpdateMetastorePartitionsRequest, BatchUpdateMetastorePartitionsResponse>
                  newBuilder()
              .setFullMethodName(
                  "google.cloud.bigquery.storage.v1alpha.MetastorePartitionService/BatchUpdateMetastorePartitions")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<BatchUpdateMetastorePartitionsRequest>newBuilder()
                      .setPath(
                          "/v1alpha/{parent=projects/*/locations/*/datasets/*/tables/*}/partitions:batchUpdate",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<BatchUpdateMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "parent", request.getParent());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<BatchUpdateMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putQueryParam(fields, "$alt", "json;enum-encoding=int");
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearParent().build(), true))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<BatchUpdateMetastorePartitionsResponse>newBuilder()
                      .setDefaultInstance(
                          BatchUpdateMetastorePartitionsResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private static final ApiMethodDescriptor<
          ListMetastorePartitionsRequest, ListMetastorePartitionsResponse>
      listMetastorePartitionsMethodDescriptor =
          ApiMethodDescriptor
              .<ListMetastorePartitionsRequest, ListMetastorePartitionsResponse>newBuilder()
              .setFullMethodName(
                  "google.cloud.bigquery.storage.v1alpha.MetastorePartitionService/ListMetastorePartitions")
              .setHttpMethod("GET")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<ListMetastorePartitionsRequest>newBuilder()
                      .setPath(
                          "/v1alpha/{parent=projects/*/locations/*/datasets/*/tables/*}/partitions:list",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<ListMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "parent", request.getParent());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<ListMetastorePartitionsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putQueryParam(fields, "filter", request.getFilter());
                            serializer.putQueryParam(fields, "$alt", "json;enum-encoding=int");
                            return fields;
                          })
                      .setRequestBodyExtractor(request -> null)
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<ListMetastorePartitionsResponse>newBuilder()
                      .setDefaultInstance(ListMetastorePartitionsResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private final UnaryCallable<
          BatchCreateMetastorePartitionsRequest, BatchCreateMetastorePartitionsResponse>
      batchCreateMetastorePartitionsCallable;
  private final UnaryCallable<BatchDeleteMetastorePartitionsRequest, Empty>
      batchDeleteMetastorePartitionsCallable;
  private final UnaryCallable<
          BatchUpdateMetastorePartitionsRequest, BatchUpdateMetastorePartitionsResponse>
      batchUpdateMetastorePartitionsCallable;
  private final UnaryCallable<ListMetastorePartitionsRequest, ListMetastorePartitionsResponse>
      listMetastorePartitionsCallable;

  private final BackgroundResource backgroundResources;
  private final HttpJsonStubCallableFactory callableFactory;

  public static final HttpJsonMetastorePartitionServiceStub create(
      MetastorePartitionServiceStubSettings settings) throws IOException {
    return new HttpJsonMetastorePartitionServiceStub(settings, ClientContext.create(settings));
  }

  public static final HttpJsonMetastorePartitionServiceStub create(ClientContext clientContext)
      throws IOException {
    return new HttpJsonMetastorePartitionServiceStub(
        MetastorePartitionServiceStubSettings.newHttpJsonBuilder().build(), clientContext);
  }

  public static final HttpJsonMetastorePartitionServiceStub create(
      ClientContext clientContext, HttpJsonStubCallableFactory callableFactory) throws IOException {
    return new HttpJsonMetastorePartitionServiceStub(
        MetastorePartitionServiceStubSettings.newHttpJsonBuilder().build(),
        clientContext,
        callableFactory);
  }

  /**
   * Constructs an instance of HttpJsonMetastorePartitionServiceStub, using the given settings. This
   * is protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonMetastorePartitionServiceStub(
      MetastorePartitionServiceStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new HttpJsonMetastorePartitionServiceCallableFactory());
  }

  /**
   * Constructs an instance of HttpJsonMetastorePartitionServiceStub, using the given settings. This
   * is protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonMetastorePartitionServiceStub(
      MetastorePartitionServiceStubSettings settings,
      ClientContext clientContext,
      HttpJsonStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    HttpJsonCallSettings<
            BatchCreateMetastorePartitionsRequest, BatchCreateMetastorePartitionsResponse>
        batchCreateMetastorePartitionsTransportSettings =
            HttpJsonCallSettings
                .<BatchCreateMetastorePartitionsRequest, BatchCreateMetastorePartitionsResponse>
                    newBuilder()
                .setMethodDescriptor(batchCreateMetastorePartitionsMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("parent", String.valueOf(request.getParent()));
                      return builder.build();
                    })
                .build();
    HttpJsonCallSettings<BatchDeleteMetastorePartitionsRequest, Empty>
        batchDeleteMetastorePartitionsTransportSettings =
            HttpJsonCallSettings.<BatchDeleteMetastorePartitionsRequest, Empty>newBuilder()
                .setMethodDescriptor(batchDeleteMetastorePartitionsMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("parent", String.valueOf(request.getParent()));
                      return builder.build();
                    })
                .build();
    HttpJsonCallSettings<
            BatchUpdateMetastorePartitionsRequest, BatchUpdateMetastorePartitionsResponse>
        batchUpdateMetastorePartitionsTransportSettings =
            HttpJsonCallSettings
                .<BatchUpdateMetastorePartitionsRequest, BatchUpdateMetastorePartitionsResponse>
                    newBuilder()
                .setMethodDescriptor(batchUpdateMetastorePartitionsMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("parent", String.valueOf(request.getParent()));
                      return builder.build();
                    })
                .build();
    HttpJsonCallSettings<ListMetastorePartitionsRequest, ListMetastorePartitionsResponse>
        listMetastorePartitionsTransportSettings =
            HttpJsonCallSettings
                .<ListMetastorePartitionsRequest, ListMetastorePartitionsResponse>newBuilder()
                .setMethodDescriptor(listMetastorePartitionsMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("parent", String.valueOf(request.getParent()));
                      return builder.build();
                    })
                .build();

    this.batchCreateMetastorePartitionsCallable =
        callableFactory.createUnaryCallable(
            batchCreateMetastorePartitionsTransportSettings,
            settings.batchCreateMetastorePartitionsSettings(),
            clientContext);
    this.batchDeleteMetastorePartitionsCallable =
        callableFactory.createUnaryCallable(
            batchDeleteMetastorePartitionsTransportSettings,
            settings.batchDeleteMetastorePartitionsSettings(),
            clientContext);
    this.batchUpdateMetastorePartitionsCallable =
        callableFactory.createUnaryCallable(
            batchUpdateMetastorePartitionsTransportSettings,
            settings.batchUpdateMetastorePartitionsSettings(),
            clientContext);
    this.listMetastorePartitionsCallable =
        callableFactory.createUnaryCallable(
            listMetastorePartitionsTransportSettings,
            settings.listMetastorePartitionsSettings(),
            clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  @InternalApi
  public static List<ApiMethodDescriptor> getMethodDescriptors() {
    List<ApiMethodDescriptor> methodDescriptors = new ArrayList<>();
    methodDescriptors.add(batchCreateMetastorePartitionsMethodDescriptor);
    methodDescriptors.add(batchDeleteMetastorePartitionsMethodDescriptor);
    methodDescriptors.add(batchUpdateMetastorePartitionsMethodDescriptor);
    methodDescriptors.add(listMetastorePartitionsMethodDescriptor);
    return methodDescriptors;
  }

  @Override
  public UnaryCallable<
          BatchCreateMetastorePartitionsRequest, BatchCreateMetastorePartitionsResponse>
      batchCreateMetastorePartitionsCallable() {
    return batchCreateMetastorePartitionsCallable;
  }

  @Override
  public UnaryCallable<BatchDeleteMetastorePartitionsRequest, Empty>
      batchDeleteMetastorePartitionsCallable() {
    return batchDeleteMetastorePartitionsCallable;
  }

  @Override
  public UnaryCallable<
          BatchUpdateMetastorePartitionsRequest, BatchUpdateMetastorePartitionsResponse>
      batchUpdateMetastorePartitionsCallable() {
    return batchUpdateMetastorePartitionsCallable;
  }

  @Override
  public UnaryCallable<ListMetastorePartitionsRequest, ListMetastorePartitionsResponse>
      listMetastorePartitionsCallable() {
    return listMetastorePartitionsCallable;
  }

  @Override
  public BidiStreamingCallable<StreamMetastorePartitionsRequest, StreamMetastorePartitionsResponse>
      streamMetastorePartitionsCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: streamMetastorePartitionsCallable(). REST transport is not implemented for this method yet.");
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
