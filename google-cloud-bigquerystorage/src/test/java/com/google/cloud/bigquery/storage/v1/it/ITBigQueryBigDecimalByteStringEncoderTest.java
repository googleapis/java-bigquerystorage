/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.bigquery.storage.v1.it;

import static org.junit.Assert.assertEquals;

import com.google.api.core.ApiFuture;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field.Mode;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.AppendResult;
import com.google.cloud.bigquery.storage.v1beta2.BigDecimalByteStringEncoder;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1beta2.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1beta2.TableFieldSchema;
import com.google.cloud.bigquery.storage.v1beta2.TableName;
import com.google.cloud.bigquery.storage.v1beta2.TableSchema;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITBigQueryBigDecimalByteStringEncoderTest {
  private static final Logger LOG =
      Logger.getLogger(ITBigQueryBigDecimalByteStringEncoderTest.class.getName());
  private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();
  private static final String TABLE = "testtable";
  private static final String DESCRIPTION = "BigQuery Write Java manual client test dataset";

  private static BigQueryWriteClient client;
  private static TableInfo tableInfo;
  private static BigQuery bigquery;

  @BeforeClass
  public static void beforeClass() throws IOException {
    client = BigQueryWriteClient.create();

    RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
    bigquery = bigqueryHelper.getOptions().getService();
    DatasetInfo datasetInfo =
        DatasetInfo.newBuilder(/* datasetId = */ DATASET).setDescription(DESCRIPTION).build();
    bigquery.create(datasetInfo);
    tableInfo =
        TableInfo.newBuilder(
                TableId.of(DATASET, TABLE),
                StandardTableDefinition.of(
                    Schema.of(
                        com.google.cloud.bigquery.Field.newBuilder(
                                "test_numeric_zero", StandardSQLTypeName.NUMERIC)
                            .build(),
                        com.google.cloud.bigquery.Field.newBuilder(
                                "test_numeric_one", StandardSQLTypeName.NUMERIC)
                            .build(),
                        com.google.cloud.bigquery.Field.newBuilder(
                                "test_numeric_repeated", StandardSQLTypeName.NUMERIC)
                            .setMode(Mode.REPEATED)
                            .build())))
            .build();
    bigquery.create(tableInfo);
  }

  @AfterClass
  public static void afterClass() {
    if (client != null) {
      client.close();
    }
    if (bigquery != null) {
      RemoteBigQueryHelper.forceDelete(bigquery, DATASET);
    }
  }

  @Test
  public void TestBigDecimalEncoding()
      throws IOException, InterruptedException, ExecutionException,
          Descriptors.DescriptorValidationException {
    TableName parent = TableName.of(ServiceOptions.getDefaultProjectId(), DATASET, TABLE);
    TableFieldSchema TEST_NUMERIC_ZERO =
        TableFieldSchema.newBuilder()
            .setType(TableFieldSchema.Type.NUMERIC)
            .setMode(TableFieldSchema.Mode.NULLABLE)
            .setName("test_numeric_zero")
            .build();
    TableFieldSchema TEST_NUMERIC_ONE =
        TableFieldSchema.newBuilder()
            .setType(TableFieldSchema.Type.NUMERIC)
            .setMode(TableFieldSchema.Mode.NULLABLE)
            .setName("test_numeric_one")
            .build();
    TableFieldSchema TEST_NUMERIC_REPEATED =
        TableFieldSchema.newBuilder()
            .setType(TableFieldSchema.Type.NUMERIC)
            .setMode(TableFieldSchema.Mode.REPEATED)
            .setName("test_numeric_repeated")
            .build();
    TableSchema tableSchema =
        TableSchema.newBuilder()
            .addFields(0, TEST_NUMERIC_ZERO)
            .addFields(1, TEST_NUMERIC_ONE)
            .addFields(2, TEST_NUMERIC_REPEATED)
            .build();
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(parent.toString(), tableSchema).build()) {
      JSONObject row = new JSONObject();
      row.put(
          "test_numeric_zero",
          BigDecimalByteStringEncoder.encodeToNumericByteString(new BigDecimal("0")));
      row.put(
          "test_numeric_one",
          BigDecimalByteStringEncoder.encodeToNumericByteString(new BigDecimal("1.2")));
      row.put(
          "test_numeric_repeated",
          new JSONArray(
              new byte[][] {
                BigDecimalByteStringEncoder.encodeToNumericByteString(new BigDecimal("0"))
                    .toByteArray(),
                BigDecimalByteStringEncoder.encodeToNumericByteString(new BigDecimal("1.2"))
                    .toByteArray(),
                BigDecimalByteStringEncoder.encodeToNumericByteString(new BigDecimal("-1.2"))
                    .toByteArray(),
                BigDecimalByteStringEncoder.encodeToNumericByteString(
                        new BigDecimal("99999999999999999999999999999.999999999"))
                    .toByteArray(),
                BigDecimalByteStringEncoder.encodeToNumericByteString(
                        new BigDecimal("-99999999999999999999999999999.999999999"))
                    .toByteArray(),
              }));
      JSONArray jsonArr = new JSONArray(new JSONObject[] {row});
      ApiFuture<AppendRowsResponse> response = jsonStreamWriter.append(jsonArr, -1);
      AppendRowsResponse arr = response.get();
      AppendResult ar = arr.getAppendResult();
      boolean ho = ar.hasOffset();
      TableResult result =
          bigquery.listTableData(
              tableInfo.getTableId(), BigQuery.TableDataListOption.startIndex(0L));
      Iterator<FieldValueList> iter = result.getValues().iterator();
      FieldValueList currentRow;
      currentRow = iter.next();
      assertEquals("0", currentRow.get(0).getStringValue());
      assertEquals("1.2", currentRow.get(1).getStringValue());
      assertEquals("0", currentRow.get(2).getRepeatedValue().get(0).getStringValue());
      assertEquals("1.2", currentRow.get(2).getRepeatedValue().get(1).getStringValue());
      assertEquals("-1.2", currentRow.get(2).getRepeatedValue().get(2).getStringValue());
      assertEquals(
          "99999999999999999999999999999.999999999",
          currentRow.get(2).getRepeatedValue().get(3).getStringValue());
      assertEquals(
          "-99999999999999999999999999999.999999999",
          currentRow.get(2).getRepeatedValue().get(4).getStringValue());
    }
  }
}
