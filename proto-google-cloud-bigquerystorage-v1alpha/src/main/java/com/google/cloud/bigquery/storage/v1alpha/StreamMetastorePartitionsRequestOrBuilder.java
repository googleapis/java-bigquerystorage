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

// Protobuf Java Version: 3.25.3
package com.google.cloud.bigquery.storage.v1alpha;

public interface StreamMetastorePartitionsRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Reference to the table to where the partition to be added, in the
   * format of
   * projects/{project}/locations/{location}/datasets/{dataset}/tables/{table}.
   * </pre>
   *
   * <code>
   * string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The parent.
   */
  java.lang.String getParent();
  /**
   *
   *
   * <pre>
   * Required. Reference to the table to where the partition to be added, in the
   * format of
   * projects/{project}/locations/{location}/datasets/{dataset}/tables/{table}.
   * </pre>
   *
   * <code>
   * string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for parent.
   */
  com.google.protobuf.ByteString getParentBytes();

  /**
   *
   *
   * <pre>
   * Optional. A list of metastore partitions to be added to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.MetastorePartition metastore_partitions = 2 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  java.util.List<com.google.cloud.bigquery.storage.v1alpha.MetastorePartition>
      getMetastorePartitionsList();
  /**
   *
   *
   * <pre>
   * Optional. A list of metastore partitions to be added to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.MetastorePartition metastore_partitions = 2 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1alpha.MetastorePartition getMetastorePartitions(int index);
  /**
   *
   *
   * <pre>
   * Optional. A list of metastore partitions to be added to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.MetastorePartition metastore_partitions = 2 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  int getMetastorePartitionsCount();
  /**
   *
   *
   * <pre>
   * Optional. A list of metastore partitions to be added to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.MetastorePartition metastore_partitions = 2 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  java.util.List<? extends com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionOrBuilder>
      getMetastorePartitionsOrBuilderList();
  /**
   *
   *
   * <pre>
   * Optional. A list of metastore partitions to be added to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.MetastorePartition metastore_partitions = 2 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionOrBuilder
      getMetastorePartitionsOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * Optional. Mimics the ifNotExists flag in IMetaStoreClient
   * add_partitions(..). If the flag is set to false, the server will return
   * ALREADY_EXISTS on commit if any partition already exists. If the flag is
   * set to true:
   *  1) the server will skip existing partitions
   *  insert only the non-existing partitions as part of the commit.
   *  2) The client must set the `skip_existing_partitions` field to true for
   *  all requests in the stream.
   * </pre>
   *
   * <code>bool skip_existing_partitions = 3 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The skipExistingPartitions.
   */
  boolean getSkipExistingPartitions();
}
