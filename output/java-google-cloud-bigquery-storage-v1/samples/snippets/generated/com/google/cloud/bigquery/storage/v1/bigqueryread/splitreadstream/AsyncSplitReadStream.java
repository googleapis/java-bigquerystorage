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

// [START bigquerystorage_v1_generated_BigQueryRead_SplitReadStream_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.storage.v1.BaseBigQueryReadClient;
import com.google.cloud.bigquery.storage.v1.ReadStreamName;
import com.google.cloud.bigquery.storage.v1.SplitReadStreamRequest;
import com.google.cloud.bigquery.storage.v1.SplitReadStreamResponse;

public class AsyncSplitReadStream {

  public static void main(String[] args) throws Exception {
    asyncSplitReadStream();
  }

  public static void asyncSplitReadStream() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (BaseBigQueryReadClient baseBigQueryReadClient = BaseBigQueryReadClient.create()) {
      SplitReadStreamRequest request =
          SplitReadStreamRequest.newBuilder()
              .setName(
                  ReadStreamName.of("[PROJECT]", "[LOCATION]", "[SESSION]", "[STREAM]").toString())
              .setFraction(-1653751294)
              .build();
      ApiFuture<SplitReadStreamResponse> future =
          baseBigQueryReadClient.splitReadStreamCallable().futureCall(request);
      // Do something.
      SplitReadStreamResponse response = future.get();
    }
  }
}
// [END bigquerystorage_v1_generated_BigQueryRead_SplitReadStream_async]
