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
// source: google/cloud/bigquery/storage/v1beta2/avro.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.bigquery.storage.v1beta2;

public final class AvroProto {
  private AvroProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1beta2_AvroSchema_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1beta2_AvroSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1beta2_AvroRows_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1beta2_AvroRows_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n0google/cloud/bigquery/storage/v1beta2/"
          + "avro.proto\022%google.cloud.bigquery.storag"
          + "e.v1beta2\"\034\n\nAvroSchema\022\016\n\006schema\030\001 \001(\t\""
          + "*\n\010AvroRows\022\036\n\026serialized_binary_rows\030\001 "
          + "\001(\014B}\n)com.google.cloud.bigquery.storage"
          + ".v1beta2B\tAvroProtoP\001ZCcloud.google.com/"
          + "go/bigquery/storage/apiv1beta2/storagepb"
          + ";storagepbb\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[] {});
    internal_static_google_cloud_bigquery_storage_v1beta2_AvroSchema_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1beta2_AvroSchema_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1beta2_AvroSchema_descriptor,
            new java.lang.String[] {
              "Schema",
            });
    internal_static_google_cloud_bigquery_storage_v1beta2_AvroRows_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1beta2_AvroRows_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1beta2_AvroRows_descriptor,
            new java.lang.String[] {
              "SerializedBinaryRows",
            });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
