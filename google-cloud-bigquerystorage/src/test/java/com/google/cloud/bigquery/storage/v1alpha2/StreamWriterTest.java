/*
 * Copyright 2016 Google LLC
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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.DataLossException;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.protobuf.DescriptorProtos;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessServerBuilder;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Duration;

@RunWith(JUnit4.class)
public class StreamWriterTest {

  private static final Logger LOG = Logger.getLogger(StreamWriterTest.class.getName());

  private static final String TEST_STREAM = "projects/p/datasets/d/tables/t/streams/s";

  private static final ExecutorProvider SINGLE_THREAD_EXECUTOR =
      InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build();

  private static final TransportChannelProvider TEST_CHANNEL_PROVIDER =
      LocalChannelProvider.create("test-server");

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

    fakeExecutor = new FakeScheduledExecutorService();
  }

  @After
  public void tearDown() throws Exception {
    serviceHelper.stop();
  }

  private AppendRowsRequest createAppendRequest(String[] messages) {
    AppendRowsRequest.Builder requestBuilder = AppendRowsRequest.newBuilder();
    AppendRowsRequest.ProtoData.Builder dataBuilder = AppendRowsRequest.ProtoData.newBuilder();
    dataBuilder.setWriterSchema(
        ProtoBufProto.ProtoSchema.newBuilder()
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
    ProtoBufProto.ProtoRows.Builder rows = ProtoBufProto.ProtoRows.newBuilder();
    for (String message : messages) {
      FooType foo = FooType.newBuilder().setFoo(message).build();
      rows.addSerializedRows(foo.toByteString());
    }
    return requestBuilder
        .setProtoRows(dataBuilder.setRows(rows.build()).build())
        .setWriteStream(TEST_STREAM)
        .build();
  }

  private ApiFuture<AppendRowsResponse> sendTestMessage(StreamWriter writer, String[] messages) {
    return writer.append(createAppendRequest(messages));
  }

  @Test
  public void testAppendByDuration() throws Exception {
    LOG.info("aaaa");
    StreamWriter writer =
        getTestStreamWriterBuilder()
            // To demonstrate that reaching duration will trigger append
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .setElementCountThreshold(10L)
                    .build())
            .build();

    LOG.info("jere");
    testBigQueryWrite.addResponse(Storage.AppendRowsResponse.newBuilder().setOffset(0).build());
    LOG.info("kere");
    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});

    assertFalse(appendFuture1.isDone());
    assertFalse(appendFuture2.isDone());

    LOG.info("Before advance");
    fakeExecutor.advanceTime(Duration.ofSeconds(10));
    LOG.info("After advance");

    assertEquals(0L, appendFuture1.get().getOffset());
    assertEquals(1L, appendFuture2.get().getOffset());

    assertEquals(
        2, testBigQueryWrite.getAppendRequests().get(0).getProtoRows().getSerializedSize());
    assertEquals(true,
        testBigQueryWrite.getAppendRequests().get(0).getProtoRows().hasWriterSchema());
    writer.shutdown();
    writer.awaitTermination(1, TimeUnit.MINUTES);
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

    testBigQueryWrite
        .addResponse(AppendRowsResponse.newBuilder().setOffset(0).build());
    testBigQueryWrite
        .addResponse(AppendRowsResponse.newBuilder().setOffset(2).build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});
    ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"C"});

    // Note we are not advancing time but message should still get published

    assertEquals(1L, appendFuture1.get().getOffset());
    assertEquals(2L, appendFuture2.get().getOffset());

    assertFalse(appendFuture3.isDone());

    ApiFuture<AppendRowsResponse> appendFuture4 = sendTestMessage(writer, new String[] {"D"});

    assertEquals(3L, appendFuture3.get().getOffset());
    assertEquals(4L, appendFuture4.get().getOffset());

    assertEquals(
        2,
        testBigQueryWrite
            .getAppendRequests()
            .get(0)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    assertEquals(
        2,
        testBigQueryWrite
            .getAppendRequests()
            .get(1)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    writer.shutdown();
    writer.awaitTermination(1, TimeUnit.MINUTES);
  }

  @Test
  public void testSingleAppendByNumBytes() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(100))
                    .build())
            .build();

    testBigQueryWrite
        .addResponse(AppendRowsResponse.newBuilder().setOffset(0).build());
    testBigQueryWrite
        .addResponse(AppendRowsResponse.newBuilder().setOffset(2).build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});
    ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"C"});

    // Note we are not advancing time but message should still get published

    assertEquals("1", appendFuture1.get());
    assertEquals("2", appendFuture2.get());
    assertFalse(appendFuture3.isDone());

    ApiFuture<AppendRowsResponse> appendFuture4 = sendTestMessage(writer, new String[] {"D"});
    assertEquals("3", appendFuture3.get());
    assertEquals("4", appendFuture4.get());

    assertEquals(2, testBigQueryWrite.getAppendRequests().size());
    writer.shutdown();
    writer.awaitTermination(1, TimeUnit.MINUTES);
  }

  @Test
  public void testWriteByShutdown() throws Exception {
    StreamWriter publisher =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(100))
                    .setElementCountThreshold(10L)
                    .build())
            .build();

    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0L).build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(publisher, new String[] {"A"});
    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(publisher, new String[] {"B"});

    // Note we are not advancing time or reaching the count threshold but messages should
    // still get published by call to shutdown

    publisher.shutdown();
    publisher.awaitTermination(1, TimeUnit.MINUTES);

    // Verify the publishes completed
    assertTrue(appendFuture1.isDone());
    assertTrue(appendFuture2.isDone());
    assertEquals(0L, appendFuture1.get().getOffset());
    assertEquals(1L, appendFuture2.get().getOffset());
  }

  @Test
  public void testWriteMixedSizeAndDuration() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            // To demonstrate that reaching duration will trigger publish
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .build())
            .build();

    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0L).build());
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(2L).build());

    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});

    fakeExecutor.advanceTime(Duration.ofSeconds(2));
    assertFalse(appendFuture1.isDone());

    ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B", "C"});

    // Write triggered by batch size
    assertEquals(0L, appendFuture1.get());
    assertEquals(1L, appendFuture2.get());

    ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, new String[] {"D"});

    assertFalse(appendFuture3.isDone());

    // Write triggered by time
    fakeExecutor.advanceTime(Duration.ofSeconds(5));

    assertEquals(3L, appendFuture3.get());

    assertEquals(
        3,
        testBigQueryWrite
            .getAppendRequests()
            .get(0)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    assertEquals(
        1,
        testBigQueryWrite
            .getAppendRequests()
            .get(0)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    writer.shutdown();
    writer.awaitTermination(1, TimeUnit.MINUTES);
  }

  @Test
  public void testStreamReconnection() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .build())
            .build();

    ApiFuture<AppendRowsResponse> future1 = sendTestMessage(writer, new String[] {"m1"});

    testBigQueryWrite.addException(new StatusException(Status.UNAVAILABLE));
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0).build());
    fakeExecutor.advanceTime(Duration.ZERO);

    // Request succeeded since the connection is transient.
    assertEquals(0L, future1.get().getError());

    testBigQueryWrite.addException(new StatusException(Status.INVALID_ARGUMENT));
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0).build());
    ApiFuture<AppendRowsResponse> future2 = sendTestMessage(writer, new String[] {"m2"});

    // Request failed since the error is not transient.
    try {
      future2.get();
      Assert.fail("This should fail.");
    } catch (ExecutionException e) {
      assertEquals(Status.INVALID_ARGUMENT, e.getCause());
    }

    writer.shutdown();
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
    try {
      sendTestMessage(writer, new String[] {"A"}).get();
      fail("should throw exception");
    } catch (ExecutionException e) {
      assertThat(e.getCause()).isInstanceOf(DataLossException.class);
    }
  }

  @Test
  public void testWriterGetters() throws Exception {
    StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);
    builder.setChannelProvider(TEST_CHANNEL_PROVIDER);
    builder.setExecutorProvider(SINGLE_THREAD_EXECUTOR);
    builder.setBatchingSettings(
        BatchingSettings.newBuilder()
            .setRequestByteThreshold(10L)
            .setDelayThreshold(Duration.ofMillis(11))
            .setElementCountThreshold(12L)
            .build());
    builder.setCredentialsProvider(NoCredentialsProvider.create());
    StreamWriter writer = builder.build();

    assertEquals(TEST_STREAM, writer.getStreamNameString());
    assertEquals(10, (long) writer.getBatchingSettings().getRequestByteThreshold());
    assertEquals(Duration.ofMillis(11), writer.getBatchingSettings().getDelayThreshold());
    assertEquals(12, (long) writer.getBatchingSettings().getElementCountThreshold());
    writer.shutdown();
    writer.awaitTermination(1, TimeUnit.MINUTES);
  }

  @Test
  public void testBuilderParametersAndDefaults() {
    StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);
    assertEquals(TEST_STREAM.toString(), builder.streamName);
    assertEquals(StreamWriter.Builder.DEFAULT_EXECUTOR_PROVIDER, builder.executorProvider);
    assertEquals(
        StreamWriter.Builder.DEFAULT_REQUEST_BYTES_THRESHOLD,
        builder.batchingSettings.getRequestByteThreshold().longValue());
    assertEquals(
        StreamWriter.Builder.DEFAULT_DELAY_THRESHOLD, builder.batchingSettings.getDelayThreshold());
    assertEquals(
        StreamWriter.Builder.DEFAULT_ELEMENT_COUNT_THRESHOLD,
        builder.batchingSettings.getElementCountThreshold().longValue());
    assertEquals(StreamWriter.Builder.DEFAULT_RETRY_SETTINGS, builder.retrySettings);
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
  public void testAwaitTermination() throws Exception {
    StreamWriter writer =
        getTestStreamWriterBuilder().setExecutorProvider(SINGLE_THREAD_EXECUTOR).build();
    ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
    writer.shutdown();
    assertTrue(writer.awaitTermination(1, TimeUnit.MINUTES));
  }

  @Test
  public void testShutDown() throws Exception {
    ApiFuture apiFuture = EasyMock.createMock(ApiFuture.class);
    StreamWriter writer = EasyMock.createMock(StreamWriter.class);
    EasyMock.expect(writer.append(createAppendRequest(new String[] {"A"}))).andReturn(apiFuture);
    EasyMock.expect(writer.awaitTermination(1, TimeUnit.MINUTES)).andReturn(true);
    writer.shutdown();
    EasyMock.expectLastCall().once();
    EasyMock.replay(writer);
    sendTestMessage(writer, new String[] {"A"});
    writer.shutdown();
    assertTrue(writer.awaitTermination(1, TimeUnit.MINUTES));
  }

  private StreamWriter.Builder getTestStreamWriterBuilder() {
    return StreamWriter.newBuilder(TEST_STREAM)
        //.setExecutorProvider(FixedExecutorProvider.create(fakeExecutor))
        .setChannelProvider(TEST_CHANNEL_PROVIDER)
        .setCredentialsProvider(NoCredentialsProvider.create());
  }
}
