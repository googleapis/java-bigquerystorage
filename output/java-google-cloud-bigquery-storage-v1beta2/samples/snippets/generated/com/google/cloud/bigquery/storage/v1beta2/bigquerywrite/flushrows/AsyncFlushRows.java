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

package com.google.cloud.bigquery.storage.v1beta2.samples;

// [START bigquerystorage_v1beta2_generated_BigQueryWrite_FlushRows_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1beta2.FlushRowsRequest;
import com.google.cloud.bigquery.storage.v1beta2.FlushRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.WriteStreamName;
import com.google.protobuf.Int64Value;

public class AsyncFlushRows {

  public static void main(String[] args) throws Exception {
    asyncFlushRows();
  }

  public static void asyncFlushRows() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (BigQueryWriteClient bigQueryWriteClient = BigQueryWriteClient.create()) {
      FlushRowsRequest request =
          FlushRowsRequest.newBuilder()
              .setWriteStream(
                  WriteStreamName.of("[PROJECT]", "[DATASET]", "[TABLE]", "[STREAM]").toString())
              .setOffset(Int64Value.newBuilder().build())
              .build();
      ApiFuture<FlushRowsResponse> future =
          bigQueryWriteClient.flushRowsCallable().futureCall(request);
      // Do something.
      FlushRowsResponse response = future.get();
    }
  }
}
// [END bigquerystorage_v1beta2_generated_BigQueryWrite_FlushRows_async]
