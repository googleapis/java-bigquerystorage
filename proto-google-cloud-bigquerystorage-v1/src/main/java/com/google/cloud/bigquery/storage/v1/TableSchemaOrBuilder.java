/*
 * Copyright 2025 Google LLC
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
// source: google/cloud/bigquery/storage/v1/table.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.bigquery.storage.v1;

public interface TableSchemaOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.bigquery.storage.v1.TableSchema)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Describes the fields in a table.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.TableFieldSchema fields = 1;</code>
   */
  java.util.List<com.google.cloud.bigquery.storage.v1.TableFieldSchema> getFieldsList();
  /**
   *
   *
   * <pre>
   * Describes the fields in a table.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.TableFieldSchema fields = 1;</code>
   */
  com.google.cloud.bigquery.storage.v1.TableFieldSchema getFields(int index);
  /**
   *
   *
   * <pre>
   * Describes the fields in a table.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.TableFieldSchema fields = 1;</code>
   */
  int getFieldsCount();
  /**
   *
   *
   * <pre>
   * Describes the fields in a table.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.TableFieldSchema fields = 1;</code>
   */
  java.util.List<? extends com.google.cloud.bigquery.storage.v1.TableFieldSchemaOrBuilder>
      getFieldsOrBuilderList();
  /**
   *
   *
   * <pre>
   * Describes the fields in a table.
   * </pre>
   *
   * <code>repeated .google.cloud.bigquery.storage.v1.TableFieldSchema fields = 1;</code>
   */
  com.google.cloud.bigquery.storage.v1.TableFieldSchemaOrBuilder getFieldsOrBuilder(int index);
}
