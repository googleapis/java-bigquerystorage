/*
 * Copyright 2021 Google LLC
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
import static org.junit.Assert.assertTrue;

import com.google.api.core.ApiFuture;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Field.Mode;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1beta2.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1beta2.TableName;
import com.google.cloud.bigquery.storage.v1beta2.it.ITBigQueryStorageLongRunningTest;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;

public class ITBigQueryStorageLongRunningWriteTest {
  public enum RowComplexity {
    SIMPLE,
    COMPLEX
  }

  private static final Logger LOG =
      Logger.getLogger(ITBigQueryStorageLongRunningTest.class.getName());
  private static final String LONG_TESTS_ENABLED_PROPERTY =
      "bigquery.storage.enable_long_running_tests";
  private static final String DESCRIPTION = "BigQuery Write Java long test dataset";

  private static String dataset;
  private static BigQueryWriteClient client;
  private static String parentProjectId;
  private static BigQuery bigquery;
  private static int requestLimit = 10;

  private static JSONObject MakeJsonObject(RowComplexity complexity) throws IOException {
    JSONObject object = new JSONObject();
    // size: (1, simple)(2,complex)()
    // TODO(jstocklass): Add option for testing protobuf format using StreamWriter2
    switch (complexity) {
      case SIMPLE:
        object.put("test_str", "aaa");
        object.put("test_numerics", new JSONArray(new String[] {"1234", "-900000"}));
        object.put("test_datetime", String.valueOf(LocalDateTime.now()));
        break;
      case COMPLEX:
        object.put("test_str", "aaa");
        object.put(
            "test_numerics1",
            new JSONArray(
                new String[] {
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                    "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                    "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
                    "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54",
                    "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
                    "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80",
                    "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93",
                    "94", "95", "96", "97", "98", "99", "100"
                }));
        object.put(
            "test_numerics2",
            new JSONArray(
                new String[] {
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                    "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                    "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
                    "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54",
                    "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
                    "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80",
                    "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93",
                    "94", "95", "96", "97", "98", "99", "100"
                }));
        object.put(
            "test_numerics3",
            new JSONArray(
                new String[] {
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                    "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                    "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
                    "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54",
                    "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
                    "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80",
                    "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93",
                    "94", "95", "96", "97", "98", "99", "100"
                }));
        object.put("test_datetime", String.valueOf(LocalDateTime.now()));
        object.put(
            "test_bools",
            new JSONArray(
                new boolean[] {
                    false, true, false, true, false, true, false, true, false, true, false, true,
                    false, true, false, true, false, true, true, false, true, false, true, false,
                    true, false, true, false, true, false, true, true, false, true, false, true,
                    false, true, false, true, false, true, false, true, true, false, true, false,
                    true, false, true, false, true, false, true, false, true, true, false, true,
                    false, true, false, true, false, true, false, true, false, true, true, false,
                    true, false, true, false, true, false, true, false, true, false, true, true,
                    false, true, false, true, false, true, false, true, false, true, false, true,
                    true, false, true, false, true, false, true, false, true, false, true, false,
                    true, true, false, true, false, true, false, true, false, true, false, true,
                    false, true,
                }));
        JSONObject sub = new JSONObject();
        sub.put("sub_bool", true);
        sub.put("sub_int", 12);
        sub.put("sub_string", "Test Test Test");
        object.put("test_subs", new JSONArray(new JSONObject[] {sub, sub, sub, sub, sub, sub}));
        break;
      default:
        break;
    }
    return object;
  }

  @BeforeClass
  public static void beforeClass() throws IOException {
    parentProjectId = String.format("projects/%s", ServiceOptions.getDefaultProjectId());

    client = BigQueryWriteClient.create();
    RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
    bigquery = bigqueryHelper.getOptions().getService();
    dataset = RemoteBigQueryHelper.generateDatasetName();
    DatasetInfo datasetInfo =
        DatasetInfo.newBuilder(/* datasetId = */ dataset).setDescription(DESCRIPTION).build();
    LOG.info("Creating dataset: " + dataset);
    bigquery.create(datasetInfo);
  }

  @AfterClass
  public static void afterClass() {
    if (client != null) {
      client.close();
    }
    if (bigquery != null && dataset != null) {
      RemoteBigQueryHelper.forceDelete(bigquery, dataset);
      LOG.info("Deleted test dataset: " + dataset);
    }
  }

  @Test
  public void testDefaultStreamSimpleSchema()
      throws IOException, InterruptedException, ExecutionException,
      Descriptors.DescriptorValidationException {
    // TODO(jstocklass): Set up a default stream. Write to it for a long time,
    // (a few minutes for now) and make sure that everything goes well, report stats.
    LOG.info(
        String.format(
            "%s tests running with parent project: %s",
            ITBigQueryStorageLongRunningWriteTest.class.getSimpleName(), parentProjectId));

    String tableName = "JsonSimpleTableDefaultStream";
    TableInfo tableInfo =
        TableInfo.newBuilder(
            TableId.of(dataset, tableName),
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

    long averageLatency = 0;
    long totalLatency = 0;
    TableName parent = TableName.of(ServiceOptions.getDefaultProjectId(), dataset, tableName);
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(parent.toString(), tableInfo.getDefinition().getSchema())
            .createDefaultStream()
            .build()) {
      for (int i = 0; i < requestLimit; i++) {
        JSONObject row = MakeJsonObject(RowComplexity.SIMPLE);
        JSONArray jsonArr = new JSONArray(new JSONObject[] {row});
        long startTime = System.nanoTime();
        // TODO(jstocklass): Make asynchronized calls instead of synchronized calls
        ApiFuture<AppendRowsResponse> response = jsonStreamWriter.append(jsonArr, -1);
        long finishTime = System.nanoTime();
        Assert.assertFalse(response.get().getAppendResult().hasOffset());
        // Ignore first entry, it is way slower than the others and ruins expected behavior
        if (i != 0) {
          totalLatency += (finishTime - startTime);
        }
      }
      averageLatency = totalLatency / requestLimit;
      // TODO(jstocklass): Is there a better way to get this than to log it?
      LOG.info("Simple average Latency: " + String.valueOf(averageLatency) + " ns");
      averageLatency = totalLatency = 0;

      TableResult result =
          bigquery.listTableData(
              tableInfo.getTableId(), BigQuery.TableDataListOption.startIndex(0L));
      Iterator<FieldValueList> iter = result.getValues().iterator();
      FieldValueList currentRow;
      for (int i = 0; i < requestLimit; i++) {
        assertTrue(iter.hasNext());
        currentRow = iter.next();
        assertEquals("aaa", currentRow.get(0).getStringValue());
      }
      assertEquals(false, iter.hasNext());
    }
  }

  @Test
  public void testDefaultStreamComplexSchema()
      throws IOException, InterruptedException, ExecutionException,
      Descriptors.DescriptorValidationException {
    StandardSQLTypeName[] array = new StandardSQLTypeName[] {StandardSQLTypeName.INT64};
    String complexTableName = "JsonComplexTableDefaultStream";
    TableInfo tableInfo2 =
        TableInfo.newBuilder(
            TableId.of(dataset, complexTableName),
            StandardTableDefinition.of(
                Schema.of(
                    Field.newBuilder("test_str", StandardSQLTypeName.STRING).build(),
                    Field.newBuilder("test_numerics1", StandardSQLTypeName.NUMERIC)
                        .setMode(Mode.REPEATED)
                        .build(),
                    Field.newBuilder("test_numerics2", StandardSQLTypeName.NUMERIC)
                        .setMode(Mode.REPEATED)
                        .build(),
                    Field.newBuilder("test_numerics3", StandardSQLTypeName.NUMERIC)
                        .setMode(Mode.REPEATED)
                        .build(),
                    Field.newBuilder("test_datetime", StandardSQLTypeName.DATETIME).build(),
                    Field.newBuilder("test_bools", StandardSQLTypeName.BOOL)
                        .setMode(Mode.REPEATED)
                        .build(),
                    Field.newBuilder(
                        "test_subs",
                        StandardSQLTypeName.STRUCT,
                        Field.of("sub_bool", StandardSQLTypeName.BOOL),
                        Field.of("sub_int", StandardSQLTypeName.INT64),
                        Field.of("sub_string", StandardSQLTypeName.STRING))
                        .setMode(Mode.REPEATED)
                        .build())))
            .build();
    bigquery.create(tableInfo2);

    long totalLatency = 0;
    long averageLatency = 0;
    TableName parent =
        TableName.of(ServiceOptions.getDefaultProjectId(), dataset, complexTableName);
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(parent.toString(), tableInfo2.getDefinition().getSchema())
            .createDefaultStream()
            .build()) {
      for (int i = 0; i < requestLimit; i++) {
        JSONObject row = MakeJsonObject(RowComplexity.COMPLEX);
        JSONArray jsonArr = new JSONArray(new JSONObject[] {row});
        long startTime = System.nanoTime();
        // TODO(jstocklass): Make asynchronized calls instead of synchronized calls
        ApiFuture<AppendRowsResponse> response = jsonStreamWriter.append(jsonArr, -1);
        long finishTime = System.nanoTime();
        Assert.assertFalse(response.get().getAppendResult().hasOffset());
        if (i != 0) {
          totalLatency += (finishTime - startTime);
        }
      }
      averageLatency = totalLatency / requestLimit;
      LOG.info("Complex average Latency: " + String.valueOf(averageLatency) + " ns");
      TableResult result2 =
          bigquery.listTableData(
              tableInfo2.getTableId(), BigQuery.TableDataListOption.startIndex(0L));
      Iterator<FieldValueList> iter = result2.getValues().iterator();
      FieldValueList currentRow2;
      for (int i = 0; i < requestLimit; i++) {
        assertTrue(iter.hasNext());
        currentRow2 = iter.next();
        assertEquals("aaa", currentRow2.get(0).getStringValue());
      }
      assertEquals(false, iter.hasNext());
    }
  }
}

