/*
 * Copyright 2018 Google LLC
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
package com.google.cloud.bigquery.storage.v1beta1;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.bigquery.storage.v1beta1.Storage.BatchCreateReadSessionStreamsRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.BatchCreateReadSessionStreamsResponse;
import com.google.cloud.bigquery.storage.v1beta1.Storage.CreateReadSessionRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.FinalizeStreamRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadRowsResponse;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadSession;
import com.google.cloud.bigquery.storage.v1beta1.Storage.SplitReadStreamRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.SplitReadStreamResponse;
import com.google.cloud.bigquery.storage.v1beta1.Storage.Stream;
import com.google.cloud.bigquery.storage.v1beta1.TableReferenceProto.TableReference;
import com.google.cloud.bigquery.storage.v1beta1.stub.EnhancedBigQueryStorageStub;
import com.google.protobuf.Empty;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service Description: BigQuery storage API.
 *
 * <p>The BigQuery storage API can be used to read data stored in BigQuery.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>
 * <code>
 * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
 *   TableReference tableReference = TableReference.newBuilder().build();
 *   String parent = "";
 *   int requestedStreams = 0;
 *   ReadSession response = bigQueryStorageClient.createReadSession(tableReference, parent, requestedStreams);
 * }
 * </code>
 * </pre>
 *
 * <p>Note: close() needs to be called on the bigQueryStorageClient object to clean up resources
 * such as threads. In the example above, try-with-resources is used, which automatically calls
 * close().
 *
 * <p>The surface of this class includes several types of Java methods for each of the API's
 * methods:
 *
 * <ol>
 *   <li>A "flattened" method. With this type of method, the fields of the request type have been
 *       converted into function parameters. It may be the case that not all fields are available as
 *       parameters, and not every API method will have a flattened method entry point.
 *   <li>A "request object" method. This type of method only takes one parameter, a request object,
 *       which must be constructed before the call. Not every API method will have a request object
 *       method.
 *   <li>A "callable" method. This type of method takes no parameters and returns an immutable API
 *       callable object, which can be used to initiate calls to the service.
 * </ol>
 *
 * <p>See the individual methods for example code.
 *
 * <p>Many parameters require resource names to be formatted in a particular way. To assist with
 * these names, this class includes a format method for each type of name, and additionally a parse
 * method to extract the individual identifiers contained within names that are returned.
 *
 * <p>This class can be customized by passing in a custom instance of BigQueryStorageSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>
 * <code>
 * BigQueryStorageSettings bigQueryStorageSettings =
 *     BigQueryStorageSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * BigQueryStorageClient bigQueryStorageClient =
 *     BigQueryStorageClient.create(bigQueryStorageSettings);
 * </code>
 * </pre>
 *
 * To customize the endpoint:
 *
 * <pre>
 * <code>
 * BigQueryStorageSettings bigQueryStorageSettings =
 *     BigQueryStorageSettings.newBuilder().setEndpoint(myEndpoint).build();
 * BigQueryStorageClient bigQueryStorageClient =
 *     BigQueryStorageClient.create(bigQueryStorageSettings);
 * </code>
 * </pre>
 */
@BetaApi
public class BigQueryStorageClient implements BackgroundResource {

  private final BigQueryStorageSettings settings;
  private final EnhancedBigQueryStorageStub stub;

