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

import com.google.cloud.bigquery.storage.test.JsonTest.*;
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
  private static ImmutableMap<Descriptor, String> AllTypesToDebugMessageTest =
      new ImmutableMap.Builder<Descriptor, String>()
          .put(BoolType.getDescriptor(), "boolean")
          .put(BytesType.getDescriptor(), "string")
          .put(Int64Type.getDescriptor(), "int64")
          .put(DoubleType.getDescriptor(), "double")
          .put(StringType.getDescriptor(), "string")
          .put(RepeatedType.getDescriptor(), "array")
          .put(ObjectType.getDescriptor(), "object")
          .build();

  private static ImmutableMap<Descriptor, String> AllRepeatedTypesToDebugMessageTest =
      new ImmutableMap.Builder<Descriptor, String>()
          .put(RepeatedBool.getDescriptor(), "boolean")
          .put(RepeatedBytes.getDescriptor(), "string")
          .put(RepeatedInt64.getDescriptor(), "int64")
          .put(RepeatedDouble.getDescriptor(), "double")
          .put(RepeatedObject.getDescriptor(), "object")
          .build();

  private static JSONObject[] simpleJSONObjects = {
    new JSONObject().put("test_field_type", 123),
    new JSONObject().put("test_field_type", 1.23),
    new JSONObject().put("test_field_type", true),
    new JSONObject().put("test_field_type", "test"),
    new JSONObject().put("test_field_type", new JSONArray("[1, 2, 3]")),
    new JSONObject().put("test_field_type", new JSONObject().put("test_int", 1))
  };

  private static JSONObject[] simpleJSONArrays = {
    new JSONObject()
        .put(
            "test_repeated",
            new JSONArray(
                new Long[] {
                  Long.MAX_VALUE,
                  Long.MIN_VALUE,
                  (long) Integer.MAX_VALUE,
                  (long) Integer.MIN_VALUE,
                  (long) Short.MAX_VALUE,
                  (long) Short.MIN_VALUE,
                  (long) Byte.MAX_VALUE,
                  (long) Byte.MIN_VALUE,
                  0L
                })),
    new JSONObject()
        .put(
            "test_repeated",
            new JSONArray(
                new Double[] {
                  Double.MAX_VALUE,
                  Double.MIN_VALUE,
                  (double) Float.MAX_VALUE,
                  (double) Float.MIN_VALUE
                })),
    new JSONObject().put("test_repeated", new JSONArray(new Boolean[] {true, false})),
    new JSONObject().put("test_repeated", new JSONArray(new String[] {"hello", "test"})),
    new JSONObject()
        .put(
            "test_repeated",
            new JSONArray(
                new JSONObject[] {
                  new JSONObject().put("test_int", 1),
                  new JSONObject().put("test_int", 2),
                  new JSONObject().put("test_int", 3)
                }))
  };

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
  public void testAllTypes() throws Exception {
    for (Map.Entry<Descriptor, String> entry : AllTypesToDebugMessageTest.entrySet()) {
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
  public void testRepeatedWithLimits() throws Exception {
    for (Map.Entry<Descriptor, String> entry : AllRepeatedTypesToDebugMessageTest.entrySet()) {
      int success = 0;
      for (JSONObject json : simpleJSONArrays) {
        try {
          DynamicMessage protoMsg =
              JsonToProtoMessage.convertJsonToProtoMessage(entry.getKey(), json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(
              e.getMessage(),
              "JSONObject does not have a "
                  + entry.getValue()
                  + " field at root.test_repeated[0].");
        }
      }
      assertEquals(1, success);
    }
  }

  @Test
  public void testStructSimple() throws Exception {
    JSONObject stringType = new JSONObject();
    stringType.put("test_field_type", "test");
    JSONObject json = new JSONObject();
    json.put("test_field_type", stringType);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(MessageType.getDescriptor(), json);
    isProtoJsonEqual(protoMsg, json);
  }

  @Test
  public void testStructSimpleFail() throws Exception {
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

  @Test
  public void testStructComplex() throws Exception {
    JSONObject complexLvl2 = new JSONObject();
    complexLvl2.put("test_int", 3);

    JSONObject complexLvl1 = new JSONObject();
    complexLvl1.put("test_int", 2);
    complexLvl1.put("complexLvl2", complexLvl2);

    JSONObject json = new JSONObject();
    json.put("test_int", 1);
    json.put("test_string", new JSONArray(new String[] {"a", "b", "c"}));
    json.put("test_bytes", "hello");
    json.put("test_bool", true);
    json.put("test_double", new JSONArray(new Double[] {1.1, 2.2, 3.3, 4.4}));
    json.put("complexLvl1", complexLvl1);
    json.put("complexLvl2", complexLvl2);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(ComplexRoot.getDescriptor(), json);
    isProtoJsonEqual(protoMsg, json);
  }

  @Test
  public void testInt64() throws Exception {
    JSONObject json = new JSONObject();
    json.put("byte", (byte) 1);
    json.put("short", (short) 1);
    json.put("int", 1);
    json.put("long", 1L);
    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(TestInt64.getDescriptor(), json);
    isProtoJsonEqual(protoMsg, json);
  }

  @Test
  public void testDouble() throws Exception {
    JSONObject json = new JSONObject();
    json.put("double", 1.2);
    json.put("float", 3.4f);
    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(TestDouble.getDescriptor(), json);
    isProtoJsonEqual(protoMsg, json);
  }

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
