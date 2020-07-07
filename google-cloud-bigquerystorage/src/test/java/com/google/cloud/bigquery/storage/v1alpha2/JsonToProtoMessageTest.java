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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.cloud.bigquery.storage.test.SchemaTest.*;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonToProtoMessageTest {
  private static ImmutableMap<Descriptor, String> SimpleDescriptorsToDebugMessageTest =
      new ImmutableMap.Builder<Descriptor, String>()
          .put(BoolType.getDescriptor(), "boolean")
          .put(BytesType.getDescriptor(), "string")
          .put(Int64Type.getDescriptor(), "int64")
          .put(DoubleType.getDescriptor(), "double")
          .put(StringType.getDescriptor(), "string")
          .build();

  private static JSONObject[] simpleJSONObjects = {
    new JSONObject().put("test_field_type", 123),
    new JSONObject().put("test_field_type", 1.23),
    new JSONObject().put("test_field_type", true),
    new JSONObject().put("test_field_type", "test")
  };

  // private static JSONObject[] simpleJSONArrays = {
  //   new JSONObject()
  //       .put(
  //           "test_field_type",
  //           new JSONArray(new Long[] {Long.MAX_VALUE, Long.MIN_VALUE, Integer.MAX_VALUE,
  // Integer.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE, 0})),
  //   new JSONObject().put("test_field_type", new JSONArray(new Double[]{Double.MAX_VALUE,
  // Double.MIN_VALUE})),
  //   new JSONObject().put("test_field_type", new JSONArray("[true, false]")),
  //   new JSONObject().put("test_field_type", new JSONArray("[hello, test]"))
  // };

  private void isProtoJsonEqual(DynamicMessage proto, JSONObject json) {
    for (Map.Entry<FieldDescriptor, java.lang.Object> entry : proto.getAllFields().entrySet()) {
      FieldDescriptor key = entry.getKey();
      java.lang.Object value = entry.getValue();
      if (key.isRepeated()) {
        isProtoArrayJsonArrayEqual(key, value, json);
      } else {
        isProtoFieldJsonFieldEqual(key, value, json);
      }
    }
  }

  private void isProtoFieldJsonFieldEqual(
      FieldDescriptor key, java.lang.Object value, JSONObject json) {
    String fieldName = key.getName();
    switch (key.getType()) {
      case BOOL:
        assertTrue((Boolean) value == json.getBoolean(fieldName));
        break;
      case BYTES:
        assertTrue(Arrays.equals((byte[]) value, json.getString(fieldName).getBytes()));
        break;
      case INT64:
        assertTrue((long) value == json.getLong(fieldName));
        break;
      case STRING:
        assertTrue(((String) value).equals(json.getString(fieldName)));
        break;
      case DOUBLE:
        assertTrue((double) value == json.getDouble(fieldName));
        break;
      case MESSAGE:
        isProtoJsonEqual((DynamicMessage) value, json.getJSONObject(fieldName));
        break;
    }
  }

  private void isProtoArrayJsonArrayEqual(
      FieldDescriptor key, java.lang.Object value, JSONObject json) {
    String fieldName = key.getName();
    JSONArray jsonArray = json.getJSONArray(fieldName);
    switch (key.getType()) {
      case BOOL:
        List<Boolean> boolArr = (List<Boolean>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          assertTrue((boolArr.get(i) == jsonArray.getBoolean(i)));
        }
        break;
      case BYTES:
        List<byte[]> byteArr = (List<byte[]>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          assertTrue(Arrays.equals(byteArr.get(i), jsonArray.getString(i).getBytes()));
        }
        break;
      case INT64:
        List<Long> longArr = (List<Long>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          assertTrue((longArr.get(i) == jsonArray.getLong(i)));
        }
        break;
      case STRING:
        List<String> stringArr = (List<String>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          assertTrue(stringArr.get(i).equals(jsonArray.getString(i)));
        }
        break;
      case DOUBLE:
        List<Double> doubleArr = (List<Double>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          assertTrue((doubleArr.get(i) == jsonArray.getDouble(i)));
        }
        break;
      case MESSAGE:
        List<DynamicMessage> messageArr = (List<DynamicMessage>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          isProtoJsonEqual(messageArr.get(i), jsonArray.getJSONObject(i));
        }
        break;
    }
  }

  @Test
  public void testSimpleTypes() throws Exception {
    for (Map.Entry<Descriptor, String> entry : SimpleDescriptorsToDebugMessageTest.entrySet()) {
      int success = 0;
      for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg =
              JsonToProtoMessage.convertJsonToProtoMessage(entry.getKey(), json);
          isProtoJsonEqual(protoMsg, json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(
              e.getMessage(),
              "JSONObject does not have a " + entry.getValue() + " field at root.test_field_type.");
        }
      }
      assertEquals(1, success);
    }
  }

  @Test
  public void testBQSchemaToProtobufferStructSimple() throws Exception {
    JSONObject stringType = new JSONObject();
    stringType.put("test_field_type", "test");
    JSONObject json = new JSONObject();
    json.put("test_field_type", stringType);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(MessageType.getDescriptor(), json);
    isProtoJsonEqual(protoMsg, json);
  }

  @Test
  public void testBQSchemaToProtobufferStructSimpleFail() throws Exception {
    JSONObject stringType = new JSONObject();
    stringType.put("test_field_type", 1);
    JSONObject json = new JSONObject();
    json.put("test_field_type", stringType);
    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(MessageType.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(
          e.getMessage(),
          "JSONObject does not have a string field at root.test_field_type.test_field_type.");
    }
  }

  // @Test
  // public void testBQSchemaToProtobufferStructComplex() throws Exception {
  //   Table.TableFieldSchema bqBytes =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.BYTES)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("bytes")
  //           .build();
  //   Table.TableFieldSchema bqInt =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("int")
  //           .build();
  //   Table.TableFieldSchema record1 =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRUCT)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("record1")
  //           .addFields(0, bqInt)
  //           .build();
  //   Table.TableFieldSchema record =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRUCT)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("record")
  //           .addFields(0, bqInt)
  //           .addFields(1, bqBytes)
  //           .addFields(2, record1)
  //           .build();
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema =
  //       Table.TableSchema.newBuilder().addFields(0, record).addFields(1, bqDouble).build();
  //
  //   JSONObject jsonRecord1 = new JSONObject();
  //   jsonRecord1.put("int", 2048);
  //   JSONObject jsonRecord = new JSONObject();
  //   jsonRecord.put("int", 1024);
  //   jsonRecord.put("bytes", "testing");
  //   jsonRecord.put("record1", jsonRecord1);
  //   JSONObject json = new JSONObject();
  //   json.put("record", jsonRecord);
  //   json.put("float", 1.23);
  //
  //   Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //   DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   assertTrue(isProtoJsonEqual(protoMsg, json));
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferStructComplexFail() throws Exception {
  //   Table.TableFieldSchema bqInt =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("int")
  //           .build();
  //   Table.TableFieldSchema record =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRUCT)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("record")
  //           .addFields(0, bqInt)
  //           .build();
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema =
  //       Table.TableSchema.newBuilder().addFields(0, record).addFields(1, bqDouble).build();
  //
  //   JSONObject json = new JSONObject();
  //   json.put("record", 1.23);
  //   json.put("float", 1.23);
  //   try {
  //     Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //     DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals(e.getMessage(), "JSONObject does not have an object field at .record.");
  //   }
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferRepeatedSimple() throws Exception {
  //   for (Map.Entry<Table.TableFieldSchema.Type, String> entry :
  //       BQTableTypeToDebugMessage.entrySet()) {
  //     Table.TableFieldSchema tableFieldSchema =
  //         Table.TableFieldSchema.newBuilder()
  //             .setType(entry.getKey())
  //             .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //             .setName("test_field_type")
  //             .build();
  //     Table.TableSchema tableSchema =
  //         Table.TableSchema.newBuilder().addFields(0, tableFieldSchema).build();
  //     int success = 0;
  //     for (JSONObject json : simpleJSONArrays) {
  //       try {
  //         Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //         DynamicMessage protoMsg =
  //             JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //         success += 1;
  //       } catch (IllegalArgumentException e) {
  //         assertEquals(
  //             e.getMessage(),
  //             "JSONObject does not have a "
  //                 + entry.getValue()
  //                 + " field at .test_field_type[0].");
  //       }
  //     }
  //     assertEquals(1, success);
  //   }
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferRepeatedSimpleInt64() throws Exception {
  //   Table.TableFieldSchema bqByte =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("byte")
  //           .build();
  //   Table.TableFieldSchema bqShort =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("short")
  //           .build();
  //   Table.TableFieldSchema bqInt =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("int")
  //           .build();
  //   Table.TableFieldSchema bqLong =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("long")
  //           .build();
  //   Table.TableSchema tableSchema =
  //       Table.TableSchema.newBuilder()
  //           .addFields(0, bqByte)
  //           .addFields(1, bqShort)
  //           .addFields(2, bqInt)
  //           .addFields(3, bqLong)
  //           .build();
  //   JSONObject json = new JSONObject();
  //   byte[] byteArr = {(byte) 1, (byte) 2};
  //   short[] shortArr = {(short) 1, (short) 2};
  //   int[] intArr = {1, 2, 3, 4, 5};
  //   long[] longArr = {1L, 2L, 3L, 4L, 5L};
  //   json.put("byte", new JSONArray(byteArr));
  //   json.put("short", new JSONArray(shortArr));
  //   json.put("int", new JSONArray(intArr));
  //   json.put("long", new JSONArray(longArr));
  //   Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //   DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   assertTrue(isProtoJsonEqual(protoMsg, json));
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferRepeatedSimpleDouble() throws Exception {
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("double")
  //           .build();
  //   Table.TableFieldSchema bqFloat =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema =
  //       Table.TableSchema.newBuilder().addFields(0, bqDouble).addFields(1, bqFloat).build();
  //   JSONObject json = new JSONObject();
  //   double[] doubleArr = {1.1, 2.2, 3.3, 4.4, 5.5};
  //   float[] floatArr = {1.1f, 2.2f, 3.3f, 4.4f, 5.5f};
  //   json.put("double", new JSONArray(doubleArr));
  //   json.put("float", new JSONArray(floatArr));
  //   Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //   DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   assertTrue(isProtoJsonEqual(protoMsg, json));
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferRepeatedSimpleFail() throws Exception {
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema = Table.TableSchema.newBuilder().addFields(0,
  // bqDouble).build();
  //   JSONObject json = new JSONObject();
  //   json.put("float", new JSONArray("[1.1, 2.2, 3.3, hello, 4.4]"));
  //   try {
  //     Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //     DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals(e.getMessage(), "JSONObject does not have a double field at .float[3].");
  //   }
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferRepeatedComplex() throws Exception {
  //   Table.TableFieldSchema bqString =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRING)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("string")
  //           .build();
  //   Table.TableFieldSchema record =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRUCT)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("test_field_type")
  //           .addFields(0, bqString)
  //           .build();
  //   Table.TableFieldSchema bqInt =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("int")
  //           .build();
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema =
  //       Table.TableSchema.newBuilder()
  //           .addFields(0, bqDouble)
  //           .addFields(1, bqInt)
  //           .addFields(2, record)
  //           .build();
  //   JSONObject json = new JSONObject();
  //   double[] doubleArr = {1.1, 2.2, 3.3, 4.4, 5.5};
  //   String[] stringArr = {"hello", "this", "is", "a", "test"};
  //   int[] intArr = {1, 2, 3, 4, 5};
  //   json.put("float", new JSONArray(doubleArr));
  //   json.put("int", new JSONArray(intArr));
  //   JSONObject jsonRecord = new JSONObject();
  //   jsonRecord.put("string", new JSONArray(stringArr));
  //   json.put("test_field_type", jsonRecord);
  //   Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //   DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   assertTrue(isProtoJsonEqual(protoMsg, json));
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferRepeatedComplexFail() throws Exception {
  //   Table.TableFieldSchema bqInt =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.INT64)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("int")
  //           .build();
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.REPEATED)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema =
  //       Table.TableSchema.newBuilder().addFields(0, bqDouble).addFields(1, bqInt).build();
  //   JSONObject json = new JSONObject();
  //   int[] intArr = {1, 2, 3, 4, 5};
  //   json.put("float", "1");
  //   json.put("int", new JSONArray(intArr));
  //   try {
  //     Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //     DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals(e.getMessage(), "JSONObject does not have an array field at .float.");
  //   }
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferAllowUnknownFields() throws Exception {
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema = Table.TableSchema.newBuilder().addFields(0,
  // bqDouble).build();
  //   JSONObject json = new JSONObject();
  //   json.put("float", 1.1);
  //   json.put("string", "hello");
  //
  //   Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //   DynamicMessage protoMsg =
  //       JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json, true);
  //   assertTrue(isProtoJsonEqual(protoMsg, json));
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferAllowUnknownFieldsFail() throws Exception {
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema = Table.TableSchema.newBuilder().addFields(0,
  // bqDouble).build();
  //   JSONObject json = new JSONObject();
  //   json.put("float", 1.1);
  //   json.put("string", "hello");
  //   try {
  //     Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //     DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals(
  //         e.getMessage(),
  //         "JSONObject has unknown fields. Set allowUnknownFields to True to ignore unknown
  // fields.");
  //   }
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferRequiredFail() throws Exception {
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.REQUIRED)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema = Table.TableSchema.newBuilder().addFields(0,
  // bqDouble).build();
  //   JSONObject json = new JSONObject();
  //   json.put("float1", 1.1);
  //   try {
  //     Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //     DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals(e.getMessage(), "JSONObject does not have the required field .float.");
  //   }
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferTopLevelMatchFail() throws Exception {
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema = Table.TableSchema.newBuilder().addFields(0,
  // bqDouble).build();
  //   JSONObject json = new JSONObject();
  //   json.put("float1", 1.1);
  //   try {
  //     Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //     DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals(
  //         e.getMessage(),
  //         "There are no matching fields found for the JSONObject and the BigQuery table.");
  //   }
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferOptional() throws Exception {
  //   Table.TableFieldSchema StringType =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRING)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("test_field_type")
  //           .build();
  //   Table.TableFieldSchema tableFieldSchema =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRUCT)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("test_field_type")
  //           .addFields(0, StringType)
  //           .build();
  //   Table.TableFieldSchema actualStringType =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.STRING)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("test_field_type1")
  //           .build();
  //   Table.TableSchema tableSchema =
  //       Table.TableSchema.newBuilder()
  //           .addFields(0, tableFieldSchema)
  //           .addFields(1, actualStringType)
  //           .build();
  //
  //   JSONObject stringType = new JSONObject();
  //   stringType.put("test_field_type1", 1);
  //   JSONObject json = new JSONObject();
  //   json.put("test_field_type", stringType);
  //   json.put("test_field_type2", "hello");
  //
  //   Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //   DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   assertTrue(isProtoJsonEqual(protoMsg, json));
  // }
  //
  // @Test
  // public void testBQSchemaToProtobufferNullJson() throws Exception {
  //   Table.TableFieldSchema bqDouble =
  //       Table.TableFieldSchema.newBuilder()
  //           .setType(Table.TableFieldSchema.Type.DOUBLE)
  //           .setMode(Table.TableFieldSchema.Mode.NULLABLE)
  //           .setName("float")
  //           .build();
  //   Table.TableSchema tableSchema = Table.TableSchema.newBuilder().addFields(0,
  // bqDouble).build();
  //   JSONObject json = new JSONObject();
  //   json.put("float", JSONObject.NULL);
  //   try {
  //     Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
  //     DynamicMessage protoMsg = JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, json);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals(e.getMessage(), "JSONObject does not have a double field at .float.");
  //   }
  // }
}
