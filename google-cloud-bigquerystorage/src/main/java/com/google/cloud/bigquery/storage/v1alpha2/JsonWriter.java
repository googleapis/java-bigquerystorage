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

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import com.google.api.gax.rpc.InvalidArgumentException;

/**
 * A class that checks the schema compatibility between user schema in proto descriptor and Bigquery
 * table schema. If this check is passed, then user can write to BigQuery table using the user
 * schema, otherwise the write will fail.
 *
 * <p>The implementation as of now is not complete, which measn, if this check passed, there is
 * still a possbility of writing will fail.
 */
public class JsonWriter {
  private BigQuery bigquery;
  private static JsonWriter writer;
  private static String tablePatternString = "projects/([^/]+)/datasets/([^/]+)/tables/([^/]+)";
  private static Pattern tablePattern = Pattern.compile(tablePatternString);
  private static Map modeMap = Collections.unmodifiableMap(new HashMap<Field.Mode, FieldDescriptorProto.Label>() {{
        put(Field.Mode.NULLABLE, FieldDescriptorProto.Label.LABEL_OPTIONAL);
        put(Field.Mode.REPEATED, FieldDescriptorProto.Label.LABEL_REPEATED);
        put(Field.Mode.REQUIRED, FieldDescriptorProto.Label.LABEL_REQUIRED);
    }});
  private static Map typeMap = Collections.unmodifiableMap(new HashMap<LegacySQLTypeName, FieldDescriptorProto.Type>() {{
        put(LegacySQLTypeName.BOOLEAN, FieldDescriptorProto.Type.TYPE_BOOL);
        put(LegacySQLTypeName.BYTES, FieldDescriptorProto.Type.TYPE_BYTES);
        put(LegacySQLTypeName.DATE, FieldDescriptorProto.Type.TYPE_INT64);
        put(LegacySQLTypeName.DATETIME, FieldDescriptorProto.Type.TYPE_INT64);
        put(LegacySQLTypeName.FLOAT, FieldDescriptorProto.Type.TYPE_DOUBLE);
        put(LegacySQLTypeName.GEOGRAPHY, FieldDescriptorProto.Type.TYPE_BYTES);
        put(LegacySQLTypeName.INTEGER, FieldDescriptorProto.Type.TYPE_INT64);
        put(LegacySQLTypeName.NUMERIC, FieldDescriptorProto.Type.TYPE_DOUBLE);
        put(LegacySQLTypeName.RECORD, FieldDescriptorProto.Type.TYPE_MESSAGE);
        put(LegacySQLTypeName.STRING, FieldDescriptorProto.Type.TYPE_STRING);
        put(LegacySQLTypeName.TIME, FieldDescriptorProto.Type.TYPE_INT64);
        put(LegacySQLTypeName.TIMESTAMP, FieldDescriptorProto.Type.TYPE_INT64);
    }});

  private JsonWriter(BigQuery bigquery) {
    this.bigquery = bigquery;
  }

  /**
   * Gets a singleton {code SchemaCompact} object.
   *
   * @return
   */
  public static JsonWriter getInstance() {
    if (writer == null) {
      RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
      writer = new JsonWriter(bigqueryHelper.getOptions().getService());
    }
    return writer;
  }

  /**
   * Gets a {code SchemaCompact} object with custom BigQuery stub.
   *
   * @param bigquery
   * @return
   */
  @VisibleForTesting
  public static JsonWriter getInstance(BigQuery bigquery) {
    return new JsonWriter(bigquery);
  }

  private TableId getTableId(String tableName) {
    Matcher matcher = tablePattern.matcher(tableName);
    if (!matcher.matches() || matcher.groupCount() != 3) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
    return TableId.of(matcher.group(1), matcher.group(2), matcher.group(3));
  }

  public DynamicMessage BQSchemaToProtoMessage(String tableName, JSONObject json)
      throws IOException, InterruptedException, InvalidArgumentException, Descriptors.DescriptorValidationException {
        Descriptor descriptor = BQSchemaToProtoSchema(tableName);
        DynamicMessage protoMsg = protoSchematoProtoMessage(descriptor, json, "");
        return protoMsg;
  }

