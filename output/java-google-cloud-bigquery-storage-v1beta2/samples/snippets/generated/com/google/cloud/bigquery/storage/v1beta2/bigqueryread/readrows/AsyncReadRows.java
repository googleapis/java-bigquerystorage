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

// [START bigquerystorage_v1beta2_generated_BigQueryRead_ReadRows_async]
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigquery.storage.v1beta2.BaseBigQueryReadClient;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.ReadStreamName;

public class AsyncReadRows {

  public static void main(String[] args) throws Exception {
    asyncReadRows();
  }

  public static void asyncReadRows() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (BaseBigQueryReadClient baseBigQueryReadClient = BaseBigQueryReadClient.create()) {
      ReadRowsRequest request =
          ReadRowsRequest.newBuilder()
              .setReadStream(
                  ReadStreamName.of("[PROJECT]", "[LOCATION]", "[SESSION]", "[STREAM]").toString())
              .setOffset(-1019779949)
              .build();
      ServerStream<ReadRowsResponse> stream =
          baseBigQueryReadClient.readRowsCallable().call(request);
      for (ReadRowsResponse response : stream) {
        // Do something when a response is received.
      }
    }
  }
}
// [END bigquerystorage_v1beta2_generated_BigQueryRead_ReadRows_async]
