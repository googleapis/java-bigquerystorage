/*
 * Copyright 2020 Google LLC
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
// source: google/cloud/bigquery/storage/v1beta2/storage.proto

package com.google.cloud.bigquery.storage.v1beta2;

public interface FlushRowsRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.FlushRowsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The stream that is the target of the flush operation.
   * </pre>
   *
   * <code>
   * string write_stream = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The writeStream.
   */
  java.lang.String getWriteStream();
  /**
   *
   *
   * <pre>
   * Required. The stream that is the target of the flush operation.
   * </pre>
   *
   * <code>
   * string write_stream = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for writeStream.
   */
  com.google.protobuf.ByteString getWriteStreamBytes();

  /**
   *
   *
   * <pre>
   * Ending offset of the flush operation. Rows before this offset(including
   * this offset) will be flushed.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value offset = 2;</code>
   *
   * @return Whether the offset field is set.
   */
  boolean hasOffset();
  /**
   *
   *
   * <pre>
   * Ending offset of the flush operation. Rows before this offset(including
   * this offset) will be flushed.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value offset = 2;</code>
   *
   * @return The offset.
   */
  com.google.protobuf.Int64Value getOffset();
  /**
   *
   *
   * <pre>
   * Ending offset of the flush operation. Rows before this offset(including
   * this offset) will be flushed.
   * </pre>
   *
   * <code>.google.protobuf.Int64Value offset = 2;</code>
   */
  com.google.protobuf.Int64ValueOrBuilder getOffsetOrBuilder();
}
