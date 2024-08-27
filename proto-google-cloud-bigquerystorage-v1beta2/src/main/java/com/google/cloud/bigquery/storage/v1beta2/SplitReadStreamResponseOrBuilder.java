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

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1beta2;

public interface SplitReadStreamResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.SplitReadStreamResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Primary stream, which contains the beginning portion of
   * |original_stream|. An empty value indicates that the original stream can no
   * longer be split.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.ReadStream primary_stream = 1;</code>
   *
   * @return Whether the primaryStream field is set.
   */
  boolean hasPrimaryStream();
  /**
   *
   *
   * <pre>
   * Primary stream, which contains the beginning portion of
   * |original_stream|. An empty value indicates that the original stream can no
   * longer be split.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.ReadStream primary_stream = 1;</code>
   *
   * @return The primaryStream.
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadStream getPrimaryStream();
  /**
   *
   *
   * <pre>
   * Primary stream, which contains the beginning portion of
   * |original_stream|. An empty value indicates that the original stream can no
   * longer be split.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.ReadStream primary_stream = 1;</code>
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadStreamOrBuilder getPrimaryStreamOrBuilder();

  /**
   *
   *
   * <pre>
   * Remainder stream, which contains the tail of |original_stream|. An empty
   * value indicates that the original stream can no longer be split.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.ReadStream remainder_stream = 2;</code>
   *
   * @return Whether the remainderStream field is set.
   */
  boolean hasRemainderStream();
  /**
   *
   *
   * <pre>
   * Remainder stream, which contains the tail of |original_stream|. An empty
   * value indicates that the original stream can no longer be split.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.ReadStream remainder_stream = 2;</code>
   *
   * @return The remainderStream.
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadStream getRemainderStream();
  /**
   *
   *
   * <pre>
   * Remainder stream, which contains the tail of |original_stream|. An empty
   * value indicates that the original stream can no longer be split.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.ReadStream remainder_stream = 2;</code>
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadStreamOrBuilder getRemainderStreamOrBuilder();
}
