// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/storage.proto

// Protobuf Java Version: 3.25.3
package com.google.cloud.bigquery.storage.v1;

public interface ReadRowsRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.ReadRowsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Required. Stream to read rows from.
   * </pre>
   *
   * <code>string read_stream = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The readStream.
   */
  java.lang.String getReadStream();
  /**
   * <pre>
   * Required. Stream to read rows from.
   * </pre>
   *
   * <code>string read_stream = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for readStream.
   */
  com.google.protobuf.ByteString
      getReadStreamBytes();

  /**
   * <pre>
   * The offset requested must be less than the last row read from Read.
   * Requesting a larger offset is undefined. If not specified, start reading
   * from offset zero.
   * </pre>
   *
   * <code>int64 offset = 2;</code>
   * @return The offset.
   */
  long getOffset();
}
