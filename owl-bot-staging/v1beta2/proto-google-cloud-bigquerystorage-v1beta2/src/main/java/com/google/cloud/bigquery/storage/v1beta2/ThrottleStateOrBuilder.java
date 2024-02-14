// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1beta2/storage.proto

// Protobuf Java Version: 3.25.2
package com.google.cloud.bigquery.storage.v1beta2;

public interface ThrottleStateOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1beta2.ThrottleState)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * How much this connection is being throttled. Zero means no throttling,
   * 100 means fully throttled.
   * </pre>
   *
   * <code>int32 throttle_percent = 1;</code>
   * @return The throttlePercent.
   */
  int getThrottlePercent();
}
