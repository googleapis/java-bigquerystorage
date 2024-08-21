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

// Protobuf Java Version: 3.25.3
package com.google.cloud.bigquery.storage.v1alpha;

public final class MetastorePartitionProto {
  private MetastorePartitionProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_FieldSchema_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_FieldSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_StorageDescriptor_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_StorageDescriptor_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_ParametersEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_ParametersEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_ParametersEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_ParametersEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionList_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionList_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_ReadStream_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_ReadStream_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_StreamList_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_StreamList_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionValues_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionValues_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n5google/cloud/bigquery/storage/v1alpha/"
          + "partition.proto\022%google.cloud.bigquery.s"
          + "torage.v1alpha\032\037google/api/field_behavio"
          + "r.proto\032\031google/api/resource.proto\032\037goog"
          + "le/protobuf/timestamp.proto\"3\n\013FieldSche"
          + "ma\022\021\n\004name\030\001 \001(\tB\003\340A\002\022\021\n\004type\030\002 \001(\tB\003\340A\002"
          + "\"\260\001\n\021StorageDescriptor\022\031\n\014location_uri\030\001"
          + " \001(\tB\003\340A\001\022\031\n\014input_format\030\002 \001(\tB\003\340A\001\022\032\n\r"
          + "output_format\030\003 \001(\tB\003\340A\001\022I\n\nserde_info\030\004"
          + " \001(\01320.google.cloud.bigquery.storage.v1a"
          + "lpha.SerDeInfoB\003\340A\001\"\320\001\n\tSerDeInfo\022\021\n\004nam"
          + "e\030\001 \001(\tB\003\340A\001\022\"\n\025serialization_library\030\002 "
          + "\001(\tB\003\340A\002\022Y\n\nparameters\030\003 \003(\0132@.google.cl"
          + "oud.bigquery.storage.v1alpha.SerDeInfo.P"
          + "arametersEntryB\003\340A\001\0321\n\017ParametersEntry\022\013"
          + "\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\t:\0028\001\"\232\003\n\022Metas"
          + "torePartition\022\023\n\006values\030\001 \003(\tB\003\340A\002\0224\n\013cr"
          + "eate_time\030\002 \001(\0132\032.google.protobuf.Timest"
          + "ampB\003\340A\003\022Y\n\022storage_descriptor\030\003 \001(\01328.g"
          + "oogle.cloud.bigquery.storage.v1alpha.Sto"
          + "rageDescriptorB\003\340A\001\022b\n\nparameters\030\004 \003(\0132"
          + "I.google.cloud.bigquery.storage.v1alpha."
          + "MetastorePartition.ParametersEntryB\003\340A\001\022"
          + "G\n\006fields\030\005 \003(\01322.google.cloud.bigquery."
          + "storage.v1alpha.FieldSchemaB\003\340A\001\0321\n\017Para"
          + "metersEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\t:"
          + "\0028\001\"l\n\026MetastorePartitionList\022R\n\npartiti"
          + "ons\030\001 \003(\01329.google.cloud.bigquery.storag"
          + "e.v1alpha.MetastorePartitionB\003\340A\002\"\272\001\n\nRe"
          + "adStream\022\024\n\004name\030\001 \001(\tB\006\340A\003\340A\010:\225\001\352A\221\001\n)b"
          + "igquerystorage.googleapis.com/ReadStream"
          + "\022Kprojects/{project}/locations/{location"
          + "}/sessions/{session}/streams/{stream}*\013r"
          + "eadStreams2\nreadStream\"U\n\nStreamList\022G\n\007"
          + "streams\030\001 \003(\01321.google.cloud.bigquery.st"
          + "orage.v1alpha.ReadStreamB\003\340A\003\"/\n\030Metasto"
          + "rePartitionValues\022\023\n\006values\030\001 \003(\tB\003\340A\002B\333"
          + "\001\n)com.google.cloud.bigquery.storage.v1a"
          + "lphaB\027MetastorePartitionProtoP\001ZCcloud.g"
          + "oogle.com/go/bigquery/storage/apiv1alpha"
          + "/storagepb;storagepb\252\002%Google.Cloud.BigQ"
          + "uery.Storage.V1Alpha\312\002%Google\\Cloud\\BigQ"
          + "uery\\Storage\\V1alphab\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.FieldBehaviorProto.getDescriptor(),
              com.google.api.ResourceProto.getDescriptor(),
              com.google.protobuf.TimestampProto.getDescriptor(),
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_FieldSchema_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1alpha_FieldSchema_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_FieldSchema_descriptor,
            new java.lang.String[] {
              "Name", "Type",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_StorageDescriptor_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1alpha_StorageDescriptor_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_StorageDescriptor_descriptor,
            new java.lang.String[] {
              "LocationUri", "InputFormat", "OutputFormat", "SerdeInfo",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_descriptor,
            new java.lang.String[] {
              "Name", "SerializationLibrary", "Parameters",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_ParametersEntry_descriptor =
        internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_descriptor
            .getNestedTypes()
            .get(0);
    internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_ParametersEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_SerDeInfo_ParametersEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_descriptor,
            new java.lang.String[] {
              "Values", "CreateTime", "StorageDescriptor", "Parameters", "Fields",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_ParametersEntry_descriptor =
        internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_descriptor
            .getNestedTypes()
            .get(0);
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_ParametersEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartition_ParametersEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionList_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionList_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionList_descriptor,
            new java.lang.String[] {
              "Partitions",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_ReadStream_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_google_cloud_bigquery_storage_v1alpha_ReadStream_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_ReadStream_descriptor,
            new java.lang.String[] {
              "Name",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_StreamList_descriptor =
        getDescriptor().getMessageTypes().get(6);
    internal_static_google_cloud_bigquery_storage_v1alpha_StreamList_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_StreamList_descriptor,
            new java.lang.String[] {
              "Streams",
            });
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionValues_descriptor =
        getDescriptor().getMessageTypes().get(7);
    internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionValues_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1alpha_MetastorePartitionValues_descriptor,
            new java.lang.String[] {
              "Values",
            });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.FieldBehaviorProto.fieldBehavior);
    registry.add(com.google.api.ResourceProto.resource);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.FieldBehaviorProto.getDescriptor();
    com.google.api.ResourceProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
