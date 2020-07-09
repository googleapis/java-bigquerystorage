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
import java.util.HashMap;
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
          .put(Int32Type.getDescriptor(), "int32")
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
          .put(RepeatedInt32.getDescriptor(), "int32")
          .put(RepeatedDouble.getDescriptor(), "double")
          .put(RepeatedObject.getDescriptor(), "object")
          .build();

  private static JSONObject[] simpleJSONObjects = {
    new JSONObject().put("test_field_type", Long.MAX_VALUE),
    new JSONObject().put("test_field_type", Integer.MAX_VALUE),
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
                new Integer[] {
                  (int) Integer.MAX_VALUE,
                  (int) Integer.MIN_VALUE,
                  (int) Short.MAX_VALUE,
                  (int) Short.MIN_VALUE,
                  (int) Byte.MAX_VALUE,
                  (int) Byte.MIN_VALUE,
                  0
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

  private void AreMatchingFieldsFilledIn(DynamicMessage proto, JSONObject json) {
    HashMap<String, String> jsonLowercaseNameToActualName = new HashMap<String, String>();
    String[] actualNames = JSONObject.getNames(json);
    for (int i = 0; i < actualNames.length; i++) {
      jsonLowercaseNameToActualName.put(actualNames[i].toLowerCase(), actualNames[i]);
    }
    for (Map.Entry<FieldDescriptor, java.lang.Object> entry : proto.getAllFields().entrySet()) {
      FieldDescriptor key = entry.getKey();
      java.lang.Object value = entry.getValue();
      if (key.isRepeated()) {
        isProtoArrayJsonArrayEqual(key, value, json, jsonLowercaseNameToActualName);
      } else {
        isProtoFieldJsonFieldEqual(key, value, json, jsonLowercaseNameToActualName);
      }
    }
  }

  private void isProtoFieldJsonFieldEqual(
      FieldDescriptor key,
      java.lang.Object value,
      JSONObject json,
      HashMap<String, String> jsonLowercaseNameToActualName) {
    String fieldName = jsonLowercaseNameToActualName.get(key.getName().toLowerCase());
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
      case INT32:
        assertTrue((int) value == json.getInt(fieldName));
        break;
      case STRING:
        assertTrue(((String) value).equals(json.getString(fieldName)));
        break;
      case DOUBLE:
        assertTrue((double) value == json.getDouble(fieldName));
        break;
      case MESSAGE:
        AreMatchingFieldsFilledIn((DynamicMessage) value, json.getJSONObject(fieldName));
        break;
    }
  }

  private void isProtoArrayJsonArrayEqual(
      FieldDescriptor key,
      java.lang.Object value,
      JSONObject json,
      HashMap<String, String> jsonLowercaseNameToActualName) {
    String fieldName = jsonLowercaseNameToActualName.get(key.getName().toLowerCase());
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
      case INT32:
        List<Integer> intArr = (List<Integer>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          assertTrue((intArr.get(i) == jsonArray.getInt(i)));
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
          AreMatchingFieldsFilledIn(messageArr.get(i), jsonArray.getJSONObject(i));
        }
        break;
    }
  }

  @Test
  public void testDifferentNameCasing() throws Exception {
    JSONObject json = new JSONObject();
    json.put("bYtE", (byte) 1);
    json.put("SHORT", (short) 1);
    json.put("inT", 1);
    json.put("lONg", 1L);
    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(TestInt64.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
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
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testInt32() throws Exception {
    JSONObject json = new JSONObject();
    json.put("byte", (byte) 1);
    json.put("short", (short) 1);
    json.put("int", 1);
    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(TestInt32.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testInt32NotMatchInt64() throws Exception {
    JSONObject json = new JSONObject();
    json.put("byte", (byte) 1);
    json.put("short", (short) 1);
    json.put("int", 1L);
    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(TestInt32.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "JSONObject does not have a int32 field at root.int.");
    }
  }

  @Test
  public void testDouble() throws Exception {
    JSONObject json = new JSONObject();
    json.put("double", 1.2);
    json.put("float", 3.4f);
    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(TestDouble.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testAllTypes() throws Exception {
    for (Map.Entry<Descriptor, String> entry : AllTypesToDebugMessageTest.entrySet()) {
      int success = 0;
      for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg =
              JsonToProtoMessage.convertJsonToProtoMessage(entry.getKey(), json);
          AreMatchingFieldsFilledIn(protoMsg, json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(
              e.getMessage(),
              "JSONObject does not have a " + entry.getValue() + " field at root.test_field_type.");
        }
      }
      if (entry.getKey() == Int64Type.getDescriptor()) {
        assertEquals(2, success);
      } else {
        assertEquals(1, success);
      }
    }
  }

  @Test
  public void testAllRepeatedTypesWithLimits() throws Exception {
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
      if (entry.getKey() == RepeatedInt64.getDescriptor()) {
        assertEquals(2, success);
      } else {
        assertEquals(1, success);
      }
    }
  }

  @Test
  public void testOptional() throws Exception {
    JSONObject json = new JSONObject();
    json.put("byte", 1);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(TestInt64.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testRepeatedIsOptional() throws Exception {
    JSONObject json = new JSONObject();
    json.put("required_double", 1.1);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(TestRepeatedIsOptional.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testRequired() throws Exception {
    JSONObject json = new JSONObject();
    json.put("test_required_double", 1.1);
    json.put("optional_double", 1.1);
    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(TestRequired.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(
          e.getMessage(), "JSONObject does not have the required field root.required_double.");
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
    AreMatchingFieldsFilledIn(protoMsg, json);
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
    json.put("test_DOUBLe", new JSONArray(new Double[] {1.1, 2.2, 3.3, 4.4}));
    json.put("complexLvl1", complexLvl1);
    json.put("complexLVL2", complexLvl2);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(ComplexRoot.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testStructComplexFail() throws Exception {
    JSONObject complexLvl2 = new JSONObject();
    complexLvl2.put("test_int", 3);

    JSONObject complexLvl1 = new JSONObject();
    complexLvl1.put("test_int", "not_int");
    complexLvl1.put("complexLvl2", complexLvl2);

    JSONObject json = new JSONObject();
    json.put("test_int", 1);
    json.put("test_string", new JSONArray(new String[] {"a", "b", "c"}));
    json.put("test_bytes", "hello");
    json.put("test_bool", true);
    json.put("test_double", new JSONArray(new Double[] {1.1, 2.2, 3.3, 4.4}));
    json.put("complexLvl1", complexLvl1);
    json.put("complexLvl2", complexLvl2);

    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(ComplexRoot.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(
          e.getMessage(), "JSONObject does not have a int64 field at root.complexLvl1.test_int.");
    }
  }

  @Test
  public void testRepeatedWithMixedTypes() throws Exception {
    JSONObject json = new JSONObject();
    json.put("test_repeated", new JSONArray("[1.1, 2.2, true]"));
    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(RepeatedDouble.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(
          e.getMessage(), "JSONObject does not have a double field at root.test_repeated[2].");
    }
  }

  @Test
  public void testNestedRepeatedComplex() throws Exception {
    double[] doubleArr = {1.1, 2.2, 3.3, 4.4, 5.5};
    String[] stringArr = {"hello", "this", "is", "a", "test"};
    int[] intArr = {1, 2, 3, 4, 5};

    JSONObject json = new JSONObject();
    json.put("double", new JSONArray(doubleArr));
    json.put("int", new JSONArray(intArr));
    JSONObject jsonRepeatedString = new JSONObject();
    jsonRepeatedString.put("test_repeated", new JSONArray(stringArr));
    json.put("repeated_string", jsonRepeatedString);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(NestedRepeated.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testNestedRepeatedComplexFail() throws Exception {
    double[] doubleArr = {1.1, 2.2, 3.3, 4.4, 5.5};
    Boolean[] fakeStringArr = {true, false};
    int[] intArr = {1, 2, 3, 4, 5};

    JSONObject json = new JSONObject();
    json.put("double", new JSONArray(doubleArr));
    json.put("int", new JSONArray(intArr));
    JSONObject jsonRepeatedString = new JSONObject();
    jsonRepeatedString.put("test_repeated", new JSONArray(fakeStringArr));
    json.put("repeated_string", jsonRepeatedString);

    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(NestedRepeated.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(
          e.getMessage(),
          "JSONObject does not have a string field at root.repeated_string.test_repeated[0].");
    }
  }

  @Test
  public void testAllowUnknownFields() throws Exception {
    JSONObject json = new JSONObject();
    json.put("test_repeated", new JSONArray(new int[] {1, 2, 3, 4, 5}));
    json.put("string", "hello");

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(RepeatedInt64.getDescriptor(), json, true);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testAllowUnknownFieldsError() throws Exception {
    JSONObject json = new JSONObject();
    json.put("double", 1.1);
    json.put("string", "hello");

    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(RepeatedInt64.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(
          e.getMessage(),
          "JSONObject has fields unknown to BigQuery: f1. Set allowUnknownFields to True to allow unknown fields.");
    }
  }

  @Test
  public void testTopLevelMatchFail() throws Exception {
    JSONObject json = new JSONObject();
    json.put("double", 1.1);
    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(Int64Type.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(
          e.getMessage(),
          "There are no matching fields found for the JSONObject and the protocol buffer descriptor.");
    }
  }

  @Test
  public void testTopLevelMatchSecondLevelNoMatch() throws Exception {
    JSONObject complexLvl2 = new JSONObject();
    complexLvl2.put("no_match", 1);
    JSONObject json = new JSONObject();
    json.put("test_int", 1);
    json.put("complexLvl2", complexLvl2);

    DynamicMessage protoMsg =
        JsonToProtoMessage.convertJsonToProtoMessage(ComplexLvl1.getDescriptor(), json);
    AreMatchingFieldsFilledIn(protoMsg, json);
  }

  @Test
  public void testJsonNullValue() throws Exception {
    JSONObject json = new JSONObject();
    json.put("long", JSONObject.NULL);
    json.put("int", 1);
    try {
      DynamicMessage protoMsg =
          JsonToProtoMessage.convertJsonToProtoMessage(TestInt64.getDescriptor(), json);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "JSONObject does not have a int64 field at root.long.");
    }
  }
}
