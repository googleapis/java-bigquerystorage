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
package com.google.cloud.bigquery.storage.v1;

import com.google.cloud.bigquery.storage.v1.TableFieldSchema.Mode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Converts a BQ table schema to protobuf descriptor. All field names will be converted to lowercase
 * when constructing the protobuf descriptor. The mapping between field types and field modes are
 * shown in the ImmutableMaps below.
 */
public class BQTableSchemaToProtoDescriptor {
  private static ImmutableMap<TableFieldSchema.Mode, FieldDescriptorProto.Label>
      BQTableSchemaModeMap =
          ImmutableMap.of(
              TableFieldSchema.Mode.NULLABLE, FieldDescriptorProto.Label.LABEL_OPTIONAL,
              TableFieldSchema.Mode.REPEATED, FieldDescriptorProto.Label.LABEL_REPEATED,
              TableFieldSchema.Mode.REQUIRED, FieldDescriptorProto.Label.LABEL_REQUIRED);

  private static ImmutableMap<TableFieldSchema.Type, FieldDescriptorProto.Type>
      BQTableSchemaTypeMap =
          new ImmutableMap.Builder<TableFieldSchema.Type, FieldDescriptorProto.Type>()
              .put(TableFieldSchema.Type.BOOL, FieldDescriptorProto.Type.TYPE_BOOL)
              .put(TableFieldSchema.Type.BYTES, FieldDescriptorProto.Type.TYPE_BYTES)
              .put(TableFieldSchema.Type.DATE, FieldDescriptorProto.Type.TYPE_INT32)
              .put(TableFieldSchema.Type.DATETIME, FieldDescriptorProto.Type.TYPE_INT64)
              .put(TableFieldSchema.Type.DOUBLE, FieldDescriptorProto.Type.TYPE_DOUBLE)
              .put(TableFieldSchema.Type.GEOGRAPHY, FieldDescriptorProto.Type.TYPE_STRING)
              .put(TableFieldSchema.Type.INT64, FieldDescriptorProto.Type.TYPE_INT64)
              .put(TableFieldSchema.Type.NUMERIC, FieldDescriptorProto.Type.TYPE_BYTES)
              .put(TableFieldSchema.Type.BIGNUMERIC, FieldDescriptorProto.Type.TYPE_BYTES)
              .put(TableFieldSchema.Type.STRING, FieldDescriptorProto.Type.TYPE_STRING)
              .put(TableFieldSchema.Type.STRUCT, FieldDescriptorProto.Type.TYPE_MESSAGE)
              .put(TableFieldSchema.Type.TIME, FieldDescriptorProto.Type.TYPE_INT64)
              .put(TableFieldSchema.Type.TIMESTAMP, FieldDescriptorProto.Type.TYPE_INT64)
              .put(TableFieldSchema.Type.JSON, FieldDescriptorProto.Type.TYPE_STRING)
              .put(TableFieldSchema.Type.INTERVAL, FieldDescriptorProto.Type.TYPE_STRING)
              .put(TableFieldSchema.Type.RANGE, FieldDescriptorProto.Type.TYPE_MESSAGE)
              .build();

  /**
   * Converts TableFieldSchema to a Descriptors.Descriptor object.
   *
   * @param BQTableSchema
   * @throws Descriptors.DescriptorValidationException
   */
  public static Descriptor convertBQTableSchemaToProtoDescriptor(TableSchema BQTableSchema)
      throws Descriptors.DescriptorValidationException {
    Preconditions.checkNotNull(BQTableSchema, "BQTableSchema is null.");
    return convertBQTableSchemaToProtoDescriptorImpl(
        BQTableSchema, "root", new HashMap<ImmutableList<TableFieldSchema>, Descriptor>());
  }

