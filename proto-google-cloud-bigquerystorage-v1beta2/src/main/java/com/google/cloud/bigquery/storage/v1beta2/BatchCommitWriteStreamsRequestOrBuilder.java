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

public interface BatchCommitWriteStreamsRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.BatchCommitWriteStreamsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Parent table that all the streams should belong to, in the form
   * of `projects/{project}/datasets/{dataset}/tables/{table}`.
   * </pre>
   *
   * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The parent.
   */
  java.lang.String getParent();
  /**
   *
   *
   * <pre>
   * Required. Parent table that all the streams should belong to, in the form
   * of `projects/{project}/datasets/{dataset}/tables/{table}`.
   * </pre>
   *
   * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The bytes for parent.
   */
  com.google.protobuf.ByteString getParentBytes();

  /**
   *
   *
   * <pre>
   * Required. The group of streams that will be committed atomically.
   * </pre>
   *
   * <code>repeated string write_streams = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return A list containing the writeStreams.
   */
  java.util.List<java.lang.String> getWriteStreamsList();
  /**
   *
   *
   * <pre>
   * Required. The group of streams that will be committed atomically.
   * </pre>
   *
   * <code>repeated string write_streams = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @return The count of writeStreams.
   */
  int getWriteStreamsCount();
  /**
   *
   *
   * <pre>
   * Required. The group of streams that will be committed atomically.
   * </pre>
   *
   * <code>repeated string write_streams = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @param index The index of the element to return.
   * @return The writeStreams at the given index.
   */
  java.lang.String getWriteStreams(int index);
  /**
   *
   *
   * <pre>
   * Required. The group of streams that will be committed atomically.
   * </pre>
   *
   * <code>repeated string write_streams = 2 [(.google.api.field_behavior) = REQUIRED];</code>
   *
   * @param index The index of the value to return.
   * @return The bytes of the writeStreams at the given index.
   */
  com.google.protobuf.ByteString getWriteStreamsBytes(int index);
}
