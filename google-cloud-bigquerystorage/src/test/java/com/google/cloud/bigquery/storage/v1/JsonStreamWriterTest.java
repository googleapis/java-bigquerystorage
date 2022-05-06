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
package com.google.cloud.bigquery.storage.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.cloud.bigquery.storage.test.JsonTest;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.test.Test.UpdatedFooType;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalTime;

@RunWith(JUnit4.class)
public class JsonStreamWriterTest {
  private static final Logger LOG = Logger.getLogger(JsonStreamWriterTest.class.getName());
  private static final String TEST_STREAM = "projects/p/datasets/d/tables/t/streams/s";
  private static final String TEST_TABLE = "projects/p/datasets/d/tables/t";
  private static final ExecutorProvider SINGLE_THREAD_EXECUTOR =
      InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build();
  private static LocalChannelProvider channelProvider;
  private FakeScheduledExecutorService fakeExecutor;
  private FakeBigQueryWrite testBigQueryWrite;
  private static MockServiceHelper serviceHelper;

  private final TableFieldSchema FOO =
      TableFieldSchema.newBuilder()
          .setType(TableFieldSchema.Type.STRING)
          .setMode(TableFieldSchema.Mode.NULLABLE)
          .setName("foo")
          .build();
  private final TableSchema TABLE_SCHEMA = TableSchema.newBuilder().addFields(0, FOO).build();

  private final TableFieldSchema BAR =
      TableFieldSchema.newBuilder()
          .setType(TableFieldSchema.Type.STRING)
          .setMode(TableFieldSchema.Mode.NULLABLE)
          .setName("bar")
          .build();
  private final TableFieldSchema BAZ =
      TableFieldSchema.newBuilder()
          .setType(TableFieldSchema.Type.STRING)
          .setMode(TableFieldSchema.Mode.NULLABLE)
          .setName("baz")
          .build();
  private final TableSchema UPDATED_TABLE_SCHEMA =
      TableSchema.newBuilder().addFields(0, FOO).addFields(1, BAR).build();
  private final TableSchema UPDATED_TABLE_SCHEMA_2 =
      TableSchema.newBuilder().addFields(0, FOO).addFields(1, BAR).addFields(2, BAZ).build();

