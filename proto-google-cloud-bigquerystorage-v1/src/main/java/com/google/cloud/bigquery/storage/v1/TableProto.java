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
// source: google/cloud/bigquery/storage/v1/table.proto

// Protobuf Java Version: 3.25.8
package com.google.cloud.bigquery.storage.v1;

public final class TableProto {
  private TableProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_TableSchema_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_TableSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_FieldElementType_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_FieldElementType_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n"
          + ",google/cloud/bigquery/storage/v1/table.proto\022"
          + " google.cloud.bigquery.storage.v1\032\037google/api/field_behavior.proto\"Q\n"
          + "\013TableSchema\022B\n"
          + "\006fields\030\001"
          + " \003(\01322.google.cloud.bigquery.storage.v1.TableFieldSchema\"\375\006\n"
          + "\020TableFieldSchema\022\021\n"
          + "\004name\030\001 \001(\tB\003\340A\002\022J\n"
          + "\004type\030\002"
          + " \001(\01627.google.cloud.bigquery.storage.v1.TableFieldSchema.TypeB\003\340A\002\022J\n"
          + "\004mode\030\003"
          + " \001(\01627.google.cloud.bigquery.storage.v1.TableFieldSchema.ModeB\003\340A\001\022G\n"
          + "\006fields\030\004"
          + " \003(\01322.google.cloud.bigquery.storage.v1.TableFieldSchemaB\003\340A\001\022\030\n"
          + "\013description\030\006 \001(\tB\003\340A\001\022\027\n\n"
          + "max_length\030\007 \001(\003B\003\340A\001\022\026\n"
          + "\tprecision\030\010 \001(\003B\003\340A\001\022\022\n"
          + "\005scale\030\t \001(\003B\003\340A\001\022%\n"
          + "\030default_value_expression\030\n"
          + " \001(\tB\003\340A\001\022d\n"
          + "\022range_element_type\030\013 \001(\0132C.google.cloud.b"
          + "igquery.storage.v1.TableFieldSchema.FieldElementTypeB\003\340A\001\032^\n"
          + "\020FieldElementType\022J\n"
          + "\004type\030\001"
          + " \001(\01627.google.cloud.bigquery.storage.v1.TableFieldSchema.TypeB\003\340A\002\"\340\001\n"
          + "\004Type\022\024\n"
          + "\020TYPE_UNSPECIFIED\020\000\022\n\n"
          + "\006STRING\020\001\022\t\n"
          + "\005INT64\020\002\022\n\n"
          + "\006DOUBLE\020\003\022\n\n"
          + "\006STRUCT\020\004\022\t\n"
          + "\005BYTES\020\005\022\010\n"
          + "\004BOOL\020\006\022\r\n"
          + "\tTIMESTAMP\020\007\022\010\n"
          + "\004DATE\020\010\022\010\n"
          + "\004TIME\020\t\022\014\n"
          + "\010DATETIME\020\n"
          + "\022\r\n"
          + "\tGEOGRAPHY\020\013\022\013\n"
          + "\007NUMERIC\020\014\022\016\n\n"
          + "BIGNUMERIC\020\r"
          + "\022\014\n"
          + "\010INTERVAL\020\016\022\010\n"
          + "\004JSON\020\017\022\t\n"
          + "\005RANGE\020\020\"F\n"
          + "\004Mode\022\024\n"
          + "\020MODE_UNSPECIFIED\020\000\022\014\n"
          + "\010NULLABLE\020\001\022\014\n"
          + "\010REQUIRED\020\002\022\014\n"
          + "\010REPEATED\020\003B\272\001\n"
          + "$com.google.cloud.bigquery.storage.v1B\n"
          + "TableProtoP\001Z>cloud.googl"
          + "e.com/go/bigquery/storage/apiv1/storagepb;storagepb\252\002"
          + " Google.Cloud.BigQuery.Storage.V1\312\002 Google\\Cloud\\BigQuery\\Storage\\V"
          + "1b\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.FieldBehaviorProto.getDescriptor(),
            });
    internal_static_google_cloud_bigquery_storage_v1_TableSchema_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_TableSchema_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_TableSchema_descriptor,
            new java.lang.String[] {
              "Fields",
            });
    internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_descriptor,
            new java.lang.String[] {
              "Name",
              "Type",
              "Mode",
              "Fields",
              "Description",
              "MaxLength",
              "Precision",
              "Scale",
              "DefaultValueExpression",
              "RangeElementType",
            });
    internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_FieldElementType_descriptor =
        internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_descriptor
            .getNestedTypes()
            .get(0);
    internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_FieldElementType_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_TableFieldSchema_FieldElementType_descriptor,
            new java.lang.String[] {
              "Type",
            });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.FieldBehaviorProto.fieldBehavior);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.FieldBehaviorProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
