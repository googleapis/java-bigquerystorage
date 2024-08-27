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
// source: google/cloud/bigquery/storage/v1beta2/stream.proto

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1beta2;

public interface ReadSessionOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.ReadSession)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Output only. Unique identifier for the session, in the form
   * `projects/{project_id}/locations/{location}/sessions/{session_id}`.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The name.
   */
  java.lang.String getName();
  /**
   *
   *
   * <pre>
   * Output only. Unique identifier for the session, in the form
   * `projects/{project_id}/locations/{location}/sessions/{session_id}`.
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = OUTPUT_ONLY];</code>
   *
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   *
   *
   * <pre>
   * Output only. Time at which the session becomes invalid. After this time, subsequent
   * requests to read this Session will return errors. The expire_time is
   * automatically assigned and currently cannot be specified or updated.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp expire_time = 2 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the expireTime field is set.
   */
  boolean hasExpireTime();
  /**
   *
   *
   * <pre>
   * Output only. Time at which the session becomes invalid. After this time, subsequent
   * requests to read this Session will return errors. The expire_time is
   * automatically assigned and currently cannot be specified or updated.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp expire_time = 2 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The expireTime.
   */
  com.google.protobuf.Timestamp getExpireTime();
  /**
   *
   *
   * <pre>
   * Output only. Time at which the session becomes invalid. After this time, subsequent
   * requests to read this Session will return errors. The expire_time is
   * automatically assigned and currently cannot be specified or updated.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp expire_time = 2 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.protobuf.TimestampOrBuilder getExpireTimeOrBuilder();

  /**
   *
   *
   * <pre>
   * Immutable. Data format of the output data.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.DataFormat data_format = 3 [(.google.api.field_behavior) = IMMUTABLE];
   * </code>
   *
   * @return The enum numeric value on the wire for dataFormat.
   */
  int getDataFormatValue();
  /**
   *
   *
   * <pre>
   * Immutable. Data format of the output data.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.DataFormat data_format = 3 [(.google.api.field_behavior) = IMMUTABLE];
   * </code>
   *
   * @return The dataFormat.
   */
  com.google.cloud.bigquery.storage.v1beta2.DataFormat getDataFormat();

  /**
   *
   *
   * <pre>
   * Output only. Avro schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.AvroSchema avro_schema = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the avroSchema field is set.
   */
  boolean hasAvroSchema();
  /**
   *
   *
   * <pre>
   * Output only. Avro schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.AvroSchema avro_schema = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The avroSchema.
   */
  com.google.cloud.bigquery.storage.v1beta2.AvroSchema getAvroSchema();
  /**
   *
   *
   * <pre>
   * Output only. Avro schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.AvroSchema avro_schema = 4 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1beta2.AvroSchemaOrBuilder getAvroSchemaOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. Arrow schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ArrowSchema arrow_schema = 5 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return Whether the arrowSchema field is set.
   */
  boolean hasArrowSchema();
  /**
   *
   *
   * <pre>
   * Output only. Arrow schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ArrowSchema arrow_schema = 5 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The arrowSchema.
   */
  com.google.cloud.bigquery.storage.v1beta2.ArrowSchema getArrowSchema();
  /**
   *
   *
   * <pre>
   * Output only. Arrow schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ArrowSchema arrow_schema = 5 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1beta2.ArrowSchemaOrBuilder getArrowSchemaOrBuilder();

  /**
   *
   *
   * <pre>
   * Immutable. Table that this ReadSession is reading from, in the form
   * `projects/{project_id}/datasets/{dataset_id}/tables/{table_id}
   * </pre>
   *
   * <code>
   * string table = 6 [(.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The table.
   */
  java.lang.String getTable();
  /**
   *
   *
   * <pre>
   * Immutable. Table that this ReadSession is reading from, in the form
   * `projects/{project_id}/datasets/{dataset_id}/tables/{table_id}
   * </pre>
   *
   * <code>
   * string table = 6 [(.google.api.field_behavior) = IMMUTABLE, (.google.api.resource_reference) = { ... }
   * </code>
   *
   * @return The bytes for table.
   */
  com.google.protobuf.ByteString getTableBytes();

  /**
   *
   *
   * <pre>
   * Optional. Any modifiers which are applied when reading from the specified table.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ReadSession.TableModifiers table_modifiers = 7 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the tableModifiers field is set.
   */
  boolean hasTableModifiers();
  /**
   *
   *
   * <pre>
   * Optional. Any modifiers which are applied when reading from the specified table.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ReadSession.TableModifiers table_modifiers = 7 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The tableModifiers.
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadSession.TableModifiers getTableModifiers();
  /**
   *
   *
   * <pre>
   * Optional. Any modifiers which are applied when reading from the specified table.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ReadSession.TableModifiers table_modifiers = 7 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadSession.TableModifiersOrBuilder
      getTableModifiersOrBuilder();

  /**
   *
   *
   * <pre>
   * Optional. Read options for this session (e.g. column selection, filters).
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ReadSession.TableReadOptions read_options = 8 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the readOptions field is set.
   */
  boolean hasReadOptions();
  /**
   *
   *
   * <pre>
   * Optional. Read options for this session (e.g. column selection, filters).
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ReadSession.TableReadOptions read_options = 8 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The readOptions.
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadSession.TableReadOptions getReadOptions();
  /**
   *
   *
   * <pre>
   * Optional. Read options for this session (e.g. column selection, filters).
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1beta2.ReadSession.TableReadOptions read_options = 8 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadSession.TableReadOptionsOrBuilder
      getReadOptionsOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. A list of streams created with the session.
   *
   * At least one stream is created with the session. In the future, larger
   * request_stream_count values *may* result in this list being unpopulated,
   * in that case, the user will need to use a List method to get the streams
   * instead, which is not yet available.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1beta2.ReadStream streams = 10 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  java.util.List<com.google.cloud.bigquery.storage.v1beta2.ReadStream> getStreamsList();
  /**
   *
   *
   * <pre>
   * Output only. A list of streams created with the session.
   *
   * At least one stream is created with the session. In the future, larger
   * request_stream_count values *may* result in this list being unpopulated,
   * in that case, the user will need to use a List method to get the streams
   * instead, which is not yet available.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1beta2.ReadStream streams = 10 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadStream getStreams(int index);
  /**
   *
   *
   * <pre>
   * Output only. A list of streams created with the session.
   *
   * At least one stream is created with the session. In the future, larger
   * request_stream_count values *may* result in this list being unpopulated,
   * in that case, the user will need to use a List method to get the streams
   * instead, which is not yet available.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1beta2.ReadStream streams = 10 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  int getStreamsCount();
  /**
   *
   *
   * <pre>
   * Output only. A list of streams created with the session.
   *
   * At least one stream is created with the session. In the future, larger
   * request_stream_count values *may* result in this list being unpopulated,
   * in that case, the user will need to use a List method to get the streams
   * instead, which is not yet available.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1beta2.ReadStream streams = 10 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  java.util.List<? extends com.google.cloud.bigquery.storage.v1beta2.ReadStreamOrBuilder>
      getStreamsOrBuilderList();
  /**
   *
   *
   * <pre>
   * Output only. A list of streams created with the session.
   *
   * At least one stream is created with the session. In the future, larger
   * request_stream_count values *may* result in this list being unpopulated,
   * in that case, the user will need to use a List method to get the streams
   * instead, which is not yet available.
   * </pre>
   *
   * <code>
   * repeated .google.cloud.bigquery.storage.v1beta2.ReadStream streams = 10 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1beta2.ReadStreamOrBuilder getStreamsOrBuilder(int index);

  com.google.cloud.bigquery.storage.v1beta2.ReadSession.SchemaCase getSchemaCase();
}
