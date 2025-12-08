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

// [START bigquerystorage_v1_generated_BigQueryRead_CreateReadSession_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.storage.v1.BaseBigQueryReadClient;
import com.google.cloud.bigquery.storage.v1.CreateReadSessionRequest;
import com.google.cloud.bigquery.storage.v1.ProjectName;
import com.google.cloud.bigquery.storage.v1.ReadSession;

public class AsyncCreateReadSession {

  public static void main(String[] args) throws Exception {
    asyncCreateReadSession();
  }

  public static void asyncCreateReadSession() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (BaseBigQueryReadClient baseBigQueryReadClient = BaseBigQueryReadClient.create()) {
      CreateReadSessionRequest request =
          CreateReadSessionRequest.newBuilder()
              .setParent(ProjectName.of("[PROJECT]").toString())
              .setReadSession(ReadSession.newBuilder().build())
              .setMaxStreamCount(940837515)
              .setPreferredMinStreamCount(-1905507237)
              .build();
      ApiFuture<ReadSession> future =
          baseBigQueryReadClient.createReadSessionCallable().futureCall(request);
      // Do something.
      ReadSession response = future.get();
    }
  }
}
// [END bigquerystorage_v1_generated_BigQueryRead_CreateReadSession_async]
