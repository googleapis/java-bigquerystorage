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

import com.google.cloud.bigquery.storage.test.Test.*;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.cloud.bigquery.storage.test.JsonTest.AllenTest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.api.core.*;
import com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream;
import java.util.concurrent.TimeUnit;
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
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.DataLossException;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
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
  private final Table.TableSchema UPDATED_SCHEMA =
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

  private JsonStreamWriter.Builder getTestJsonStreamWriterBuilder(String testStream, Table.TableSchema BQTableSchema) {
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

  private ApiFuture<AppendRowsResponse> sendTestMessage(JsonStreamWriter writer, String[] messages) {
    return writer.append(createAppendRequest(messages, -1));
  }

  @Test
  public void testAppendByDuration() throws Exception {
    try {
      JsonStreamWriter writer =
          getTestJsonStreamWriterBuilder(TEST_STREAM, TABLE_SCHEMA)
              .setBatchingSettings(
                  StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                      .toBuilder()
                      .setDelayThreshold(Duration.ofSeconds(5))
                      .setElementCountThreshold(10L)
                      .build())
              .setExecutorProvider(FixedExecutorProvider.create(fakeExecutor))
              .build();

      testBigQueryWrite.addResponse(Storage.AppendRowsResponse.newBuilder().setOffset(0).setUpdatedSchema(UPDATED_SCHEMA).build());
      ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, new String[] {"A"});
      ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, new String[] {"B"});

      assertFalse(appendFuture1.isDone());
      assertFalse(appendFuture2.isDone());
      fakeExecutor.advanceTime(Duration.ofSeconds(10));

      assertEquals(0L, appendFuture1.get().getOffset());
      assertEquals(1L, appendFuture2.get().getOffset());
      // System.out.println(appendFuture1.get().getUpdatedSchema());

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
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }

  }
  // private static final Logger LOG = Logger.getLogger(JsonStreamWriterTest.class.getName());

  // @Test
  // public void testStuff() throws Exception {
  //   try (BigQueryWriteClient client = BigQueryWriteClient.create()) {
  //     WriteStream response =
  //         client.createWriteStream(
  //             CreateWriteStreamRequest.newBuilder()
  //                 .setParent("projects/bigquerytestdefault/datasets/allenTest/tables/allenTable")
  //                 .setWriteStream(WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build())
  //                 .build());
  //
  //     JsonStreamWriter jsonStreamWriter =
  //         JsonStreamWriter.newBuilder(
  //                 response.getName(),
  //                 response.getTableSchema(),
  //                 client)
  //             .build();
  //     System.out.println(response.getTableSchema());
  //     System.out.flush();
  //     TimeUnit.SECONDS.sleep(10);
  //     AllenTest allen1 = AllenTest.newBuilder().setFoo("hello").setTestConnection("hello again").build();
  //     AllenTest allen2 = AllenTest.newBuilder().setFoo("hello3").setTestConnection("hello again3").build();
  //     List<AllenTest> protoRows = new ArrayList<AllenTest>();
  //     protoRows.add(allen1);
  //     protoRows.add(allen2);
  //     ApiFuture<AppendRowsResponse> appendResponseFuture = jsonStreamWriter.append(protoRows);
  //     System.out.println("Not waiting for api response");
  //     System.out.println(appendResponseFuture.get());
  //     System.out.println("waiting for api response");
  //     // AppendRowsResponse appendResponse = appendResponseFuture.get();
  //     // System.out.println(appendResponse.hasUpdatedSchema());
  //     // System.out.println(appendResponse.hasError());
  //     // System.out.println(appendResponse.getUpdatedSchema());
  //     // System.out.println(appendResponse.getOffset());
  //     // System.out.println(appendResponse.getError());
  //   }
  // }
}
