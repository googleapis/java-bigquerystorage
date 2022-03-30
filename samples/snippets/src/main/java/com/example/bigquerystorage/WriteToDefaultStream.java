/*
 * Copyright 2020 Google LLC
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

// [START bigquerystorage_jsonstreamwriter_default]
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.bigquery.storage.v1.TableSchema;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

import org.json.JSONArray;
import org.json.JSONObject;

public class WriteToDefaultStream {

  private JsonStreamWriter streamWriter;
  private final Phaser inflightRequestPhaser = new Phaser(1);

  public static void runWriteToDefaultStream()
      throws DescriptorValidationException, InterruptedException, IOException {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "MY_PROJECT_ID";
    String datasetName = "MY_DATASET_NAME";
    String tableName = "MY_TABLE_NAME";
    writeToDefaultStream(projectId, datasetName, tableName);
  }

  public static void writeToDefaultStream(String projectId, String datasetName, String tableName)
      throws DescriptorValidationException, InterruptedException, IOException {
    WriteToDefaultStream writer = new WriteToDefaultStream();
    // One time initialization.
    writer.initialize(projectId, datasetName, tableName);

    try {
      // Write two batches to the stream, each with 10 JSON records. A writer should be used for as
      // many writes as possible. Creating a writer for just one write is an antipattern.
      for (int i = 0; i < 2; i++) {
        // Create a JSON object that is compatible with the table schema.
        JSONArray jsonArr = new JSONArray();
        for (int j = 0; j < 10; j++) {
          JSONObject record = new JSONObject();
          record.put("test_string", String.format("record %03d-%03d", i, j));
          jsonArr.put(record);
        }
        writer.append(jsonArr);
      }
    } catch (ExecutionException e) {
      // If the wrapped exception is a StatusRuntimeException, check the state of the operation.
      // If the state is INTERNAL, CANCELLED, or ABORTED, you can retry. For more information, see:
      // https://grpc.github.io/grpc-java/javadoc/io/grpc/StatusRuntimeException.html
      System.out.println("Failed to append records. \n" + e.toString());
    }
    System.out.println("Appended records successfully.");

    // Final cleanup.
    writer.cleanup();
  }

  public void initialize(String projectId, String datasetName, String tableName) throws DescriptorValidationException, IOException, InterruptedException {
    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
    Table table = bigquery.getTable(datasetName, tableName);
    TableName parentTable = TableName.of(projectId, datasetName, tableName);
    Schema schema = table.getDefinition().getSchema();
    TableSchema tableSchema = BqToBqStorageSchemaConverter.convertTableSchema(schema);

    // Use the JSON stream writer to send records in JSON format.
    // For more information about JsonStreamWriter, see:
    // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1/JsonStreamWriter.html
    this.streamWriter = JsonStreamWriter.newBuilder(parentTable.toString(), tableSchema).build();
  }

  public void append(JSONArray jsonArr) throws DescriptorValidationException, IOException, ExecutionException {
    ApiFuture<AppendRowsResponse> future = this.streamWriter.append(jsonArr);
    this.inflightRequestPhaser.register();
    ApiFutures.addCallback(
            future, new AppendCompleteCallback(this), MoreExecutors.directExecutor());
  }

  public void cleanup() {
    // Wait for all in-flight requests to complete.
    this.inflightRequestPhaser.arriveAndAwaitAdvance();
  }

  class AppendCompleteCallback implements ApiFutureCallback<AppendRowsResponse> {
    private final WriteToDefaultStream parent;

    AppendCompleteCallback(WriteToDefaultStream parent) {
      this.parent = parent;
    }

    public void onSuccess(AppendRowsResponse response) {
      if (response.hasError()) {
        System.out.format("Error: %s\n", response.getError());
      } else {
        System.out.format("Append %d success\n", response.getAppendResult().getOffset().getValue());
      }
      callbackDone();
    }

    public void onFailure(Throwable throwable) {
      System.out.format("Error: %s\n", throwable.toString());
      callbackDone();
    }

    private void callbackDone() {
      this.parent.inflightRequestPhaser.arriveAndDeregister();
    }
  }
}
// [END bigquerystorage_jsonstreamwriter_default]
