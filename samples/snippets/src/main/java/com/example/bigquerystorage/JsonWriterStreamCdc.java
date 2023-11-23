/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bigquerystorage;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.PrimaryKey;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableConstraints;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.TableFieldSchema;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.bigquery.storage.v1.TableFieldSchema.Mode;
import com.google.cloud.bigquery.storage.v1.TableSchema;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;


public class JsonWriterStreamCdc {

  private static final String CHANGE_TYPE_PSEUDO_COLUMN = "_change_type";

  private static final String CREATE_TABLE_QUERY = "CREATE TABLE `%s.%s` (\n"
      + " Customer_ID INT64 PRIMARY KEY NOT ENFORCED,\n"
      + " Customer_Enrollment_Date DATE,\n"
      + " Customer_Name STRING,\n"
      + " Customer_Address STRING,\n"
      + " Customer_Tier STRING,\n"
      + " Active_Subscriptions JSON)\n"
      + "OPTIONS(max_staleness = INTERVAL 15 MINUTE);";

  public static void main(String[] args) throws Exception {
    if (args.length != 4) {
      System.out.println("Arguments: project, dataset, table, data_file");
      return;
    }

    String projectId = args[0];
    String datasetName = args[1];
    String tableName = args[2];
    String dataFile = args[3];

    createDestinationTable(projectId, datasetName, tableName);
    JSONArray records = getRecordsFromDataFile(dataFile);
    writeToDefaultStream(projectId, datasetName, tableName, records);
  }

  public static void createDestinationTable(
          String projectId, String datasetName, String tableName) {
    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(
            String.format(CREATE_TABLE_QUERY, datasetName, tableName)).build();
    try {
      bigquery.query(queryConfig);
    } catch (BigQueryException | InterruptedException e) {
      System.out.println("Query did not run \n" + e.toString());
    }
  }

  // writeToDefaultStream: Writes records from the source file to the destination table.
  public static void writeToDefaultStream(
          String projectId, String datasetName, String tableName, JSONArray data)
          throws DescriptorValidationException, InterruptedException, IOException {
    // Build the table schema with an additional _change_type column.
    TableSchema tableSchema = TableSchema.newBuilder()
        .addFields(
            TableFieldSchema.newBuilder()
                .setName("Customer_ID")
                .setType(TableFieldSchema.Type.INT64)
                .setMode(Mode.NULLABLE)
                .build())
        .addFields(
            TableFieldSchema.newBuilder()
                .setName("Customer_Enrollment_Date")
                .setType(TableFieldSchema.Type.DATE)
                .setMode(Mode.NULLABLE)
                .build())
        .addFields(
            TableFieldSchema.newBuilder()
                .setName("Customer_Name")
                .setType(TableFieldSchema.Type.STRING)
                .setMode(Mode.NULLABLE)
                .build())
        .addFields(
            TableFieldSchema.newBuilder()
                .setName("Customer_Address")
                .setType(TableFieldSchema.Type.STRING)
                .setMode(Mode.NULLABLE)
                .build())
        .addFields(
            TableFieldSchema.newBuilder()
                .setName("Customer_Tier")
                .setType(TableFieldSchema.Type.STRING)
                .setMode(Mode.NULLABLE)
                .build())
        .addFields(
            TableFieldSchema.newBuilder()
                .setName("Active_Subscriptions")
                .setType(TableFieldSchema.Type.JSON)
                .setMode(Mode.NULLABLE)
                .build())
        .addFields(
            TableFieldSchema.newBuilder()
                .setName(CHANGE_TYPE_PSEUDO_COLUMN)
                .setType(TableFieldSchema.Type.STRING)
                .setMode(Mode.NULLABLE)
                .build())
        .build();

    // Use the JSON stream writer to send records in JSON format.
    TableName parentTable = TableName.of(projectId, datasetName, tableName);
    try (JsonStreamWriter writer =
                 JsonStreamWriter.newBuilder(parentTable.toString(), tableSchema)
                         .build()) {

      ApiFuture<AppendRowsResponse> future = writer.append(data);
      // The append method is asynchronous. Rather than waiting for the method to complete,
      // which can hurt performance, register a completion callback and continue streaming.
      ApiFutures.addCallback(
          future, new AppendCompleteCallback(), MoreExecutors.directExecutor());
    }
  }

  public static JSONArray getRecordsFromDataFile(String dataFile)
      throws FileNotFoundException, IOException {
    JSONArray result = new JSONArray();

    BufferedReader reader = new BufferedReader(new FileReader(dataFile));
    String line = reader.readLine();
    while (line != null) {
      JSONObject record = new JSONObject(line);
      result.put(record);
      line = reader.readLine();
    }

    return result;
  }

  static class AppendCompleteCallback implements ApiFutureCallback<AppendRowsResponse> {
    private static final Object lock = new Object();
    private static int batchCount = 0;

    public void onSuccess(AppendRowsResponse response) {
      synchronized (lock) {
        if (response.hasError()) {
          System.out.format("Error: %s\n", response.getError());
        } else {
          ++batchCount;
          System.out.format("Wrote batch %d\n", batchCount);
        }
      }
    }

    public void onFailure(Throwable throwable) {
      System.out.format("Error: %s\n", throwable.toString());
    }
  }
}