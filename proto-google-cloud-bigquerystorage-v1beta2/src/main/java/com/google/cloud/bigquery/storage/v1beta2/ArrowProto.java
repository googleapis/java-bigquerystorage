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
// source: google/cloud/bigquery/storage/v1beta2/arrow.proto

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1beta2;

public final class ArrowProto {
  private ArrowProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSchema_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1beta2_ArrowRecordBatch_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1beta2_ArrowRecordBatch_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSerializationOptions_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSerializationOptions_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n1google/cloud/bigquery/storage/v1beta2/"
          + "arrow.proto\022%google.cloud.bigquery.stora"
          + "ge.v1beta2\"(\n\013ArrowSchema\022\031\n\021serialized_"
          + "schema\030\001 \001(\014\"3\n\020ArrowRecordBatch\022\037\n\027seri"
          + "alized_record_batch\030\001 \001(\014\"\266\001\n\031ArrowSeria"
          + "lizationOptions\022W\n\006format\030\001 \001(\0162G.google"
          + ".cloud.bigquery.storage.v1beta2.ArrowSer"
          + "ializationOptions.Format\"@\n\006Format\022\026\n\022FO"
          + "RMAT_UNSPECIFIED\020\000\022\016\n\nARROW_0_14\020\001\022\016\n\nAR"
          + "ROW_0_15\020\002B~\n)com.google.cloud.bigquery."
          + "storage.v1beta2B\nArrowProtoP\001ZCcloud.goo"
          + "gle.com/go/bigquery/storage/apiv1beta2/s"
          + "toragepb;storagepbb\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[] {});
    internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSchema_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSchema_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSchema_descriptor,
            new java.lang.String[] {
              "SerializedSchema",
            });
    internal_static_google_cloud_bigquery_storage_v1beta2_ArrowRecordBatch_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1beta2_ArrowRecordBatch_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1beta2_ArrowRecordBatch_descriptor,
            new java.lang.String[] {
              "SerializedRecordBatch",
            });
    internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSerializationOptions_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSerializationOptions_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1beta2_ArrowSerializationOptions_descriptor,
            new java.lang.String[] {
              "Format",
            });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
