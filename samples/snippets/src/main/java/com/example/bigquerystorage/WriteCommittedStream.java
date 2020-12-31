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

import com.google.api.client.util.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.storage.v1beta2.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONObject;

public class WriteCommittedStream {

  static Status.Code getStatusCode(StatusRuntimeException e) {
    return e.getStatus().getCode();
  }

  // Returns true if the operation should be retried.
  static Boolean isRetryable(ExecutionException e) {
    Throwable cause = e.getCause();
    if (cause instanceof StatusRuntimeException) {
        Status status = ((StatusRuntimeException)cause).getStatus();
	return (status == Status.ABORTED);
    }
    return false;
  }

  public static void runWriteCommittedStream() {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "MY_PROJECT_ID";
    String datasetName = "MY_DATASET_NAME";
    String tableName = "MY_TABLE_NAME";
    writeCommittedStream(projectId, datasetName, tableName);
  }

  public static void writeCommittedStream(String projectId, String datasetName, String tableName) {

    try (BigQueryWriteClient client = BigQueryWriteClient.create()) {

      // Initialize a write stream for the specified table.
      WriteStream stream = WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build();

      TableName parent = TableName.of(projectId, datasetName, tableName);

      CreateWriteStreamRequest createWriteStreamRequest =
          CreateWriteStreamRequest.newBuilder()
              .setParent(parent.toString())
              .setWriteStream(stream)
              .build();
      WriteStream writeStream = client.createWriteStream(createWriteStreamRequest);

      // Use the JSON stream writer to send records in JSON format.
      try (JsonStreamWriter writer =
          JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema(), client)
              .build()) {

        int offsets[] = {0, 1, 2, 3, 4, 5, 5};
        // The last offset is repeated. This will cause an ALREADY_EXISTS error.

        for (int i : offsets) {
          JSONObject record = new JSONObject();
          record.put("col1", String.format("record %03d", i));
          JSONArray jsonArr = new JSONArray();
          jsonArr.put(record);

          ApiFuture<AppendRowsResponse> future = writer.append(jsonArr, i);
          AppendRowsResponse response = future.get();
        }

      } catch (ExecutionException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof StatusRuntimeException) {
          System.out.println(
              "Failed with status = " + getStatusCode((StatusRuntimeException) cause));
        }
        throw ex;
      }

      System.out.println("Appended records successfully.");

    } catch (Exception e) {
      System.out.println("Failed to append records.\n" + e.toString());
    }
  }

  public static void writeToDefaultStream(String projectId, String datasetName, String tableName) {

    TableName parent = TableName.of(projectId, datasetName, tableName);

    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
    Table table = bigquery.getTable(datasetName, tableName);
    Schema schema = table.getDefinition().getSchema();

    try (JsonStreamWriter writer =
        JsonStreamWriter.newBuilder(parent.toString(), schema).createDefaultStream().build()) {

      ExponentialBackOff backoff = new ExponentialBackOff();

      for (int i = 0; i < 10; i++) {
        JSONObject record = new JSONObject();
        record.put("col1", String.format("record %03d", i));
        JSONArray jsonArr = new JSONArray();
        jsonArr.put(record);

	backoff.reset();
	Boolean retry = true;
        while (retry) {
	  try {

            ApiFuture<AppendRowsResponse> future = writer.append(jsonArr);
            AppendRowsResponse response = future.get();
	    retry = false;

	  } catch (ExecutionException ex) {
	    // If the error is retryable, retry the operation with exponential backoff.
	    // Don't retry past the maximum retry interval.
	    long backOffMillis = backoff.nextBackOffMillis();
            if (isRetryable(ex) && backOffMillis != BackOff.STOP) {
	      Thread.sleep(backOffMillis);
	    }
	    else {
              throw ex;
	    }
	  }
	}
      }

      System.out.println("Appended records successfully.");

    } catch (Exception e) {
      System.out.println("Failed to append records.\n" + e.toString());
    }
  }
}
