/*
 * Copyright 2025 Google LLC
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
// source: google/cloud/bigquery/storage/v1/arrow.proto

// Protobuf Java Version: 3.25.8
package com.google.cloud.bigquery.storage.v1;

public final class ArrowProto {
  private ArrowProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_ArrowSchema_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ArrowSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_ArrowSerializationOptions_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ArrowSerializationOptions_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n,google/cloud/bigquery/storage/v1/arrow"
          + ".proto\022 google.cloud.bigquery.storage.v1"
          + "\"(\n\013ArrowSchema\022\031\n\021serialized_schema\030\001 \001"
          + "(\014\"J\n\020ArrowRecordBatch\022\037\n\027serialized_rec"
          + "ord_batch\030\001 \001(\014\022\025\n\trow_count\030\002 \001(\003B\002\030\001\"\317"
          + "\001\n\031ArrowSerializationOptions\022h\n\022buffer_c"
          + "ompression\030\002 \001(\0162L.google.cloud.bigquery"
          + ".storage.v1.ArrowSerializationOptions.Co"
          + "mpressionCodec\"H\n\020CompressionCodec\022\033\n\027CO"
          + "MPRESSION_UNSPECIFIED\020\000\022\r\n\tLZ4_FRAME\020\001\022\010"
          + "\n\004ZSTD\020\002B\272\001\n$com.google.cloud.bigquery.s"
          + "torage.v1B\nArrowProtoP\001Z>cloud.google.co"
          + "m/go/bigquery/storage/apiv1/storagepb;st"
          + "oragepb\252\002 Google.Cloud.BigQuery.Storage."
          + "V1\312\002 Google\\Cloud\\BigQuery\\Storage\\V1b\006p"
          + "roto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[] {});
    internal_static_google_cloud_bigquery_storage_v1_ArrowSchema_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_ArrowSchema_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_ArrowSchema_descriptor,
            new java.lang.String[] {
              "SerializedSchema",
            });
    internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_descriptor,
            new java.lang.String[] {
              "SerializedRecordBatch", "RowCount",
            });
    internal_static_google_cloud_bigquery_storage_v1_ArrowSerializationOptions_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_bigquery_storage_v1_ArrowSerializationOptions_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_ArrowSerializationOptions_descriptor,
            new java.lang.String[] {
              "BufferCompression",
            });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
