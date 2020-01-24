
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

public interface StreamStatsOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.StreamStats)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Represents the progress of the current stream.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.StreamStats.Progress progress = 2;</code>
   * @return Whether the progress field is set.
   */
  boolean hasProgress();
  /**
   * <pre>
   * Represents the progress of the current stream.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.StreamStats.Progress progress = 2;</code>
   * @return The progress.
   */
  com.google.cloud.bigquery.storage.v1beta2.StreamStats.Progress getProgress();
  /**
   * <pre>
   * Represents the progress of the current stream.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.StreamStats.Progress progress = 2;</code>
   */
  com.google.cloud.bigquery.storage.v1beta2.StreamStats.ProgressOrBuilder getProgressOrBuilder();
}
