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

package com.google.cloud.bigquery.storage.v1alpha.samples;

// [START bigquerystorage_v1alpha_generated_MetastorePartitionService_BatchDeleteMetastorePartitions_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.bigquery.storage.v1alpha.BatchDeleteMetastorePartitionsRequest;
import com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionServiceClient;
import com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionValues;
import com.google.cloud.bigquery.storage.v1alpha.TableName;
import com.google.protobuf.Empty;
import java.util.ArrayList;

public class AsyncBatchDeleteMetastorePartitions {

  public static void main(String[] args) throws Exception {
    asyncBatchDeleteMetastorePartitions();
  }

  public static void asyncBatchDeleteMetastorePartitions() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (MetastorePartitionServiceClient metastorePartitionServiceClient =
        MetastorePartitionServiceClient.create()) {
      BatchDeleteMetastorePartitionsRequest request =
          BatchDeleteMetastorePartitionsRequest.newBuilder()
              .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
              .addAllPartitionValues(new ArrayList<MetastorePartitionValues>())
              .setTraceId("traceId-1067401920")
              .build();
      ApiFuture<Empty> future =
          metastorePartitionServiceClient
              .batchDeleteMetastorePartitionsCallable()
              .futureCall(request);
      // Do something.
      future.get();
    }
  }
}
// [END bigquerystorage_v1alpha_generated_MetastorePartitionService_BatchDeleteMetastorePartitions_async]