  private final TableFieldSchema TEST_INT =
      TableFieldSchema.newBuilder()
          .setType(TableFieldSchema.Type.INT64)
          .setMode(TableFieldSchema.Mode.NULLABLE)
          .setName("test_int")
          .build();
  private final TableFieldSchema TEST_STRING =
      TableFieldSchema.newBuilder()
          .setType(TableFieldSchema.Type.STRING)
          .setMode(TableFieldSchema.Mode.REPEATED)
          .setName("test_string")
          .build();

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
    serviceHelper.stop();
  }

  private JsonStreamWriter.Builder getTestJsonStreamWriterBuilder(
      String testStream, TableSchema BQTableSchema) {
    return JsonStreamWriter.newBuilder(testStream, BQTableSchema)
        .setChannelProvider(channelProvider)
        .setCredentialsProvider(NoCredentialsProvider.create());
  }

  @Test
  public void testTwoParamNewBuilder_nullSchema() {
    try {
      getTestJsonStreamWriterBuilder(null, TABLE_SCHEMA);
      Assert.fail("expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals(e.getMessage(), "StreamOrTableName is null.");
    }
  }

  @Test
  public void testTwoParamNewBuilder_nullStream() {
    try {
      getTestJsonStreamWriterBuilder(TEST_STREAM, null);
      Assert.fail("expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals(e.getMessage(), "TableSchema is null.");
    }
  }

  @Test
  public void testTwoParamNewBuilder()
      throws DescriptorValidationException, IOException, InterruptedException {
    JsonStreamWriter writer = getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA).build();
    assertEquals(TEST_STREAM, writer.getStreamName());
  }

  @Test
  public void testSingleAppendSimpleJson() throws Exception {
    FooType expectedProto = FooType.newBuilder().setFoo("allen").build();
    JSONObject foo = new JSONObject();
    foo.put("foo", "allen");
    JSONArray jsonArr = new JSONArray();
    jsonArr.put(foo);

    try (JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA)
            .setTraceId("test:empty")
            .build()) {

      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
              .build());

      ApiFuture<AppendRowsResponse> appendFuture = writer.append(jsonArr);
      assertEquals(0L, appendFuture.get().getAppendResult().getOffset().getValue());
      appendFuture.get();
      assertEquals(
          1,
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRows(0),
          expectedProto.toByteString());
      assertEquals(
          testBigQueryWrite.getAppendRequests().get(0).getTraceId(), "JsonWriter_test:empty");
    }
  }

  @Test
  public void testSpecialTypeAppend() throws Exception {
    TableFieldSchema field =
        TableFieldSchema.newBuilder()
            .setName("time")
            .setType(TableFieldSchema.Type.TIME)
            .setMode(TableFieldSchema.Mode.REPEATED)
            .build();
    TableSchema tableSchema = TableSchema.newBuilder().addFields(field).build();

    JsonTest.TestTime expectedProto =
        JsonTest.TestTime.newBuilder()
            .addTime(CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.of(1, 0, 1)))
            .build();
    JSONObject foo = new JSONObject();
    foo.put("time", new JSONArray(new String[] {"01:00:01"}));
    JSONArray jsonArr = new JSONArray();
    jsonArr.put(foo);

    try (JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, tableSchema).build()) {

      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
              .build());

      ApiFuture<AppendRowsResponse> appendFuture = writer.append(jsonArr);
      assertEquals(0L, appendFuture.get().getAppendResult().getOffset().getValue());
      appendFuture.get();
      assertEquals(
          1,
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRows(0),
          expectedProto.toByteString());
    }
  }

  @Test
  public void testSingleAppendMultipleSimpleJson() throws Exception {
    FooType expectedProto = FooType.newBuilder().setFoo("allen").build();
    JSONObject foo = new JSONObject();
    foo.put("foo", "allen");
    JSONObject foo1 = new JSONObject();
    foo1.put("foo", "allen");
    JSONObject foo2 = new JSONObject();
    foo2.put("foo", "allen");
    JSONObject foo3 = new JSONObject();
    foo3.put("foo", "allen");
    JSONArray jsonArr = new JSONArray();
    jsonArr.put(foo);
    jsonArr.put(foo1);
    jsonArr.put(foo2);
    jsonArr.put(foo3);

    try (JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA).build()) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
              .build());

      ApiFuture<AppendRowsResponse> appendFuture = writer.append(jsonArr);

      assertEquals(0L, appendFuture.get().getAppendResult().getOffset().getValue());
      appendFuture.get();
      assertEquals(
          4,
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(testBigQueryWrite.getAppendRequests().get(0).getTraceId(), "JsonWriter:null");
      for (int i = 0; i < 4; i++) {
        assertEquals(
            testBigQueryWrite
                .getAppendRequests()
                .get(0)
                .getProtoRows()
                .getRows()
                .getSerializedRows(i),
            expectedProto.toByteString());
      }
    }
  }

  @Test
  public void testMultipleAppendSimpleJson() throws Exception {
    FooType expectedProto = FooType.newBuilder().setFoo("allen").build();
    JSONObject foo = new JSONObject();
    foo.put("foo", "allen");
    JSONArray jsonArr = new JSONArray();
    jsonArr.put(foo);

    try (JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA).build()) {
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
      ApiFuture<AppendRowsResponse> appendFuture;
      for (int i = 0; i < 4; i++) {
        appendFuture = writer.append(jsonArr);
        assertEquals((long) i, appendFuture.get().getAppendResult().getOffset().getValue());
        appendFuture.get();
        assertEquals(
            1,
            testBigQueryWrite
                .getAppendRequests()
                .get(i)
                .getProtoRows()
                .getRows()
                .getSerializedRowsCount());
        assertEquals(
            testBigQueryWrite
                .getAppendRequests()
                .get(i)
                .getProtoRows()
                .getRows()
                .getSerializedRows(0),
            expectedProto.toByteString());
      }
    }
  }

  @Test
  public void testAppendOutOfRangeException() throws Exception {
    try (JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA).build()) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setError(com.google.rpc.Status.newBuilder().setCode(11).build())
              .build());
      JSONObject foo = new JSONObject();
      foo.put("foo", "allen");
      JSONArray jsonArr = new JSONArray();
      jsonArr.put(foo);
      ApiFuture<AppendRowsResponse> appendFuture = writer.append(jsonArr);
      try {
        appendFuture.get();
        Assert.fail("expected ExecutionException");
      } catch (ExecutionException ex) {
        assertEquals(ex.getCause().getMessage(), "OUT_OF_RANGE: ");
      }
    }
  }

  @Test
  public void testCreateDefaultStream() throws Exception {
    TableSchema tableSchema =
        TableSchema.newBuilder().addFields(0, TEST_INT).addFields(1, TEST_STRING).build();
    try (JsonStreamWriter writer =
        JsonStreamWriter.newBuilder(TEST_TABLE, tableSchema)
            .setChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build()) {
      assertEquals("projects/p/datasets/d/tables/t/_default", writer.getStreamName());
    }
  }

  @Test
  public void testSimpleSchemaUpdate() throws Exception {
    try (JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA).build()) {
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(0)).build())
              .setUpdatedSchema(UPDATED_TABLE_SCHEMA)
              .build());
      testBigQueryWrite.addResponse(
          AppendRowsResponse.newBuilder()
              .setAppendResult(
                  AppendRowsResponse.AppendResult.newBuilder().setOffset(Int64Value.of(1)).build())
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
      // First append
      JSONObject foo = new JSONObject();
      foo.put("foo", "aaa");
      JSONArray jsonArr = new JSONArray();
      jsonArr.put(foo);

      ApiFuture<AppendRowsResponse> appendFuture1 = writer.append(jsonArr);
      ApiFuture<AppendRowsResponse> appendFuture2 = writer.append(jsonArr);
      ApiFuture<AppendRowsResponse> appendFuture3 = writer.append(jsonArr);

      assertEquals(0L, appendFuture1.get().getAppendResult().getOffset().getValue());
      assertEquals(1L, appendFuture2.get().getAppendResult().getOffset().getValue());
      assertEquals(
          1,
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          testBigQueryWrite
              .getAppendRequests()
              .get(0)
              .getProtoRows()
              .getRows()
              .getSerializedRows(0),
          FooType.newBuilder().setFoo("aaa").build().toByteString());

      assertEquals(2L, appendFuture3.get().getAppendResult().getOffset().getValue());
      assertEquals(
          1,
          testBigQueryWrite
              .getAppendRequests()
              .get(1)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          testBigQueryWrite
              .getAppendRequests()
              .get(1)
              .getProtoRows()
              .getRows()
              .getSerializedRows(0),
          FooType.newBuilder().setFoo("aaa").build().toByteString());

      // Second append with updated schema.
      JSONObject updatedFoo = new JSONObject();
      updatedFoo.put("foo", "aaa");
      updatedFoo.put("bar", "bbb");
      JSONArray updatedJsonArr = new JSONArray();
      updatedJsonArr.put(updatedFoo);

      ApiFuture<AppendRowsResponse> appendFuture4 = writer.append(updatedJsonArr);

      assertEquals(3L, appendFuture4.get().getAppendResult().getOffset().getValue());
      assertEquals(4, testBigQueryWrite.getAppendRequests().size());
      assertEquals(
          1,
          testBigQueryWrite
              .getAppendRequests()
              .get(3)
              .getProtoRows()
              .getRows()
              .getSerializedRowsCount());
      assertEquals(
          testBigQueryWrite
              .getAppendRequests()
              .get(3)
              .getProtoRows()
              .getRows()
              .getSerializedRows(0),
          UpdatedFooType.newBuilder().setFoo("aaa").setBar("bbb").build().toByteString());

      assertTrue(testBigQueryWrite.getAppendRequests().get(0).getProtoRows().hasWriterSchema());
      assertTrue(
          testBigQueryWrite.getAppendRequests().get(2).getProtoRows().hasWriterSchema()
              || testBigQueryWrite.getAppendRequests().get(3).getProtoRows().hasWriterSchema());
    }
  }

  @Test
  public void testWithoutIgnoreUnknownFields() throws Exception {
    TableSchema tableSchema = TableSchema.newBuilder().addFields(0, TEST_INT).build();
    try (JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, tableSchema).build()) {
      JSONObject foo = new JSONObject();
      foo.put("test_int", 10);
      JSONObject bar = new JSONObject();
      bar.put("test_unknown", 10);
      JSONArray jsonArr = new JSONArray();
      jsonArr.put(foo);
      jsonArr.put(bar);
      try {
        ApiFuture<AppendRowsResponse> appendFuture = writer.append(jsonArr);
        Assert.fail("expected ExecutionException");
      } catch (Exception ex) {
        assertEquals(
            ex.getMessage(), "JSONObject has fields unknown to BigQuery: root.test_unknown.");
      }
    }
  }

  @Test
  public void testWithIgnoreUnknownFields() throws Exception {
    TableSchema tableSchema = TableSchema.newBuilder().addFields(0, TEST_INT).build();
    try (JsonStreamWriter writer =
        JsonStreamWriter.newBuilder(TEST_STREAM, tableSchema)
            .setChannelProvider(channelProvider)
            .setIgnoreUnknownFields(true)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build()) {
      testBigQueryWrite.addResponse(AppendRowsResponse.newBuilder().build());
      JSONObject foo = new JSONObject();
      foo.put("test_int", 10);
      JSONObject bar = new JSONObject();
      bar.put("test_unknown", 10);
      JSONArray jsonArr = new JSONArray();
      jsonArr.put(foo);
      jsonArr.put(bar);
      ApiFuture<AppendRowsResponse> appendFuture = writer.append(jsonArr);
      appendFuture.get();
    }
  }

  @Test
  public void testFlowControlSetting() throws Exception {
    TableSchema tableSchema = TableSchema.newBuilder().addFields(0, TEST_INT).build();
    try (JsonStreamWriter writer =
        JsonStreamWriter.newBuilder(TEST_STREAM, tableSchema)
            .setChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .setFlowControlSettings(
                FlowControlSettings.newBuilder()
                    .setLimitExceededBehavior(FlowController.LimitExceededBehavior.ThrowException)
                    .setMaxOutstandingRequestBytes(1L)
                    .build())
            .build()) {
      JSONObject foo = new JSONObject();
      foo.put("test_int", 10);
      JSONArray jsonArr = new JSONArray();
      jsonArr.put(foo);
      StatusRuntimeException ex =
          assertThrows(
              StatusRuntimeException.class,
              new ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                  writer.append(jsonArr);
                }
              });
      assertEquals(ex.getStatus().getCode(), Status.RESOURCE_EXHAUSTED.getCode());
      assertTrue(
          ex.getStatus()
              .getDescription()
              .contains(
                  "Exceeds client side inflight buffer, consider add more buffer or open more connections"));
    }
  }
}
