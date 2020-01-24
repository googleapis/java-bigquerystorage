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
package com.google.cloud.bigquery.storage.v1alpha2;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 *
 *
 * <pre>
 * BigQuery Write API.
 * The Write API can be used to write data to BigQuery.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/cloud/bigquery/storage/v1alpha2/storage.proto")
public final class BigQueryWriteGrpc {

  private BigQueryWriteGrpc() {}

  public static final String SERVICE_NAME = "google.cloud.bigquery.storage.v1alpha2.BigQueryWrite";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
      getCreateWriteStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateWriteStream",
      requestType =
          com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest.class,
      responseType = com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
      getCreateWriteStreamMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest,
            com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
        getCreateWriteStreamMethod;
    if ((getCreateWriteStreamMethod = BigQueryWriteGrpc.getCreateWriteStreamMethod) == null) {
      synchronized (BigQueryWriteGrpc.class) {
        if ((getCreateWriteStreamMethod = BigQueryWriteGrpc.getCreateWriteStreamMethod) == null) {
          BigQueryWriteGrpc.getCreateWriteStreamMethod =
              getCreateWriteStreamMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest,
                          com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateWriteStream"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage
                                  .CreateWriteStreamRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new BigQueryWriteMethodDescriptorSupplier("CreateWriteStream"))
                      .build();
        }
      }
    }
    return getCreateWriteStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>
      getAppendRowsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AppendRows",
      requestType = com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest.class,
      responseType = com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>
      getAppendRowsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest,
            com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>
        getAppendRowsMethod;
    if ((getAppendRowsMethod = BigQueryWriteGrpc.getAppendRowsMethod) == null) {
      synchronized (BigQueryWriteGrpc.class) {
        if ((getAppendRowsMethod = BigQueryWriteGrpc.getAppendRowsMethod) == null) {
          BigQueryWriteGrpc.getAppendRowsMethod =
              getAppendRowsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest,
                          com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AppendRows"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(new BigQueryWriteMethodDescriptorSupplier("AppendRows"))
                      .build();
        }
      }
    }
    return getAppendRowsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
      getGetWriteStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetWriteStream",
      requestType = com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest.class,
      responseType = com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
      getGetWriteStreamMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest,
            com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
        getGetWriteStreamMethod;
    if ((getGetWriteStreamMethod = BigQueryWriteGrpc.getGetWriteStreamMethod) == null) {
      synchronized (BigQueryWriteGrpc.class) {
        if ((getGetWriteStreamMethod = BigQueryWriteGrpc.getGetWriteStreamMethod) == null) {
          BigQueryWriteGrpc.getGetWriteStreamMethod =
              getGetWriteStreamMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest,
                          com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetWriteStream"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage
                                  .GetWriteStreamRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new BigQueryWriteMethodDescriptorSupplier("GetWriteStream"))
                      .build();
        }
      }
    }
    return getGetWriteStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse>
      getFinalizeWriteStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FinalizeWriteStream",
      requestType =
          com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest.class,
      responseType =
          com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse>
      getFinalizeWriteStreamMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest,
            com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse>
        getFinalizeWriteStreamMethod;
    if ((getFinalizeWriteStreamMethod = BigQueryWriteGrpc.getFinalizeWriteStreamMethod) == null) {
      synchronized (BigQueryWriteGrpc.class) {
        if ((getFinalizeWriteStreamMethod = BigQueryWriteGrpc.getFinalizeWriteStreamMethod)
            == null) {
          BigQueryWriteGrpc.getFinalizeWriteStreamMethod =
              getFinalizeWriteStreamMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.bigquery.storage.v1alpha2.Storage
                              .FinalizeWriteStreamRequest,
                          com.google.cloud.bigquery.storage.v1alpha2.Storage
                              .FinalizeWriteStreamResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "FinalizeWriteStream"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage
                                  .FinalizeWriteStreamRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage
                                  .FinalizeWriteStreamResponse.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new BigQueryWriteMethodDescriptorSupplier("FinalizeWriteStream"))
                      .build();
        }
      }
    }
    return getFinalizeWriteStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse>
      getBatchCommitWriteStreamsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BatchCommitWriteStreams",
      requestType =
          com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest.class,
      responseType =
          com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest,
          com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse>
      getBatchCommitWriteStreamsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest,
            com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse>
        getBatchCommitWriteStreamsMethod;
    if ((getBatchCommitWriteStreamsMethod = BigQueryWriteGrpc.getBatchCommitWriteStreamsMethod)
        == null) {
      synchronized (BigQueryWriteGrpc.class) {
        if ((getBatchCommitWriteStreamsMethod = BigQueryWriteGrpc.getBatchCommitWriteStreamsMethod)
            == null) {
          BigQueryWriteGrpc.getBatchCommitWriteStreamsMethod =
              getBatchCommitWriteStreamsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.bigquery.storage.v1alpha2.Storage
                              .BatchCommitWriteStreamsRequest,
                          com.google.cloud.bigquery.storage.v1alpha2.Storage
                              .BatchCommitWriteStreamsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "BatchCommitWriteStreams"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage
                                  .BatchCommitWriteStreamsRequest.getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.bigquery.storage.v1alpha2.Storage
                                  .BatchCommitWriteStreamsResponse.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new BigQueryWriteMethodDescriptorSupplier("BatchCommitWriteStreams"))
                      .build();
        }
      }
    }
    return getBatchCommitWriteStreamsMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static BigQueryWriteStub newStub(io.grpc.Channel channel) {
    return new BigQueryWriteStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BigQueryWriteBlockingStub newBlockingStub(io.grpc.Channel channel) {
    return new BigQueryWriteBlockingStub(channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static BigQueryWriteFutureStub newFutureStub(io.grpc.Channel channel) {
    return new BigQueryWriteFutureStub(channel);
  }

  /**
   *
   *
   * <pre>
   * BigQuery Write API.
   * The Write API can be used to write data to BigQuery.
   * </pre>
   */
  public abstract static class BigQueryWriteImplBase implements io.grpc.BindableService {

    /**
     *
     *
     * <pre>
     * Creates a write stream to the given table.
     * </pre>
     */
    public void createWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
            responseObserver) {
      asyncUnimplementedUnaryCall(getCreateWriteStreamMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Appends data to the given stream.
     * If `offset` is specified, the `offset` is checked against the end of
     * stream. The server returns `OUT_OF_RANGE` in `AppendRowsResponse` if an
     * attempt is made to append to an offset beyond the current end of the stream
     * or `ALREADY_EXISTS` if user provids an `offset` that has already been
     * written to. User can retry with adjusted offset within the same RPC
     * stream. If `offset` is not specified, append happens at the end of the
     * stream.
     * The response contains the offset at which the append happened. Responses
     * are received in the same order in which requests are sent. There will be
     * one response for each successful request. If the `offset` is not set in
     * response, it means append didn't happen due to some errors. If one request
     * fails, all the subsequent requests will also fail until a success request
     * is made again.
     * If the stream is of `PENDING` type, data will only be available for read
     * operations after the stream is committed.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest>
        appendRows(
            io.grpc.stub.StreamObserver<
                    com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>
                responseObserver) {
      return asyncUnimplementedStreamingCall(getAppendRowsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets a write stream.
     * </pre>
     */
    public void getWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
            responseObserver) {
      asyncUnimplementedUnaryCall(getGetWriteStreamMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Finalize a write stream so that no new data can be appended to the
     * stream.
     * </pre>
     */
    public void finalizeWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getFinalizeWriteStreamMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Atomically commits a group of `PENDING` streams that belong to the same
     * `parent` table.
     * Streams must be finalized before commit and cannot be committed multiple
     * times. Once a stream is committed, data in the stream becomes available
     * for read operations.
     * </pre>
     */
    public void batchCommitWriteStreams(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getBatchCommitWriteStreamsMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
              getCreateWriteStreamMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest,
                      com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>(
                      this, METHODID_CREATE_WRITE_STREAM)))
          .addMethod(
              getAppendRowsMethod(),
              asyncBidiStreamingCall(
                  new MethodHandlers<
                      com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest,
                      com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>(
                      this, METHODID_APPEND_ROWS)))
          .addMethod(
              getGetWriteStreamMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest,
                      com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>(
                      this, METHODID_GET_WRITE_STREAM)))
          .addMethod(
              getFinalizeWriteStreamMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest,
                      com.google.cloud.bigquery.storage.v1alpha2.Storage
                          .FinalizeWriteStreamResponse>(this, METHODID_FINALIZE_WRITE_STREAM)))
          .addMethod(
              getBatchCommitWriteStreamsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.bigquery.storage.v1alpha2.Storage
                          .BatchCommitWriteStreamsRequest,
                      com.google.cloud.bigquery.storage.v1alpha2.Storage
                          .BatchCommitWriteStreamsResponse>(
                      this, METHODID_BATCH_COMMIT_WRITE_STREAMS)))
          .build();
    }
  }

  /**
   *
   *
   * <pre>
   * BigQuery Write API.
   * The Write API can be used to write data to BigQuery.
   * </pre>
   */
  public static final class BigQueryWriteStub extends io.grpc.stub.AbstractStub<BigQueryWriteStub> {
    private BigQueryWriteStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BigQueryWriteStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BigQueryWriteStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BigQueryWriteStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a write stream to the given table.
     * </pre>
     */
    public void createWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateWriteStreamMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Appends data to the given stream.
     * If `offset` is specified, the `offset` is checked against the end of
     * stream. The server returns `OUT_OF_RANGE` in `AppendRowsResponse` if an
     * attempt is made to append to an offset beyond the current end of the stream
     * or `ALREADY_EXISTS` if user provids an `offset` that has already been
     * written to. User can retry with adjusted offset within the same RPC
     * stream. If `offset` is not specified, append happens at the end of the
     * stream.
     * The response contains the offset at which the append happened. Responses
     * are received in the same order in which requests are sent. There will be
     * one response for each successful request. If the `offset` is not set in
     * response, it means append didn't happen due to some errors. If one request
     * fails, all the subsequent requests will also fail until a success request
     * is made again.
     * If the stream is of `PENDING` type, data will only be available for read
     * operations after the stream is committed.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest>
        appendRows(
            io.grpc.stub.StreamObserver<
                    com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>
                responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getAppendRowsMethod(), getCallOptions()), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Gets a write stream.
     * </pre>
     */
    public void getWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetWriteStreamMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Finalize a write stream so that no new data can be appended to the
     * stream.
     * </pre>
     */
    public void finalizeWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFinalizeWriteStreamMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Atomically commits a group of `PENDING` streams that belong to the same
     * `parent` table.
     * Streams must be finalized before commit and cannot be committed multiple
     * times. Once a stream is committed, data in the stream becomes available
     * for read operations.
     * </pre>
     */
    public void batchCommitWriteStreams(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getBatchCommitWriteStreamsMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   *
   *
   * <pre>
   * BigQuery Write API.
   * The Write API can be used to write data to BigQuery.
   * </pre>
   */
  public static final class BigQueryWriteBlockingStub
      extends io.grpc.stub.AbstractStub<BigQueryWriteBlockingStub> {
    private BigQueryWriteBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BigQueryWriteBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BigQueryWriteBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BigQueryWriteBlockingStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a write stream to the given table.
     * </pre>
     */
    public com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream createWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateWriteStreamMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Gets a write stream.
     * </pre>
     */
    public com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream getWriteStream(
        com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest request) {
      return blockingUnaryCall(getChannel(), getGetWriteStreamMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Finalize a write stream so that no new data can be appended to the
     * stream.
     * </pre>
     */
    public com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse
        finalizeWriteStream(
            com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest request) {
      return blockingUnaryCall(
          getChannel(), getFinalizeWriteStreamMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Atomically commits a group of `PENDING` streams that belong to the same
     * `parent` table.
     * Streams must be finalized before commit and cannot be committed multiple
     * times. Once a stream is committed, data in the stream becomes available
     * for read operations.
     * </pre>
     */
    public com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse
        batchCommitWriteStreams(
            com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest
                request) {
      return blockingUnaryCall(
          getChannel(), getBatchCommitWriteStreamsMethod(), getCallOptions(), request);
    }
  }

  /**
   *
   *
   * <pre>
   * BigQuery Write API.
   * The Write API can be used to write data to BigQuery.
   * </pre>
   */
  public static final class BigQueryWriteFutureStub
      extends io.grpc.stub.AbstractStub<BigQueryWriteFutureStub> {
    private BigQueryWriteFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BigQueryWriteFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BigQueryWriteFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BigQueryWriteFutureStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a write stream to the given table.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
        createWriteStream(
            com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateWriteStreamMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Gets a write stream.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>
        getWriteStream(
            com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetWriteStreamMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Finalize a write stream so that no new data can be appended to the
     * stream.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamResponse>
        finalizeWriteStream(
            com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFinalizeWriteStreamMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Atomically commits a group of `PENDING` streams that belong to the same
     * `parent` table.
     * Streams must be finalized before commit and cannot be committed multiple
     * times. Once a stream is committed, data in the stream becomes available
     * for read operations.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsResponse>
        batchCommitWriteStreams(
            com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest
                request) {
      return futureUnaryCall(
          getChannel().newCall(getBatchCommitWriteStreamsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_WRITE_STREAM = 0;
  private static final int METHODID_GET_WRITE_STREAM = 1;
  private static final int METHODID_FINALIZE_WRITE_STREAM = 2;
  private static final int METHODID_BATCH_COMMIT_WRITE_STREAMS = 3;
  private static final int METHODID_APPEND_ROWS = 4;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BigQueryWriteImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BigQueryWriteImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_WRITE_STREAM:
          serviceImpl.createWriteStream(
              (com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>)
                  responseObserver);
          break;
        case METHODID_GET_WRITE_STREAM:
          serviceImpl.getWriteStream(
              (com.google.cloud.bigquery.storage.v1alpha2.Storage.GetWriteStreamRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream>)
                  responseObserver);
          break;
        case METHODID_FINALIZE_WRITE_STREAM:
          serviceImpl.finalizeWriteStream(
              (com.google.cloud.bigquery.storage.v1alpha2.Storage.FinalizeWriteStreamRequest)
                  request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.bigquery.storage.v1alpha2.Storage
                          .FinalizeWriteStreamResponse>)
                  responseObserver);
          break;
        case METHODID_BATCH_COMMIT_WRITE_STREAMS:
          serviceImpl.batchCommitWriteStreams(
              (com.google.cloud.bigquery.storage.v1alpha2.Storage.BatchCommitWriteStreamsRequest)
                  request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.bigquery.storage.v1alpha2.Storage
                          .BatchCommitWriteStreamsResponse>)
                  responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_APPEND_ROWS:
          return (io.grpc.stub.StreamObserver<Req>)
              serviceImpl.appendRows(
                  (io.grpc.stub.StreamObserver<
                          com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse>)
                      responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private abstract static class BigQueryWriteBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BigQueryWriteBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.cloud.bigquery.storage.v1alpha2.Storage.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BigQueryWrite");
    }
  }

  private static final class BigQueryWriteFileDescriptorSupplier
      extends BigQueryWriteBaseDescriptorSupplier {
    BigQueryWriteFileDescriptorSupplier() {}
  }

  private static final class BigQueryWriteMethodDescriptorSupplier
      extends BigQueryWriteBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BigQueryWriteMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BigQueryWriteGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new BigQueryWriteFileDescriptorSupplier())
                      .addMethod(getCreateWriteStreamMethod())
                      .addMethod(getAppendRowsMethod())
                      .addMethod(getGetWriteStreamMethod())
                      .addMethod(getFinalizeWriteStreamMethod())
                      .addMethod(getBatchCommitWriteStreamsMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
