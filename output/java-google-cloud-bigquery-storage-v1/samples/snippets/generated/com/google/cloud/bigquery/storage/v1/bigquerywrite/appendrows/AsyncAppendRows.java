/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.bigquery.storage.v1.samples;

// [START bigquerystorage_v1_generated_BigQueryWrite_AppendRows_async]
import com.google.api.gax.rpc.BidiStream;
import com.google.cloud.bigquery.storage.v1.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.WriteStreamName;
import com.google.protobuf.Int64Value;
import java.util.HashMap;

public class AsyncAppendRows {

  public static void main(String[] args) throws Exception {
    asyncAppendRows();
  }

  public static void asyncAppendRows() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (BigQueryWriteClient bigQueryWriteClient = BigQueryWriteClient.create()) {
      BidiStream<AppendRowsRequest, AppendRowsResponse> bidiStream =
          bigQueryWriteClient.appendRowsCallable().call();
      AppendRowsRequest request =
          AppendRowsRequest.newBuilder()
              .setWriteStream(
                  WriteStreamName.of("[PROJECT]", "[DATASET]", "[TABLE]", "[STREAM]").toString())
              .setOffset(Int64Value.newBuilder().build())
              .setTraceId("traceId-1067401920")
              .putAllMissingValueInterpretations(
                  new HashMap<String, AppendRowsRequest.MissingValueInterpretation>())
              .build();
      bidiStream.send(request);
      for (AppendRowsResponse response : bidiStream) {
        // Do something when a response is received.
      }
    }
  }
}
// [END bigquerystorage_v1_generated_BigQueryWrite_AppendRows_async]
