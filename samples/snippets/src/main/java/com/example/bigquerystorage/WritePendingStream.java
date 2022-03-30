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

// [START bigquerystorage_jsonstreamwriter_pending]
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsRequest;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.CreateWriteStreamRequest;
import com.google.cloud.bigquery.storage.v1.FinalizeWriteStreamResponse;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.StorageError;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.bigquery.storage.v1.WriteStream;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

import org.json.JSONArray;
import org.json.JSONObject;

public class WritePendingStream {

  private BigQueryWriteClient client;
  private JsonStreamWriter streamWriter;
  private WriteStream writeStream;
  private final Phaser inflightRequestPhaser = new Phaser(1);

  public static void runWritePendingStream()
      throws DescriptorValidationException, InterruptedException, IOException {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "MY_PROJECT_ID";
    String datasetName = "MY_DATASET_NAME";
    String tableName = "MY_TABLE_NAME";

    writePendingStream(projectId, datasetName, tableName);
  }

  public static void writePendingStream(String projectId, String datasetName, String tableName)
      throws DescriptorValidationException, InterruptedException, IOException {
    BigQueryWriteClient client = BigQueryWriteClient.create();
    TableName parentTable = TableName.of(projectId, datasetName, tableName);

    WritePendingStream writer = new WritePendingStream();
    // One time initialization.
    writer.initialize(parentTable, client);

    try {
      // Write two batches to the stream, each with 10 JSON records.
      for (int i = 0; i < 2; i++) {
        // Create a JSON object that is compatible with the table schema.
        JSONArray jsonArr = new JSONArray();
        for (int j = 0; j < 10; j++) {
          JSONObject record = new JSONObject();
          record.put("col1", String.format("batch-record %03d-%03d", i, j));
          jsonArr.put(record);
        }
        writer.append(jsonArr);
      }
    } catch (ExecutionException e) {
      // If the wrapped exception is a StatusRuntimeException, check the state of the operation.
      // If the state is INTERNAL, CANCELLED, or ABORTED, you can retry. For more information, see:
      // https://grpc.github.io/grpc-java/javadoc/io/grpc/StatusRuntimeException.html
      System.out.println("Failed to append records. \n" + e);
    }
    System.out.println("Appended records successfully.");

    // Final cleanup for the stream.
    writer.cleanup();

    // Once all streams are done, commit all of them in one request. This example only has the one stream.
    BatchCommitWriteStreamsRequest commitRequest =
            BatchCommitWriteStreamsRequest.newBuilder()
                    .setParent(parentTable.toString())
                    .addWriteStreams(writer.getStreamName())
                    .build();
    BatchCommitWriteStreamsResponse commitResponse =
            client.batchCommitWriteStreams(commitRequest);
    // If the response does not have a commit time, it means the commit operation failed.
    if (commitResponse.hasCommitTime() == false) {
      for (StorageError err : commitResponse.getStreamErrorsList()) {
        System.out.println(err.getErrorMessage());
      }
      throw new RuntimeException("Error committing the streams");
    }
    System.out.println("Appended and committed records successfully.");
  }

  public void initialize(TableName parentTable, BigQueryWriteClient client) throws IOException, DescriptorValidationException, InterruptedException {
    this.client = client;

    // Initialize a write stream for the specified table.
    // For more information on WriteStream.Type, see:
    // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1/WriteStream.Type.html
    WriteStream stream = WriteStream.newBuilder().setType(WriteStream.Type.PENDING).build();

    CreateWriteStreamRequest createWriteStreamRequest =
            CreateWriteStreamRequest.newBuilder()
                    .setParent(parentTable.toString())
                    .setWriteStream(stream)
                    .build();
    this.writeStream = client.createWriteStream(createWriteStreamRequest);

    // Use the JSON stream writer to send records in JSON format.
    // For more information about JsonStreamWriter, see:
    // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1beta2/JsonStreamWriter.html
    this.streamWriter = JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema()).build();
  }

  public void append(JSONArray data) throws DescriptorValidationException, IOException, ExecutionException {
    ApiFuture<AppendRowsResponse> future = streamWriter.append(data);
    this.inflightRequestPhaser.register();
    ApiFutures.addCallback(
            future, new AppendCompleteCallback(this), MoreExecutors.directExecutor());
  }

  public void cleanup() {
    // Wait for all in-flight requests to complete.
    this.inflightRequestPhaser.arriveAndAwaitAdvance();
    FinalizeWriteStreamResponse finalizeResponse =
            client.finalizeWriteStream(writeStream.getName());
    System.out.println("Rows written: " + finalizeResponse.getRowCount());
  }

  public String getStreamName() {
    return this.writeStream.getName();
  }

  static class AppendCompleteCallback implements ApiFutureCallback<AppendRowsResponse> {
    private final WritePendingStream parent;

    AppendCompleteCallback(WritePendingStream parent) {
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
// [END bigquerystorage_jsonstreamwriter_pending]
