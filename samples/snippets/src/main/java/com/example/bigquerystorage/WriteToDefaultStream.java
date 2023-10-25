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
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.v1.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteSettings;
import com.google.cloud.bigquery.storage.v1.Exceptions;
import com.google.cloud.bigquery.storage.v1.Exceptions.AppendSerializationError;
import com.google.cloud.bigquery.storage.v1.Exceptions.StorageException;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import io.grpc.Status;
import io.grpc.Status.Code;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;
import org.json.JSONArray;
import org.json.JSONObject;

public class WriteToDefaultStream {

  public static void runWriteToDefaultStream()
      throws DescriptorValidationException, InterruptedException, IOException {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "MY_PROJECT_ID";
    String datasetName = "MY_DATASET_NAME";
    String tableName = "MY_TABLE_NAME";
    writeToDefaultStream(projectId, datasetName, tableName);
  }

  private static ByteString buildByteString() {
    byte[] bytes = new byte[] {1, 2, 3, 4, 5};
    return ByteString.copyFrom(bytes);
  }

  // Create a JSON object that is compatible with the table schema.
  private static JSONObject buildRecord(int i, int j) {
    JSONObject record = new JSONObject();
    StringBuilder sbSuffix = new StringBuilder();
    for (int k = 0; k < j; k++) {
      sbSuffix.append(k);
    }
    record.put("test_string", String.format("record %03d-%03d %s", i, j, sbSuffix.toString()));
    ByteString byteString = buildByteString();
    record.put("test_bytes", byteString);
    return record;
  }

  public static void writeToDefaultStream(String projectId, String datasetName, String tableName)
      throws DescriptorValidationException, InterruptedException, IOException {
    TableName parentTable = TableName.of(projectId, datasetName, tableName);

    DataWriter writer = new DataWriter();
    // One time initialization for the worker.
    writer.initialize(parentTable);

    // Write two batches of fake data to the stream, each with 10 JSON records.  Data may be
    // batched up to the maximum request size:
    // https://cloud.google.com/bigquery/quotas#write-api-limits
    for (int i = 0; i < 2; i++) {
      JSONArray jsonArr = new JSONArray();
      for (int j = 0; j < 10; j++) {
        JSONObject record = buildRecord(i, j);
        jsonArr.put(record);
      }

      writer.append(new AppendContext(jsonArr, 0));
    }

    // Final cleanup for the stream during worker teardown.
    writer.cleanup();
    verifyExpectedRowCount(parentTable, 12);
    System.out.println("Appended records successfully.");
  }

  private static void verifyExpectedRowCount(TableName parentTable, int expectedRowCount)
      throws InterruptedException {
    String queryRowCount =
        "SELECT COUNT(*) FROM `"
            + parentTable.getProject()
            + "."
            + parentTable.getDataset()
            + "."
            + parentTable.getTable()
            + "`";
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(queryRowCount).build();
    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
    TableResult results = bigquery.query(queryConfig);
    int countRowsActual =
        Integer.parseInt(results.getValues().iterator().next().get("f0_").getStringValue());
    if (countRowsActual != expectedRowCount) {
      throw new RuntimeException(
          "Unexpected row count. Expected: " + expectedRowCount + ". Actual: " + countRowsActual);
    }
  }

  private static class AppendContext {

    JSONArray data;
    int retryCount = 0;

    AppendContext(JSONArray data, int retryCount) {
      this.data = data;
      this.retryCount = retryCount;
    }
  }

  private static class DataWriter {

    private static final int MAX_RETRY_COUNT = 3;
    private static final int MAX_RECREATE_COUNT = 3;
    private static final ImmutableList<Code> RETRIABLE_ERROR_CODES =
        ImmutableList.of(
            Code.INTERNAL,
            Code.ABORTED,
            Code.CANCELLED,
            Code.FAILED_PRECONDITION,
            Code.DEADLINE_EXCEEDED,
            Code.UNAVAILABLE);

    // Track the number of in-flight requests to wait for all responses before shutting down.
    private final Phaser inflightRequestCount = new Phaser(1);
    private final Object lock = new Object();
    private JsonStreamWriter streamWriter;

    @GuardedBy("lock")
    private RuntimeException error = null;

    private AtomicInteger recreateCount = new AtomicInteger(0);

