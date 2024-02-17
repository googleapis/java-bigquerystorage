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
// source: google/cloud/bigquery/storage/v1beta2/protobuf.proto

// Protobuf Java Version: 3.25.2
package com.google.cloud.bigquery.storage.v1beta2;

public final class ProtoBufProto {
  private ProtoBufProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1beta2_ProtoSchema_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1beta2_ProtoSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1beta2_ProtoRows_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1beta2_ProtoRows_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n4google/cloud/bigquery/storage/v1beta2/"
          + "protobuf.proto\022%google.cloud.bigquery.st"
          + "orage.v1beta2\032 google/protobuf/descripto"
          + "r.proto\"I\n\013ProtoSchema\022:\n\020proto_descript"
          + "or\030\001 \001(\0132 .google.protobuf.DescriptorPro"
          + "to\"$\n\tProtoRows\022\027\n\017serialized_rows\030\001 \003(\014"
          + "B\201\001\n)com.google.cloud.bigquery.storage.v"
          + "1beta2B\rProtoBufProtoP\001ZCcloud.google.co"
          + "m/go/bigquery/storage/apiv1beta2/storage"
          + "pb;storagepbb\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.protobuf.DescriptorProtos.getDescriptor(),
            });
    internal_static_google_cloud_bigquery_storage_v1beta2_ProtoSchema_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1beta2_ProtoSchema_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1beta2_ProtoSchema_descriptor,
            new java.lang.String[] {
              "ProtoDescriptor",
            });
    internal_static_google_cloud_bigquery_storage_v1beta2_ProtoRows_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1beta2_ProtoRows_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1beta2_ProtoRows_descriptor,
            new java.lang.String[] {
              "SerializedRows",
            });
    com.google.protobuf.DescriptorProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
