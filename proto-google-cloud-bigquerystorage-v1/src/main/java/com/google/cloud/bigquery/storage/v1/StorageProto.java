/*
 * Copyright 2020 Google LLC
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
// source: google/cloud/bigquery/storage/v1/storage.proto

package com.google.cloud.bigquery.storage.v1;

public final class StorageProto {
  private StorageProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_CreateReadSessionRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_CreateReadSessionRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_ReadRowsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ReadRowsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_ThrottleState_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ThrottleState_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_StreamStats_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_StreamStats_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_StreamStats_Progress_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_StreamStats_Progress_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_ReadRowsResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ReadRowsResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n.google/cloud/bigquery/storage/v1/stora"
          + "ge.proto\022 google.cloud.bigquery.storage."
          + "v1\032\034google/api/annotations.proto\032\027google"
          + "/api/client.proto\032\037google/api/field_beha"
          + "vior.proto\032\031google/api/resource.proto\032,g"
          + "oogle/cloud/bigquery/storage/v1/arrow.pr"
          + "oto\032+google/cloud/bigquery/storage/v1/av"
          + "ro.proto\032-google/cloud/bigquery/storage/"
          + "v1/stream.proto\"\303\001\n\030CreateReadSessionReq"
          + "uest\022C\n\006parent\030\001 \001(\tB3\340A\002\372A-\n+cloudresou"
          + "rcemanager.googleapis.com/Project\022H\n\014rea"
          + "d_session\030\002 \001(\0132-.google.cloud.bigquery."
          + "storage.v1.ReadSessionB\003\340A\002\022\030\n\020max_strea"
          + "m_count\030\003 \001(\005\"i\n\017ReadRowsRequest\022F\n\013read"
          + "_stream\030\001 \001(\tB1\340A\002\372A+\n)bigquerystorage.g"
          + "oogleapis.com/ReadStream\022\016\n\006offset\030\002 \001(\003"
          + "\")\n\rThrottleState\022\030\n\020throttle_percent\030\001 "
          + "\001(\005\"\227\001\n\013StreamStats\022H\n\010progress\030\002 \001(\01326."
          + "google.cloud.bigquery.storage.v1.StreamS"
          + "tats.Progress\032>\n\010Progress\022\031\n\021at_response"
          + "_start\030\001 \001(\001\022\027\n\017at_response_end\030\002 \001(\001\"\347\003"
          + "\n\020ReadRowsResponse\022?\n\tavro_rows\030\003 \001(\0132*."
          + "google.cloud.bigquery.storage.v1.AvroRow"
          + "sH\000\022P\n\022arrow_record_batch\030\004 \001(\01322.google"
          + ".cloud.bigquery.storage.v1.ArrowRecordBa"
          + "tchH\000\022\021\n\trow_count\030\006 \001(\003\022<\n\005stats\030\002 \001(\0132"
          + "-.google.cloud.bigquery.storage.v1.Strea"
          + "mStats\022G\n\016throttle_state\030\005 \001(\0132/.google."
          + "cloud.bigquery.storage.v1.ThrottleState\022"
          + "H\n\013avro_schema\030\007 \001(\0132,.google.cloud.bigq"
          + "uery.storage.v1.AvroSchemaB\003\340A\003H\001\022J\n\014arr"
          + "ow_schema\030\010 \001(\0132-.google.cloud.bigquery."
          + "storage.v1.ArrowSchemaB\003\340A\003H\001B\006\n\004rowsB\010\n"
          + "\006schema\"k\n\026SplitReadStreamRequest\022?\n\004nam"
          + "e\030\001 \001(\tB1\340A\002\372A+\n)bigquerystorage.googlea"
          + "pis.com/ReadStream\022\020\n\010fraction\030\002 \001(\001\"\247\001\n"
          + "\027SplitReadStreamResponse\022D\n\016primary_stre"
          + "am\030\001 \001(\0132,.google.cloud.bigquery.storage"
          + ".v1.ReadStream\022F\n\020remainder_stream\030\002 \001(\013"
          + "2,.google.cloud.bigquery.storage.v1.Read"
          + "Stream2\306\006\n\014BigQueryRead\022\351\001\n\021CreateReadSe"
          + "ssion\022:.google.cloud.bigquery.storage.v1"
          + ".CreateReadSessionRequest\032-.google.cloud"
          + ".bigquery.storage.v1.ReadSession\"i\202\323\344\223\002<"
          + "\"7/v1/{read_session.table=projects/*/dat"
          + "asets/*/tables/*}:\001*\332A$parent,read_sessi"
          + "on,max_stream_count\022\317\001\n\010ReadRows\0221.googl"
          + "e.cloud.bigquery.storage.v1.ReadRowsRequ"
          + "est\0322.google.cloud.bigquery.storage.v1.R"
          + "eadRowsResponse\"Z\202\323\344\223\002?\022=/v1/{read_strea"
          + "m=projects/*/locations/*/sessions/*/stre"
          + "ams/*}\332A\022read_stream,offset0\001\022\306\001\n\017SplitR"
          + "eadStream\0228.google.cloud.bigquery.storag"
          + "e.v1.SplitReadStreamRequest\0329.google.clo"
          + "ud.bigquery.storage.v1.SplitReadStreamRe"
          + "sponse\">\202\323\344\223\0028\0226/v1/{name=projects/*/loc"
          + "ations/*/sessions/*/streams/*}\032\256\001\312A\036bigq"
          + "uerystorage.googleapis.com\322A\211\001https://ww"
          + "w.googleapis.com/auth/bigquery,https://w"
          + "ww.googleapis.com/auth/bigquery.readonly"
          + ",https://www.googleapis.com/auth/cloud-p"
          + "latformB\235\002\n$com.google.cloud.bigquery.st"
          + "orage.v1B\014StorageProtoP\001ZGgoogle.golang."
          + "org/genproto/googleapis/cloud/bigquery/s"
          + "torage/v1;storage\252\002 Google.Cloud.BigQuer"
          + "y.Storage.V1\312\002 Google\\Cloud\\BigQuery\\Sto"
          + "rage\\V1\352AU\n\035bigquery.googleapis.com/Tabl"
          + "e\0224projects/{project}/datasets/{dataset}"
          + "/tables/{table}b\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.AnnotationsProto.getDescriptor(),
              com.google.api.ClientProto.getDescriptor(),
              com.google.api.FieldBehaviorProto.getDescriptor(),
              com.google.api.ResourceProto.getDescriptor(),
              com.google.cloud.bigquery.storage.v1.ArrowProto.getDescriptor(),
              com.google.cloud.bigquery.storage.v1.AvroProto.getDescriptor(),
              com.google.cloud.bigquery.storage.v1.StreamProto.getDescriptor(),
            });
    internal_static_google_cloud_bigquery_storage_v1_CreateReadSessionRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_CreateReadSessionRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_CreateReadSessionRequest_descriptor,
            new java.lang.String[] {
              "Parent", "ReadSession", "MaxStreamCount",
            });
    internal_static_google_cloud_bigquery_storage_v1_ReadRowsRequest_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_ReadRowsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_ReadRowsRequest_descriptor,
            new java.lang.String[] {
              "ReadStream", "Offset",
            });
    internal_static_google_cloud_bigquery_storage_v1_ThrottleState_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_bigquery_storage_v1_ThrottleState_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_ThrottleState_descriptor,
            new java.lang.String[] {
              "ThrottlePercent",
            });
    internal_static_google_cloud_bigquery_storage_v1_StreamStats_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_google_cloud_bigquery_storage_v1_StreamStats_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_StreamStats_descriptor,
            new java.lang.String[] {
              "Progress",
            });
    internal_static_google_cloud_bigquery_storage_v1_StreamStats_Progress_descriptor =
        internal_static_google_cloud_bigquery_storage_v1_StreamStats_descriptor
            .getNestedTypes()
            .get(0);
    internal_static_google_cloud_bigquery_storage_v1_StreamStats_Progress_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_StreamStats_Progress_descriptor,
            new java.lang.String[] {
              "AtResponseStart", "AtResponseEnd",
            });
    internal_static_google_cloud_bigquery_storage_v1_ReadRowsResponse_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_google_cloud_bigquery_storage_v1_ReadRowsResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_ReadRowsResponse_descriptor,
            new java.lang.String[] {
              "AvroRows",
              "ArrowRecordBatch",
              "RowCount",
              "Stats",
              "ThrottleState",
              "AvroSchema",
              "ArrowSchema",
              "Rows",
              "Schema",
            });
    internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamRequest_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamRequest_descriptor,
            new java.lang.String[] {
              "Name", "Fraction",
            });
    internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamResponse_descriptor =
        getDescriptor().getMessageTypes().get(6);
    internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_bigquery_storage_v1_SplitReadStreamResponse_descriptor,
            new java.lang.String[] {
              "PrimaryStream", "RemainderStream",
            });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.ClientProto.defaultHost);
    registry.add(com.google.api.FieldBehaviorProto.fieldBehavior);
    registry.add(com.google.api.AnnotationsProto.http);
    registry.add(com.google.api.ClientProto.methodSignature);
    registry.add(com.google.api.ClientProto.oauthScopes);
    registry.add(com.google.api.ResourceProto.resourceDefinition);
    registry.add(com.google.api.ResourceProto.resourceReference);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.api.ClientProto.getDescriptor();
    com.google.api.FieldBehaviorProto.getDescriptor();
    com.google.api.ResourceProto.getDescriptor();
    com.google.cloud.bigquery.storage.v1.ArrowProto.getDescriptor();
    com.google.cloud.bigquery.storage.v1.AvroProto.getDescriptor();
    com.google.cloud.bigquery.storage.v1.StreamProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
