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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.api.core.*;
import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.test.Test.UpdatedFooType;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.protobuf.Timestamp;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

@RunWith(JUnit4.class)
public class JsonStreamWriterTest {
  private static final Logger LOG = Logger.getLogger(JsonStreamWriterTest.class.getName());
  private static final String TEST_STREAM = "projects/p/datasets/d/tables/t/streams/s";
  private static final ExecutorProvider SINGLE_THREAD_EXECUTOR =
      InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build();
  private static LocalChannelProvider channelProvider;
  private FakeScheduledExecutorService fakeExecutor;
  private FakeBigQueryWrite testBigQueryWrite;
  private static MockServiceHelper serviceHelper;

  private final Table.TableFieldSchema FOO =
      Table.TableFieldSchema.newBuilder()
          .setType(Table.TableFieldSchema.Type.STRING)
          .setMode(Table.TableFieldSchema.Mode.NULLABLE)
          .setName("foo")
          .build();
  private final Table.TableSchema TABLE_SCHEMA =
      Table.TableSchema.newBuilder().addFields(0, FOO).build();

  private final Table.TableFieldSchema BAR =
      Table.TableFieldSchema.newBuilder()
          .setType(Table.TableFieldSchema.Type.STRING)
          .setMode(Table.TableFieldSchema.Mode.NULLABLE)
          .setName("bar")
          .build();
  private final Table.TableSchema UPDATED_TABLE_SCHEMA =
      Table.TableSchema.newBuilder().addFields(0, FOO).addFields(1, BAR).build();

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
  }

  @After
  public void tearDown() throws Exception {
    LOG.info("tearDown called");
    serviceHelper.stop();
  }

  private JsonStreamWriter.Builder getTestJsonStreamWriterBuilder(
      String testStream, Table.TableSchema BQTableSchema) {
    return JsonStreamWriter.newBuilder(testStream, BQTableSchema)
        .setChannelProvider(channelProvider)
        .setExecutorProvider(SINGLE_THREAD_EXECUTOR)
        .setCredentialsProvider(NoCredentialsProvider.create());
  }

  @Test
  public void testTwoParamNewBuilder() throws Exception {
    try {
      getTestJsonStreamWriterBuilder(null, TABLE_SCHEMA);
    } catch (NullPointerException e) {
      assertEquals(e.getMessage(), "StreamName is null.");
    }

    try {
      getTestJsonStreamWriterBuilder(TEST_STREAM, null);
    } catch (NullPointerException e) {
      assertEquals(e.getMessage(), "TableSchema is null.");
    }

    JsonStreamWriter writer = getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA).build();
    assertEquals(TABLE_SCHEMA, writer.getTableSchema());
    assertEquals(TEST_STREAM, writer.getStreamName());
  }

  @Test
  public void testAppendByDuration() throws Exception {
    JsonStreamWriter writer =
        getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA)
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setDelayThreshold(Duration.ofSeconds(5))
                    .setElementCountThreshold(1L)
                    .build())
            .setExecutorProvider(FixedExecutorProvider.create(fakeExecutor))
            .build();
    // Add fake resposne for FakeBigQueryWrite, first response has updated schema.
    testBigQueryWrite.addResponse(
        Storage.AppendRowsResponse.newBuilder()
            .setOffset(0)
            .setUpdatedSchema(UPDATED_TABLE_SCHEMA)
            .build());
    testBigQueryWrite.addResponse(Storage.AppendRowsResponse.newBuilder().setOffset(1).build());
    // First append
    JSONObject foo = new JSONObject();
    foo.put("foo", "allen");
    JSONArray fooArr = new JSONArray();
    fooArr.put(foo);
    ApiFuture<AppendRowsResponse> appendFuture1 =
        writer.append(fooArr, -1, /* allowUnknownFields */ false);

    assertFalse(appendFuture1.isDone());
    fakeExecutor.advanceTime(Duration.ofSeconds(10));

    Table.TableSchema updatedSchema = appendFuture1.get().getUpdatedSchema();
    int millis = 0;
    while (millis < 1000) {
      if (updatedSchema.equals(writer.getTableSchema())) {
        break;
      }
      Thread.sleep(10);
      millis += 10;
    }

    assertTrue(appendFuture1.isDone());
    assertEquals(0L, appendFuture1.get().getOffset());
    assertEquals(
        1,
        testBigQueryWrite
            .getAppendRequests()
            .get(0)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    assertEquals(
        testBigQueryWrite.getAppendRequests().get(0).getProtoRows().getRows().getSerializedRows(0),
        FooType.newBuilder().setFoo("allen").build().toByteString());

    // Second append with updated schema.
    JSONObject updatedFoo = new JSONObject();
    updatedFoo.put("foo", "allen");
    updatedFoo.put("bar", "allen2");
    JSONArray UpdatedFooArr = new JSONArray();
    UpdatedFooArr.put(updatedFoo);
    ApiFuture<AppendRowsResponse> appendFuture2 =
        writer.append(UpdatedFooArr, -1, /* allowUnknownFields */ false);

    assertEquals(1L, appendFuture2.get().getOffset());
    assertEquals(
        testBigQueryWrite.getAppendRequests().get(1).getProtoRows().getRows().getSerializedRows(0),
        UpdatedFooType.newBuilder().setFoo("allen").setBar("allen2").build().toByteString());
    assertEquals(
        1,
        testBigQueryWrite
            .getAppendRequests()
            .get(1)
            .getProtoRows()
            .getRows()
            .getSerializedRowsCount());
    // Check if writer schemas were added in for both connections.
    assertTrue(
        testBigQueryWrite
            .getAppendRequests()
            .get(0)
            .getProtoRows()
            .hasWriterSchema());
    assertTrue(
        testBigQueryWrite
            .getAppendRequests()
            .get(1)
            .getProtoRows()
            .hasWriterSchema());
    assertEquals(2, testBigQueryWrite.getAppendRequests().size());
    writer.close();
  }
}
