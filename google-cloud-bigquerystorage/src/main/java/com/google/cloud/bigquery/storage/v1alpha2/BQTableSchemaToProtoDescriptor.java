/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery.storage.v1alpha2;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * This class converts a BQ table schema to protobuf descriptor. The mapping between field types and field modes are shown in the ImmutableMaps below.
 */
public class BQTableSchemaToProtoSchema {
  private static ImmutableMap<Table.TableFieldSchema.Mode, FieldDescriptorProto.Label>
      BQTableSchemaModeMap =
          ImmutableMap.of(
              Table.TableFieldSchema.Mode.NULLABLE, FieldDescriptorProto.Label.LABEL_OPTIONAL,
              Table.TableFieldSchema.Mode.REPEATED, FieldDescriptorProto.Label.LABEL_REPEATED,
              Table.TableFieldSchema.Mode.REQUIRED, FieldDescriptorProto.Label.LABEL_REQUIRED);

  private static ImmutableMap<Table.TableFieldSchema.Type, FieldDescriptorProto.Type>
      BQTableSchemaTypeMap =
          new ImmutableMap.Builder<Table.TableFieldSchema.Type, FieldDescriptorProto.Type>()
              .put(Table.TableFieldSchema.Type.BOOL, FieldDescriptorProto.Type.TYPE_BOOL)
              .put(Table.TableFieldSchema.Type.BYTES, FieldDescriptorProto.Type.TYPE_BYTES)
              .put(Table.TableFieldSchema.Type.DATE, FieldDescriptorProto.Type.TYPE_INT64)
              .put(Table.TableFieldSchema.Type.DATETIME, FieldDescriptorProto.Type.TYPE_INT64)
              .put(Table.TableFieldSchema.Type.DOUBLE, FieldDescriptorProto.Type.TYPE_DOUBLE)
              .put(Table.TableFieldSchema.Type.GEOGRAPHY, FieldDescriptorProto.Type.TYPE_BYTES)
              .put(Table.TableFieldSchema.Type.INT64, FieldDescriptorProto.Type.TYPE_INT64)
              .put(Table.TableFieldSchema.Type.NUMERIC, FieldDescriptorProto.Type.TYPE_BYTES)
              .put(Table.TableFieldSchema.Type.STRING, FieldDescriptorProto.Type.TYPE_STRING)
              .put(Table.TableFieldSchema.Type.STRUCT, FieldDescriptorProto.Type.TYPE_MESSAGE)
              .put(Table.TableFieldSchema.Type.TIME, FieldDescriptorProto.Type.TYPE_INT64)
              .put(Table.TableFieldSchema.Type.TIMESTAMP, FieldDescriptorProto.Type.TYPE_INT64)
              .build();

  /**
   * Converts Table.TableSchema to a Descriptors.Descriptor object.
   *
   * @param BQTableSchema
   * @throws Descriptors.DescriptorValidationException
   */
  public static Descriptor ConvertBQTableSchemaToProtoSchema(Table.TableSchema BQTableSchema)
      throws Descriptors.DescriptorValidationException {
    return ConvertBQTableSchemaToProtoSchemaImpl(BQTableSchema, "root");
  }

  /**
   * Implementation that converts a Table.TableSchema to a Descriptors.Descriptor object.
   *
   * @param BQTableSchema
   * @param scope Keeps track of current scope to prevent repeated naming while constructing
   *     descriptor.
   * @throws Descriptors.DescriptorValidationException
   */
  private static Descriptor ConvertBQTableSchemaToProtoSchemaImpl(
      Table.TableSchema BQTableSchema, String scope)
      throws Descriptors.DescriptorValidationException {
    List<FileDescriptor> dependenciesList = new ArrayList<FileDescriptor>();
    List<FieldDescriptorProto> fields = new ArrayList<FieldDescriptorProto>();
    int index = 1;
    for (Table.TableFieldSchema BQTableField : BQTableSchema.getFieldsList()) {
      if (BQTableField.getType() == Table.TableFieldSchema.Type.STRUCT) {
        String currentScope = scope + BQTableField.getName();
        dependenciesList.add(
            ConvertBQTableSchemaToProtoSchemaImpl(
                    Table.TableSchema.newBuilder()
                        .addAllFields(BQTableField.getFieldsList())
                        .build(),
                    currentScope)
                .getFile());
        fields.add(ConvertBQStructToProtoMessage(BQTableField, index++, currentScope));
      } else {
        fields.add(ConvertBQTableFieldToProtoField(BQTableField, index++));
      }
    }
    FileDescriptor[] dependenciesArray = new FileDescriptor[dependenciesList.size()];
    dependenciesArray = dependenciesList.toArray(dependenciesArray);
    DescriptorProto descriptorProto =
        DescriptorProto.newBuilder().setName(scope).addAllField(fields).build();
    FileDescriptorProto fileDescriptorProto =
        FileDescriptorProto.newBuilder().addMessageType(descriptorProto).build();
    FileDescriptor fileDescriptor =
        FileDescriptor.buildFrom(fileDescriptorProto, dependenciesArray);
    Descriptor descriptor = fileDescriptor.findMessageTypeByName(scope);
    return descriptor;
  }

  /**
   * Constructs a FieldDescriptorProto for non-struct BQ fields.
   *
   * @param BQTableField BQ Field used to construct a FieldDescriptorProto
   * @param index Index for protobuf fields.
   */
  private static FieldDescriptorProto ConvertBQTableFieldToProtoField(
      Table.TableFieldSchema BQTableField, int index) {
    String fieldName = BQTableField.getName();
    Table.TableFieldSchema.Mode mode = BQTableField.getMode();
    return FieldDescriptorProto.newBuilder()
        .setName(fieldName)
        .setType((FieldDescriptorProto.Type) BQTableSchemaTypeMap.get(BQTableField.getType()))
        .setLabel((FieldDescriptorProto.Label) BQTableSchemaModeMap.get(mode))
        .setNumber(index)
        .build();
  }

  /**
   * Constructs a FieldDescriptorProto for a Struct type BQ field.
   *
   * @param BQTableField BQ Field used to construct a FieldDescriptorProto
   * @param index Index for protobuf fields.
   * @param scope Need scope to prevent naming issues
   */
  private static FieldDescriptorProto ConvertBQStructToProtoMessage(
      Table.TableFieldSchema BQTableField, int index, String scope) {
    String fieldName = BQTableField.getName();
    Table.TableFieldSchema.Mode mode = BQTableField.getMode();
    return FieldDescriptorProto.newBuilder()
        .setName(fieldName)
        .setTypeName(scope)
        .setLabel((FieldDescriptorProto.Label) BQTableSchemaModeMap.get(mode))
        .setNumber(index)
        .build();
  }
}
