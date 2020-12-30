package com.example.bigquerystorage;

import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.storage.v1beta2.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class WritePendingStream {

  public static void runWritePendingStream() {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "MY_PROJECT_ID";
    String datasetName = "MY_DATASET_NAME";
    String tableName = "MY_TABLE_NAME";

    writePendingStream(projectId, datasetName, tableName);
  }

  public static void writePendingStream(String projectId, String datasetName, String tableName) {

    try (BigQueryWriteClient client = BigQueryWriteClient.create()) {

      WriteStream stream = WriteStream.newBuilder().setType(WriteStream.Type.PENDING).build();

      TableName parent = TableName.of(projectId, datasetName, tableName);

      CreateWriteStreamRequest createWriteStreamRequest =
          CreateWriteStreamRequest.newBuilder()
              .setParent(parent.toString())
              .setWriteStream(stream)
              .build();
      WriteStream writeStream = client.createWriteStream(createWriteStreamRequest);

      try (JsonStreamWriter writer =
          JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema(), client)
              .build()) {

        for (int i = 0; i < 10; i++) {
          JSONObject record = new JSONObject();
          record.put("col1", String.format("batch-record %03d", i));
          JSONArray jsonArr = new JSONArray();
          jsonArr.put(record);

          ApiFuture<AppendRowsResponse> future = writer.append(jsonArr, false);
          AppendRowsResponse response = future.get();
        }
        FinalizeWriteStreamResponse finalizeResponse =
            client.finalizeWriteStream(writeStream.getName());
        System.out.println("Rows written: " + finalizeResponse.getRowCount());
      }

      // Commit the streams
      BatchCommitWriteStreamsRequest commitRequest =
          BatchCommitWriteStreamsRequest.newBuilder()
              .setParent(parent.toString())
              .addWriteStreams(writeStream.getName())
              .build();
      BatchCommitWriteStreamsResponse commitResponse =
          client.batchCommitWriteStreams(commitRequest);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
