/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/arrow.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.bigquery.storage.v1;

public interface ArrowRecordBatchOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.ArrowRecordBatch)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * IPC-serialized Arrow RecordBatch.
   * </pre>
   *
   * <code>bytes serialized_record_batch = 1;</code>
   *
   * @return The serializedRecordBatch.
   */
  com.google.protobuf.ByteString getSerializedRecordBatch();

  /**
   *
   *
   * <pre>
   * [Deprecated] The count of rows in `serialized_record_batch`.
   * Please use the format-independent ReadRowsResponse.row_count instead.
   * </pre>
   *
   * <code>int64 row_count = 2 [deprecated = true];</code>
   *
   * @deprecated google.cloud.bigquery.storage.v1.ArrowRecordBatch.row_count is deprecated. See
   *     google/cloud/bigquery/storage/v1/arrow.proto;l=43
   * @return The rowCount.
   */
  @java.lang.Deprecated
  long getRowCount();
}
