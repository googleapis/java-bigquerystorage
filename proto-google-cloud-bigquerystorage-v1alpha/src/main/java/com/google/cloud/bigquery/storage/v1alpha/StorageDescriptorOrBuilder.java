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
// source: google/cloud/bigquery/storage/v1alpha/partition.proto

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1alpha;

public interface StorageDescriptorOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1alpha.StorageDescriptor)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Optional. The physical location of the metastore partition
   * (e.g. `gs://spark-dataproc-data/pangea-data/case_sensitive/` or
   * `gs://spark-dataproc-data/pangea-data/&#42;`).
   * </pre>
   *
   * <code>string location_uri = 1 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The locationUri.
   */
  java.lang.String getLocationUri();
  /**
   *
   *
   * <pre>
   * Optional. The physical location of the metastore partition
   * (e.g. `gs://spark-dataproc-data/pangea-data/case_sensitive/` or
   * `gs://spark-dataproc-data/pangea-data/&#42;`).
   * </pre>
   *
   * <code>string location_uri = 1 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for locationUri.
   */
  com.google.protobuf.ByteString getLocationUriBytes();

  /**
   *
   *
   * <pre>
   * Optional. Specifies the fully qualified class name of the InputFormat
   * (e.g. "org.apache.hadoop.hive.ql.io.orc.OrcInputFormat").
   * The maximum length is 128 characters.
   * </pre>
   *
   * <code>string input_format = 2 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The inputFormat.
   */
  java.lang.String getInputFormat();
  /**
   *
   *
   * <pre>
   * Optional. Specifies the fully qualified class name of the InputFormat
   * (e.g. "org.apache.hadoop.hive.ql.io.orc.OrcInputFormat").
   * The maximum length is 128 characters.
   * </pre>
   *
   * <code>string input_format = 2 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for inputFormat.
   */
  com.google.protobuf.ByteString getInputFormatBytes();

  /**
   *
   *
   * <pre>
   * Optional. Specifies the fully qualified class name of the OutputFormat
   * (e.g. "org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat").
   * The maximum length is 128 characters.
   * </pre>
   *
   * <code>string output_format = 3 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The outputFormat.
   */
  java.lang.String getOutputFormat();
  /**
   *
   *
   * <pre>
   * Optional. Specifies the fully qualified class name of the OutputFormat
   * (e.g. "org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat").
   * The maximum length is 128 characters.
   * </pre>
   *
   * <code>string output_format = 3 [(.google.api.field_behavior) = OPTIONAL];</code>
   *
   * @return The bytes for outputFormat.
   */
  com.google.protobuf.ByteString getOutputFormatBytes();

  /**
   *
   *
   * <pre>
   * Optional. Serializer and deserializer information.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1alpha.SerDeInfo serde_info = 4 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the serdeInfo field is set.
   */
  boolean hasSerdeInfo();
  /**
   *
   *
   * <pre>
   * Optional. Serializer and deserializer information.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1alpha.SerDeInfo serde_info = 4 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The serdeInfo.
   */
  com.google.cloud.bigquery.storage.v1alpha.SerDeInfo getSerdeInfo();
  /**
   *
   *
   * <pre>
   * Optional. Serializer and deserializer information.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1alpha.SerDeInfo serde_info = 4 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1alpha.SerDeInfoOrBuilder getSerdeInfoOrBuilder();
}
