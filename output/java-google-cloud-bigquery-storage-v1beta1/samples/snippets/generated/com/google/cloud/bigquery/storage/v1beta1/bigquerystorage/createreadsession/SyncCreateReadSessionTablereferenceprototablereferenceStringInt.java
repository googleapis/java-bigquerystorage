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

package com.google.cloud.bigquery.storage.v1beta1.samples;

// [START bigquerystorage_v1beta1_generated_BigQueryStorage_CreateReadSession_TablereferenceprototablereferenceStringInt_sync]
import com.google.cloud.bigquery.storage.v1beta1.BaseBigQueryStorageClient;
import com.google.cloud.bigquery.storage.v1beta1.ProjectName;
import com.google.cloud.bigquery.storage.v1beta1.Storage;
import com.google.cloud.bigquery.storage.v1beta1.TableReferenceProto;

public class SyncCreateReadSessionTablereferenceprototablereferenceStringInt {

  public static void main(String[] args) throws Exception {
    syncCreateReadSessionTablereferenceprototablereferenceStringInt();
  }

  public static void syncCreateReadSessionTablereferenceprototablereferenceStringInt()
      throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (BaseBigQueryStorageClient baseBigQueryStorageClient = BaseBigQueryStorageClient.create()) {
      TableReferenceProto.TableReference tableReference =
          TableReferenceProto.TableReference.newBuilder().build();
      String parent = ProjectName.of("[PROJECT]").toString();
      int requestedStreams = 1017221410;
      Storage.ReadSession response =
          baseBigQueryStorageClient.createReadSession(tableReference, parent, requestedStreams);
    }
  }
}
// [END bigquerystorage_v1beta1_generated_BigQueryStorage_CreateReadSession_TablereferenceprototablereferenceStringInt_sync]