  public Descriptor BQSchemaToProtoSchema(String tableName)
    throws IllegalArgumentException, Descriptors.DescriptorValidationException {
      TableId tableId = getTableId(tableName);
      Table table = bigquery.getTable(tableId);
      Schema BQSchema = table.getDefinition().getSchema();
      String BQSchemaName = tableId.getTable();
      Descriptor descriptor = BQSchemaToProtoSchemaImpl(BQSchema, BQSchemaName);
      return descriptor;
  }

  /**
   * Converts a BQ schema to a proto Schema by mapping BQ fields to proto fields, then constructing
   * the message through DescriptorProtos.
   *
   * @param BQSchema        BQ schema that is to be converted to a protobuf descriptor.
   * @param scope           Used to construct FieldDescriptorProtos.
   * @throws Descriptors.DescriptorValidationException if descriptor cannot be constructed.
   */
  private Descriptor BQSchemaToProtoSchemaImpl(Schema BQSchema, String scope)
      throws Descriptors.DescriptorValidationException {
      List<FileDescriptor> dependenciesList = new ArrayList<FileDescriptor>();
      List<FieldDescriptorProto> fields = new ArrayList<FieldDescriptorProto>();
      int index = 1;
      for (Field BQField : BQSchema.getFields()) {
        if (BQField.getType() == LegacySQLTypeName.RECORD) {
          String currentScope = scope + BQField.getName();
          dependenciesList.add(BQSchemaToProtoSchemaImpl(Schema.of(BQField.getSubFields()), currentScope).getFile());
          fields.add(BQRecordToProtoMessage(BQField, index++, currentScope));
        } else {
          fields.add(BQFieldToProtoField(BQField, index++));
        }
      }
      FileDescriptor[] dependenciesArray = new FileDescriptor[dependenciesList.size()];
      dependenciesArray = dependenciesList.toArray(dependenciesArray);
      DescriptorProto descriptorProto = DescriptorProto.newBuilder().setName(scope).addAllField(fields).build();
      FileDescriptorProto fileDescriptorProto = FileDescriptorProto.newBuilder().addMessageType(descriptorProto).build();
      FileDescriptor fileDescriptor = FileDescriptor.buildFrom(fileDescriptorProto, dependenciesArray);
      Descriptor descriptor = fileDescriptor.findMessageTypeByName(scope);
      return descriptor;
  }

  /**
   * Constructs a FieldDescriptorProto for simple BQ fields.
   *
   * @param BQField       BQ Field used to construct a FieldDescriptorProto
   * @param index       Index for protobuf fields.
   */
  private FieldDescriptorProto BQFieldToProtoField(Field BQField, int index) {
      String fieldName = BQField.getName();
      Field.Mode mode = BQField.getMode();
      return FieldDescriptorProto.newBuilder()
                                 .setName(fieldName)
                                 .setType((FieldDescriptorProto.Type) typeMap.get(BQField.getType()))
                                 .setLabel((FieldDescriptorProto.Label) modeMap.get(mode))
                                 .setNumber(index)
                                 .build();
  }

  /**
   * Constructs a FieldDescriptorProto for a record type BQ field.
   *
   * @param BQField     BQ Field used to construct a FieldDescriptorProto
   * @param index       Index for protobuf fields.
   * @param scope       Need scope to prevent naming issues (same name, but different message)
   */
  private FieldDescriptorProto BQRecordToProtoMessage(Field BQField, int index, String scope) {
      String fieldName = BQField.getName();
      Field.Mode mode = BQField.getMode();
      return FieldDescriptorProto.newBuilder()
                                 .setName(fieldName)
                                 .setTypeName(scope)
                                 .setLabel((FieldDescriptorProto.Label) modeMap.get(mode))
                                 .setNumber(index)
                                 .build();
  }

  private DynamicMessage protoSchematoProtoMessage(Descriptors.Descriptor protoSchema, JSONObject json, String jsonScope)
    throws IllegalArgumentException{
    DynamicMessage.Builder protoMsg = DynamicMessage.newBuilder(protoSchema);

    for (Descriptors.FieldDescriptor field : protoSchema.getFields()) {
      String fieldName = field.getName();
      String currentScope = jsonScope + "." + fieldName;

      if (!json.has(fieldName)) {
        if (field.isRequired()) {
          throw new IllegalArgumentException("JSONObject does not have the required field " + currentScope +  ".");
        }
        else {
          continue;
        }
      }

      if (!field.isRepeated()) {
        fillField(protoMsg, field, json, currentScope);
      }
      else {
        fillRepeatedField(protoMsg, field, json, currentScope);
      }
    }
    return protoMsg.build();
  }

