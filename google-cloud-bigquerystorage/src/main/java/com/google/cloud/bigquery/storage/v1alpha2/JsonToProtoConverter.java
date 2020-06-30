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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class that checks the schema compatibility between user schema in proto descriptor and Bigquery
 * table schema. If this check is passed, then user can write to BigQuery table using the user
 * schema, otherwise the write will fail.
 *
 * <p>The implementation as of now is not complete, which measn, if this check passed, there is
 * still a possbility of writing will fail.
 */
public class JsonToProtoConverter {
  private static Map modeMap =
      Collections.unmodifiableMap(
          new HashMap<Table.TableFieldSchema.Mode, FieldDescriptorProto.Label>() {
            {
              put(Table.TableFieldSchema.Mode.NULLABLE, FieldDescriptorProto.Label.LABEL_OPTIONAL);
              put(Table.TableFieldSchema.Mode.REPEATED, FieldDescriptorProto.Label.LABEL_REPEATED);
              put(Table.TableFieldSchema.Mode.REQUIRED, FieldDescriptorProto.Label.LABEL_REQUIRED);
            }
          });
  private static Map typeMap =
      Collections.unmodifiableMap(
          new HashMap<Table.TableFieldSchema.Type, FieldDescriptorProto.Type>() {
            {
              put(Table.TableFieldSchema.Type.BOOL, FieldDescriptorProto.Type.TYPE_BOOL);
              put(Table.TableFieldSchema.Type.BYTES, FieldDescriptorProto.Type.TYPE_BYTES);
              put(Table.TableFieldSchema.Type.DATE, FieldDescriptorProto.Type.TYPE_INT64);
              put(Table.TableFieldSchema.Type.DATETIME, FieldDescriptorProto.Type.TYPE_INT64);
              put(Table.TableFieldSchema.Type.DOUBLE, FieldDescriptorProto.Type.TYPE_DOUBLE);
              put(Table.TableFieldSchema.Type.GEOGRAPHY, FieldDescriptorProto.Type.TYPE_BYTES);
              put(Table.TableFieldSchema.Type.INT64, FieldDescriptorProto.Type.TYPE_INT64);
              put(Table.TableFieldSchema.Type.NUMERIC, FieldDescriptorProto.Type.TYPE_DOUBLE);
              put(Table.TableFieldSchema.Type.STRING, FieldDescriptorProto.Type.TYPE_STRING);
              put(Table.TableFieldSchema.Type.STRUCT, FieldDescriptorProto.Type.TYPE_MESSAGE);
              put(Table.TableFieldSchema.Type.TIME, FieldDescriptorProto.Type.TYPE_INT64);
              put(Table.TableFieldSchema.Type.TIMESTAMP, FieldDescriptorProto.Type.TYPE_INT64);
            }
          });

  public static DynamicMessage BQTableSchemaToProtoMessage(
      Table.TableSchema BQTableSchema, JSONObject json)
      throws IOException, InterruptedException, InvalidArgumentException,
          Descriptors.DescriptorValidationException {
    Descriptor descriptor = BQTableSchemaToProtoSchema(BQTableSchema);
    DynamicMessage protoMsg = protoSchemaToProtoMessage(descriptor, json);
    return protoMsg;
  }

  public static Descriptor BQTableSchemaToProtoSchema(Table.TableSchema BQTableSchema)
      throws IllegalArgumentException, Descriptors.DescriptorValidationException {
    Descriptor descriptor = BQTableSchemaToProtoSchemaImpl(BQTableSchema, "root");
    return descriptor;
  }

  /**
   * Converts a BQ schema to a proto Schema by mapping BQ fields to proto fields, then constructing
   * the message through DescriptorProtos.
   *
   * @param BQTableSchema BQ schema that is to be converted to a protobuf descriptor.
   * @param scope Used to construct FieldDescriptorProtos.
   * @throws Descriptors.DescriptorValidationException if descriptor cannot be constructed.
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
   * Constructs a FieldDescriptorProto for simple BQ fields.
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
        .setType((FieldDescriptorProto.Type) typeMap.get(BQTableField.getType()))
        .setLabel((FieldDescriptorProto.Label) modeMap.get(mode))
        .setNumber(index)
        .build();
  }

  /**
   * Constructs a FieldDescriptorProto for a Struct type BQ field.
   *
   * @param BQTableField BQ Field used to construct a FieldDescriptorProto
   * @param index Index for protobuf fields.
   * @param scope Need scope to prevent naming issues (same name, but different message)
   */
  private static FieldDescriptorProto BQStructToProtoMessage(
      Table.TableFieldSchema BQTableField, int index, String scope) {
    String fieldName = BQTableField.getName();
    Table.TableFieldSchema.Mode mode = BQTableField.getMode();
    return FieldDescriptorProto.newBuilder()
        .setName(fieldName)
        .setTypeName(scope)
        .setLabel((FieldDescriptorProto.Label) modeMap.get(mode))
        .setNumber(index)
        .build();
  }

