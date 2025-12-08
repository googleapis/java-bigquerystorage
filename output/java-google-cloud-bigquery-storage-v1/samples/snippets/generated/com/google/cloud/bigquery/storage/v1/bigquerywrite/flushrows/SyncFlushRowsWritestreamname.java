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

// [START bigquerystorage_v1_generated_BigQueryWrite_FlushRows_Writestreamname_sync]
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.FlushRowsResponse;
import com.google.cloud.bigquery.storage.v1.WriteStreamName;

public class SyncFlushRowsWritestreamname {

  public static void main(String[] args) throws Exception {
    syncFlushRowsWritestreamname();
  }

  public static void syncFlushRowsWritestreamname() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (BigQueryWriteClient bigQueryWriteClient = BigQueryWriteClient.create()) {
      WriteStreamName writeStream =
          WriteStreamName.of("[PROJECT]", "[DATASET]", "[TABLE]", "[STREAM]");
      FlushRowsResponse response = bigQueryWriteClient.flushRows(writeStream);
    }
  }
}
// [END bigquerystorage_v1_generated_BigQueryWrite_FlushRows_Writestreamname_sync]
