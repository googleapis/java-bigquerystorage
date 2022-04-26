// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/storage.proto

package com.google.cloud.bigquery.storage.v1;

public interface FlushRowsResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.FlushRowsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The rows before this offset (including this offset) are flushed.
   * </pre>
   *
   * <code>int64 offset = 1;</code>
   * @return The offset.
   */
  long getOffset();
}