  public static DynamicMessage protoSchemaToProtoMessage(
      Descriptors.Descriptor protoSchema, JSONObject json) throws IllegalArgumentException {
    return protoSchemaToProtoMessageImpl(protoSchema, json, "");
  }

  private static DynamicMessage protoSchemaToProtoMessageImpl(
      Descriptors.Descriptor protoSchema, JSONObject json, String jsonScope)
      throws IllegalArgumentException {
    DynamicMessage.Builder protoMsg = DynamicMessage.newBuilder(protoSchema);

    for (Descriptors.FieldDescriptor field : protoSchema.getFields()) {
      String fieldName = field.getName();
      String currentScope = jsonScope + "." + fieldName;

      if (!json.has(fieldName)) {
        if (field.isRequired()) {
          throw new IllegalArgumentException(
              "JSONObject does not have the required field " + currentScope + ".");
        } else {
          continue;
        }
      }

      if (!field.isRepeated()) {
        fillField(protoMsg, field, json, currentScope);
      } else {
        fillRepeatedField(protoMsg, field, json, currentScope);
      }
    }
    return protoMsg.build();
  }

  private static void fillField(
      DynamicMessage.Builder protoMsg,
      Descriptors.FieldDescriptor field,
      JSONObject json,
      String currentScope)
      throws IllegalArgumentException {

    String fieldName = field.getName();
    switch (field.getType()) {
      case BOOL:
        try {
          protoMsg.setField(field, new Boolean(json.getBoolean(fieldName)));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have the boolean field " + currentScope + ".");
        }
        break;
      case BYTES:
        try {
          protoMsg.setField(field, json.getString(fieldName).getBytes());
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have the string field " + currentScope + ".");
        }
        break;
      case INT64:
        try {
          java.lang.Object val = json.get(fieldName);
          if (val instanceof Integer) {
            protoMsg.setField(field, new Long((Integer) val));
          } else if (val instanceof Long) {
            protoMsg.setField(field, new Long((Long) val));
          } else {
            throw new IllegalArgumentException(
                "JSONObject does not have the int64 field " + currentScope + ".");
          }
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have the int64 field " + currentScope + ".");
        }
        break;
      case STRING:
        try {
          protoMsg.setField(field, json.getString(fieldName));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have the string field " + currentScope + ".");
        }
        break;
      case DOUBLE:
        try {
          protoMsg.setField(field, new Double(json.getDouble(fieldName)));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have the double field " + currentScope + ".");
        }
        break;
      case MESSAGE:
        Message.Builder message = protoMsg.newBuilderForField(field);
        try {
          protoMsg.setField(
              field,
              protoSchemaToProtoMessageImpl(
                  field.getMessageType(), json.getJSONObject(fieldName), currentScope));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have the object field " + currentScope + ".");
        }
        break;
    }
  }

  private static void fillRepeatedField(
      DynamicMessage.Builder protoMsg,
      Descriptors.FieldDescriptor field,
      JSONObject json,
      String currentScope)
      throws IllegalArgumentException {
    String fieldName = field.getName();
    JSONArray jsonArray;
    try {
      jsonArray = json.getJSONArray(fieldName);
    } catch (JSONException e) {
      throw new IllegalArgumentException(
          "JSONObject does not have the array field " + currentScope + ".");
    }

    switch (field.getType()) {
      case BOOL:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, new Boolean(jsonArray.getBoolean(i)));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have the boolean field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case BYTES:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, jsonArray.getString(i).getBytes());
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have the string field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case INT64:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            java.lang.Object val = json.get(fieldName);
            if (val instanceof Integer) {
              protoMsg.setField(field, new Long((Integer) val));
            } else if (val instanceof Long) {
              protoMsg.setField(field, new Long((Long) val));
            } else {
              throw new IllegalArgumentException(
                  "JSONObject does not have the int64 field " + currentScope + "[" + i + "]" + ".");
            }
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have the int64 field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case STRING:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, jsonArray.getString(i));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have the string field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case DOUBLE:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, new Double(jsonArray.getDouble(i)));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have the double field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case MESSAGE:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            Message.Builder message = protoMsg.newBuilderForField(field);
            protoMsg.addRepeatedField(
                field,
                protoSchemaToProtoMessageImpl(
                    field.getMessageType(), jsonArray.getJSONObject(i), currentScope));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have the object field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
    }
  }
}