    public void initialize(TableName parentTable)
        throws DescriptorValidationException, IOException, InterruptedException {
      // Use the JSON stream writer to send records in JSON format. Specify the table name to write
      // to the default stream.
      // For more information about JsonStreamWriter, see:
      // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1/JsonStreamWriter.html
      streamWriter =
          JsonStreamWriter.newBuilder(parentTable.toString(), BigQueryWriteClient.create())
              .setExecutorProvider(
                  FixedExecutorProvider.create(Executors.newScheduledThreadPool(100)))
              .setChannelProvider(
                  BigQueryWriteSettings.defaultGrpcTransportProviderBuilder()
                      .setKeepAliveTime(org.threeten.bp.Duration.ofMinutes(1))
                      .setKeepAliveTimeout(org.threeten.bp.Duration.ofMinutes(1))
                      .setKeepAliveWithoutCalls(true)
                      .setChannelsPerCpu(2)
                      .build())
              .setEnableConnectionPool(true)
              // If value is missing in json and there is a default value configured on bigquery
              // column, apply the default value to the missing value field.
              .setDefaultMissingValueInterpretation(
                  AppendRowsRequest.MissingValueInterpretation.DEFAULT_VALUE)
              .build();
    }

    public void append(AppendContext appendContext)
        throws DescriptorValidationException, IOException, InterruptedException {
      synchronized (this.lock) {
        if (!streamWriter.isUserClosed()
            && streamWriter.isClosed()
            && recreateCount.getAndIncrement() < MAX_RECREATE_COUNT) {
          streamWriter =
              JsonStreamWriter.newBuilder(
                      streamWriter.getStreamName(), BigQueryWriteClient.create())
                  .build();
          this.error = null;
        }
        // If earlier appends have failed, we need to reset before continuing.
        if (this.error != null) {
          throw this.error;
        }
      }
      // Append asynchronously for increased throughput.
      ApiFuture<AppendRowsResponse> future = streamWriter.append(appendContext.data);
      ApiFutures.addCallback(
          future, new AppendCompleteCallback(this, appendContext), MoreExecutors.directExecutor());

      // Increase the count of in-flight requests.
      inflightRequestCount.register();
    }

    public void cleanup() {
      // Wait for all in-flight requests to complete.
      inflightRequestCount.arriveAndAwaitAdvance();

      // Close the connection to the server.
      streamWriter.close();

      // Verify that no error occurred in the stream.
      synchronized (this.lock) {
        if (this.error != null) {
          throw this.error;
        }
      }
    }

    static class AppendCompleteCallback implements ApiFutureCallback<AppendRowsResponse> {

      private final DataWriter parent;
      private final AppendContext appendContext;

      public AppendCompleteCallback(DataWriter parent, AppendContext appendContext) {
        this.parent = parent;
        this.appendContext = appendContext;
      }

      public void onSuccess(AppendRowsResponse response) {
        System.out.format("Append success\n");
        this.parent.recreateCount.set(0);
        done();
      }

      public void onFailure(Throwable throwable) {
        // If the wrapped exception is a StatusRuntimeException, check the state of the operation.
        // If the state is INTERNAL, CANCELLED, or ABORTED, you can retry. For more information,
        // see: https://grpc.github.io/grpc-java/javadoc/io/grpc/StatusRuntimeException.html
        Status status = Status.fromThrowable(throwable);
        if (appendContext.retryCount < MAX_RETRY_COUNT
            && RETRIABLE_ERROR_CODES.contains(status.getCode())) {
          appendContext.retryCount++;
          try {
            // Since default stream appends are not ordered, we can simply retry the appends.
            // Retrying with exclusive streams requires more careful consideration.
            this.parent.append(appendContext);
            // Mark the existing attempt as done since it's being retried.
            done();
            return;
          } catch (Exception e) {
            // Fall through to return error.
            System.out.format("Failed to retry append: %s\n", e);
          }
        }

        if (throwable instanceof AppendSerializationError) {
          AppendSerializationError ase = (AppendSerializationError) throwable;
          Map<Integer, String> rowIndexToErrorMessage = ase.getRowIndexToErrorMessage();
          if (rowIndexToErrorMessage.size() > 0) {
            // Omit the faulty rows
            JSONArray dataNew = new JSONArray();
            for (int i = 0; i < appendContext.data.length(); i++) {
              if (!rowIndexToErrorMessage.containsKey(i)) {
                dataNew.put(appendContext.data.get(i));
              } else {
                // process faulty rows by placing them on a dead-letter-queue, for instance
              }
            }

            // Retry the remaining valid rows, but using a separate thread to
            // avoid potentially blocking while we are in a callback.
            if (dataNew.length() > 0) {
              try {
                this.parent.append(new AppendContext(dataNew, 0));
              } catch (DescriptorValidationException e) {
                throw new RuntimeException(e);
              } catch (IOException e) {
                throw new RuntimeException(e);
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
            }
            // Mark the existing attempt as done since we got a response for it
            done();
            return;
          }
        }

        synchronized (this.parent.lock) {
          if (this.parent.error == null) {
            StorageException storageException = Exceptions.toStorageException(throwable);
            this.parent.error =
                (storageException != null) ? storageException : new RuntimeException(throwable);
          }
        }
        done();
      }

      private void done() {
        // Reduce the count of in-flight requests.
        this.parent.inflightRequestCount.arriveAndDeregister();
      }
    }
  }
}
// [END bigquerystorage_jsonstreamwriter_default]
