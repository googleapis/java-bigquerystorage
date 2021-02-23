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
package com.google.cloud.bigquery.storage.v1beta2;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.AbortedException;
import com.google.api.gax.rpc.DataLossException;
import com.google.api.gax.rpc.UnknownException;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.common.base.Strings;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

@RunWith(JUnit4.class)
public class StreamWriterTest {
  private static final Logger LOG = Logger.getLogger(StreamWriterTest.class.getName());
  private static final String TEST_STREAM = "projects/p/datasets/d/tables/t/streams/s";
  private static final String TEST_TABLE = "projects/p/datasets/d/tables/t";
  private static final ExecutorProvider SINGLE_THREAD_EXECUTOR =
      InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build();
  private static LocalChannelProvider channelProvider;
  private FakeScheduledExecutorService fakeExecutor;
  private FakeBigQueryWrite testBigQueryWrite;
  private static MockServiceHelper serviceHelper;

  @Before
  public void setUp() throws Exception {
    testBigQueryWrite = new FakeBigQueryWrite();
    serviceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(testBigQueryWrite));
    serviceHelper.start();
    channelProvider = serviceHelper.createChannelProvider();
    fakeExecutor = new FakeScheduledExecutorService();
    testBigQueryWrite.setExecutor(fakeExecutor);
    Instant time = Instant.now();
    Timestamp timestamp =
        Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
    // Add enough GetWriteStream response.
    for (int i = 0; i < 4; i++) {
      testBigQueryWrite.addResponse(
          WriteStream.newBuilder().setName(TEST_STREAM).setCreateTime(timestamp).build());
    }
  }

  @After
  public void tearDown() throws Exception {
    LOG.info("tearDown called");
    serviceHelper.stop();
  }

  private StreamWriter.Builder getTestStreamWriterBuilder(String testStream) {
    return StreamWriter.newBuilder(testStream)
        .setChannelProvider(channelProvider)
        .setExecutorProvider(SINGLE_THREAD_EXECUTOR)
        .setCredentialsProvider(NoCredentialsProvider.create());
  }

  private StreamWriter.Builder getTestStreamWriterBuilder() {
    return getTestStreamWriterBuilder(TEST_STREAM);
  }

  private AppendRowsRequest createAppendRequest(String[] messages, long offset) {
    AppendRowsRequest.Builder requestBuilder = AppendRowsRequest.newBuilder();
    AppendRowsRequest.ProtoData.Builder dataBuilder = AppendRowsRequest.ProtoData.newBuilder();
    dataBuilder.setWriterSchema(
        ProtoSchema.newBuilder()
            .setProtoDescriptor(
                DescriptorProtos.DescriptorProto.newBuilder()
                    .setName("Message")
                    .addField(
                        DescriptorProtos.FieldDescriptorProto.newBuilder()
                            .setName("foo")
                            .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING)
                            .setNumber(1)
                            .build())
                    .build()));
    ProtoRows.Builder rows = ProtoRows.newBuilder();
    for (String message : messages) {
      FooType foo = FooType.newBuilder().setFoo(message).build();
      rows.addSerializedRows(foo.toByteString());
    }
    if (offset > 0) {
      requestBuilder.setOffset(Int64Value.of(offset));
    }
    return requestBuilder
        .setProtoRows(dataBuilder.setRows(rows.build()).build())
        .setWriteStream(TEST_STREAM)
        .build();
  }

  private ApiFuture<AppendRowsResponse> sendTestMessage(
      StreamWriter writer, String[] messages, int offset) {
    return writer.append(createAppendRequest(messages, offset));
  }

  private ApiFuture<AppendRowsResponse> sendTestMessage(StreamWriter writer, String[] messages) {
    return writer.append(createAppendRequest(messages, -1));
  }

  @Test
  public void testTableName() throws Exception {
    try (StreamWriter writer = getTestStreamWriterBuilder().build()) {
      assertEquals("projects/p/datasets/d/tables/t", writer.getTableNameString());
    }
  }

  @Test
  public void testDefaultStream() throws Exception {
    try (StreamWriter writer =
        StreamWriter.newBuilder(TEST_TABLE)
            .createDefaultStream()
            .setChannelProvider(channelProvider)
            .setExecutorProvider(SINGLE_THREAD_EXECUTOR)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build()) {
      assertEquals("projects/p/datasets/d/tables/t", writer.getTableNameString());
      assertEquals("projects/p/datasets/d/tables/t/_default", writer.getStreamNameString());
    }
  }

  @Test
  public void testAppendByDuration() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .setElementCountThreshold(10L)
                    .build())
            .setExecutorProvider(FixedExecutorProvider.create(fakeExecutor))
            .build();

    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
            .build());
    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});

    assertFalse(appendFuture1.isDone());
    assertFalse(appendFuture2.isDone());
    fakeExecutor.advanceTime(Duration.ofSeconds(10));

    assertEquals(0L, appendFuture1.get().getAppendResult().getOffset().getValue());
    assertEquals(1L, appendFuture2.get().getAppendResult().getOffset().getValue());
    appendFuture1.get();
    appendFuture2.get();
    assertEquals(1, testBigQueryWrite.getAppendRequests().size());

    assertEquals(
        2,
        testBigQueryWrite
            .getAppendRequests()
            .get(0)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    assertEquals(
        true, testBigQueryWrite.getAppendRequests().get(0).getProtoRows().hasWriterSchema());
    writer.close();
  }

  @Test
  public void testAppendByNumBatchedMessages() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(100))
                    .build())
            .build();
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(2)).build())
            .build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});
    ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"C"});

    assertEquals(0L, appendFuture1.get().getAppendResult().getOffset().getValue());
    assertEquals(1L, appendFuture2.get().getAppendResult().getOffset().getValue());

    assertFalse(appendFuture3.isDone());

    ApiFuture<AppendRowsResponse> appendFuture4 = sendTestMessage(writer, new String[] {"D"});

    assertEquals(2L, appendFuture3.get().getAppendResult().getOffset().getValue());
    assertEquals(3L, appendFuture4.get().getAppendResult().getOffset().getValue());

    assertEquals(2, testBigQueryWrite.getAppendRequests().size());
    assertEquals(
        2,
        testBigQueryWrite
            .getAppendRequests()
            .get(0)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    assertEquals(
        true, testBigQueryWrite.getAppendRequests().get(0).getProtoRows().hasWriterSchema());
    assertEquals(
        2,
        testBigQueryWrite
            .getAppendRequests()
            .get(1)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    assertEquals(
        false, testBigQueryWrite.getAppendRequests().get(1).getProtoRows().hasWriterSchema());
    writer.close();
  }

  @Test
  public void testAppendByNumBytes() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    // Each message is 32 bytes, setting batch size to 70 bytes allows 2 messages.
                    .setRequestByteThreshold(70L)
                    .setDelayThreshold(Duration.ofSeconds(100000))
                    .build())
            .build();

    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(2)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(3)).build())
            .build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});
    ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"C"});

    assertEquals(0L, appendFuture1.get().getAppendResult().getOffset().getValue());
    assertEquals(1L, appendFuture2.get().getAppendResult().getOffset().getValue());

    assertFalse(appendFuture3.isDone());

    // This message is big enough to trigger send on the previous message and itself.
    ApiFuture<AppendRowsResponse> appendFuture4 =
        sendTestMessage(writer, new String[] {Strings.repeat("A", 100)});
    assertEquals(2L, appendFuture3.get().getAppendResult().getOffset().getValue());
    assertEquals(3L, appendFuture4.get().getAppendResult().getOffset().getValue());

    assertEquals(3, testBigQueryWrite.getAppendRequests().size());

    writer.close();
  }

  @Test
  public void testShutdownFlushBatch() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(100))
                    .setElementCountThreshold(10L)
                    .build())
            .build();
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(1)).build())
            .build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});

    // Note we are not advancing time or reaching the count threshold but messages should
    // still get written by call to shutdown

    writer.close();

    // Verify the appends completed
    assertTrue(appendFuture1.isDone());
    assertTrue(appendFuture2.isDone());
    assertEquals(0L, appendFuture1.get().getAppendResult().getOffset().getValue());
    assertEquals(1L, appendFuture2.get().getAppendResult().getOffset().getValue());
  }

  @Test
  public void testWriteMixedSizeAndDuration() throws Exception {
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(1))
                    .build())
            .build()) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
              .build());
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(3)).build())
              .build());

      ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
      assertFalse(appendFuture1.isDone());

      ApiFuture<AppendRowsResponse> appendFuture2 =
          sendTestMessage(writer, new String[] {"B", "C"});

      // Write triggered by batch size
      assertEquals(0L, appendFuture1.get().getAppendResult().getOffset().getValue());
      assertEquals(1L, appendFuture2.get().getAppendResult().getOffset().getValue());

      ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"D"});
      assertFalse(appendFuture3.isDone());
      // Eventually will be triggered by time elapsed.
      assertEquals(3L, appendFuture3.get().getAppendResult().getOffset().getValue());

      assertEquals(
          3,
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          true, testBigQueryWrite.getAppendRequests().get(0).getProtoRows().hasWriterSchema());
      assertEquals(
          1,
          testBigQueryWrite
              .getAppendRequests()
              .get(1) // this gives IndexOutOfBounds error at the moment
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          false, testBigQueryWrite.getAppendRequests().get(1).getProtoRows().hasWriterSchema());
      Thread.sleep(1005);
      assertTrue(appendFuture3.isDone());
    }
  }

  @Test
  public void testBatchIsFlushed() throws Exception {
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(1))
                    .build())
            .build()) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
              .build());
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(3)).build())
              .build());

      ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
      assertFalse(appendFuture1.isDone());
      writer.shutdown();
      // Write triggered by shutdown.
      assertEquals(0L, appendFuture1.get().getAppendResult().getOffset().getValue());
    }
  }

  @Test
  public void testBatchIsFlushedWithError() throws Exception {
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(1))
                    .build())
            .build()) {
      testBigQueryWrite.addException(Status.DATA_LOSS.asException());
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(3)).build())
              .build());

      ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
      ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});
      ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"C"});
      try {
        appendFuture2.get();
      } catch (ExecutionException ex) {
        assertEquals(DataLossException.class, ex.getCause().getClass());
      }
      assertFalse(appendFuture3.isDone());
      writer.shutdown();
      try {
        appendFuture3.get();
      } catch (ExecutionException ex) {
        assertEquals(AbortedException.class, ex.getCause().getClass());
      }
    }
  }

  @Test
  public void testFlowControlBehaviorBlock() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setFlowControlSettings(
                        StreamWriter.Builder.DEFAULT_FLOW_CONTROL_SETTINGS
                            .toBuilder()
                            .setMaxOutstandingElementCount(2L)
                            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                            .build())
                    .build())
            .build();

    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(2)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(3)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(4)).build())
            .build());
    // Response will have a 10 second delay before server sends them back.
    testBigQueryWrite.setResponseDelay(Duration.ofSeconds(10));

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"}, 2);
    final StreamWriter writer1 = writer;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Throwable> callable =
        new Callable<Throwable>() {
          @Override
          public Throwable call() {
            try {
              ApiFuture<AppendRowsResponse> appendFuture2 =
                  sendTestMessage(writer1, new String[] {"B"}, 3);
              ApiFuture<AppendRowsResponse> appendFuture3 =
                  sendTestMessage(writer1, new String[] {"C"}, 4);
              // This request will be send out immediately because there is space in inflight queue.
              // The time advance in the main thread will cause it to be sent back.
              if (3 != appendFuture2.get().getAppendResult().getOffset().getValue()) {
                return new Exception(
                    "expected 3 but got "
                        + appendFuture2.get().getAppendResult().getOffset().getValue());
              }
              testBigQueryWrite.waitForResponseScheduled();
              // Wait a while so that the close is called before we release the last response on the
              // ohter thread.
              Thread.sleep(500);
              // This triggers the last response to come back.
              fakeExecutor.advanceTime(Duration.ofSeconds(10));
              // This request will be waiting for previous response to come back.
              if (4 != appendFuture3.get().getAppendResult().getOffset().getValue()) {
                return new Exception(
                    "expected 4 but got "
                        + appendFuture3.get().getAppendResult().getOffset().getValue());
              }
              return null;
            } catch (Exception e) {
              return e;
            }
          }
        };
    Future<Throwable> future = executor.submit(callable);
    assertEquals(false, appendFuture1.isDone());
    testBigQueryWrite.waitForResponseScheduled();
    testBigQueryWrite.waitForResponseScheduled();
    // This will trigger the previous two responses to come back.
    fakeExecutor.advanceTime(Duration.ofSeconds(10));
    assertEquals(2L, appendFuture1.get().getAppendResult().getOffset().getValue());
    // When close is called, there should be one inflight request waiting.
    writer.close();
    if (future.get() != null) {
      future.get().printStackTrace();
      fail("Call got exception: " + future.get().toString());
    }
    // Everything should come back.
    executor.shutdown();
  }

  @Test
  public void testFlowControlBehaviorBlockWithError() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setFlowControlSettings(
                        StreamWriter.Builder.DEFAULT_FLOW_CONTROL_SETTINGS
                            .toBuilder()
                            .setMaxOutstandingElementCount(2L)
                            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                            .build())
                    .build())
            .build();

    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(2)).build())
            .build());
    testBigQueryWrite.addException(Status.DATA_LOSS.asException());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"}, 2);
    final StreamWriter writer1 = writer;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Throwable> callable =
        new Callable<Throwable>() {
          @Override
          public Throwable call() {
            try {
              ApiFuture<AppendRowsResponse> appendFuture2 =
                  sendTestMessage(writer1, new String[] {"B"}, 3);
              ApiFuture<AppendRowsResponse> appendFuture3 =
                  sendTestMessage(writer1, new String[] {"C"}, 4);
              try {
                // This request will be send out immediately because there is space in inflight
                // queue.
                assertEquals(3L, appendFuture2.get().getAppendResult().getOffset().getValue());
                return new Exception("Should have failure on future2");
              } catch (ExecutionException e) {
                if (e.getCause().getClass() != DataLossException.class) {
                  return e;
                }
              }
              try {
                // This request will be waiting for previous response to come back.
                assertEquals(4L, appendFuture3.get().getAppendResult().getOffset().getValue());
                fail("Should be aborted future3");
              } catch (ExecutionException e) {
                if (e.getCause().getClass() != AbortedException.class) {
                  return e;
                }
              }
              return null;
            } catch (Exception e) {
              return e;
            }
          }
        };
    Future<Throwable> future = executor.submit(callable);
    // Wait is necessary for response to be scheduled before timer is advanced.
    testBigQueryWrite.waitForResponseScheduled();
    testBigQueryWrite.waitForResponseScheduled();
    // This will trigger the previous two responses to come back.
    fakeExecutor.advanceTime(Duration.ofSeconds(10));
    // The first requests gets back while the second one is blocked.
    assertEquals(2L, appendFuture1.get().getAppendResult().getOffset().getValue());
    // When close is called, there should be one inflight request waiting.
    writer.close();
    if (future.get() != null) {
      future.get().printStackTrace();
      fail("Call got exception: " + future.get().toString());
    }
    // Everything should come back.
    executor.shutdown();
  }

  @Test
  public void testAppendWhileShutdownSuccess() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    // When shutdown, we should have something in batch.
                    .setElementCountThreshold(3L)
                    .setFlowControlSettings(
                        StreamWriter.Builder.DEFAULT_FLOW_CONTROL_SETTINGS
                            .toBuilder()
                            // When shutdown, we should have something in flight.
                            .setMaxOutstandingElementCount(5L)
                            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                            .build())
                    .build())
            .build();

    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(2)).build())
            .build());
    for (int i = 1; i < 13; i++) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder()
                      .setOffset(Int64Value.of(i * 3 + 2))
                      .build())
              .build());
    }
    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"}, 2);
    final StreamWriter writer1 = writer;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Throwable> callable =
        new Callable<Throwable>() {
          @Override
          public Throwable call() {
            try {
              LinkedList<ApiFuture<AppendRowsResponse>> responses =
                  new LinkedList<ApiFuture<AppendRowsResponse>>();
              int last_count = 0;
              for (int i = 0; i < 20; i++) {
                try {
                  responses.add(sendTestMessage(writer1, new String[] {"B"}, i + 3));
                } catch (IllegalStateException ex) {
                  LOG.info("Stopped at " + i + " responses:" + responses.size());
                  last_count = i;
                  if ("Cannot append on a shut-down writer." != ex.getMessage()) {
                    return new Exception("Got unexpected message:" + ex.getMessage());
                  }
                  break;
                }
              }
              // For all the requests that are sent in, we should get a finished callback.
              for (int i = 0; i < last_count; i++) {
                if (i + 3 != responses.get(i).get().getAppendResult().getOffset().getValue()) {
                  return new Exception(
                      "Got unexpected offset expect:"
                          + i
                          + " actual:"
                          + responses.get(i - 3).get().getAppendResult().getOffset().getValue());
                }
              }
              return null;
            } catch (Exception e) {
              return e;
            }
          }
        };
    Future<Throwable> future = executor.submit(callable);
    assertEquals(false, appendFuture1.isDone());
    // The first requests gets back while the second one is blocked.
    assertEquals(2L, appendFuture1.get().getAppendResult().getOffset().getValue());
    // When close is called, there should be one inflight request waiting.
    writer.close();
    if (future.get() != null) {
      future.get().printStackTrace();
      fail("Call got exception: " + future.get().toString());
    }
    // Everything should come back.
    executor.shutdown();
  }

  @Test
  public void testAppendWhileShutdownFailed() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    // When shutdown, we should have something in batch.
                    .setElementCountThreshold(3L)
                    .setDelayThreshold(Duration.ofSeconds(10))
                    .setFlowControlSettings(
                        StreamWriter.Builder.DEFAULT_FLOW_CONTROL_SETTINGS
                            .toBuilder()
                            // When shutdown, we should have something in flight.
                            .setMaxOutstandingElementCount(5L)
                            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                            .build())
                    .build())
            .build();

    // The responses are for every 3 messages.
    for (int i = 0; i < 2; i++) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder()
                      .setOffset(Int64Value.of(i * 3))
                      .build())
              .build());
    }
    for (int i = 2; i < 6; i++) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setError(
                  com.google.rpc.Status.newBuilder().setCode(3).setMessage("error " + i).build())
              .build());
    }
    // Stream failed at 7th request.
    for (int i = 6; i < 10; i++) {
      testBigQueryWrite.addException(new UnsupportedOperationException("Strange exception"));
    }
    final StreamWriter writer1 = writer;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Throwable> callable =
        new Callable<Throwable>() {
          @Override
          public Throwable call() {
            try {
              LinkedList<ApiFuture<AppendRowsResponse>> responses =
                  new LinkedList<ApiFuture<AppendRowsResponse>>();
              int last_count = 30;
              LOG.info(
                  "Send 30 messages, will be batched into 10 messages, start fail at 7th message");
              for (int i = 0; i < 30; i++) {
                try {
                  responses.add(sendTestMessage(writer1, new String[] {"B"}, i));
                  Thread.sleep(500);
                } catch (IllegalStateException ex) {
                  LOG.info("Stopped at sending request no." + i + " ex: " + ex.toString());
                  last_count = i;
                  if ("Stream already failed." != ex.getMessage()
                      && "Cannot append on a shut-down writer." != ex.getMessage()) {
                    return new Exception("Got unexpected message:" + ex.getMessage());
                  }
                  break;
                }
              }
              // Verify sent responses.
              // For all the requests that are sent in, we should get a finished callback.
              for (int i = 0; i < 2 * 3; i++) {
                try {
                  if (i != responses.get(i).get().getAppendResult().getOffset().getValue()) {
                    return new Exception(
                        "Got unexpected offset expect:"
                            + i
                            + " actual:"
                            + responses.get(i).get().getAppendResult().getOffset().getValue());
                  }
                } catch (Exception e) {
                  return e;
                }
              }
              // For all the requests that are sent in, we should get a finished callback.
              for (int i = 2 * 3; i < 6 * 3; i++) {
                try {
                  responses.get(i).get();
                  return new Exception(
                      "Expect response return an error after a in-stream exception");
                } catch (Exception e) {
                  if (e.getCause().getClass() != StatusRuntimeException.class) {
                    return new Exception(
                        "Expect first error of stream exception to be the original exception but got"
                            + e.getCause().toString());
                  }
                }
              }
              LOG.info("Last count is:" + last_count);
              for (int i = 6 * 3; i < last_count; i++) {
                try {
                  responses.get(i).get();
                  return new Exception("Expect response return an error after a stream exception");
                } catch (Exception e) {
                  if (e.getCause().getClass() != UnknownException.class
                      && e.getCause().getClass() != AbortedException.class) {
                    return new Exception("Unexpected stream exception:" + e.toString());
                  }
                }
              }
              return null;
            } catch (Exception e) {
              return e;
            }
          }
        };
    Future<Throwable> future = executor.submit(callable);
    // Wait for at least 7 request (after batch) to reach server.
    for (int i = 0; i < 7; i++) {
      LOG.info("Wait for " + i + " response scheduled");
      testBigQueryWrite.waitForResponseScheduled();
    }
    Thread.sleep(500);
    writer.close();
    if (future.get() != null) {
      future.get().printStackTrace();
      fail("Callback got exception");
    }
    // Everything should come back.
    executor.shutdown();
  }

  @Test
  public void testFlowControlBehaviorBlockAbortOnShutdown() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setFlowControlSettings(
                        StreamWriter.Builder.DEFAULT_FLOW_CONTROL_SETTINGS
                            .toBuilder()
                            .setMaxOutstandingElementCount(2L)
                            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                            .build())
                    .build())
            .build();

    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(2)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(3)).build())
            .build());
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(4)).build())
            .build());
    // Response will have a 10 second delay before server sends them back.
    testBigQueryWrite.setResponseDelay(Duration.ofSeconds(10));

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"}, 2);
    final StreamWriter writer1 = writer;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Callable<Throwable> callable =
        new Callable<Throwable>() {
          @Override
          public Throwable call() {
            ApiFuture<AppendRowsResponse> appendFuture2 =
                sendTestMessage(writer1, new String[] {"B"}, 3);
            ApiFuture<AppendRowsResponse> appendFuture3 =
                sendTestMessage(writer1, new String[] {"C"}, 4);
            try {
              // This request will be send out immediately because there is space in inflight queue.
              if (3L != appendFuture2.get().getAppendResult().getOffset().getValue()) {
                return new Exception(
                    "Expect offset to be 3 but got "
                        + appendFuture2.get().getAppendResult().getOffset().getValue());
              }
              testBigQueryWrite.waitForResponseScheduled();
              // This triggers the last response to come back.
              fakeExecutor.advanceTime(Duration.ofSeconds(10));
              // This request will be waiting for previous response to come back.
              if (4L != appendFuture3.get().getAppendResult().getOffset().getValue()) {
                return new Exception(
                    "Expect offset to be 4 but got "
                        + appendFuture2.get().getAppendResult().getOffset().getValue());
              }
            } catch (InterruptedException e) {
              return e;
            } catch (ExecutionException e) {
              return e;
            }
            return null;
          }
        };
    Future<Throwable> future = executor.submit(callable);
    assertEquals(false, appendFuture1.isDone());
    // Wait is necessary for response to be scheduled before timer is advanced.
    testBigQueryWrite.waitForResponseScheduled();
    testBigQueryWrite.waitForResponseScheduled();
    // This will trigger the previous two responses to come back.
    fakeExecutor.advanceTime(Duration.ofSeconds(10));
    // The first requests gets back while the second one is blocked.
    assertEquals(2L, appendFuture1.get().getAppendResult().getOffset().getValue());
    // When close is called, there should be one inflight request waiting.
    writer.close();
    assertEquals(future.get(), null);
    // Everything should come back.
    executor.shutdown();
  }

  @Test
  public void testFlowControlBehaviorException() throws Exception {
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setFlowControlSettings(
                        StreamWriter.Builder.DEFAULT_FLOW_CONTROL_SETTINGS
                            .toBuilder()
                            .setMaxOutstandingElementCount(1L)
                            .setLimitExceededBehavior(
                                FlowController.LimitExceededBehavior.ThrowException)
                            .build())
                    .build())
            .build()) {
      assertEquals(
          1L,
          writer
              .getBatchingSettings()
              .getFlowControlSettings()
              .getMaxOutstandingElementCount()
              .longValue());
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(1)).build())
              .build());
      testBigQueryWrite.setResponseDelay(Duration.ofSeconds(10));
      ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
      ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});
      // Wait is necessary for response to be scheduled before timer is advanced.
      testBigQueryWrite.waitForResponseScheduled();
      fakeExecutor.advanceTime(Duration.ofSeconds(10));
      try {
        appendFuture2.get();
        Assert.fail("This should fail");
      } catch (Exception e) {
        assertEquals(
            "java.util.concurrent.ExecutionException: The maximum number of batch elements: 1 have been reached.",
            e.toString());
      }
      assertEquals(1L, appendFuture1.get().getAppendResult().getOffset().getValue());
    }
  }

  @Test
  public void testStreamReconnectionPermanant() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(100000))
                    .setElementCountThreshold(1L)
                    .build())
            .build();
    StatusRuntimeException permanentError = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    testBigQueryWrite.addException(permanentError);
    ApiFuture<AppendRowsResponse> future2 = sendTestMessage(writer, new String[] {"m2"});
    try {
      future2.get();
      Assert.fail("This should fail.");
    } catch (ExecutionException e) {
      assertEquals(permanentError.toString(), e.getCause().getCause().toString());
    }
    writer.close();
  }

  @Test
  public void testOffset() throws Exception {
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .build())
            .build()) {

      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(10)).build())
              .build());
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(13)).build())
              .build());

      AppendRowsRequest request1 = createAppendRequest(new String[] {"A"}, 10L);
      ApiFuture<AppendRowsResponse> appendFuture1 = writer.append(request1);
      AppendRowsRequest request2 = createAppendRequest(new String[] {"B", "C"}, 11L);
      ApiFuture<AppendRowsResponse> appendFuture2 = writer.append(request2);
      AppendRowsRequest request3 = createAppendRequest(new String[] {"E", "F"}, 13L);
      ApiFuture<AppendRowsResponse> appendFuture3 = writer.append(request3);
      AppendRowsRequest request4 = createAppendRequest(new String[] {"G"}, 15L);
      ApiFuture<AppendRowsResponse> appendFuture4 = writer.append(request4);
      assertEquals(10L, appendFuture1.get().getAppendResult().getOffset().getValue());
      assertEquals(11L, appendFuture2.get().getAppendResult().getOffset().getValue());
      assertEquals(13L, appendFuture3.get().getAppendResult().getOffset().getValue());
      assertEquals(15L, appendFuture4.get().getAppendResult().getOffset().getValue());
    }
  }

  @Test
  public void testOffsetMismatch() throws Exception {
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .build())
            .build()) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(11)).build())
              .build());
      AppendRowsRequest request1 = createAppendRequest(new String[] {"A"}, 10L);
      ApiFuture<AppendRowsResponse> appendFuture1 = writer.append(request1);

      appendFuture1.get();
      fail("Should throw exception");
    } catch (Exception e) {
      assertEquals(
          "java.lang.IllegalStateException: The append result offset 11 does not match the expected offset 10.",
          e.getCause().toString());
    }
  }

  @Test
  public void testStreamAppendDirectException() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .build())
            .build();
    testBigQueryWrite.addException(Status.DATA_LOSS.asException());
    ApiFuture<AppendRowsResponse> future1 = sendTestMessage(writer, new String[] {"A"});
    try {
      future1.get();
      fail("Expected furture1 to fail");
    } catch (ExecutionException ex) {
      assertEquals(DataLossException.class, ex.getCause().getClass());
    }
    try {
      sendTestMessage(writer, new String[] {"B"});
      fail("Expected furture2 to fail");
    } catch (IllegalStateException ex) {
      assertEquals("Stream already failed.", ex.getMessage());
    }
    writer.shutdown();
    try {
      sendTestMessage(writer, new String[] {"C"});
      fail("Expected furture3 to fail");
    } catch (IllegalStateException ex) {
      assertEquals("Cannot append on a shut-down writer.", ex.getMessage());
    }
  }

  @Test
  public void testErrorPropagation() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setExecutorProvider(SINGLE_THREAD_EXECUTOR)
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .build())
            .build();
    testBigQueryWrite.addException(Status.DATA_LOSS.asException());
    testBigQueryWrite.addException(Status.DATA_LOSS.asException());
    ApiFuture<AppendRowsResponse> future1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> future2 = sendTestMessage(writer, new String[] {"B"});
    try {
      future1.get();
      fail("should throw exception");
    } catch (ExecutionException e) {
      assertThat(e.getCause()).isInstanceOf(DataLossException.class);
    }
    try {
      future2.get();
      fail("should throw exception");
    } catch (ExecutionException e) {
      assertThat(e.getCause()).isInstanceOf(AbortedException.class);
    }
  }

  @Test
  public void testWriterGetters() throws Exception {
    StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);
    builder.setChannelProvider(channelProvider);
    builder.setExecutorProvider(SINGLE_THREAD_EXECUTOR);
    builder.setBatchingSettings(
        BatchingSettings.newBuilder()
            .setRequestByteThreshold(10L)
            .setDelayThreshold(Duration.ofMillis(11))
            .setElementCountThreshold(12L)
            .setFlowControlSettings(
                FlowControlSettings.newBuilder()
                    .setMaxOutstandingElementCount(100L)
                    .setMaxOutstandingRequestBytes(1000L)
                    .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                    .build())
            .build());
    builder.setCredentialsProvider(NoCredentialsProvider.create());
    StreamWriter writer = builder.build();

    assertEquals(TEST_STREAM, writer.getStreamNameString());
    assertEquals(10, (long) writer.getBatchingSettings().getRequestByteThreshold());
    assertEquals(Duration.ofMillis(11), writer.getBatchingSettings().getDelayThreshold());
    assertEquals(12, (long) writer.getBatchingSettings().getElementCountThreshold());
    assertEquals(
        FlowController.LimitExceededBehavior.Block,
        writer.getBatchingSettings().getFlowControlSettings().getLimitExceededBehavior());
    assertEquals(
        100L,
        writer
            .getBatchingSettings()
            .getFlowControlSettings()
            .getMaxOutstandingElementCount()
            .longValue());
    assertEquals(
        1000L,
        writer
            .getBatchingSettings()
            .getFlowControlSettings()
            .getMaxOutstandingRequestBytes()
            .longValue());
    writer.close();
  }

  @Test
  public void testBuilderParametersAndDefaults() {
    StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);
    assertEquals(StreamWriter.Builder.DEFAULT_EXECUTOR_PROVIDER, builder.executorProvider);
    assertEquals(100 * 1024L, builder.batchingSettings.getRequestByteThreshold().longValue());
    assertEquals(Duration.ofMillis(10), builder.batchingSettings.getDelayThreshold());
    assertEquals(100L, builder.batchingSettings.getElementCountThreshold().longValue());
    assertEquals(StreamWriter.Builder.DEFAULT_RETRY_SETTINGS, builder.retrySettings);
    assertEquals(Duration.ofMillis(100), builder.retrySettings.getInitialRetryDelay());
    assertEquals(3, builder.retrySettings.getMaxAttempts());
  }

  @Test
  public void testBuilderInvalidArguments() {
    StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);

    try {
      builder.setChannelProvider(null);
      fail("Should have thrown an NullPointerException");
    } catch (NullPointerException expected) {
      // Expected
    }

    try {
      builder.setExecutorProvider(null);
      fail("Should have thrown an NullPointerException");
    } catch (NullPointerException expected) {
      // Expected
    }
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setRequestByteThreshold(null)
              .build());
      fail("Should have thrown an NullPointerException");
    } catch (NullPointerException expected) {
      // Expected
    }
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setRequestByteThreshold(0L)
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setRequestByteThreshold(-1L)
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }

    builder.setBatchingSettings(
        StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
            .toBuilder()
            .setDelayThreshold(Duration.ofMillis(1))
            .build());
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setDelayThreshold(null)
              .build());
      fail("Should have thrown an NullPointerException");
    } catch (NullPointerException expected) {
      // Expected
    }
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setDelayThreshold(Duration.ofMillis(-1))
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }

    builder.setBatchingSettings(
        StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
            .toBuilder()
            .setElementCountThreshold(1L)
            .build());
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setElementCountThreshold(null)
              .build());
      fail("Should have thrown an NullPointerException");
    } catch (NullPointerException expected) {
      // Expected
    }
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setElementCountThreshold(0L)
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }
    try {
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setElementCountThreshold(-1L)
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }

    try {
      FlowControlSettings flowControlSettings =
          FlowControlSettings.newBuilder().setMaxOutstandingElementCount(-1L).build();
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setFlowControlSettings(flowControlSettings)
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }

    try {
      FlowControlSettings flowControlSettings =
          FlowControlSettings.newBuilder().setMaxOutstandingRequestBytes(-1L).build();
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setFlowControlSettings(flowControlSettings)
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }

    try {
      FlowControlSettings flowControlSettings =
          FlowControlSettings.newBuilder()
              .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore)
              .build();
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setFlowControlSettings(flowControlSettings)
              .build());
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // Expected
    }

    try {
      FlowControlSettings flowControlSettings =
          FlowControlSettings.newBuilder().setLimitExceededBehavior(null).build();
      builder.setBatchingSettings(
          StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
              .toBuilder()
              .setFlowControlSettings(flowControlSettings)
              .build());
      fail("Should have thrown an NullPointerException");
    } catch (NullPointerException expected) {
      // Expected
    }
  }

  @Test
  public void testExistingClient() throws Exception {
    BigQueryWriteSettings settings =
        BigQueryWriteSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    BigQueryWriteClient client = BigQueryWriteClient.create(settings);
    StreamWriter writer = StreamWriter.newBuilder(TEST_STREAM, client).build();
    writer.close();
    assertFalse(client.isShutdown());
    client.shutdown();
    client.awaitTermination(1, TimeUnit.MINUTES);
  }

  @Test
  public void testDatasetTraceId() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .build())
            .setDataflowTraceId()
            .build();
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().build());
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});
    appendFuture1.get();
    appendFuture2.get();
    assertEquals("Dataflow", testBigQueryWrite.getAppendRequests().get(0).getTraceId());
    assertEquals("", testBigQueryWrite.getAppendRequests().get(1).getTraceId());
  }

  @Test
  public void testShutdownWithConnectionError() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .build())
            .build();
    // Three request will reach the server.
    testBigQueryWrite.addResponse(
        AppendRowsResponse.newBuilder()
            .setAppendResult(
                AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(1)).build())
            .build());
    testBigQueryWrite.addException(Status.DATA_LOSS.asException());
    testBigQueryWrite.addException(Status.DATA_LOSS.asException());
    testBigQueryWrite.addException(Status.DATA_LOSS.asException());
    testBigQueryWrite.setResponseDelay(Duration.ofSeconds(10));

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"}, 1);
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"}, 2);
    ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"C"}, 3);
    testBigQueryWrite.waitForResponseScheduled();
    testBigQueryWrite.waitForResponseScheduled();
    testBigQueryWrite.waitForResponseScheduled();
    fakeExecutor.advanceTime(Duration.ofSeconds(10));
    // This will will never be in inflight and aborted by previous failure, because its delay is set
    // after timer advance.
    Thread.sleep(500);
    try {
      ApiFuture<AppendRowsResponse> appendFuture4 = sendTestMessage(writer, new String[] {"D"}, 4);
    } catch (IllegalStateException ex) {
      assertEquals("Stream already failed.", ex.getMessage());
    }
    // Shutdown writer immediately and there will be some error happened when flushing the queue.
    writer.shutdown();
    assertEquals(1, appendFuture1.get().getAppendResult().getOffset().getValue());
    try {
      appendFuture2.get();
      fail("Should fail with exception future2");
    } catch (ExecutionException e) {
      assertThat(e.getCause()).isInstanceOf(DataLossException.class);
    }
    try {
      appendFuture3.get();
      fail("Should fail with exception future3");
    } catch (ExecutionException e) {
      assertThat(e.getCause()).isInstanceOf(AbortedException.class);
    }
  }
}
