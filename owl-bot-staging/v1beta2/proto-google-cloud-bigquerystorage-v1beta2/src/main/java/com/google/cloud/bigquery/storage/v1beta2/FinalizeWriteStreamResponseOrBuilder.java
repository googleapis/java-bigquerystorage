// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1beta2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.cloud.bigquery.storage.v1beta2;

public interface FinalizeWriteStreamResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.FinalizeWriteStreamResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Number of rows in the finalized stream.
   * </pre>
   *
   * <code>int64 row_count = 1;</code>
   * @return The rowCount.
   */
  long getRowCount();
}
