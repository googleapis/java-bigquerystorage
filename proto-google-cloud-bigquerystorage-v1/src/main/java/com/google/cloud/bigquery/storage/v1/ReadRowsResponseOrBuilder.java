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
// source: google/cloud/bigquery/storage/v1/storage.proto

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1;

public interface ReadRowsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.ReadRowsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Serialized row data in AVRO format.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.AvroRows avro_rows = 3;</code>
   *
   * @return Whether the avroRows field is set.
   */
  boolean hasAvroRows();
  /**
   *
   *
   * <pre>
   * Serialized row data in AVRO format.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.AvroRows avro_rows = 3;</code>
   *
   * @return The avroRows.
   */
  com.google.cloud.bigquery.storage.v1.AvroRows getAvroRows();
  /**
   *
   *
   * <pre>
   * Serialized row data in AVRO format.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.AvroRows avro_rows = 3;</code>
   */
  com.google.cloud.bigquery.storage.v1.AvroRowsOrBuilder getAvroRowsOrBuilder();

  /**
   *
   *
   * <pre>
   * Serialized row data in Arrow RecordBatch format.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.ArrowRecordBatch arrow_record_batch = 4;</code>
   *
   * @return Whether the arrowRecordBatch field is set.
   */
  boolean hasArrowRecordBatch();
  /**
   *
   *
   * <pre>
   * Serialized row data in Arrow RecordBatch format.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.ArrowRecordBatch arrow_record_batch = 4;</code>
   *
   * @return The arrowRecordBatch.
   */
  com.google.cloud.bigquery.storage.v1.ArrowRecordBatch getArrowRecordBatch();
  /**
   *
   *
   * <pre>
   * Serialized row data in Arrow RecordBatch format.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.ArrowRecordBatch arrow_record_batch = 4;</code>
   */
  com.google.cloud.bigquery.storage.v1.ArrowRecordBatchOrBuilder getArrowRecordBatchOrBuilder();

  /**
   *
   *
   * <pre>
   * Number of serialized rows in the rows block.
   * </pre>
   *
   * <code>int64 row_count = 6;</code>
   *
   * @return The rowCount.
   */
  long getRowCount();

  /**
   *
   *
   * <pre>
   * Statistics for the stream.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.StreamStats stats = 2;</code>
   *
   * @return Whether the stats field is set.
   */
  boolean hasStats();
  /**
   *
   *
   * <pre>
   * Statistics for the stream.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.StreamStats stats = 2;</code>
   *
   * @return The stats.
   */
  com.google.cloud.bigquery.storage.v1.StreamStats getStats();
  /**
   *
   *
   * <pre>
   * Statistics for the stream.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.StreamStats stats = 2;</code>
   */
  com.google.cloud.bigquery.storage.v1.StreamStatsOrBuilder getStatsOrBuilder();

  /**
   *
   *
   * <pre>
   * Throttling state. If unset, the latest response still describes
   * the current throttling status.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.ThrottleState throttle_state = 5;</code>
   *
   * @return Whether the throttleState field is set.
   */
  boolean hasThrottleState();
  /**
   *
   *
   * <pre>
   * Throttling state. If unset, the latest response still describes
   * the current throttling status.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.ThrottleState throttle_state = 5;</code>
   *
   * @return The throttleState.
   */
  com.google.cloud.bigquery.storage.v1.ThrottleState getThrottleState();
  /**
   *
   *
   * <pre>
   * Throttling state. If unset, the latest response still describes
   * the current throttling status.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1.ThrottleState throttle_state = 5;</code>
   */
  com.google.cloud.bigquery.storage.v1.ThrottleStateOrBuilder getThrottleStateOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. Avro schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1.AvroSchema avro_schema = 7 [(.google.api.field_behavior) = OUTPUT_ONLY];
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
   * .google.cloud.bigquery.storage.v1.AvroSchema avro_schema = 7 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The avroSchema.
   */
  com.google.cloud.bigquery.storage.v1.AvroSchema getAvroSchema();
  /**
   *
   *
   * <pre>
   * Output only. Avro schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1.AvroSchema avro_schema = 7 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1.AvroSchemaOrBuilder getAvroSchemaOrBuilder();

  /**
   *
   *
   * <pre>
   * Output only. Arrow schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1.ArrowSchema arrow_schema = 8 [(.google.api.field_behavior) = OUTPUT_ONLY];
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
   * .google.cloud.bigquery.storage.v1.ArrowSchema arrow_schema = 8 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   *
   * @return The arrowSchema.
   */
  com.google.cloud.bigquery.storage.v1.ArrowSchema getArrowSchema();
  /**
   *
   *
   * <pre>
   * Output only. Arrow schema.
   * </pre>
   *
   * <code>
   * .google.cloud.bigquery.storage.v1.ArrowSchema arrow_schema = 8 [(.google.api.field_behavior) = OUTPUT_ONLY];
   * </code>
   */
  com.google.cloud.bigquery.storage.v1.ArrowSchemaOrBuilder getArrowSchemaOrBuilder();

  /**
   *
   *
   * <pre>
   * Optional. If the row data in this ReadRowsResponse is compressed, then
   * uncompressed byte size is the original size of the uncompressed row data.
   * If it is set to a value greater than 0, then decompress into a buffer of
   * size uncompressed_byte_size using the compression codec that was requested
   * during session creation time and which is specified in
   * TableReadOptions.response_compression_codec in ReadSession.
   * This value is not set if no response_compression_codec was not requested
   * and it is -1 if the requested compression would not have reduced the size
   * of this ReadRowsResponse's row data. This attempts to match Apache Arrow's
   * behavior described here https://github.com/apache/arrow/issues/15102 where
   * the uncompressed length may be set to -1 to indicate that the data that
   * follows is not compressed, which can be useful for cases where compression
   * does not yield appreciable savings. When uncompressed_byte_size is not
   * greater than 0, the client should skip decompression.
   * </pre>
   *
   * <code>optional int64 uncompressed_byte_size = 9 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the uncompressedByteSize field is set.
   */
  boolean hasUncompressedByteSize();
  /**
   *
   *
   * <pre>
   * Optional. If the row data in this ReadRowsResponse is compressed, then
   * uncompressed byte size is the original size of the uncompressed row data.
   * If it is set to a value greater than 0, then decompress into a buffer of
   * size uncompressed_byte_size using the compression codec that was requested
   * during session creation time and which is specified in
   * TableReadOptions.response_compression_codec in ReadSession.
   * This value is not set if no response_compression_codec was not requested
   * and it is -1 if the requested compression would not have reduced the size
   * of this ReadRowsResponse's row data. This attempts to match Apache Arrow's
   * behavior described here https://github.com/apache/arrow/issues/15102 where
   * the uncompressed length may be set to -1 to indicate that the data that
   * follows is not compressed, which can be useful for cases where compression
   * does not yield appreciable savings. When uncompressed_byte_size is not
   * greater than 0, the client should skip decompression.
   * </pre>
   *
   * <code>optional int64 uncompressed_byte_size = 9 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The uncompressedByteSize.
   */
  long getUncompressedByteSize();

  com.google.cloud.bigquery.storage.v1.ReadRowsResponse.RowsCase getRowsCase();

  com.google.cloud.bigquery.storage.v1.ReadRowsResponse.SchemaCase getSchemaCase();
}
