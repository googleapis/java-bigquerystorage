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

public interface ArrowSerializationOptionsOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.ArrowSerializationOptions)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The compression codec to use for Arrow buffers in serialized record
   * batches.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1.ArrowSerializationOptions.CompressionCodec buffer_compression = 2;
   * </code>
   *
   * @return The enum numeric value on the wire for bufferCompression.
   */
  int getBufferCompressionValue();
  /**
   *
   *
   * <pre>
   * The compression codec to use for Arrow buffers in serialized record
   * batches.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1.ArrowSerializationOptions.CompressionCodec buffer_compression = 2;
   * </code>
   *
   * @return The bufferCompression.
   */
  com.google.cloud.bigquery.storage.v1.ArrowSerializationOptions.CompressionCodec
      getBufferCompression();
}
