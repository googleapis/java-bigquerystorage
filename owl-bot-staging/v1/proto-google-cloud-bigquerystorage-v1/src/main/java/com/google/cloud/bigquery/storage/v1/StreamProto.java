// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/stream.proto

package com.google.cloud.bigquery.storage.v1;

public final class StreamProto {
  private StreamProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ReadSession_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableModifiers_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableModifiers_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableReadOptions_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableReadOptions_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_ReadStream_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_ReadStream_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n-google/cloud/bigquery/storage/v1/strea" +
      "m.proto\022 google.cloud.bigquery.storage.v" +
      "1\032\037google/api/field_behavior.proto\032\031goog" +
      "le/api/resource.proto\032,google/cloud/bigq" +
      "uery/storage/v1/arrow.proto\032+google/clou" +
      "d/bigquery/storage/v1/avro.proto\032\037google" +
      "/protobuf/timestamp.proto\"\244\010\n\013ReadSessio" +
      "n\022\021\n\004name\030\001 \001(\tB\003\340A\003\0224\n\013expire_time\030\002 \001(" +
      "\0132\032.google.protobuf.TimestampB\003\340A\003\022F\n\013da" +
      "ta_format\030\003 \001(\0162,.google.cloud.bigquery." +
      "storage.v1.DataFormatB\003\340A\005\022H\n\013avro_schem" +
      "a\030\004 \001(\0132,.google.cloud.bigquery.storage." +
      "v1.AvroSchemaB\003\340A\003H\000\022J\n\014arrow_schema\030\005 \001" +
      "(\0132-.google.cloud.bigquery.storage.v1.Ar" +
      "rowSchemaB\003\340A\003H\000\0224\n\005table\030\006 \001(\tB%\340A\005\372A\037\n" +
      "\035bigquery.googleapis.com/Table\022Z\n\017table_" +
      "modifiers\030\007 \001(\0132<.google.cloud.bigquery." +
      "storage.v1.ReadSession.TableModifiersB\003\340" +
      "A\001\022Y\n\014read_options\030\010 \001(\0132>.google.cloud." +
      "bigquery.storage.v1.ReadSession.TableRea" +
      "dOptionsB\003\340A\001\022B\n\007streams\030\n \003(\0132,.google." +
      "cloud.bigquery.storage.v1.ReadStreamB\003\340A" +
      "\003\022*\n\035estimated_total_bytes_scanned\030\014 \001(\003" +
      "B\003\340A\003\032C\n\016TableModifiers\0221\n\rsnapshot_time" +
      "\030\001 \001(\0132\032.google.protobuf.Timestamp\032\324\001\n\020T" +
      "ableReadOptions\022\027\n\017selected_fields\030\001 \003(\t" +
      "\022\027\n\017row_restriction\030\002 \001(\t\022g\n\033arrow_seria" +
      "lization_options\030\003 \001(\0132;.google.cloud.bi" +
      "gquery.storage.v1.ArrowSerializationOpti" +
      "onsB\003\340A\001H\000B%\n#output_format_serializatio" +
      "n_options:k\352Ah\n*bigquerystorage.googleap" +
      "is.com/ReadSession\022:projects/{project}/l" +
      "ocations/{location}/sessions/{session}B\010" +
      "\n\006schema\"\234\001\n\nReadStream\022\021\n\004name\030\001 \001(\tB\003\340" +
      "A\003:{\352Ax\n)bigquerystorage.googleapis.com/" +
      "ReadStream\022Kprojects/{project}/locations" +
      "/{location}/sessions/{session}/streams/{" +
      "stream}*>\n\nDataFormat\022\033\n\027DATA_FORMAT_UNS" +
      "PECIFIED\020\000\022\010\n\004AVRO\020\001\022\t\n\005ARROW\020\002B\304\001\n$com." +
      "google.cloud.bigquery.storage.v1B\013Stream" +
      "ProtoP\001ZGgoogle.golang.org/genproto/goog" +
      "leapis/cloud/bigquery/storage/v1;storage" +
      "\252\002 Google.Cloud.BigQuery.Storage.V1\312\002 Go" +
      "ogle\\Cloud\\BigQuery\\Storage\\V1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.FieldBehaviorProto.getDescriptor(),
          com.google.api.ResourceProto.getDescriptor(),
          com.google.cloud.bigquery.storage.v1.ArrowProto.getDescriptor(),
          com.google.cloud.bigquery.storage.v1.AvroProto.getDescriptor(),
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ReadSession_descriptor,
        new java.lang.String[] { "Name", "ExpireTime", "DataFormat", "AvroSchema", "ArrowSchema", "Table", "TableModifiers", "ReadOptions", "Streams", "EstimatedTotalBytesScanned", "Schema", });
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableModifiers_descriptor =
      internal_static_google_cloud_bigquery_storage_v1_ReadSession_descriptor.getNestedTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableModifiers_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableModifiers_descriptor,
        new java.lang.String[] { "SnapshotTime", });
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableReadOptions_descriptor =
      internal_static_google_cloud_bigquery_storage_v1_ReadSession_descriptor.getNestedTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableReadOptions_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ReadSession_TableReadOptions_descriptor,
        new java.lang.String[] { "SelectedFields", "RowRestriction", "ArrowSerializationOptions", "OutputFormatSerializationOptions", });
    internal_static_google_cloud_bigquery_storage_v1_ReadStream_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_ReadStream_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ReadStream_descriptor,
        new java.lang.String[] { "Name", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.FieldBehaviorProto.fieldBehavior);
    registry.add(com.google.api.ResourceProto.resource);
    registry.add(com.google.api.ResourceProto.resourceReference);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.FieldBehaviorProto.getDescriptor();
    com.google.api.ResourceProto.getDescriptor();
    com.google.cloud.bigquery.storage.v1.ArrowProto.getDescriptor();
    com.google.cloud.bigquery.storage.v1.AvroProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
