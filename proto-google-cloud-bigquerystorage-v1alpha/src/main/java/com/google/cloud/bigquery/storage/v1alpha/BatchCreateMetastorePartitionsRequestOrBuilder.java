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

public interface BatchCreateMetastorePartitionsRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1alpha.BatchCreateMetastorePartitionsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. Reference to the table to where the metastore partitions to be
   * added, in the format of
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
   * Required. Reference to the table to where the metastore partitions to be
   * added, in the format of
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
   * Required. Requests to add metastore partitions to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequest requests = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  java.util.List<com.google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequest>
      getRequestsList();
  /**
   *
   *
   * <pre>
   * Required. Requests to add metastore partitions to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequest requests = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequest getRequests(int index);
  /**
   *
   *
   * <pre>
   * Required. Requests to add metastore partitions to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequest requests = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  int getRequestsCount();
  /**
   *
   *
   * <pre>
   * Required. Requests to add metastore partitions to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequest requests = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  java.util.List<
          ? extends
              com.google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequestOrBuilder>
      getRequestsOrBuilderList();
  /**
   *
   *
   * <pre>
   * Required. Requests to add metastore partitions to the table.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequest requests = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1alpha.CreateMetastorePartitionRequestOrBuilder
      getRequestsOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * Optional. Mimics the ifNotExists flag in IMetaStoreClient
   * add_partitions(..). If the flag is set to false, the server will return
   * ALREADY_EXISTS if any partition already exists. If the flag is set to true,
   * the server will skip existing partitions and insert only the non-existing
   * partitions.
   * </pre>
   *
   * <code>bool skip_existing_partitions = 3 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The skipExistingPartitions.
   */
  boolean getSkipExistingPartitions();
}
