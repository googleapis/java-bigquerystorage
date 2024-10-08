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
// source: google/cloud/bigquery/storage/v1beta2/storage.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.bigquery.storage.v1beta2;

public interface BatchCommitWriteStreamsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.BatchCommitWriteStreamsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The time at which streams were committed in microseconds granularity.
   * This field will only exist when there are no stream errors.
   * **Note** if this field is not set, it means the commit was not successful.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp commit_time = 1;</code>
   *
   * @return Whether the commitTime field is set.
   */
  boolean hasCommitTime();
  /**
   *
   *
   * <pre>
   * The time at which streams were committed in microseconds granularity.
   * This field will only exist when there are no stream errors.
   * **Note** if this field is not set, it means the commit was not successful.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp commit_time = 1;</code>
   *
   * @return The commitTime.
   */
  com.google.protobuf.Timestamp getCommitTime();
  /**
   *
   *
   * <pre>
   * The time at which streams were committed in microseconds granularity.
   * This field will only exist when there are no stream errors.
   * **Note** if this field is not set, it means the commit was not successful.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp commit_time = 1;</code>
   */
  com.google.protobuf.TimestampOrBuilder getCommitTimeOrBuilder();

  /**
   *
   *
   * <pre>
   * Stream level error if commit failed. Only streams with error will be in
   * the list.
   * If empty, there is no error and all streams are committed successfully.
   * If non empty, certain streams have errors and ZERO stream is committed due
   * to atomicity guarantee.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1beta2.StorageError stream_errors = 2;</code>
   */
  java.util.List<com.google.cloud.bigquery.storage.v1beta2.StorageError> getStreamErrorsList();
  /**
   *
   *
   * <pre>
   * Stream level error if commit failed. Only streams with error will be in
   * the list.
   * If empty, there is no error and all streams are committed successfully.
   * If non empty, certain streams have errors and ZERO stream is committed due
   * to atomicity guarantee.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1beta2.StorageError stream_errors = 2;</code>
   */
  com.google.cloud.bigquery.storage.v1beta2.StorageError getStreamErrors(int index);
  /**
   *
   *
   * <pre>
   * Stream level error if commit failed. Only streams with error will be in
   * the list.
   * If empty, there is no error and all streams are committed successfully.
   * If non empty, certain streams have errors and ZERO stream is committed due
   * to atomicity guarantee.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1beta2.StorageError stream_errors = 2;</code>
   */
  int getStreamErrorsCount();
  /**
   *
   *
   * <pre>
   * Stream level error if commit failed. Only streams with error will be in
   * the list.
   * If empty, there is no error and all streams are committed successfully.
   * If non empty, certain streams have errors and ZERO stream is committed due
   * to atomicity guarantee.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1beta2.StorageError stream_errors = 2;</code>
   */
  java.util.List<? extends com.google.cloud.bigquery.storage.v1beta2.StorageErrorOrBuilder>
      getStreamErrorsOrBuilderList();
  /**
   *
   *
   * <pre>
   * Stream level error if commit failed. Only streams with error will be in
   * the list.
   * If empty, there is no error and all streams are committed successfully.
   * If non empty, certain streams have errors and ZERO stream is committed due
   * to atomicity guarantee.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1beta2.StorageError stream_errors = 2;</code>
   */
  com.google.cloud.bigquery.storage.v1beta2.StorageErrorOrBuilder getStreamErrorsOrBuilder(
      int index);
}
