// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/avro.proto

package com.google.cloud.bigquery.storage.v1;

public final class AvroProto {
  private AvroProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_AvroSchema_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_AvroSchema_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_AvroRows_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_AvroRows_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n+google/cloud/bigquery/storage/v1/avro." +
      "proto\022 google.cloud.bigquery.storage.v1\"" +
      "\034\n\nAvroSchema\022\016\n\006schema\030\001 \001(\t\"=\n\010AvroRow" +
      "s\022\036\n\026serialized_binary_rows\030\001 \001(\014\022\021\n\trow" +
      "_count\030\002 \001(\003B\302\001\n$com.google.cloud.bigque" +
      "ry.storage.v1B\tAvroProtoP\001ZGgoogle.golan" +
      "g.org/genproto/googleapis/cloud/bigquery" +
      "/storage/v1;storage\252\002 Google.Cloud.BigQu" +
      "ery.Storage.V1\312\002 Google\\Cloud\\BigQuery\\S" +
      "torage\\V1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_google_cloud_bigquery_storage_v1_AvroSchema_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_AvroSchema_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_AvroSchema_descriptor,
        new java.lang.String[] { "Schema", });
    internal_static_google_cloud_bigquery_storage_v1_AvroRows_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_AvroRows_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_AvroRows_descriptor,
        new java.lang.String[] { "SerializedBinaryRows", "RowCount", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
