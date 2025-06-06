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
// source: google/cloud/bigquery/storage/v1/annotations.proto

// Protobuf Java Version: 3.25.8
package com.google.cloud.bigquery.storage.v1;

public final class AnnotationsProto {
  private AnnotationsProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
    registry.add(com.google.cloud.bigquery.storage.v1.AnnotationsProto.columnName);
  }

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static final int COLUMN_NAME_FIELD_NUMBER = 454943157;

  /**
   *
   *
   * <pre>
   * Setting the column_name extension allows users to reference
   * bigquery column independently of the field name in the protocol buffer
   * message.
   *
   * The intended use of this annotation is to reference a destination column
   * named using characters unavailable for protobuf field names (e.g. unicode
   * characters).
   *
   * More details about BigQuery naming limitations can be found here:
   * https://cloud.google.com/bigquery/docs/schemas#column_names
   *
   * This extension is currently experimental.
   * </pre>
   *
   * <code>extend .google.protobuf.FieldOptions { ... }</code>
   */
  public static final com.google.protobuf.GeneratedMessage.GeneratedExtension<
          com.google.protobuf.DescriptorProtos.FieldOptions, java.lang.String>
      columnName =
          com.google.protobuf.GeneratedMessage.newFileScopedGeneratedExtension(
              java.lang.String.class, null);

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n2google/cloud/bigquery/storage/v1/annot"
          + "ations.proto\022 google.cloud.bigquery.stor"
          + "age.v1\032 google/protobuf/descriptor.proto"
          + ":9\n\013column_name\022\035.google.protobuf.FieldO"
          + "ptions\030\265\303\367\330\001 \001(\t\210\001\001B\300\001\n$com.google.cloud"
          + ".bigquery.storage.v1B\020AnnotationsProtoP\001"
          + "Z>cloud.google.com/go/bigquery/storage/a"
          + "piv1/storagepb;storagepb\252\002 Google.Cloud."
          + "BigQuery.Storage.V1\312\002 Google\\Cloud\\BigQu"
          + "ery\\Storage\\V1b\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.protobuf.DescriptorProtos.getDescriptor(),
            });
    columnName.internalInit(descriptor.getExtensions().get(0));
    com.google.protobuf.DescriptorProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
