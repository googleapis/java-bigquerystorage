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

import com.google.api.core.*;
import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.cloud.bigquery.storage.test.Test.*;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Timestamp;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
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

  private AppendRowsRequest createAppendRequest(String[] messages, long offset) {
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
    if (offset > 0) {
      requestBuilder.setOffset(Int64Value.of(offset));
    }
    return requestBuilder
        .setProtoRows(dataBuilder.setRows(rows.build()).build())
        .setWriteStream(TEST_STREAM)
        .build();
  }

  private AppendRowsRequest createUpdatedAppendRequest(String[] messages, long offset) {
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
                    .addField(
                        DescriptorProtos.FieldDescriptorProto.newBuilder()
                            .setName("bar")
                            .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING)
                            .setNumber(2)
                            .build())
                    .build()));
    ProtoBufProto.ProtoRows.Builder rows = ProtoBufProto.ProtoRows.newBuilder();
    for (String message : messages) {
      String[] splitMessage = message.split(",");
      FooBarType foo =
          FooBarType.newBuilder().setFoo(splitMessage[0]).setBar(splitMessage[1]).build();
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
      JsonStreamWriter writer, String[] messages) {
    return writer.append(createAppendRequest(messages, -1));
  }

  private ApiFuture<AppendRowsResponse> sendUpdatedTestMessage(
      JsonStreamWriter writer, String[] messages) {
    return writer.append(createUpdatedAppendRequest(messages, -1));
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
      assertEquals(e.getMessage(), "BQTableSchema is null.");
    }

    JsonStreamWriter writer = getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA).build();
    assertEquals(TABLE_SCHEMA, writer.getBQTableSchema());
    assertEquals(TEST_STREAM, writer.getStreamName());
  }

  // @Test
  // public void testAppendByDuration() throws Exception {
  //   JsonStreamWriter writer =
  //       getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA)
  //           .setBatchingSettings(
  //               StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
  //                   .toBuilder()
  //                   .setDelayThreshold(Duration.ofSeconds(5))
  //                   .setElementCountThreshold(1L)
  //                   .build())
  //           .setExecutorProvider(FixedExecutorProvider.create(fakeExecutor))
  //           .build();
  //
  //   testBigQueryWrite.addResponse(
  //       Storage.AppendRowsResponse.newBuilder()
  //           .setOffset(0)
  //           .setUpdatedSchema(UPDATED_SCHEMA)
  //           .build());
  //   testBigQueryWrite.addResponse(Storage.AppendRowsResponse.newBuilder().setOffset(1).build());
  //   ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
  //
  //   assertFalse(appendFuture1.isDone());
  //   fakeExecutor.advanceTime(Duration.ofSeconds(10));
  //
  //   Table.TableSchema updatedSchema = appendFuture1.get().getUpdatedSchema();
  //   int millis = 0;
  //   while (millis < 1000) {
  //     if (updatedSchema.equals(writer.getBQTableSchema())) {
  //       break;
  //     }
  //     Thread.sleep(10);
  //     millis += 10;
  //   }
  //   assertEquals(0L, appendFuture1.get().getOffset());
  //   assertTrue(appendFuture1.isDone());
  //   ApiFuture<AppendRowsResponse> appendFuture2 =
  //       sendUpdatedTestMessage(writer, new String[] {"B,C"});
  //
  //   assertEquals(1L, appendFuture2.get().getOffset());
  //   assertEquals(2, testBigQueryWrite.getAppendRequests().size());
  //
  //   assertEquals(
  //       1,
  //       testBigQueryWrite
  //           .getAppendRequests()
  //           .get(0)
  //           .getProtoRows()
  //           .getRows()
  //           .getSerializedRowsCount());
  //
  //   assertEquals(
  //       1,
  //       testBigQueryWrite
  //           .getAppendRequests()
  //           .get(1)
  //           .getProtoRows()
  //           .getRows()
  //           .getSerializedRowsCount());
  //   assertEquals(
  //       true, testBigQueryWrite.getAppendRequests().get(0).getProtoRows().hasWriterSchema());
  //   writer.close();
  // }
}
