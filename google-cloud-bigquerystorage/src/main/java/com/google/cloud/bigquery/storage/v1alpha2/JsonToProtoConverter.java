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
 * A class that checks the schema compatibility between user schema in proto descriptor and Bigquery
 * table schema. If this check is passed, then user can write to BigQuery table using the user
 * schema, otherwise the write will fail.
 *
 * <p>The implementation as of now is not complete, which measn, if this check passed, there is
 * still a possbility of writing will fail.
 */
public class JsonToProtoConverter {
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
  public static Descriptor BQTableSchemaToProtoSchema(Table.TableSchema BQTableSchema)
      throws Descriptors.DescriptorValidationException {
    Descriptor descriptor = BQTableSchemaToProtoSchemaImpl(BQTableSchema, "root");
    return descriptor;
  }

  /**
   * Implementation that converts a Table.TableSchema to a Descriptors.Descriptor object.
   *
   * @param BQTableSchema
   * @param scope Keeps track of current scope to prevent repeated naming while constructing
   *     descriptor.
   * @throws Descriptors.DescriptorValidationException
   */
  private static Descriptor BQTableSchemaToProtoSchemaImpl(
      Table.TableSchema BQTableSchema, String scope)
      throws Descriptors.DescriptorValidationException {
    List<FileDescriptor> dependenciesList = new ArrayList<FileDescriptor>();
    List<FieldDescriptorProto> fields = new ArrayList<FieldDescriptorProto>();
    int index = 1;
    for (Table.TableFieldSchema BQTableField : BQTableSchema.getFieldsList()) {
      if (BQTableField.getType() == Table.TableFieldSchema.Type.STRUCT) {
        String currentScope = scope + BQTableField.getName();
        dependenciesList.add(
            BQTableSchemaToProtoSchemaImpl(
                    Table.TableSchema.newBuilder()
                        .addAllFields(BQTableField.getFieldsList())
                        .build(),
                    currentScope)
                .getFile());
        fields.add(BQStructToProtoMessage(BQTableField, index++, currentScope));
      } else {
        fields.add(BQTableFieldToProtoField(BQTableField, index++));
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
  private static FieldDescriptorProto BQTableFieldToProtoField(
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
  private static FieldDescriptorProto BQStructToProtoMessage(
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
