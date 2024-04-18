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
// source: google/cloud/bigquery/storage/v1beta2/stream.proto

// Protobuf Java Version: 3.25.3
package com.google.cloud.bigquery.storage.v1beta2;

public interface WriteStreamOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.WriteStream)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Output only. Name of the stream, in the form
   * `projects/{project}/datasets/{dataset}/tables/{table}/streams/{stream}`.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The name.
   */
  java.lang.String getName();
  /**
   *
   *
   * <pre>
   * Output only. Name of the stream, in the form
   * `projects/{project}/datasets/{dataset}/tables/{table}/streams/{stream}`.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   *
   *
   * <pre>
   * Immutable. Type of the stream.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.WriteStream.Type type = 2 [(.google.api.field_behavior) = IMMUTABLE];
   * </code>
   *
   * @return The enum numeric value on the wire for type.
   */
  int getTypeValue();
  /**
   *
   *
   * <pre>
   * Immutable. Type of the stream.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.WriteStream.Type type = 2 [(.google.api.field_behavior) = IMMUTABLE];
   * </code>
   *
   * @return The type.
   */
  com.google.cloud.bigquery.storage.v1beta2.WriteStream.Type getType();

  /**
   *
   *
   * <pre>
   * Output only. Create time of the stream. For the _default stream, this is the
   * creation_time of the table.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 3 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the createTime field is set.
   */
  boolean hasCreateTime();
  /**
   *
   *
   * <pre>
   * Output only. Create time of the stream. For the _default stream, this is the
   * creation_time of the table.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 3 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The createTime.
   */
  com.google.protobuf.Timestamp getCreateTime();
  /**
   *
   *
   * <pre>
   * Output only. Create time of the stream. For the _default stream, this is the
   * creation_time of the table.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp create_time = 3 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.protobuf.TimestampOrBuilder getCreateTimeOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. Commit time of the stream.
   * If a stream is of `COMMITTED` type, then it will have a commit_time same as
   * `create_time`. If the stream is of `PENDING` type, commit_time being empty
   * means it is not committed.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp commit_time = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the commitTime field is set.
   */
  boolean hasCommitTime();
  /**
   *
   *
   * <pre>
   * Output only. Commit time of the stream.
   * If a stream is of `COMMITTED` type, then it will have a commit_time same as
   * `create_time`. If the stream is of `PENDING` type, commit_time being empty
   * means it is not committed.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp commit_time = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The commitTime.
   */
  com.google.protobuf.Timestamp getCommitTime();
  /**
   *
   *
   * <pre>
   * Output only. Commit time of the stream.
   * If a stream is of `COMMITTED` type, then it will have a commit_time same as
   * `create_time`. If the stream is of `PENDING` type, commit_time being empty
   * means it is not committed.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp commit_time = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.protobuf.TimestampOrBuilder getCommitTimeOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. The schema of the destination table. It is only returned in
   * `CreateWriteStream` response. Caller should generate data that's
   * compatible with this schema to send in initial `AppendRowsRequest`.
   * The table schema could go out of date during the life time of the stream.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.TableSchema table_schema = 5 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the tableSchema field is set.
   */
  boolean hasTableSchema();
  /**
   *
   *
   * <pre>
   * Output only. The schema of the destination table. It is only returned in
   * `CreateWriteStream` response. Caller should generate data that's
   * compatible with this schema to send in initial `AppendRowsRequest`.
   * The table schema could go out of date during the life time of the stream.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.TableSchema table_schema = 5 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The tableSchema.
   */
  com.google.cloud.bigquery.storage.v1beta2.TableSchema getTableSchema();
  /**
   *
   *
   * <pre>
   * Output only. The schema of the destination table. It is only returned in
   * `CreateWriteStream` response. Caller should generate data that's
   * compatible with this schema to send in initial `AppendRowsRequest`.
   * The table schema could go out of date during the life time of the stream.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.TableSchema table_schema = 5 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1beta2.TableSchemaOrBuilder getTableSchemaOrBuilder();
}
