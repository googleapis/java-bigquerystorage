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
// source: google/cloud/bigquery/storage/v1/storage.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.bigquery.storage.v1;

public interface StorageErrorOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.StorageError)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * BigQuery Storage specific error code.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.StorageError.StorageErrorCode code = 1;</code>
   *
   * @return The enum numeric value on the wire for code.
   */
  int getCodeValue();
  /**
   *
   *
   * <pre>
   * BigQuery Storage specific error code.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.StorageError.StorageErrorCode code = 1;</code>
   *
   * @return The code.
   */
  com.google.cloud.bigquery.storage.v1.StorageError.StorageErrorCode getCode();

  /**
   *
   *
   * <pre>
   * Name of the failed entity.
   * </pre>
   *
   * <code>string entity = 2;</code>
   *
   * @return The entity.
   */
  java.lang.String getEntity();
  /**
   *
   *
   * <pre>
   * Name of the failed entity.
   * </pre>
   *
   * <code>string entity = 2;</code>
   *
   * @return The bytes for entity.
   */
  com.google.protobuf.ByteString getEntityBytes();

  /**
   *
   *
   * <pre>
   * Message that describes the error.
   * </pre>
   *
   * <code>string error_message = 3;</code>
   *
   * @return The errorMessage.
   */
  java.lang.String getErrorMessage();
  /**
   *
   *
   * <pre>
   * Message that describes the error.
   * </pre>
   *
   * <code>string error_message = 3;</code>
   *
   * @return The bytes for errorMessage.
   */
  com.google.protobuf.ByteString getErrorMessageBytes();
}
