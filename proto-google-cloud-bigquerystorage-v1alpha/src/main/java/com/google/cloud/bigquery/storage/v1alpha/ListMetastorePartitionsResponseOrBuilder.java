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
// source: google/cloud/bigquery/storage/v1alpha/metastore_partition.proto

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1alpha;

public interface ListMetastorePartitionsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1alpha.ListMetastorePartitionsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The list of partitions.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1alpha.MetastorePartitionList partitions = 1;</code>
   *
   * @return Whether the partitions field is set.
   */
  boolean hasPartitions();
  /**
   *
   *
   * <pre>
   * The list of partitions.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1alpha.MetastorePartitionList partitions = 1;</code>
   *
   * @return The partitions.
   */
  com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionList getPartitions();
  /**
   *
   *
   * <pre>
   * The list of partitions.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1alpha.MetastorePartitionList partitions = 1;</code>
   */
  com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionListOrBuilder
      getPartitionsOrBuilder();

  /**
   *
   *
   * <pre>
   * The list of streams.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1alpha.StreamList streams = 2;</code>
   *
   * @return Whether the streams field is set.
   */
  boolean hasStreams();
  /**
   *
   *
   * <pre>
   * The list of streams.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1alpha.StreamList streams = 2;</code>
   *
   * @return The streams.
   */
  com.google.cloud.bigquery.storage.v1alpha.StreamList getStreams();
  /**
   *
   *
   * <pre>
   * The list of streams.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1alpha.StreamList streams = 2;</code>
   */
  com.google.cloud.bigquery.storage.v1alpha.StreamListOrBuilder getStreamsOrBuilder();

  com.google.cloud.bigquery.storage.v1alpha.ListMetastorePartitionsResponse.ResponseCase
      getResponseCase();
}