  /**
   * Converts a TableFieldSchema to a Descriptors.Descriptor object.
   *
   * @param BQTableSchema
   * @param scope Keeps track of current scope to prevent repeated naming while constructing
   *     descriptor.
   * @param dependencyMap Stores already constructed descriptors to prevent reconstruction
   * @throws Descriptors.DescriptorValidationException
   */
  private static Descriptor convertBQTableSchemaToProtoDescriptorImpl(
      TableSchema BQTableSchema,
      String scope,
      HashMap<ImmutableList<TableFieldSchema>, Descriptor> dependencyMap)
      throws Descriptors.DescriptorValidationException, IllegalArgumentException {
    List<FileDescriptor> dependenciesList = new ArrayList<FileDescriptor>();
    List<FieldDescriptorProto> fields = new ArrayList<FieldDescriptorProto>();
    int index = 1;
    for (TableFieldSchema BQTableField : BQTableSchema.getFieldsList()) {
      String scopeName =
          BigQuerySchemaUtil.isProtoCompatible(BQTableField.getName())
              ? BQTableField.getName()
              : BigQuerySchemaUtil.generatePlaceholderFieldName(BQTableField.getName());
      String currentScope = scope + "__" + scopeName;
      switch (BQTableField.getType()) {
        case STRUCT:
          ImmutableList<TableFieldSchema> fieldList =
              ImmutableList.copyOf(BQTableField.getFieldsList());
          if (dependencyMap.containsKey(fieldList)) {
            Descriptor descriptor = dependencyMap.get(fieldList);
            dependenciesList.add(descriptor.getFile());
            fields.add(
                convertBQTableFieldToProtoField(BQTableField, index++, descriptor.getName()));
          } else {
            Descriptor descriptor =
                convertBQTableSchemaToProtoDescriptorImpl(
                    TableSchema.newBuilder().addAllFields(fieldList).build(),
                    currentScope,
                    dependencyMap);
            dependenciesList.add(descriptor.getFile());
            dependencyMap.put(fieldList, descriptor);
            fields.add(convertBQTableFieldToProtoField(BQTableField, index++, currentScope));
          }
          break;
        case RANGE:
          switch (BQTableField.getRangeElementType().getType()) {
            case DATE:
            case DATETIME:
            case TIMESTAMP:
              break;
            default:
              throw new IllegalArgumentException(
                  String.format(
                      "Error: %s of type RANGE requires range element type (DATE, DATETIME,"
                          + " TIMESTAMP)",
                      currentScope));
          }
          // For RANGE type, expliclitly add the fields start and end of the same FieldElementType
          // as it is not expliclity defined in the TableSchema.
          ImmutableList<TableFieldSchema> rangeFields =
              ImmutableList.of(
                  TableFieldSchema.newBuilder()
                      .setType(BQTableField.getRangeElementType().getType())
                      .setName("start")
                      .setMode(Mode.NULLABLE)
                      .build(),
                  TableFieldSchema.newBuilder()
                      .setType(BQTableField.getRangeElementType().getType())
                      .setName("end")
                      .setMode(Mode.NULLABLE)
                      .build());

          if (dependencyMap.containsKey(rangeFields)) {
            Descriptor descriptor = dependencyMap.get(rangeFields);
            dependenciesList.add(descriptor.getFile());
            fields.add(
                convertBQTableFieldToProtoField(BQTableField, index++, descriptor.getName()));
          } else {
            Descriptor descriptor =
                convertBQTableSchemaToProtoDescriptorImpl(
                    TableSchema.newBuilder().addAllFields(rangeFields).build(),
                    currentScope,
                    dependencyMap);
            dependenciesList.add(descriptor.getFile());
            dependencyMap.put(rangeFields, descriptor);
            fields.add(convertBQTableFieldToProtoField(BQTableField, index++, currentScope));
          }
          break;
        default:
          fields.add(convertBQTableFieldToProtoField(BQTableField, index++, currentScope));
          break;
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
   * Converts a BQTableField to ProtoField
   *
   * @param BQTableField BQ Field used to construct a FieldDescriptorProto
   * @param index Index for protobuf fields.
   * @param scope used to name descriptors
   */
  private static FieldDescriptorProto convertBQTableFieldToProtoField(
      TableFieldSchema BQTableField, int index, String scope) {
    TableFieldSchema.Mode mode = BQTableField.getMode();
    String fieldName = BQTableField.getName().toLowerCase();

    FieldDescriptorProto.Builder fieldDescriptor =
        FieldDescriptorProto.newBuilder()
            .setName(fieldName)
            .setNumber(index)
            .setLabel((FieldDescriptorProto.Label) BQTableSchemaModeMap.get(mode));

    switch (BQTableField.getType()) {
      case STRUCT:
        fieldDescriptor.setTypeName(scope);
        break;
      case RANGE:
        fieldDescriptor.setType(
            (FieldDescriptorProto.Type) BQTableSchemaTypeMap.get(BQTableField.getType()));
        fieldDescriptor.setTypeName(scope);
        break;
      default:
        fieldDescriptor.setType(
            (FieldDescriptorProto.Type) BQTableSchemaTypeMap.get(BQTableField.getType()));
        break;
    }

    // Sets columnName annotation when field name is not proto comptaible.
    if (!BigQuerySchemaUtil.isProtoCompatible(fieldName)) {
      fieldDescriptor.setName(BigQuerySchemaUtil.generatePlaceholderFieldName(fieldName));

      // The following work around (instead of setting FieldOptions directly) for when
      // FieldOptions.Builder changes from GeneratedMessageV3 in 3.25 to GeneratedMessage in 4.28 as
      // it no longer depends on FieldOptions.
      Message.Builder fieldOptionBuilder = FieldOptions.newBuilder();
      fieldOptionBuilder.setField(AnnotationsProto.columnName.getDescriptor(), fieldName);
      fieldDescriptor.setOptions((FieldOptions) fieldOptionBuilder.build());
    }
    return fieldDescriptor.build();
  }
}
