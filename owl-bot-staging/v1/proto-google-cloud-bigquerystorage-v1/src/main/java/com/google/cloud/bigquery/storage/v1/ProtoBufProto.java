// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/protobuf.proto

package com.google.cloud.bigquery.storage.v1;

public final class ProtoBufProto {
  private ProtoBufProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_ProtoSchema_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ProtoSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_ProtoRows_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ProtoRows_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n/google/cloud/bigquery/storage/v1/proto" +
      "buf.proto\022 google.cloud.bigquery.storage" +
      ".v1\032 google/protobuf/descriptor.proto\"I\n" +
      "\013ProtoSchema\022:\n\020proto_descriptor\030\001 \001(\0132 " +
      ".google.protobuf.DescriptorProto\"$\n\tProt" +
      "oRows\022\027\n\017serialized_rows\030\001 \003(\014B\275\001\n$com.g" +
      "oogle.cloud.bigquery.storage.v1B\rProtoBu" +
      "fProtoP\001Z>cloud.google.com/go/bigquery/s" +
      "torage/apiv1/storagepb;storagepb\252\002 Googl" +
      "e.Cloud.BigQuery.Storage.V1\312\002 Google\\Clo" +
      "ud\\BigQuery\\Storage\\V1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.DescriptorProtos.getDescriptor(),
        });
    internal_static_google_cloud_bigquery_storage_v1_ProtoSchema_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_ProtoSchema_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ProtoSchema_descriptor,
        new java.lang.String[] { "ProtoDescriptor", });
    internal_static_google_cloud_bigquery_storage_v1_ProtoRows_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_ProtoRows_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ProtoRows_descriptor,
        new java.lang.String[] { "SerializedRows", });
    com.google.protobuf.DescriptorProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
