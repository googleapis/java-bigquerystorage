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

package com.google.cloud.bigquery.storage.v1beta.samples;

// [START bigquerystorage_v1beta_generated_MetastorePartitionService_BatchUpdateMetastorePartitions_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.storage.v1beta.BatchUpdateMetastorePartitionsRequest;
import com.google.cloud.bigquery.storage.v1beta.BatchUpdateMetastorePartitionsResponse;
import com.google.cloud.bigquery.storage.v1beta.MetastorePartitionServiceClient;
import com.google.cloud.bigquery.storage.v1beta.TableName;
import com.google.cloud.bigquery.storage.v1beta.UpdateMetastorePartitionRequest;
import java.util.ArrayList;

public class AsyncBatchUpdateMetastorePartitions {

  public static void main(String[] args) throws Exception {
    asyncBatchUpdateMetastorePartitions();
  }

  public static void asyncBatchUpdateMetastorePartitions() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (MetastorePartitionServiceClient metastorePartitionServiceClient =
        MetastorePartitionServiceClient.create()) {
      BatchUpdateMetastorePartitionsRequest request =
          BatchUpdateMetastorePartitionsRequest.newBuilder()
              .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
              .addAllRequests(new ArrayList<UpdateMetastorePartitionRequest>())
              .setTraceId("traceId-1067401920")
              .build();
      ApiFuture<BatchUpdateMetastorePartitionsResponse> future =
          metastorePartitionServiceClient
              .batchUpdateMetastorePartitionsCallable()
              .futureCall(request);
      // Do something.
      BatchUpdateMetastorePartitionsResponse response = future.get();
    }
  }
}
// [END bigquerystorage_v1beta_generated_MetastorePartitionService_BatchUpdateMetastorePartitions_async]