  private void fillField(DynamicMessage.Builder protoMsg, Descriptors.FieldDescriptor field, JSONObject json, String currentScope)
    throws IllegalArgumentException {

    String fieldName = field.getName();
    switch(field.getType()) {
      case BOOL:
        try {
          protoMsg.setField(field, new Boolean(json.getBoolean(fieldName)));
        } catch (JSONException e) {
          throw new IllegalArgumentException("JSONObject does not have the boolean field " + currentScope + ".");
        }
        break;
      case BYTES:
        try {
          protoMsg.setField(field, json.getString(fieldName).getBytes());
        } catch (JSONException e) {
          throw new IllegalArgumentException("JSONObject does not have the string field " + currentScope + ".");
        }
        break;
      case INT64:
        try {
          java.lang.Object val = json.get(fieldName);
          if (val instanceof Integer || val instanceof Long) {
            protoMsg.setField(field, new Long((Long)val));
          }
          else {
            throw new IllegalArgumentException("JSONObject does not have the int64 field " + currentScope + ".");
          }
        } catch (JSONException e) {
          throw new IllegalArgumentException("JSONObject does not have the int64 field " + currentScope + ".");
        }
        break;
      case STRING:
        try {
          protoMsg.setField(field, json.getString(fieldName));
        } catch (JSONException e) {
          throw new IllegalArgumentException("JSONObject does not have the string field " + currentScope + ".");
        }
        break;
      case DOUBLE:
        try {
          protoMsg.setField(field, new Double(json.getDouble(fieldName)));
        } catch (JSONException e) {
          throw new IllegalArgumentException("JSONObject does not have the double field " + currentScope + ".");
        }
        break;
      case MESSAGE:
        Message.Builder message = protoMsg.newBuilderForField(field);
        try {
          protoMsg.setField(field, protoSchematoProtoMessage(field.getMessageType(), json.getJSONObject(fieldName), currentScope));
        } catch (JSONException e) {
          throw new IllegalArgumentException("JSONObject does not have the object field " + currentScope + ".");
        }
        break;
    }
  }

  private void fillRepeatedField(DynamicMessage.Builder protoMsg, Descriptors.FieldDescriptor field, JSONObject json, String currentScope)
    throws IllegalArgumentException {
    String fieldName = field.getName();
    JSONArray jsonArray;
    try {
      jsonArray = json.getJSONArray(fieldName);
    } catch (JSONException e) {
      throw new IllegalArgumentException("JSONObject does not have the array field " + currentScope + ".");
    }

    switch(field.getType()) {
      case BOOL:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, new Boolean(jsonArray.getBoolean(i)));
          } catch (JSONException e) {
            throw new IllegalArgumentException("JSONObject does not have the boolean field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case BYTES:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, jsonArray.getString(i).getBytes());
          } catch (JSONException e) {
            throw new IllegalArgumentException("JSONObject does not have the string field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case INT64:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            java.lang.Object val = jsonArray.get(i);
            if (val instanceof Integer || val instanceof Long) {
              protoMsg.addRepeatedField(field, new Long((Long)val));
            }
            else {
              throw new IllegalArgumentException("JSONObject does not have the int64 field " + currentScope + "[" + i + "]" + ".");
            }
          } catch (JSONException e) {
            throw new IllegalArgumentException("JSONObject does not have the int64 field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case STRING:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, jsonArray.getString(i));
          } catch (JSONException e) {
            throw new IllegalArgumentException("JSONObject does not have the string field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case DOUBLE:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, new Double(jsonArray.getDouble(i)));
          } catch (JSONException e) {
            throw new IllegalArgumentException("JSONObject does not have the double field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case MESSAGE:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            Message.Builder message = protoMsg.newBuilderForField(field);
            protoMsg.addRepeatedField(field, protoSchematoProtoMessage(field.getMessageType(), jsonArray.getJSONObject(i), currentScope));
          } catch (JSONException e) {
            throw new IllegalArgumentException("JSONObject does not have the object field " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
    }
  }
}
