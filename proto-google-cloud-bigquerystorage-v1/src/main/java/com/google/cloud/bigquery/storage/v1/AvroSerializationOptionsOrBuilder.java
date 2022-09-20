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
// source: google/cloud/bigquery/storage/v1/avro.proto

package com.google.cloud.bigquery.storage.v1;

public interface AvroSerializationOptionsOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.AvroSerializationOptions)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Enable displayName attribute in Avro schema.
   * The Avro specification requires field names to be alphanumeric.  By
   * default, in cases when column names do not conform to these requirements
   * (e.g. non-ascii unicode codepoints) and Avro is requested as an output
   * format, the CreateReadSession call will fail.
   * Setting this field to true, populates avro field names with a placeholder
   * value and populates a "displayName" attribute for every avro field with the
   * original column name.
   * </pre>
   *
   * <code>bool enable_display_name_attribute = 1;</code>
   *
   * @return The enableDisplayNameAttribute.
   */
  boolean getEnableDisplayNameAttribute();
}
