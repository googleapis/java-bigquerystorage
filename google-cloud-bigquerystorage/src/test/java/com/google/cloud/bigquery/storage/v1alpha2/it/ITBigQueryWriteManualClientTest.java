/*
 * Copyright 2019 Google LLC
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

package com.google.cloud.bigquery.storage.v1alpha2.it;

import static org.junit.Assert.assertEquals;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.storage.v1alpha2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream;
import com.google.cloud.bigquery.storage.v1alpha2.StreamWriter;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.protobuf.*;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.bp.Duration;

/** Integration tests for BigQuery Storage API. */
public class ITBigQueryWriteManualClientTest {
  private static final Logger LOG =
      Logger.getLogger(ITBigQueryWriteManualClientTest.class.getName());
  private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();
  private static final String TABLE = "testtable";
  private static final String DESCRIPTION = "BigQuery Write Java manual client test dataset";

  private static BigQueryWriteClient client;
  private static TableInfo tableInfo;
  private static String tableId;
  private static BigQuery bigquery;

  @BeforeClass
  public static void beforeClass() throws IOException {
    client = BigQueryWriteClient.create();

    RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
    bigquery = bigqueryHelper.getOptions().getService();
    DatasetInfo datasetInfo =
        DatasetInfo.newBuilder(/* datasetId = */ DATASET).setDescription(DESCRIPTION).build();
    bigquery.create(datasetInfo);
    LOG.info("Created test dataset: " + DATASET);
    tableInfo =
        TableInfo.newBuilder(
                TableId.of(DATASET, TABLE),
                StandardTableDefinition.of(
                    Schema.of(
                        com.google.cloud.bigquery.Field.newBuilder("foo", LegacySQLTypeName.STRING)
                            .build())))
            .build();
    bigquery.create(tableInfo);
    tableId =
        String.format(
            "projects/%s/datasets/%s/tables/%s",
            ServiceOptions.getDefaultProjectId(), DATASET, TABLE);
    LOG.info(
        String.format(
            "%s tests running with table: %s",
            ITBigQueryWriteManualClientTest.class.getSimpleName(), tableId));
  }

  @AfterClass
  public static void afterClass() {
    if (client != null) {
      client.close();
    }

    if (bigquery != null) {
      RemoteBigQueryHelper.forceDelete(bigquery, DATASET);
      LOG.info("Deleted test dataset: " + DATASET);
    }
  }

  private AppendRowsRequest createAppendRequest(String streamName, String[] messages) {
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
    ProtobufEnvelope pe = new ProtobufEnvelope();
    try {
      for (String message : messages) {
        pe.addField("foo", message, DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
        rows.addSerializedRows(pe.constructMessage("t").toByteString());
        pe.clear();
      }
    } catch (Descriptors.DescriptorValidationException e) {
      throw new RuntimeException(e);
    }
    dataBuilder.setRows(rows.build());
    return requestBuilder.setProtoRows(dataBuilder.build()).setWriteStream(streamName).build();
  }

  @Test
  public void testDefaultWrite() throws IOException, InterruptedException, ExecutionException {
    WriteStream writeStream =
        client.createWriteStream(
            CreateWriteStreamRequest.newBuilder()
                .setParent(tableId)
                .setWriteStream(
                    WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build())
                .build());
    StreamWriter streamWriter = StreamWriter.newBuilder(writeStream.getName()).build();

    AppendRowsRequest request = createAppendRequest(writeStream.getName(), new String[] {"aaa"});
    ApiFuture<AppendRowsResponse> response = streamWriter.append(request);
    assertEquals(0, response.get().getOffset());

    streamWriter.shutdown();

    // Settings
    BatchingSettings batchingSettings = streamWriter.getBatchingSettings();
    assertEquals(100L, batchingSettings.getElementCountThreshold().longValue());
    assertEquals(
        100 * 1024L, // 10 Kb
        batchingSettings.getRequestByteThreshold().longValue());
    assertEquals(Duration.ofMillis(1), batchingSettings.getDelayThreshold());
    assertEquals(true, batchingSettings.getIsEnabled());
    assertEquals(
        FlowController.LimitExceededBehavior.Block,
        batchingSettings.getFlowControlSettings().getLimitExceededBehavior());
    assertEquals(
        1000L,
        batchingSettings.getFlowControlSettings().getMaxOutstandingElementCount().longValue());
    assertEquals(
        100 * 1024 * 1024L, // 100 Mb
        batchingSettings.getFlowControlSettings().getMaxOutstandingRequestBytes().longValue());
  }

  @Test
  public void testBatchWrite() throws IOException, InterruptedException, ExecutionException {
    WriteStream writeStream =
        client.createWriteStream(
            CreateWriteStreamRequest.newBuilder()
                .setParent(tableId)
                .setWriteStream(
                    WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build())
                .build());
    StreamWriter streamWriter =
        StreamWriter.newBuilder(writeStream.getName())
            .setBatchingSettings(
                BatchingSettings.newBuilder()
                    .setRequestByteThreshold(1024 * 1024L) // 1 Mb
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(2))
                    .build())
            .build();

    LOG.info("Sending one message");
    ApiFuture<AppendRowsResponse> response =
        streamWriter.append(createAppendRequest(writeStream.getName(), new String[] {"aaa"}));
    assertEquals(0, response.get().getOffset());

    LOG.info("Sending two more messages");
    ApiFuture<AppendRowsResponse> response1 =
        streamWriter.append(
            createAppendRequest(writeStream.getName(), new String[] {"bbb", "ccc"}));
    ApiFuture<AppendRowsResponse> response2 =
        streamWriter.append(createAppendRequest(writeStream.getName(), new String[] {"ddd"}));
    assertEquals(1, response1.get().getOffset());
    assertEquals(3, response2.get().getOffset());

    TableResult result =
        bigquery.listTableData(tableInfo.getTableId(), BigQuery.TableDataListOption.startIndex(0L));
    Iterator<FieldValueList> iter = result.getValues().iterator();
    assertEquals("aaa", iter.next().get(0).getStringValue());
    assertEquals("bbb", iter.next().get(0).getStringValue());
    assertEquals("ccc", iter.next().get(0).getStringValue());
    assertEquals("ddd", iter.next().get(0).getStringValue());
    assertEquals(false, iter.hasNext());

    streamWriter.shutdown();
  }
}
