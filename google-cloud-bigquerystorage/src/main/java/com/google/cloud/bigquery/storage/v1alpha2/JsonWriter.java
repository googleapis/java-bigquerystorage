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

  public Descriptor append(String tableName, JSONObject json)
      throws IOException, InterruptedException, InvalidArgumentException, Descriptors.DescriptorValidationException {
        TableId tableId = getTableId(tableName);
        Table table = bigquery.getTable(tableId);
        Schema BQSchema = table.getDefinition().getSchema();
        String BQSchemaName = tableId.getTable();
        Descriptor descriptor = BQSchemaToProtoSchema(BQSchema, BQSchemaName, BQSchemaName);
        // System.out.println(descriptor.getName());
        testPrint(descriptor, descriptor.getName());
        return descriptor;
        //
      }

  private void testPrint(Descriptor descriptor, String scope) {
    for (FieldDescriptor field : descriptor.getFields()) {
      if (field.getType() == FieldDescriptor.Type.MESSAGE) {
        System.out.println(field.getName());
        testPrint(field.getMessageType(), scope + field.getName());
      } else {
        System.out.println(field.getName());
      }
    }
  }

  /**
   * Checks if the userSchema is compatible with the table's current schema for writing. The current
   * implementatoin is not complete. If the check failed, the write couldn't succeed.
   *
   * @param tableName The name of the table to write to.
   * @param userSchema The schema user uses to append data.
   * @throws IllegalArgumentException the check failed.
   */
  public Descriptor BQSchemaToProtoSchema(Schema BQSchema, String BQSchemaName, String scope)
      throws IllegalArgumentException, Descriptors.DescriptorValidationException {
      List<FileDescriptor> dependenciesList = new ArrayList<FileDescriptor>();
      List<FieldDescriptorProto> fields = new ArrayList<FieldDescriptorProto>();
      int index = 1;
      for (Field BQField : BQSchema.getFields()) {
        if (BQField.getType() == LegacySQLTypeName.RECORD) {
          String currentScope = scope + BQField.getName();
          dependenciesList.add(BQSchemaToProtoSchema(Schema.of(BQField.getSubFields()), BQField.getName(), currentScope).getFile());
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

  private FieldDescriptorProto BQRecordToProtoMessage(Field BQField, int index, String scope) {
      String fieldName = BQField.getName();
      Field.Mode mode = BQField.getMode();
      return FieldDescriptorProto.newBuilder()
                                 .setName("aaa")
                                 .setTypeName(scope)
                                 .setLabel((FieldDescriptorProto.Label) modeMap.get(mode))
                                 .setNumber(index)
                                 .build();
  }

  private DynamicMessage createProto(Descriptors.Descriptor protoSchema, JSONObject json, String jsonScope)
    throws IllegalArgumentException{
      // List<DynamicMessage> protoRows = new ArrayList<DynamicMessage>();
      // protoRows.add(createProto(myDescriptor, json, BQSchemaName));
    DynamicMessage.Builder protoMsg = DynamicMessage.newBuilder(protoSchema);

    for (Descriptors.FieldDescriptor field : protoSchema.getFields()) {
      String fieldName = field.getName();
      String currentScope = jsonScope + "." + fieldName;
      if (!json.has(fieldName)) {
        throw new IllegalArgumentException("JSONObject does not have the field " + currentScope +  ".");
      }
      switch(field.getType().toString()) {
        case "BOOL":
          try {
            protoMsg.setField(protoSchema.findFieldByName(fieldName), new Boolean(json.getBoolean(fieldName)));
          } catch (JSONException e) {
            throw new IllegalArgumentException(currentScope + " is not a boolean value.");
          }
          break;
        case "BYTES":
          try {
            protoMsg.setField(protoSchema.findFieldByName(fieldName), json.getString(fieldName).getBytes());
          } catch (JSONException e) {
            throw new IllegalArgumentException(currentScope + " is not a string value.");
          }
          break;
        case "INT64":
          try {
            protoMsg.setField(protoSchema.findFieldByName(fieldName), new Long(json.getInt(fieldName)));
          } catch (JSONException e) {
            throw new IllegalArgumentException(currentScope + " is not a integer value.");
          }
          break;
        case "STRING":
          try {
            protoMsg.setField(protoSchema.findFieldByName(fieldName), json.getString(fieldName));
          } catch (JSONException e) {
            throw new IllegalArgumentException(currentScope + " is not a string value.");
          }
          break;
        case "DOUBLE":
          try {
            protoMsg.setField(protoSchema.findFieldByName(fieldName), new Double(json.getNumber(fieldName).doubleValue()));
          } catch (JSONException e) {
            throw new IllegalArgumentException(currentScope + " is not a double value.");
          }
          break;
        case "MESSAGE":
          // Need to do some recursive construction
          break;
        default:
          System.out.println(field.getType().toString());
      }
    }
    return protoMsg.build();
  }
}
