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
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_google_cloud_bigquery_storage_v1_WriteStream_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_bigquery_storage_v1_WriteStream_fieldAccessorTable;

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
      "d/bigquery/storage/v1/avro.proto\032,google" +
      "/cloud/bigquery/storage/v1/table.proto\032\037" +
      "google/protobuf/timestamp.proto\"\216\n\n\013Read" +
      "Session\022\022\n\004name\030\001 \001(\tB\004\342A\001\003\0225\n\013expire_ti" +
      "me\030\002 \001(\0132\032.google.protobuf.TimestampB\004\342A" +
      "\001\003\022G\n\013data_format\030\003 \001(\0162,.google.cloud.b" +
      "igquery.storage.v1.DataFormatB\004\342A\001\005\022I\n\013a" +
      "vro_schema\030\004 \001(\0132,.google.cloud.bigquery" +
      ".storage.v1.AvroSchemaB\004\342A\001\003H\000\022K\n\014arrow_" +
      "schema\030\005 \001(\0132-.google.cloud.bigquery.sto" +
      "rage.v1.ArrowSchemaB\004\342A\001\003H\000\0225\n\005table\030\006 \001" +
      "(\tB&\342A\001\005\372A\037\n\035bigquery.googleapis.com/Tab" +
      "le\022[\n\017table_modifiers\030\007 \001(\0132<.google.clo" +
      "ud.bigquery.storage.v1.ReadSession.Table" +
      "ModifiersB\004\342A\001\001\022Z\n\014read_options\030\010 \001(\0132>." +
      "google.cloud.bigquery.storage.v1.ReadSes" +
      "sion.TableReadOptionsB\004\342A\001\001\022C\n\007streams\030\n" +
      " \003(\0132,.google.cloud.bigquery.storage.v1." +
      "ReadStreamB\004\342A\001\003\022+\n\035estimated_total_byte" +
      "s_scanned\030\014 \001(\003B\004\342A\001\003\022!\n\023estimated_row_c" +
      "ount\030\016 \001(\003B\004\342A\001\003\022\026\n\010trace_id\030\r \001(\tB\004\342A\001\001" +
      "\032C\n\016TableModifiers\0221\n\rsnapshot_time\030\001 \001(" +
      "\0132\032.google.protobuf.Timestamp\032\371\002\n\020TableR" +
      "eadOptions\022\027\n\017selected_fields\030\001 \003(\t\022\027\n\017r" +
      "ow_restriction\030\002 \001(\t\022h\n\033arrow_serializat" +
      "ion_options\030\003 \001(\0132;.google.cloud.bigquer" +
      "y.storage.v1.ArrowSerializationOptionsB\004" +
      "\342A\001\001H\000\022f\n\032avro_serialization_options\030\004 \001" +
      "(\0132:.google.cloud.bigquery.storage.v1.Av" +
      "roSerializationOptionsB\004\342A\001\001H\000\022$\n\021sample" +
      "_percentage\030\005 \001(\001B\004\342A\001\001H\001\210\001\001B%\n#output_f" +
      "ormat_serialization_optionsB\024\n\022_sample_p" +
      "ercentage:k\352Ah\n*bigquerystorage.googleap" +
      "is.com/ReadSession\022:projects/{project}/l" +
      "ocations/{location}/sessions/{session}B\010" +
      "\n\006schema\"\235\001\n\nReadStream\022\022\n\004name\030\001 \001(\tB\004\342" +
      "A\001\003:{\352Ax\n)bigquerystorage.googleapis.com" +
      "/ReadStream\022Kprojects/{project}/location" +
      "s/{location}/sessions/{session}/streams/" +
      "{stream}\"\202\005\n\013WriteStream\022\022\n\004name\030\001 \001(\tB\004" +
      "\342A\001\003\022F\n\004type\030\002 \001(\01622.google.cloud.bigque" +
      "ry.storage.v1.WriteStream.TypeB\004\342A\001\005\0225\n\013" +
      "create_time\030\003 \001(\0132\032.google.protobuf.Time" +
      "stampB\004\342A\001\003\0225\n\013commit_time\030\004 \001(\0132\032.googl" +
      "e.protobuf.TimestampB\004\342A\001\003\022I\n\014table_sche" +
      "ma\030\005 \001(\0132-.google.cloud.bigquery.storage" +
      ".v1.TableSchemaB\004\342A\001\003\022Q\n\nwrite_mode\030\007 \001(" +
      "\01627.google.cloud.bigquery.storage.v1.Wri" +
      "teStream.WriteModeB\004\342A\001\005\022\026\n\010location\030\010 \001" +
      "(\tB\004\342A\001\005\"F\n\004Type\022\024\n\020TYPE_UNSPECIFIED\020\000\022\r" +
      "\n\tCOMMITTED\020\001\022\013\n\007PENDING\020\002\022\014\n\010BUFFERED\020\003" +
      "\"3\n\tWriteMode\022\032\n\026WRITE_MODE_UNSPECIFIED\020" +
      "\000\022\n\n\006INSERT\020\001:v\352As\n*bigquerystorage.goog" +
      "leapis.com/WriteStream\022Eprojects/{projec" +
      "t}/datasets/{dataset}/tables/{table}/str" +
      "eams/{stream}*>\n\nDataFormat\022\033\n\027DATA_FORM" +
      "AT_UNSPECIFIED\020\000\022\010\n\004AVRO\020\001\022\t\n\005ARROW\020\002*I\n" +
      "\017WriteStreamView\022!\n\035WRITE_STREAM_VIEW_UN" +
      "SPECIFIED\020\000\022\t\n\005BASIC\020\001\022\010\n\004FULL\020\002B\273\001\n$com" +
      ".google.cloud.bigquery.storage.v1B\013Strea" +
      "mProtoP\001Z>cloud.google.com/go/bigquery/s" +
      "torage/apiv1/storagepb;storagepb\252\002 Googl" +
      "e.Cloud.BigQuery.Storage.V1\312\002 Google\\Clo" +
      "ud\\BigQuery\\Storage\\V1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.FieldBehaviorProto.getDescriptor(),
          com.google.api.ResourceProto.getDescriptor(),
          com.google.cloud.bigquery.storage.v1.ArrowProto.getDescriptor(),
          com.google.cloud.bigquery.storage.v1.AvroProto.getDescriptor(),
          com.google.cloud.bigquery.storage.v1.TableProto.getDescriptor(),
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_bigquery_storage_v1_ReadSession_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ReadSession_descriptor,
        new java.lang.String[] { "Name", "ExpireTime", "DataFormat", "AvroSchema", "ArrowSchema", "Table", "TableModifiers", "ReadOptions", "Streams", "EstimatedTotalBytesScanned", "EstimatedRowCount", "TraceId", "Schema", });
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
        new java.lang.String[] { "SelectedFields", "RowRestriction", "ArrowSerializationOptions", "AvroSerializationOptions", "SamplePercentage", "OutputFormatSerializationOptions", "SamplePercentage", });
    internal_static_google_cloud_bigquery_storage_v1_ReadStream_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_bigquery_storage_v1_ReadStream_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_ReadStream_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_google_cloud_bigquery_storage_v1_WriteStream_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_bigquery_storage_v1_WriteStream_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_google_cloud_bigquery_storage_v1_WriteStream_descriptor,
        new java.lang.String[] { "Name", "Type", "CreateTime", "CommitTime", "TableSchema", "WriteMode", "Location", });
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
    com.google.cloud.bigquery.storage.v1.TableProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