  /** Constructs an instance of {@link BigQueryStorageClient} with default settings. */
  public static final BigQueryStorageClient create() throws IOException {
    return create(BigQueryStorageSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of {@link BigQueryStorageClient}, using the given settings. The channels
   * are created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final BigQueryStorageClient create(BigQueryStorageSettings settings)
      throws IOException {
    return new BigQueryStorageClient(settings);
  }

  /**
   * Constructs an instance of {@link BigQueryStorageClient}, using the given stub for making calls.
   * This is for advanced usage - prefer to use BigQueryStorageSettings}.
   */
  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public static final BigQueryStorageClient create(EnhancedBigQueryStorageStub stub) {
    return new BigQueryStorageClient(stub);
  }

  /**
   * Constructs an instance of {@link BigQueryStorageClient}, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected BigQueryStorageClient(BigQueryStorageSettings settings) throws IOException {
    this.settings = settings;
    this.stub =
        EnhancedBigQueryStorageStub.create(
            settings.getTypedStubSettings(), settings.getReadRowsRetryAttemptListener());
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  protected BigQueryStorageClient(EnhancedBigQueryStorageStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final BigQueryStorageSettings getSettings() {
    return settings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public EnhancedBigQueryStorageStub getStub() {
    return stub;
  }

  /**
   * Creates a new read session. A read session divides the contents of a BigQuery table into one or
   * more streams, which can then be used to read data from the table. The read session also
   * specifies properties of the data to be read, such as a list of columns or a push-down filter
   * describing the rows to be returned.
   *
   * <p>A particular row can be read by at most one stream. When the caller has reached the end of
   * each stream in the session, then all the data in the table has been read.
   *
   * <p>Read sessions automatically expire 24 hours after they are created and do not require manual
   * clean-up by the caller.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   TableReference tableReference = TableReference.newBuilder().build();
   *   String parent = "";
   *   int requestedStreams = 0;
   *   ReadSession response = bigQueryStorageClient.createReadSession(tableReference, parent, requestedStreams);
   * }
   * </code></pre>
   *
   * @param tableReference Required. Reference to the table to read.
   * @param parent Required. String of the form "projects/your-project-id" indicating the project
   *     this ReadSession is associated with. This is the project that will be billed for usage.
   * @param requestedStreams Optional. Initial number of streams. If unset or 0, we will provide a
   *     value of streams so as to produce reasonable throughput. Must be non-negative. The number
   *     of streams may be lower than the requested number, depending on the amount parallelism that
   *     is reasonable for the table and the maximum amount of parallelism allowed by the system.
   *     <p>Streams must be read starting from offset 0.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ReadSession createReadSession(
      TableReference tableReference, String parent, int requestedStreams) {

    CreateReadSessionRequest request =
        CreateReadSessionRequest.newBuilder()
            .setTableReference(tableReference)
            .setParent(parent)
            .setRequestedStreams(requestedStreams)
            .build();
    return createReadSession(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Creates a new read session. A read session divides the contents of a BigQuery table into one or
   * more streams, which can then be used to read data from the table. The read session also
   * specifies properties of the data to be read, such as a list of columns or a push-down filter
   * describing the rows to be returned.
   *
   * <p>A particular row can be read by at most one stream. When the caller has reached the end of
   * each stream in the session, then all the data in the table has been read.
   *
   * <p>Read sessions automatically expire 24 hours after they are created and do not require manual
   * clean-up by the caller.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   TableReference tableReference = TableReference.newBuilder().build();
   *   String parent = "";
   *   CreateReadSessionRequest request = CreateReadSessionRequest.newBuilder()
   *     .setTableReference(tableReference)
   *     .setParent(parent)
   *     .build();
   *   ReadSession response = bigQueryStorageClient.createReadSession(request);
   * }
   * </code></pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ReadSession createReadSession(CreateReadSessionRequest request) {
    return createReadSessionCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Creates a new read session. A read session divides the contents of a BigQuery table into one or
   * more streams, which can then be used to read data from the table. The read session also
   * specifies properties of the data to be read, such as a list of columns or a push-down filter
   * describing the rows to be returned.
   *
   * <p>A particular row can be read by at most one stream. When the caller has reached the end of
   * each stream in the session, then all the data in the table has been read.
   *
   * <p>Read sessions automatically expire 24 hours after they are created and do not require manual
   * clean-up by the caller.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   TableReference tableReference = TableReference.newBuilder().build();
   *   String parent = "";
   *   CreateReadSessionRequest request = CreateReadSessionRequest.newBuilder()
   *     .setTableReference(tableReference)
   *     .setParent(parent)
   *     .build();
   *   ApiFuture&lt;ReadSession&gt; future = bigQueryStorageClient.createReadSessionCallable().futureCall(request);
   *   // Do something
   *   ReadSession response = future.get();
   * }
   * </code></pre>
   */
  public final UnaryCallable<CreateReadSessionRequest, ReadSession> createReadSessionCallable() {
    return stub.createReadSessionCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Reads rows from the table in the format prescribed by the read session. Each response contains
   * one or more table rows, up to a maximum of 10 MiB per response; read requests which attempt to
   * read individual rows larger than this will fail.
   *
   * <p>Each request also returns a set of stream statistics reflecting the estimated total number
   * of rows in the read stream. This number is computed based on the total table size and the
   * number of active streams in the read session, and may change as other streams continue to read
   * data.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   StreamPosition readPosition = StreamPosition.newBuilder().build();
   *   ReadRowsRequest request = ReadRowsRequest.newBuilder()
   *     .setReadPosition(readPosition)
   *     .build();
   *
   *   ServerStream&lt;ReadRowsResponse&gt; stream = bigQueryStorageClient.readRowsCallable().call(request);
   *   for (ReadRowsResponse response : stream) {
   *     // Do something when receive a response
   *   }
   * }
   * </code></pre>
   */
  public final ServerStreamingCallable<ReadRowsRequest, ReadRowsResponse> readRowsCallable() {
    return stub.readRowsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Creates additional streams for a ReadSession. This API can be used to dynamically adjust the
   * parallelism of a batch processing task upwards by adding additional workers.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   ReadSession session = ReadSession.newBuilder().build();
   *   int requestedStreams = 0;
   *   BatchCreateReadSessionStreamsResponse response = bigQueryStorageClient.batchCreateReadSessionStreams(session, requestedStreams);
   * }
   * </code></pre>
   *
   * @param session Required. Must be a non-expired session obtained from a call to
   *     CreateReadSession. Only the name field needs to be set.
   * @param requestedStreams Required. Number of new streams requested. Must be positive. Number of
   *     added streams may be less than this, see CreateReadSessionRequest for more information.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final BatchCreateReadSessionStreamsResponse batchCreateReadSessionStreams(
      ReadSession session, int requestedStreams) {

    BatchCreateReadSessionStreamsRequest request =
        BatchCreateReadSessionStreamsRequest.newBuilder()
            .setSession(session)
            .setRequestedStreams(requestedStreams)
            .build();
    return batchCreateReadSessionStreams(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Creates additional streams for a ReadSession. This API can be used to dynamically adjust the
   * parallelism of a batch processing task upwards by adding additional workers.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   ReadSession session = ReadSession.newBuilder().build();
   *   int requestedStreams = 0;
   *   BatchCreateReadSessionStreamsRequest request = BatchCreateReadSessionStreamsRequest.newBuilder()
   *     .setSession(session)
   *     .setRequestedStreams(requestedStreams)
   *     .build();
   *   BatchCreateReadSessionStreamsResponse response = bigQueryStorageClient.batchCreateReadSessionStreams(request);
   * }
   * </code></pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final BatchCreateReadSessionStreamsResponse batchCreateReadSessionStreams(
      BatchCreateReadSessionStreamsRequest request) {
    return batchCreateReadSessionStreamsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Creates additional streams for a ReadSession. This API can be used to dynamically adjust the
   * parallelism of a batch processing task upwards by adding additional workers.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   ReadSession session = ReadSession.newBuilder().build();
   *   int requestedStreams = 0;
   *   BatchCreateReadSessionStreamsRequest request = BatchCreateReadSessionStreamsRequest.newBuilder()
   *     .setSession(session)
   *     .setRequestedStreams(requestedStreams)
   *     .build();
   *   ApiFuture&lt;BatchCreateReadSessionStreamsResponse&gt; future = bigQueryStorageClient.batchCreateReadSessionStreamsCallable().futureCall(request);
   *   // Do something
   *   BatchCreateReadSessionStreamsResponse response = future.get();
   * }
   * </code></pre>
   */
  public final UnaryCallable<
          BatchCreateReadSessionStreamsRequest, BatchCreateReadSessionStreamsResponse>
      batchCreateReadSessionStreamsCallable() {
    return stub.batchCreateReadSessionStreamsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Triggers the graceful termination of a single stream in a ReadSession. This API can be used to
   * dynamically adjust the parallelism of a batch processing task downwards without losing data.
   *
   * <p>This API does not delete the stream -- it remains visible in the ReadSession, and any data
   * processed by the stream is not released to other streams. However, no additional data will be
   * assigned to the stream once this call completes. Callers must continue reading data on the
   * stream until the end of the stream is reached so that data which has already been assigned to
   * the stream will be processed.
   *
   * <p>This method will return an error if there are no other live streams in the Session, or if
   * SplitReadStream() has been called on the given Stream.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   Stream stream = Stream.newBuilder().build();
   *   bigQueryStorageClient.finalizeStream(stream);
   * }
   * </code></pre>
   *
   * @param stream Stream to finalize.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void finalizeStream(Stream stream) {
    FinalizeStreamRequest request = FinalizeStreamRequest.newBuilder().setStream(stream).build();
    finalizeStream(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Triggers the graceful termination of a single stream in a ReadSession. This API can be used to
   * dynamically adjust the parallelism of a batch processing task downwards without losing data.
   *
   * <p>This API does not delete the stream -- it remains visible in the ReadSession, and any data
   * processed by the stream is not released to other streams. However, no additional data will be
   * assigned to the stream once this call completes. Callers must continue reading data on the
   * stream until the end of the stream is reached so that data which has already been assigned to
   * the stream will be processed.
   *
   * <p>This method will return an error if there are no other live streams in the Session, or if
   * SplitReadStream() has been called on the given Stream.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   Stream stream = Stream.newBuilder().build();
   *   FinalizeStreamRequest request = FinalizeStreamRequest.newBuilder()
   *     .setStream(stream)
   *     .build();
   *   bigQueryStorageClient.finalizeStream(request);
   * }
   * </code></pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void finalizeStream(FinalizeStreamRequest request) {
    finalizeStreamCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Triggers the graceful termination of a single stream in a ReadSession. This API can be used to
   * dynamically adjust the parallelism of a batch processing task downwards without losing data.
   *
   * <p>This API does not delete the stream -- it remains visible in the ReadSession, and any data
   * processed by the stream is not released to other streams. However, no additional data will be
   * assigned to the stream once this call completes. Callers must continue reading data on the
   * stream until the end of the stream is reached so that data which has already been assigned to
   * the stream will be processed.
   *
   * <p>This method will return an error if there are no other live streams in the Session, or if
   * SplitReadStream() has been called on the given Stream.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   Stream stream = Stream.newBuilder().build();
   *   FinalizeStreamRequest request = FinalizeStreamRequest.newBuilder()
   *     .setStream(stream)
   *     .build();
   *   ApiFuture&lt;Void&gt; future = bigQueryStorageClient.finalizeStreamCallable().futureCall(request);
   *   // Do something
   *   future.get();
   * }
   * </code></pre>
   */
  public final UnaryCallable<FinalizeStreamRequest, Empty> finalizeStreamCallable() {
    return stub.finalizeStreamCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Splits a given read stream into two Streams. These streams are referred to as the primary and
   * the residual of the split. The original stream can still be read from in the same manner as
   * before. Both of the returned streams can also be read from, and the total rows return by both
   * child streams will be the same as the rows read from the original stream.
   *
   * <p>Moreover, the two child streams will be allocated back to back in the original Stream.
   * Concretely, it is guaranteed that for streams Original, Primary, and Residual, that
   * Original[0-j] = Primary[0-j] and Original[j-n] = Residual[0-m] once the streams have been read
   * to completion.
   *
   * <p>This method is guaranteed to be idempotent.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   Stream originalStream = Stream.newBuilder().build();
   *   SplitReadStreamResponse response = bigQueryStorageClient.splitReadStream(originalStream);
   * }
   * </code></pre>
   *
   * @param originalStream Stream to split.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final SplitReadStreamResponse splitReadStream(Stream originalStream) {
    SplitReadStreamRequest request =
        SplitReadStreamRequest.newBuilder().setOriginalStream(originalStream).build();
    return splitReadStream(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Splits a given read stream into two Streams. These streams are referred to as the primary and
   * the residual of the split. The original stream can still be read from in the same manner as
   * before. Both of the returned streams can also be read from, and the total rows return by both
   * child streams will be the same as the rows read from the original stream.
   *
   * <p>Moreover, the two child streams will be allocated back to back in the original Stream.
   * Concretely, it is guaranteed that for streams Original, Primary, and Residual, that
   * Original[0-j] = Primary[0-j] and Original[j-n] = Residual[0-m] once the streams have been read
   * to completion.
   *
   * <p>This method is guaranteed to be idempotent.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   Stream originalStream = Stream.newBuilder().build();
   *   SplitReadStreamRequest request = SplitReadStreamRequest.newBuilder()
   *     .setOriginalStream(originalStream)
   *     .build();
   *   SplitReadStreamResponse response = bigQueryStorageClient.splitReadStream(request);
   * }
   * </code></pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final SplitReadStreamResponse splitReadStream(SplitReadStreamRequest request) {
    return splitReadStreamCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Splits a given read stream into two Streams. These streams are referred to as the primary and
   * the residual of the split. The original stream can still be read from in the same manner as
   * before. Both of the returned streams can also be read from, and the total rows return by both
   * child streams will be the same as the rows read from the original stream.
   *
   * <p>Moreover, the two child streams will be allocated back to back in the original Stream.
   * Concretely, it is guaranteed that for streams Original, Primary, and Residual, that
   * Original[0-j] = Primary[0-j] and Original[j-n] = Residual[0-m] once the streams have been read
   * to completion.
   *
   * <p>This method is guaranteed to be idempotent.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (BigQueryStorageClient bigQueryStorageClient = BigQueryStorageClient.create()) {
   *   Stream originalStream = Stream.newBuilder().build();
   *   SplitReadStreamRequest request = SplitReadStreamRequest.newBuilder()
   *     .setOriginalStream(originalStream)
   *     .build();
   *   ApiFuture&lt;SplitReadStreamResponse&gt; future = bigQueryStorageClient.splitReadStreamCallable().futureCall(request);
   *   // Do something
   *   SplitReadStreamResponse response = future.get();
   * }
   * </code></pre>
   */
  public final UnaryCallable<SplitReadStreamRequest, SplitReadStreamResponse>
      splitReadStreamCallable() {
    return stub.splitReadStreamCallable();
  }

  @Override
  public final void close() {
    stub.close();
  }

  @Override
  public void shutdown() {
    stub.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return stub.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return stub.isTerminated();
  }

  @Override
  public void shutdownNow() {
    stub.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return stub.awaitTermination(duration, unit);
  }
}
