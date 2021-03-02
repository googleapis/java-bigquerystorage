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

package com.google.cloud.bigquery.storage.v1beta2.st;

import static org.junit.Assert.assertEquals;

import com.google.api.core.ApiFuture;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1beta2.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1beta2.StreamWriter;
import com.google.cloud.bigquery.storage.v1beta2.TableName;
import com.google.cloud.bigquery.storage.v1beta2.it.ITBigQueryStorageLongRunningTest;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

public class STBigQueryStorageLongRunningWriteTest {
  private static final Logger LOG =
      Logger.getLogger(ITBigQueryStorageLongRunningTest.class.getName());

  private static BigQueryWriteClient client;
  private static String parentProjectId;
  private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();
  private static final String TABLE = "testtable";
  private static final String DESCRIPTION = "BigQuery Write Java long test dataset";

  private static TableInfo tableInfo;
  private static String tableId;
  private static BigQuery bigquery;

  private static JSONObject MakeJsonObject(int size) throws IOException {
    JSONObject object = new JSONObject();
    // size: (1, simple)(2,complex)()
    if (size == 1) {
      object.put("test_str", "aaa");
      object.put("test_numerics", new JSONArray(new String[]{"1234", "-900000"}));
      object.put("test_datetime", String.valueOf(LocalDateTime.now()));
    }
    else if (size == 2) {  // make it complicated and large
      // TODO(jstocklass): Make a better json object that doesn't break the format rules.
    }
    return object;
  }

  @BeforeClass
  public static void beforeClass() throws IOException {
    //Assume.assumeTrue(LONG_TESTS_DISABLED_MESSAGE, Boolean.getBoolean(LONG_TESTS_ENABLED_PROPERTY));
    client = BigQueryWriteClient.create();
    parentProjectId = String.format("projects/%s", ServiceOptions.getDefaultProjectId());
    RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
    bigquery = bigqueryHelper.getOptions().getService();
    DatasetInfo datasetInfo = DatasetInfo.newBuilder(/* datasetId = */ DATASET).setDescription(DESCRIPTION).build();
    bigquery.create(datasetInfo);
    LOG.info("Created test dataset: " + DATASET);
    tableInfo =
        TableInfo.newBuilder(
            TableId.of(DATASET, TABLE),
            StandardTableDefinition.of(
                Schema.of(
                    com.google.cloud.bigquery.Field.newBuilder("foo", LegacySQLTypeName.STRING)
                        .setMode(Field.Mode.NULLABLE)
                        .build())))
            .build();
    com.google.cloud.bigquery.Field.Builder innerTypeFieldBuilder =
        com.google.cloud.bigquery.Field.newBuilder(
            "inner_type",
            LegacySQLTypeName.RECORD,
            com.google.cloud.bigquery.Field.newBuilder("value", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.REPEATED)
                .build());

    bigquery.create(tableInfo);
    tableId =
        String.format(
            "projects/%s/datasets/%s/tables/%s",
            ServiceOptions.getDefaultProjectId(), DATASET, TABLE);
    LOG.info(
        String.format(
            "%s tests running with parent project: %s",
            ITBigQueryStorageLongRunningTest.class.getSimpleName(), parentProjectId));
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

  @Test
  public void testDefaultStream() throws IOException, InterruptedException, ExecutionException,
      Descriptors.DescriptorValidationException {
    // Set up a default stream. Write to it for a long time, (a few minutes for now) and make
    // sure that everything goes well.
    String tableName = "JsonTableDefaultStream";
    TableInfo tableInfo =
        TableInfo.newBuilder(
            TableId.of(DATASET, tableName),
            StandardTableDefinition.of(
                Schema.of(
                    com.google.cloud.bigquery.Field.newBuilder(
                        "test_str", StandardSQLTypeName.STRING)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "test_numerics", StandardSQLTypeName.NUMERIC)
                        .setMode(Field.Mode.REPEATED)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "test_datetime", StandardSQLTypeName.DATETIME)
                        .build())))
            .build();
    bigquery.create(tableInfo);
    TableName parent = TableName.of(ServiceOptions.getDefaultProjectId(), DATASET, tableName);
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(parent.toString(), tableInfo.getDefinition().getSchema())
            .createDefaultStream()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setRequestByteThreshold(1024 * 1024L) // 1 Mb
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(2))
                    .build())
            .build()) {
      for (int i = 0; i < 5; i++){
        LOG.info("Sending a message");
        // Ramping up the size increases the latency
        JSONObject row = MakeJsonObject(1);
        JSONArray jsonArr = new JSONArray(new JSONObject[] {row});

        LocalDateTime start = LocalDateTime.now();
        Date startTime = new Date();
        //TODO(jstocklass): Make asynchronized calls instead of synchronized calls
        ApiFuture<AppendRowsResponse> response = jsonStreamWriter.append(jsonArr, -1);
        assertEquals(0, response.get().getAppendResult().getOffset().getValue());
        Assert.assertFalse(response.get().getAppendResult().hasOffset());
        Date finishTime = new Date();
        LOG.info("Latency: ".concat(String.valueOf(finishTime.getTime() - startTime.getTime())).concat(" ms"));
         // seems like 2 or 3 seconds on average
        //LOG.info(String.valueOf((Math.abs(finish.getSecond())-start.getSecond())));
      }

      TableResult result =
          bigquery.listTableData(
              tableInfo.getTableId(), BigQuery.TableDataListOption.startIndex(0L));
      Iterator<FieldValueList> iter = result.getValues().iterator();
      FieldValueList currentRow;
      for (int i = 0; i < 5; i++) {
        currentRow = iter.next();
        assertEquals("aaa", currentRow.get(0).getStringValue());
      }
      assertEquals(false, iter.hasNext());
    }
  }

  @Test
  public void testDedicatedStream() {
    // set up a dedicated stream. Write to it for a long time, (a few minutes) and make
    // sure that everything goes well.
  }

  @Test
  public void testBufferedStreamWithFlushOperation() {
    // Set up a buffered stream with an ongoing flush operation and write to it for a long time,
    // (a few minutes) and make sure that everything goes well.

  }
}
