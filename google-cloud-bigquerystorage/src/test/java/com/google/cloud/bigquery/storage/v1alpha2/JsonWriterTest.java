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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.DataLossException;
import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

@RunWith(JUnit4.class)
public class JsonWriterTest {
  @Mock private BigQuery mockBigquery;
  @Mock private com.google.cloud.bigquery.Table mockBigqueryTable;
  private static final Logger LOG = Logger.getLogger(StreamWriterTest.class.getName());
  private static final String TEST_STREAM = "projects/p/datasets/d/tables/t/streams/s";
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
          Stream.WriteStream.newBuilder().setName(TEST_STREAM).setCreateTime(timestamp).build());
    }
    MockitoAnnotations.initMocks(this);
    when(mockBigquery.getTable(any(TableId.class))).thenReturn(mockBigqueryTable);
  }

  @After
  public void tearDown() throws Exception {
    LOG.info("tearDown called");
    serviceHelper.stop();
    verifyNoMoreInteractions(mockBigquery);
    verifyNoMoreInteractions(mockBigqueryTable);
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

  private JSONArray createFooJsonArray(String[] msgs) {
    JSONArray jsonArray = new JSONArray();
    for (String msg : msgs) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("Foo", msg);
      jsonArray.put(jsonObject);
    }
    return jsonArray;
  }

  public void customizeSchema(final Schema schema) {
    com.google.cloud.bigquery.TableDefinition definition =
        new TableDefinition() {
          @Override
          public Type getType() {
            return null;
          }

          @Nullable
          @Override
          public Schema getSchema() {
            return schema;
          }

          @Override
          public Builder toBuilder() {
            return null;
          }
        };
    when(mockBigqueryTable.getDefinition()).thenReturn(definition);
  }

  @Test
  public void testAppendByDuration() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
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
    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    testBigQueryWrite.addResponse(Storage.AppendRowsResponse.newBuilder().setOffset(0).build());

    ApiFuture<AppendRowsResponse> appendFuture1 =
        jsonWriter.append(createFooJsonArray(new String[] {"A"}));
    ApiFuture<AppendRowsResponse> appendFuture2 =
        jsonWriter.append(createFooJsonArray(new String[] {"B"}));

    assertFalse(appendFuture1.isDone());
    assertFalse(appendFuture2.isDone());
    fakeExecutor.advanceTime(Duration.ofSeconds(10));

    assertEquals(0L, appendFuture1.get().getOffset());
    assertEquals(1L, appendFuture2.get().getOffset());

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
    jsonWriter.close();
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testAppendByNumBatchedMessages() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(100))
                    .build())
            .build();

    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0).build());
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(2).build());

    ApiFuture<AppendRowsResponse> appendFuture1 =
        jsonWriter.append(createFooJsonArray(new String[] {"A"}));
    ApiFuture<AppendRowsResponse> appendFuture2 =
        jsonWriter.append(createFooJsonArray(new String[] {"B"}));
    ApiFuture<AppendRowsResponse> appendFuture3 =
        jsonWriter.append(createFooJsonArray(new String[] {"C"}));

    assertEquals(0L, appendFuture1.get().getOffset());
    assertEquals(1L, appendFuture2.get().getOffset());

    assertFalse(appendFuture3.isDone());

    ApiFuture<AppendRowsResponse> appendFuture4 =
        jsonWriter.append(createFooJsonArray(new String[] {"D"}));

    assertEquals(2L, appendFuture3.get().getOffset());
    assertEquals(3L, appendFuture4.get().getOffset());

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
    jsonWriter.close();
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testAppendByNumBytes() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
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

    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0).build());
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(2).build());
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(3).build());

    ApiFuture<AppendRowsResponse> appendFuture1 =
        jsonWriter.append(createFooJsonArray(new String[] {"A"}));
    ApiFuture<AppendRowsResponse> appendFuture2 =
        jsonWriter.append(createFooJsonArray(new String[] {"B"}));
    ApiFuture<AppendRowsResponse> appendFuture3 =
        jsonWriter.append(createFooJsonArray(new String[] {"C"}));

    assertEquals(0L, appendFuture1.get().getOffset());
    assertEquals(1L, appendFuture2.get().getOffset());
    assertFalse(appendFuture3.isDone());

    // This message is big enough to trigger send on the pervious message and itself.
    ApiFuture<AppendRowsResponse> appendFuture4 =
        jsonWriter.append(createFooJsonArray(new String[] {StringUtils.repeat('A', 100)}));
    assertEquals(2L, appendFuture3.get().getOffset());
    assertEquals(3L, appendFuture4.get().getOffset());

    assertEquals(3, testBigQueryWrite.getAppendRequests().size());

    jsonWriter.close();
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testWriteByShutdown() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(100))
                    .setElementCountThreshold(10L)
                    .build())
            .build();
    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0L).build());
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(1L).build());

    ApiFuture<AppendRowsResponse> appendFuture1 =
        jsonWriter.append(createFooJsonArray(new String[] {"A"}));
    ApiFuture<AppendRowsResponse> appendFuture2 =
        jsonWriter.append(createFooJsonArray(new String[] {"B"}));

    // Note we are not advancing time or reaching the count threshold but messages should
    // still get written by call to shutdown

    jsonWriter.close();

    // Verify the appends completed
    assertTrue(appendFuture1.isDone());
    assertTrue(appendFuture2.isDone());
    assertEquals(0L, appendFuture1.get().getOffset());
    assertEquals(1L, appendFuture2.get().getOffset());
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testWriteMixedSizeAndDuration() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .build())
            .build()) {
      JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
      testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0L).build());
      testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(2L).build());

      ApiFuture<AppendRowsResponse> appendFuture1 =
          jsonWriter.append(createFooJsonArray(new String[] {"A"}));

      fakeExecutor.advanceTime(Duration.ofSeconds(2));
      assertFalse(appendFuture1.isDone());

      ApiFuture<AppendRowsResponse> appendFuture2 =
          jsonWriter.append(createFooJsonArray(new String[] {"B", "C"}));

      // Write triggered by batch size
      assertEquals(0L, appendFuture1.get().getOffset());
      assertEquals(1L, appendFuture2.get().getOffset());

      ApiFuture<AppendRowsResponse> appendFuture3 =
          jsonWriter.append(createFooJsonArray(new String[] {"D"}));

      assertFalse(appendFuture3.isDone());

      // Write triggered by time
      fakeExecutor.advanceTime(Duration.ofSeconds(5));

      assertEquals(2L, appendFuture3.get().getOffset());

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
              .get(1)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          false, testBigQueryWrite.getAppendRequests().get(1).getProtoRows().hasWriterSchema());
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testFlowControlBehaviorBlock() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setFlowControlSettings(
                        StreamWriter.Builder.DEFAULT_FLOW_CONTROL_SETTINGS
                            .toBuilder()
                            .setMaxOutstandingRequestBytes(40L)
                            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                            .build())
                    .build())
            .build();
    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(2L).build());
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(3L).build());
    testBigQueryWrite.setResponseDelay(Duration.ofSeconds(10));

    ApiFuture<AppendRowsResponse> appendFuture1 =
        jsonWriter.append(createFooJsonArray(new String[] {"A"}));
    final JsonWriter jsonWriter1 = jsonWriter;
    Runnable runnable =
        new Runnable() {
          @Override
          public void run() {
            ApiFuture<AppendRowsResponse> appendFuture2 =
                jsonWriter1.append(createFooJsonArray(new String[] {"B"}));
          }
        };
    Thread t = new Thread(runnable);
    t.start();
    assertEquals(true, t.isAlive());
    assertEquals(false, appendFuture1.isDone());
    // Wait is necessary for response to be scheduled before timer is advanced.
    Thread.sleep(5000L);
    fakeExecutor.advanceTime(Duration.ofSeconds(10));
    // The first requests gets back while the second one is blocked.
    assertEquals(2L, appendFuture1.get().getOffset());
    Thread.sleep(5000L);
    // Wait is necessary for response to be scheduled before timer is advanced.
    fakeExecutor.advanceTime(Duration.ofSeconds(10));
    t.join();
    jsonWriter.close();
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testFlowControlBehaviorException() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
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
      JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
      testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(1L).build());
      testBigQueryWrite.setResponseDelay(Duration.ofSeconds(10));
      ApiFuture<AppendRowsResponse> appendFuture1 =
          jsonWriter.append(createFooJsonArray(new String[] {"A"}));
      ApiFuture<AppendRowsResponse> appendFuture2 =
          jsonWriter.append(createFooJsonArray(new String[] {"B"}));
      // Wait is necessary for response to be scheduled before timer is advanced.
      Thread.sleep(5000L);
      fakeExecutor.advanceTime(Duration.ofSeconds(10));
      try {
        appendFuture2.get();
        Assert.fail("This should fail");
      } catch (Exception e) {
        assertEquals(
            "java.util.concurrent.ExecutionException: The maximum number of batch elements: 1 have been reached.",
            e.toString());
      }
      assertEquals(1L, appendFuture1.get().getOffset());
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testStreamReconnectionTransient() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(100000))
                    .setElementCountThreshold(1L)
                    .build())
            .build();
    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    StatusRuntimeException transientError = new StatusRuntimeException(Status.UNAVAILABLE);
    testBigQueryWrite.addException(transientError);
    testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(0).build());
    ApiFuture<AppendRowsResponse> future1 =
        jsonWriter.append(createFooJsonArray(new String[] {"m1"}));
    assertEquals(false, future1.isDone());
    // Retry is scheduled to be 7 seconds later.
    assertEquals(0L, future1.get().getOffset());
    jsonWriter.close();
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testStreamReconnectionPermanant() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(100000))
                    .setElementCountThreshold(1L)
                    .build())
            .build();
    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    StatusRuntimeException permanentError = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    testBigQueryWrite.addException(permanentError);
    ApiFuture<AppendRowsResponse> future2 =
        jsonWriter.append(createFooJsonArray(new String[] {"m2"}));
    try {
      future2.get();
      Assert.fail("This should fail.");
    } catch (ExecutionException e) {
      assertEquals(permanentError.toString(), e.getCause().getCause().toString());
    }
    jsonWriter.close();
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testStreamReconnectionExceedRetry() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(100000))
                    .setElementCountThreshold(1L)
                    .build())
            .setRetrySettings(
                RetrySettings.newBuilder()
                    .setMaxRetryDelay(Duration.ofMillis(100))
                    .setMaxAttempts(1)
                    .build())
            .build();
    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    assertEquals(1, writer.getRetrySettings().getMaxAttempts());
    StatusRuntimeException transientError = new StatusRuntimeException(Status.UNAVAILABLE);
    testBigQueryWrite.addException(transientError);
    testBigQueryWrite.addException(transientError);
    ApiFuture<AppendRowsResponse> future3 =
        jsonWriter.append(createFooJsonArray(new String[] {"toomanyretry"}));
    try {
      future3.get();
      Assert.fail("This should fail.");
    } catch (ExecutionException e) {
      assertEquals(transientError.toString(), e.getCause().getCause().toString());
    }
    jsonWriter.close();
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testOffset() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(2L)
                    .build())
            .build()) {
      JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
      testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(10L).build());
      testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(13L).build());
      ApiFuture<AppendRowsResponse> appendFuture1 =
          jsonWriter.append(createFooJsonArray(new String[] {"A"}), 10L);
      ApiFuture<AppendRowsResponse> appendFuture2 =
          jsonWriter.append(createFooJsonArray(new String[] {"B", "C"}), 11L);
      ApiFuture<AppendRowsResponse> appendFuture3 =
          jsonWriter.append(createFooJsonArray(new String[] {"E", "F"}), 13L);
      ApiFuture<AppendRowsResponse> appendFuture4 =
          jsonWriter.append(createFooJsonArray(new String[] {"G"}), 15L);
      assertEquals(10L, appendFuture1.get().getOffset());
      assertEquals(11L, appendFuture2.get().getOffset());
      assertEquals(13L, appendFuture3.get().getOffset());
      assertEquals(15L, appendFuture4.get().getOffset());
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testOffsetMismatch() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .build())
            .build()) {
      JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
      testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().setOffset(11L).build());
      ApiFuture<AppendRowsResponse> appendFuture1 =
          jsonWriter.append(createFooJsonArray(new String[] {"A"}), 10L);

      appendFuture1.get();
      fail("Should throw exception");
    } catch (Exception e) {
      assertEquals(
          "java.lang.IllegalStateException: The append result offset 11 does not match the expected offset 10.",
          e.getCause().toString());
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testErrorPropagation() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    try (StreamWriter writer =
        getTestStreamWriterBuilder()
            .setExecutorProvider(SINGLE_THREAD_EXECUTOR)
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setElementCountThreshold(1L)
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .build())
            .build()) {
      JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
      testBigQueryWrite.addException(Status.DATA_LOSS.asException());
      jsonWriter.append(createFooJsonArray(new String[] {"A"}), 10L).get();
      fail("should throw exception");
    } catch (ExecutionException e) {
      assertThat(e.getCause()).isInstanceOf(DataLossException.class);
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testExistingClient() throws Exception {
    customizeSchema(
        Schema.of(
            Field.newBuilder("Foo", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    BigQueryWriteSettings settings =
        BigQueryWriteSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    BigQueryWriteClient client = BigQueryWriteClient.create(settings);
    StreamWriter writer = StreamWriter.newBuilder(TEST_STREAM, client).build();
    JsonWriter jsonWriter = new JsonWriter(writer, mockBigquery);
    jsonWriter.close();
    assertFalse(client.isShutdown());
    client.shutdown();
    client.awaitTermination(1, TimeUnit.MINUTES);
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }
}
