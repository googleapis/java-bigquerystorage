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

// [START bigquerystorage_jsonstreamwriter_buffered]
import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.storage.v1.*;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Int64Value;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

import io.grpc.StatusRuntimeException;
import jdk.nashorn.internal.ir.Block;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.concurrent.GuardedBy;

public class WriteBufferedStream implements AutoCloseable {
  public static class AckHandler {
    public void ack() {}
    public void nack() {}
  };
  private static final Logger log = Logger.getLogger(WriteBufferedStream.class.getName());
  public static class OffsetAckHandler {
    long offset;
    AckHandler ackHandler;
    public OffsetAckHandler(long offset, AckHandler ackHandler) {
      this.offset = offset;
      this.ackHandler = ackHandler;
    }
  };
  // A bounded concurrent queue that contains offsets to flush.
  private BlockingQueue<OffsetAckHandler> ackHandlerQueue;
  // A separate thread to handle actual communication with server.
  private Thread flushThread;

  private String projectId;
  private String datasetName;
  private String tableName;

  private BigQueryWriteClient client;

  WriteStream writeStream;
  private Lock lock;
  @GuardedBy("lock")
  private boolean jobDone = false;
  private Condition hasMessageInAckHandlerQueue;
  void WriteBufferedStream(String projectId, String datasetName, String tableName) {
    this.ackHandlerQueue = new LinkedBlockingDeque<>(10000);
    this.client = BigQueryWriteClient.create();
    this.projectId = projectId;
    this.datasetName = datasetName;
    this.tableName = tableName;
  }

  public static void runWriteBufferedStream()
      throws DescriptorValidationException, InterruptedException, IOException {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "MY_PROJECT_ID";
    String datasetName = "MY_DATASET_NAME";
    String tableName = "MY_TABLE_NAME";

    WriteBufferedStream sample = new WriteBufferedStream(projectId, datasetName, tableName);
    sample.writeBufferedStream();
  }

  public void writeBufferedStream()
      throws DescriptorValidationException, InterruptedException, IOException {
      createWriteStream();
      this.flushThread =
          new Thread(
              new Runnable() {
                @Override
                public void run() {
                  flushLoop();
                }
              });
      this.flushThread.start();
      try {
        // Use the JSON stream writer to send records in JSON format.
        // For more information about JsonStreamWriter, see:
        // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1beta2/JsonStreamWriter.html
        JsonStreamWriter writer = createJsonWriter();
        // Write three batches (files) to the stream, each with 10 JSON records.
        for (int file = 0; file < 3; file++) {
          long flushOffset = 0;
          AckHandler ackHandler = new AckHandler();
          // There are 3 appends in each file.
          for (int i = 0; i < 3; i++) {
            try {
              JSONArray jsonArr = getMessage();
              // We could retry with offset setting but that would make the code more complicate.
              ApiFuture<AppendRowsResponse> future = writer.append(jsonArr);
              AppendRowsResponse response = future.get();
              flushOffset = response.getAppendResult().getOffset().getValue() + jsonArr.length() - 1;
            } catch (StatusRuntimeException ex) {
              ackHandler.nack();
            }
          }
          ackHandlerQueue.put(new OffsetAckHandler(flushOffset, ackHandler));
          hasMessageInAckHandlerQueue.signal();
        }
      this.lock.lock();
      try {
        jobDone = true;
      } finally {
        this.lock.unlock();
      }
      flushThread.join();
      // Finalize the stream after use.
      FinalizeWriteStreamRequest finalizeWriteStreamRequest =
          FinalizeWriteStreamRequest.newBuilder().setName(writeStream.getName()).build();
      client.finalizeWriteStream(finalizeWriteStreamRequest);
      System.out.println("Job finalized.");
    } catch (ExecutionException e) {
      System.out.println(e);
    }
  }

  @Override
  public void close() {
    if (client != null) {
      client.Close();
    }
  }

  private JSONArray getMessage() {
    JSONArray jsonArr = new JSONArray();
    for (int j = 0; j < 10; j++) {
      // Create a JSON object that is compatible with the table schema.
      JSONObject record = new JSONObject();
      record.put("col1", String.format("buffered-record %03d", i));
      jsonArr.put(record);
    }
    return jsonArr;
  }

  private void CreateWriteStream() {
    // Initialize a write stream for the specified table.
    // For more information on WriteStream.Type, see:
    // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1/WriteStream.Type.html
    WriteStream stream = WriteStream.newBuilder().setType(WriteStream.Type.BUFFERED).build();
    TableName parentTable = TableName.of(projectId, datasetName, tableName);
    CreateWriteStreamRequest createWriteStreamRequest =
        CreateWriteStreamRequest.newBuilder()
            .setParent(parentTable.toString())
            .setWriteStream(stream)
            .build();
    writeStream = client.createWriteStream(createWriteStreamRequest);
  }

  private JsonStreamWriter createJsonWriter() {
    lock.lock();
    try {
      if (writeStream == null) {
        CreateWriteStream();
      }
    } finally {
      this.lock.unlock();
    }
    return JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema()).build();
  }

  private boolean ackHandlerQueueDrained() {
    lock.lock();
    try {
      return this.jobDone && this.ackHandlerQueue.isEmpty();
    } finally {
      this.lock.unlock();
    }
  }

  private void flushLoop() {
    Deque<OffsetAckHandler> localAckHandlerQueue = new LinkedList<OffsetAckHandler>();
    while (!ackHandlerQueueDrained()) {
      try {
        hasMessageInAckHandlerQueue.await(100, TimeUnit.MILLISECONDS);
        while (!this.ackHandlerQueue.isEmpty()) {
          this.ackHandlerQueue.drainTo(localAckHandlerQueue);
        }
      } catch (InterruptedException e) {
        log.warning(
            "Interrupted while waiting for message.");
      }

      if (localAckHandlerQueue.isEmpty()) {
        continue;
      }
      long flushOffset = localAckHandlerQueue.getLast().offset;
      // Flush the buffer.
      // TODO(yiru): Allow setting special deadline on FlushRows, currently it is 120s.
      FlushRowsRequest flushRowsRequest =
          FlushRowsRequest.newBuilder()
              .setWriteStream(writeStream.getName())
              .setOffset(Int64Value.of(flushOffset))
              .build();
      try {
        FlushRowsResponse flushRowsResponse = client.flushRows(flushRowsRequest);
      } catch (Exception ex) {
        System.out.println("FlushRows failed: " + ex.toString());
        // Clean up local queue first.
        while (!localAckHandlerQueue.isEmpty()) {
          OffsetAckHandler ackHandler = ackHandlerQueue.remove();
          ackHandler.nack();
        }
        lock.lock();
        try {
          // If flush fails, we want to nack all the stuff in the flush queue and just
          // recreate a new stream, start from a new stream.
          while (!ackHandlerQueue.isEmpty()) {
            OffsetAckHandler ackHandler = ackHandlerQueue.remove();
            ackHandler.nack();
          }
          if (writeStream == null) {
            CreateWriteStream();
          }
        } finally {
          this.lock.unlock();
        }
      }
    }
    System.out.println("flushLoop end successfully.");
  }
}
// [END bigquerystorage_jsonstreamwriter_buffered]
