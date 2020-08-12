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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.cloud.bigquery.storage.test.Test.*;
import com.google.common.collect.Sets;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Timestamp;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.threeten.bp.Instant;
import org.json.JSONObject;
import org.json.JSONArray;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;

@RunWith(JUnit4.class)
public class JsonDirectWriterTest {
  private static final Logger LOG = Logger.getLogger(JsonDirectWriterTest.class.getName());

  private static final String TEST_TABLE = "projects/p/datasets/d/tables/t";
  private static final String TEST_STREAM = "projects/p/datasets/d/tables/t/streams/s";
  private static final String TEST_STREAM_2 = "projects/p/datasets/d/tables/t/streams/s2";

  private static MockBigQueryWrite mockBigQueryWrite;
  private static MockServiceHelper serviceHelper;
  private BigQueryWriteClient client;
  private LocalChannelProvider channelProvider;

  private final Table.TableFieldSchema FOO =
      Table.TableFieldSchema.newBuilder()
          .setType(Table.TableFieldSchema.Type.STRING)
          .setMode(Table.TableFieldSchema.Mode.NULLABLE)
          .setName("foo")
          .build();
  private final Table.TableSchema TABLE_SCHEMA =
      Table.TableSchema.newBuilder().addFields(0, FOO).build();

  @Mock private static SchemaCompatibility schemaCheck;

  @BeforeClass
  public static void startStaticServer() {
    mockBigQueryWrite = new MockBigQueryWrite();
    serviceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockBigQueryWrite));
    serviceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    serviceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    serviceHelper.reset();
    channelProvider = serviceHelper.createChannelProvider();
    BigQueryWriteSettings settings =
        BigQueryWriteSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = BigQueryWriteClient.create(settings);
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  /** Response mocks for create a new writer */
  void WriterCreationResponseMock(String testStreamName, Set<Long> responseOffsets) {
    // Response from CreateWriteStream
    Stream.WriteStream expectedResponse =
        Stream.WriteStream.newBuilder().setName(testStreamName).setTableSchema(TABLE_SCHEMA).build();
    mockBigQueryWrite.addResponse(expectedResponse);

    // Response from GetWriteStream
    Instant time = Instant.now();
    Timestamp timestamp =
        Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
    Stream.WriteStream expectedResponse2 =
        Stream.WriteStream.newBuilder()
            .setName(testStreamName)
            .setType(Stream.WriteStream.Type.COMMITTED)
            .setCreateTime(timestamp)
            .setTableSchema(TABLE_SCHEMA)
            .build();
    mockBigQueryWrite.addResponse(expectedResponse2);

    for (Long offset : responseOffsets) {
      Storage.AppendRowsResponse response =
          Storage.AppendRowsResponse.newBuilder().setOffset(offset).build();
      mockBigQueryWrite.addResponse(response);
    }
  }

  @Test
  public void testWriteSuccess() throws Exception {
    JsonDirectWriter.testSetStub(client, 10);
    JSONObject foo1 = new JSONObject();
    foo1.put("foo", "m1");
    JSONObject foo2 = new JSONObject();
    foo2.put("foo", "m2");
    JSONArray json = new JSONArray();
    json.put(foo1);
    json.put(foo2);
    FooType m1 = FooType.newBuilder().setFoo("m1").build();
    FooType m2 = FooType.newBuilder().setFoo("m2").build();

    WriterCreationResponseMock(TEST_STREAM, Sets.newHashSet(Long.valueOf(0L)));
    ApiFuture<Long> ret = JsonDirectWriter.append(TEST_TABLE, json);
    assertEquals(Long.valueOf(0L), ret.get());
    List<AbstractMessage> actualRequests = mockBigQueryWrite.getRequests();
    Assert.assertEquals(3, actualRequests.size());
    assertEquals(
        TEST_TABLE, ((Storage.CreateWriteStreamRequest) actualRequests.get(0)).getParent());
    assertEquals(
        Stream.WriteStream.Type.COMMITTED,
        ((Storage.CreateWriteStreamRequest) actualRequests.get(0)).getWriteStream().getType());
    assertEquals(TEST_STREAM, ((Storage.GetWriteStreamRequest) actualRequests.get(1)).getName());
    assertEquals(((AppendRowsRequest)actualRequests.get(2)).getProtoRows().getRows().getSerializedRows(0), m1.toByteString());
    assertEquals(((AppendRowsRequest)actualRequests.get(2)).getProtoRows().getRows().getSerializedRows(1), m2.toByteString());

    Storage.AppendRowsResponse response =
        Storage.AppendRowsResponse.newBuilder().setOffset(2).build();
    mockBigQueryWrite.addResponse(response);
    JSONArray json2 = new JSONArray();
    json2.put(foo1);
    // // Append again, write stream name and schema are cleared.
    ret = JsonDirectWriter.append(TEST_TABLE, json2);
    assertEquals(Long.valueOf(2L), ret.get());
    assertEquals(((AppendRowsRequest)actualRequests.get(3)).getProtoRows().getRows().getSerializedRows(0), m1.toByteString());
    JsonDirectWriter.clearCache();
  }

  @Test
  public void testWriteBadTableName() throws Exception {
    JsonDirectWriter.testSetStub(client, 10);
    JSONObject foo1 = new JSONObject();
    foo1.put("foo", "m1");
    JSONObject foo2 = new JSONObject();
    foo2.put("foo", "m2");
    JSONArray json = new JSONArray();
    json.put(foo1);
    json.put(foo2);

    try {
      ApiFuture<Long> ret = JsonDirectWriter.append("abc", json);
      fail("should fail");
    } catch (IllegalArgumentException expected) {
      assertEquals("Invalid table name: abc", expected.getMessage());
    }

    JsonDirectWriter.clearCache();
  }

  @Test
  public void testConcurrentAccess() throws Exception {
    JsonDirectWriter.testSetStub(client, 2);
    JSONObject foo1 = new JSONObject();
    foo1.put("foo", "m1");
    JSONObject foo2 = new JSONObject();
    foo2.put("foo", "m2");
    final JSONArray json = new JSONArray();
    json.put(foo1);
    json.put(foo2);

    final Set<Long> expectedOffset =
        Sets.newHashSet(
            Long.valueOf(0L),
            Long.valueOf(2L),
            Long.valueOf(4L),
            Long.valueOf(6L),
            Long.valueOf(8L));
    // Make sure getting the same table writer in multiple thread only cause create to be called
    // once.
    WriterCreationResponseMock(TEST_STREAM, expectedOffset);
    ExecutorService executor = Executors.newFixedThreadPool(5);
    for (int i = 0; i < 5; i++) {
      executor.execute(
          new Runnable() {
            @Override
            public void run() {
              try {
                ApiFuture<Long> result =
                    JsonDirectWriter.append(TEST_TABLE, json);
                synchronized (expectedOffset) {
                  assertTrue(expectedOffset.remove(result.get()));
                }
              } catch (Exception e) {
                fail(e.toString());
              }
            }
          });
    }
    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      LOG.info(e.toString());
    }
    JsonDirectWriter.clearCache();
  }
}
