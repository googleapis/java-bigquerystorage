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

package com.google.cloud.bigquery.storage.v1.it;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.api.core.ApiFuture;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.test.Test.ComplicateType;
import com.google.cloud.bigquery.storage.test.Test.FooTimestampType;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.test.Test.InnerType;
import com.google.cloud.bigquery.storage.test.Test.UpdatedFooType;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsRequest;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsResponse;
import com.google.cloud.bigquery.storage.v1.BigDecimalByteStringEncoder;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.CivilTimeEncoder;
import com.google.cloud.bigquery.storage.v1.ConnectionWorkerPool;
import com.google.cloud.bigquery.storage.v1.CreateWriteStreamRequest;
import com.google.cloud.bigquery.storage.v1.Exceptions.AppendSerializationError;
import com.google.cloud.bigquery.storage.v1.Exceptions.OffsetAlreadyExists;
import com.google.cloud.bigquery.storage.v1.Exceptions.OffsetOutOfRange;
import com.google.cloud.bigquery.storage.v1.Exceptions.SchemaMismatchedException;
import com.google.cloud.bigquery.storage.v1.Exceptions.StreamFinalizedException;
import com.google.cloud.bigquery.storage.v1.FinalizeWriteStreamRequest;
import com.google.cloud.bigquery.storage.v1.FinalizeWriteStreamResponse;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.ProtoRows;
import com.google.cloud.bigquery.storage.v1.ProtoSchemaConverter;
import com.google.cloud.bigquery.storage.v1.StreamWriter;
import com.google.cloud.bigquery.storage.v1.TableFieldSchema;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.bigquery.storage.v1.TableSchema;
import com.google.cloud.bigquery.storage.v1.WriteStream;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import io.grpc.Status;
import io.grpc.Status.Code;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

/** Integration tests for BigQuery Write API. */
public class ITBigQueryWriteNonQuotaRetryTest {
  private static final Logger LOG =
      Logger.getLogger(ITBigQueryWriteNonQuotaRetryTest.class.getName());
  private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();
  private static final String TABLE = "testtable";
  private static final String TABLE2 = "complicatedtable";
  private static final String DESCRIPTION = "BigQuery Write Java manual client test dataset";
  private static final String NON_QUOTA_RETRY_PROJECT_ID = "bq-write-api-java-retry-test";

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
                        Field.newBuilder("foo", LegacySQLTypeName.STRING)
                            .setMode(Field.Mode.NULLABLE)
                            .build())))
            .build();
    Field.Builder innerTypeFieldBuilder =
        Field.newBuilder(
            "inner_type",
            LegacySQLTypeName.RECORD,
            Field.newBuilder("value", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.REPEATED)
                .build());
    bigquery.create(tableInfo);
    tableId =
        String.format(
            "projects/%s/datasets/%s/tables/%s",
            ServiceOptions.getDefaultProjectId(), DATASET, TABLE);
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
  public void testJsonStreamWriterCommittedStreamWithNonQuotaRetry()
      throws IOException, InterruptedException, ExecutionException,
          DescriptorValidationException {
    RetrySettings retrySettings =
        RetrySettings.newBuilder()
            .setInitialRetryDelay(Duration.ofMillis(500))
            .setRetryDelayMultiplier(1.1)
            .setMaxAttempts(5)
            .setMaxRetryDelay(Duration.ofMinutes(1))
            .build();
    String tableName = "CommittedRetry";
    TableId tableId = TableId.of(DATASET, tableName);
    Field col1 = Field.newBuilder("col1", StandardSQLTypeName.STRING).build();
    Schema schema = Schema.of(col1);
    TableInfo tableInfo = TableInfo.newBuilder(tableId, StandardTableDefinition.of(schema)).build();
    bigquery.create(tableInfo);
    TableName parent = TableName.of(NON_QUOTA_RETRY_PROJECT_ID, DATASET, tableName);

    WriteStream writeStream =
        client.createWriteStream(
            CreateWriteStreamRequest.newBuilder()
                .setParent(parent.toString())
                .setWriteStream(
                    WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build())
                .build());
    int totalRequest = 901;
    int rowBatch = 1;
    ArrayList<ApiFuture<AppendRowsResponse>> allResponses =
        new ArrayList<ApiFuture<AppendRowsResponse>>(totalRequest);
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema())
            .setRetrySettings(retrySettings)
            .build()) {
      for (int k = 0; k < totalRequest; k++) {
        JSONObject row = new JSONObject();
        row.put("col1", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        JSONArray jsonArr = new JSONArray();
        // 3MB batch.
        for (int j = 0; j < rowBatch; j++) {
          jsonArr.put(row);
        }
        LOG.info("Appending: " + k + "/" + totalRequest);
        allResponses.add(jsonStreamWriter.append(jsonArr, k * rowBatch));
      }
      LOG.info("Waiting for all responses to come back");
      for (int i = 0; i < totalRequest; i++) {
        LOG.info("Waiting for request " + i);
        try {
          Assert.assertEquals(
              allResponses.get(i).get().getAppendResult().getOffset().getValue(), i * rowBatch);
        } catch (ExecutionException ex) {
          Assert.fail("Unexpected error " + ex);
        }
      }
    }
  }

  @Test
  public void testJsonStreamWriterDefaultStreamWithNonQuotaRetry()
      throws IOException, InterruptedException, ExecutionException,
          DescriptorValidationException {
    RetrySettings retrySettings =
        RetrySettings.newBuilder()
            .setInitialRetryDelay(Duration.ofMillis(500))
            .setRetryDelayMultiplier(1.1)
            .setMaxAttempts(5)
            .setMaxRetryDelay(Duration.ofMinutes(1))
            .build();
    String tableName = "JsonTableDefaultStream";
    TableFieldSchema TEST_STRING =
        TableFieldSchema.newBuilder()
            .setType(TableFieldSchema.Type.STRING)
            .setMode(TableFieldSchema.Mode.NULLABLE)
            .setName("test_str")
            .build();
    TableSchema tableSchema = TableSchema.newBuilder().addFields(0, TEST_STRING).build();
    TableInfo tableInfo =
        TableInfo.newBuilder(
                TableId.of(DATASET, tableName),
                StandardTableDefinition.of(
                    Schema.of(
                        Field.newBuilder(
                                "test_str", StandardSQLTypeName.STRING)
                            .build())))
            .build();

    bigquery.create(tableInfo);
    TableName parent = TableName.of(NON_QUOTA_RETRY_PROJECT_ID, DATASET, tableName);

    int totalRequest = 901;
    int rowBatch = 1;
    ArrayList<ApiFuture<AppendRowsResponse>> allResponses =
        new ArrayList<ApiFuture<AppendRowsResponse>>(totalRequest);
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(parent.toString(), tableSchema)
            .setIgnoreUnknownFields(true)
            .setRetrySettings(retrySettings)
            .build()) {
      for (int k = 0; k < totalRequest; k++) {
        JSONObject row = new JSONObject();
        row.put("test_str", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        JSONArray jsonArr = new JSONArray();
        // 3MB batch.
        for (int j = 0; j < rowBatch; j++) {
          jsonArr.put(row);
        }
        LOG.info("Appending: " + k + "/" + totalRequest);
        allResponses.add(jsonStreamWriter.append(jsonArr));
      }
      LOG.info("Waiting for all responses to come back");
      for (int i = 0; i < totalRequest; i++) {
        LOG.info("Waiting for request " + i);
        try {
          assertFalse(allResponses.get(i).get().hasError());
        } catch (Exception ex) {
          Assert.fail("Unexpected error " + ex);
        }
      }
    }
  }
}
