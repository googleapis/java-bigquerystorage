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

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1;

public interface AppendRowsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.AppendRowsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Result if the append is successful.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.AppendRowsResponse.AppendResult append_result = 1;
   * </code>
   *
   * @return Whether the appendResult field is set.
   */
  boolean hasAppendResult();
  /**
   *
   *
   * <pre>
   * Result if the append is successful.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.AppendRowsResponse.AppendResult append_result = 1;
   * </code>
   *
   * @return The appendResult.
   */
  com.google.cloud.bigquery.storage.v1.AppendRowsResponse.AppendResult getAppendResult();
  /**
   *
   *
   * <pre>
   * Result if the append is successful.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.AppendRowsResponse.AppendResult append_result = 1;
   * </code>
   */
  com.google.cloud.bigquery.storage.v1.AppendRowsResponse.AppendResultOrBuilder
      getAppendResultOrBuilder();

  /**
   *
   *
   * <pre>
   * Error returned when problems were encountered.  If present,
   * it indicates rows were not accepted into the system.
   * Users can retry or continue with other append requests within the
   * same connection.
   *
   * Additional information about error signalling:
   *
   * ALREADY_EXISTS: Happens when an append specified an offset, and the
   * backend already has received data at this offset.  Typically encountered
   * in retry scenarios, and can be ignored.
   *
   * OUT_OF_RANGE: Returned when the specified offset in the stream is beyond
   * the current end of the stream.
   *
   * INVALID_ARGUMENT: Indicates a malformed request or data.
   *
   * ABORTED: Request processing is aborted because of prior failures.  The
   * request can be retried if previous failure is addressed.
   *
   * INTERNAL: Indicates server side error(s) that can be retried.
   * </pre>
   *
   * <code>.google.rpc.Status error = 2;</code>
   *
   * @return Whether the error field is set.
   */
  boolean hasError();
  /**
   *
   *
   * <pre>
   * Error returned when problems were encountered.  If present,
   * it indicates rows were not accepted into the system.
   * Users can retry or continue with other append requests within the
   * same connection.
   *
   * Additional information about error signalling:
   *
   * ALREADY_EXISTS: Happens when an append specified an offset, and the
   * backend already has received data at this offset.  Typically encountered
   * in retry scenarios, and can be ignored.
   *
   * OUT_OF_RANGE: Returned when the specified offset in the stream is beyond
   * the current end of the stream.
   *
   * INVALID_ARGUMENT: Indicates a malformed request or data.
   *
   * ABORTED: Request processing is aborted because of prior failures.  The
   * request can be retried if previous failure is addressed.
   *
   * INTERNAL: Indicates server side error(s) that can be retried.
   * </pre>
   *
   * <code>.google.rpc.Status error = 2;</code>
   *
   * @return The error.
   */
  com.google.rpc.Status getError();
  /**
   *
   *
   * <pre>
   * Error returned when problems were encountered.  If present,
   * it indicates rows were not accepted into the system.
   * Users can retry or continue with other append requests within the
   * same connection.
   *
   * Additional information about error signalling:
   *
   * ALREADY_EXISTS: Happens when an append specified an offset, and the
   * backend already has received data at this offset.  Typically encountered
   * in retry scenarios, and can be ignored.
   *
   * OUT_OF_RANGE: Returned when the specified offset in the stream is beyond
   * the current end of the stream.
   *
   * INVALID_ARGUMENT: Indicates a malformed request or data.
   *
   * ABORTED: Request processing is aborted because of prior failures.  The
   * request can be retried if previous failure is addressed.
   *
   * INTERNAL: Indicates server side error(s) that can be retried.
   * </pre>
   *
   * <code>.google.rpc.Status error = 2;</code>
   */
  com.google.rpc.StatusOrBuilder getErrorOrBuilder();

  /**
   *
   *
   * <pre>
   * If backend detects a schema update, pass it to user so that user can
   * use it to input new type of message. It will be empty when no schema
   * updates have occurred.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.TableSchema updated_schema = 3;</code>
   *
   * @return Whether the updatedSchema field is set.
   */
  boolean hasUpdatedSchema();
  /**
   *
   *
   * <pre>
   * If backend detects a schema update, pass it to user so that user can
   * use it to input new type of message. It will be empty when no schema
   * updates have occurred.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.TableSchema updated_schema = 3;</code>
   *
   * @return The updatedSchema.
   */
  com.google.cloud.bigquery.storage.v1.TableSchema getUpdatedSchema();
  /**
   *
   *
   * <pre>
   * If backend detects a schema update, pass it to user so that user can
   * use it to input new type of message. It will be empty when no schema
   * updates have occurred.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.TableSchema updated_schema = 3;</code>
   */
  com.google.cloud.bigquery.storage.v1.TableSchemaOrBuilder getUpdatedSchemaOrBuilder();

  /**
   *
   *
   * <pre>
   * If a request failed due to corrupted rows, no rows in the batch will be
   * appended. The API will return row level error info, so that the caller can
   * remove the bad rows and retry the request.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.RowError row_errors = 4;</code>
   */
  java.util.List<com.google.cloud.bigquery.storage.v1.RowError> getRowErrorsList();
  /**
   *
   *
   * <pre>
   * If a request failed due to corrupted rows, no rows in the batch will be
   * appended. The API will return row level error info, so that the caller can
   * remove the bad rows and retry the request.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.RowError row_errors = 4;</code>
   */
  com.google.cloud.bigquery.storage.v1.RowError getRowErrors(int index);
  /**
   *
   *
   * <pre>
   * If a request failed due to corrupted rows, no rows in the batch will be
   * appended. The API will return row level error info, so that the caller can
   * remove the bad rows and retry the request.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.RowError row_errors = 4;</code>
   */
  int getRowErrorsCount();
  /**
   *
   *
   * <pre>
   * If a request failed due to corrupted rows, no rows in the batch will be
   * appended. The API will return row level error info, so that the caller can
   * remove the bad rows and retry the request.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.RowError row_errors = 4;</code>
   */
  java.util.List<? extends com.google.cloud.bigquery.storage.v1.RowErrorOrBuilder>
      getRowErrorsOrBuilderList();
  /**
   *
   *
   * <pre>
   * If a request failed due to corrupted rows, no rows in the batch will be
   * appended. The API will return row level error info, so that the caller can
   * remove the bad rows and retry the request.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.RowError row_errors = 4;</code>
   */
  com.google.cloud.bigquery.storage.v1.RowErrorOrBuilder getRowErrorsOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * The target of the append operation. Matches the write_stream in the
   * corresponding request.
   * </pre>
   *
   * <code>string write_stream = 5;</code>
   *
   * @return The writeStream.
   */
  java.lang.String getWriteStream();
  /**
   *
   *
   * <pre>
   * The target of the append operation. Matches the write_stream in the
   * corresponding request.
   * </pre>
   *
   * <code>string write_stream = 5;</code>
   *
   * @return The bytes for writeStream.
   */
  com.google.protobuf.ByteString getWriteStreamBytes();

  com.google.cloud.bigquery.storage.v1.AppendRowsResponse.ResponseCase getResponseCase();
}
