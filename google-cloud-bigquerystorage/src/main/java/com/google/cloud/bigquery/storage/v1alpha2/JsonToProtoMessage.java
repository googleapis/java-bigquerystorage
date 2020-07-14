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
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Converts Json data to protocol buffer messages given the protocol buffer descriptor. */
public class JsonToProtoMessage {
  private static ImmutableMap<FieldDescriptor.Type, String> FieldTypeToDebugMessage =
      new ImmutableMap.Builder<FieldDescriptor.Type, String>()
          .put(FieldDescriptor.Type.BOOL, "boolean")
          .put(FieldDescriptor.Type.BYTES, "string")
          .put(FieldDescriptor.Type.INT32, "int32")
          .put(FieldDescriptor.Type.DOUBLE, "double")
          .put(FieldDescriptor.Type.INT64, "int64")
          .put(FieldDescriptor.Type.STRING, "string")
          .put(FieldDescriptor.Type.MESSAGE, "object")
          .build();

  /**
   * Converts Json data to protocol buffer messages given the protocol buffer descriptor.
   *
   * @param protoSchema
   * @param json
   * @param allowUnknownFields Ignores unknown JSON fields.
   * @throws IllegalArgumentException when JSON data is not compatible with proto descriptor.
   */
  public static DynamicMessage convertJsonToProtoMessage(
      Descriptor protoSchema, JSONObject json, boolean allowUnknownFields)
      throws IllegalArgumentException {
    if (json.length() == 0) {
      throw new IllegalArgumentException("JSONObject is empty.");
    }
    return convertJsonToProtoMessageImpl(
        protoSchema, json, "root", /*topLevel=*/ true, allowUnknownFields);
  }

  /**
   * Converts Json data to protocol buffer messages given the protocol buffer descriptor.
   *
   * @param protoSchema
   * @param json
   * @param jsonScope Debugging purposes
   * @param allowUnknownFields Ignores unknown JSON fields.
   * @param topLevel checks if root level has any matching fields.
   * @throws IllegalArgumentException when JSON data is not compatible with proto descriptor.
   */
  private static DynamicMessage convertJsonToProtoMessageImpl(
      Descriptor protoSchema,
      JSONObject json,
      String jsonScope,
      boolean topLevel,
      boolean allowUnknownFields)
      throws IllegalArgumentException {

    DynamicMessage.Builder protoMsg = DynamicMessage.newBuilder(protoSchema);
    List<FieldDescriptor> protoFields = protoSchema.getFields();

    Set<String> protoFieldNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    for (FieldDescriptor field : protoFields) {
      protoFieldNames.add(field.getName());
    }

    HashMap<String, String> jsonLowercaseNameToJsonName = new HashMap<String, String>();
    String[] jsonNames = JSONObject.getNames(json);
    for (int i = 0; i < jsonNames.length; i++) {
      jsonLowercaseNameToJsonName.put(jsonNames[i].toLowerCase(), jsonNames[i]);
    }

    if (!allowUnknownFields) {
      for (int i = 0; i < jsonNames.length; i++) {
        if (!protoFieldNames.contains(jsonNames[i])) {
          throw new IllegalArgumentException(
              String.format(
                  "JSONObject has fields unknown to BigQuery: %s.%s. Set allowUnknownFields to True to allow unknown fields.",
                  jsonScope, jsonNames[i]));
        }
      }
    }

    int matchedFields = 0;
    for (FieldDescriptor field : protoFields) {
      String lowercaseFieldName = field.getName().toLowerCase();
      String currentScope = jsonScope + "." + field.getName();

      if (!jsonLowercaseNameToJsonName.containsKey(lowercaseFieldName)) {
        if (field.isRequired()) {
          throw new IllegalArgumentException(
              "JSONObject does not have the required field " + currentScope + ".");
        } else {
          continue;
        }
      }
      matchedFields++;
      if (!field.isRepeated()) {
        fillField(
            protoMsg,
            field,
            json,
            jsonLowercaseNameToJsonName.get(lowercaseFieldName),
            currentScope,
            allowUnknownFields);
      } else {
        fillRepeatedField(
            protoMsg,
            field,
            json,
            jsonLowercaseNameToJsonName.get(lowercaseFieldName),
            currentScope,
            allowUnknownFields);
      }
    }
    if (matchedFields == 0 && topLevel) {
      throw new IllegalArgumentException(
          "There are no matching fields found for the JSONObject and the protocol buffer descriptor.");
    }
    return protoMsg.build();
  }

  /**
   * Fills a non-repetaed protoField with the json data.
   *
   * @param protoMsg The protocol buffer message being constructed
   * @param fieldDescriptor
   * @param json
   * @param actualJsonKeyName Actual key name in JSONObject instead of lowercased version
   * @param currentScope Debugging purposes
   * @param allowUnknownFields Ignores unknown JSON fields.
   * @throws IllegalArgumentException when JSON data is not compatible with proto descriptor.
   */
  private static void fillField(
      DynamicMessage.Builder protoMsg,
      FieldDescriptor fieldDescriptor,
      JSONObject json,
      String actualJsonKeyName,
      String currentScope,
      boolean allowUnknownFields)
      throws IllegalArgumentException {
    java.lang.Object val;
    String error;
    try {
      switch (fieldDescriptor.getType()) {
        case BOOL:
          protoMsg.setField(fieldDescriptor, new Boolean(json.getBoolean(actualJsonKeyName)));
          break;
        case BYTES:
          protoMsg.setField(fieldDescriptor, json.getString(actualJsonKeyName).getBytes());
          break;
        case INT64:
          val = json.get(actualJsonKeyName);
          if (val instanceof Integer) {
            protoMsg.setField(fieldDescriptor, new Long((Integer) val));
          } else if (val instanceof Long) {
            protoMsg.setField(fieldDescriptor, new Long((Long) val));
          } else {
            throw new JSONException("");
          }
          break;
        case INT32:
          val = json.get(actualJsonKeyName);
          if (val instanceof Integer) {
            protoMsg.setField(fieldDescriptor, new Integer((Integer) val));
          } else {
            throw new JSONException("");
          }
          break;
        case STRING:
          protoMsg.setField(fieldDescriptor, json.getString(actualJsonKeyName));
          break;
        case DOUBLE:
          val = json.get(actualJsonKeyName);
          if (val instanceof Double) {
            protoMsg.setField(fieldDescriptor, new Double((double) val));
          } else if (val instanceof Float) {
            protoMsg.setField(fieldDescriptor, new Double((float) val));
          } else {
            throw new JSONException("");
          }
          break;
        case MESSAGE:
          Message.Builder message = protoMsg.newBuilderForField(fieldDescriptor);
          protoMsg.setField(
              fieldDescriptor,
              convertJsonToProtoMessageImpl(
                  fieldDescriptor.getMessageType(),
                  json.getJSONObject(actualJsonKeyName),
                  currentScope,
                  /*topLevel =*/ false,
                  allowUnknownFields));
          break;
      }
    } catch (JSONException e) {
      throw new IllegalArgumentException(
          String.format(
              "JSONObject does not have a %s field at %s.",
              FieldTypeToDebugMessage.get(fieldDescriptor.getType()), currentScope));
    }
  }

  /**
   * Fills a repeated protoField with the json data.
   *
   * @param protoMsg The protocol buffer message being constructed
   * @param fieldDescriptor
   * @param json If root level has no matching fields, throws exception.
   * @param actualJsonKeyName Actual key name in JSONObject instead of lowercased version
   * @param currentScope Debugging purposes
   * @param allowUnknownFields Ignores unknown JSON fields.
   * @throws IllegalArgumentException when JSON data is not compatible with proto descriptor.
   */
  private static void fillRepeatedField(
      DynamicMessage.Builder protoMsg,
      FieldDescriptor fieldDescriptor,
      JSONObject json,
      String actualJsonKeyName,
      String currentScope,
      boolean allowUnknownFields)
      throws IllegalArgumentException {

    JSONArray jsonArray;
    try {
      jsonArray = json.getJSONArray(actualJsonKeyName);
    } catch (JSONException e) {
      throw new IllegalArgumentException(
          "JSONObject does not have a array field at " + currentScope + ".");
    }
    java.lang.Object val;
    switch (fieldDescriptor.getType()) {
      case BOOL:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(fieldDescriptor, new Boolean(jsonArray.getBoolean(i)));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                String.format(
                    "JSONObject does not have a boolean field at %s[%d].", currentScope, i));
          }
        }
        break;
      case BYTES:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(fieldDescriptor, jsonArray.getString(i).getBytes());
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                String.format(
                    "JSONObject does not have a string field at %s[%d].", currentScope, i));
          }
        }
        break;
      case INT64:
        for (int i = 0; i < jsonArray.length(); i++) {
          val = jsonArray.get(i);
          if (val instanceof Integer) {
            protoMsg.addRepeatedField(fieldDescriptor, new Long((Integer) val));
          } else if (val instanceof Long) {
            protoMsg.addRepeatedField(fieldDescriptor, new Long((Long) val));
          } else {
            throw new IllegalArgumentException(
                String.format(
                    "JSONObject does not have a int64 field at %s[%d].", currentScope, i));
          }
        }
        break;
      case INT32:
        for (int i = 0; i < jsonArray.length(); i++) {
          val = jsonArray.get(i);
          if (val instanceof Integer) {
            protoMsg.addRepeatedField(fieldDescriptor, new Integer((Integer) val));
          } else {
            throw new IllegalArgumentException(
                String.format(
                    "JSONObject does not have a int32 field at %s[%d].", currentScope, i));
          }
        }
        break;
      case STRING:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(fieldDescriptor, jsonArray.getString(i));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                String.format(
                    "JSONObject does not have a string field at %s[%d].", currentScope, i));
          }
        }
        break;
      case DOUBLE:
        for (int i = 0; i < jsonArray.length(); i++) {
          val = jsonArray.get(i);
          if (val instanceof Double) {
            protoMsg.addRepeatedField(fieldDescriptor, new Double((double) val));
          } else if (val instanceof Float) {
            protoMsg.addRepeatedField(fieldDescriptor, new Double((float) val));
          } else {
            throw new IllegalArgumentException(
                String.format(
                    "JSONObject does not have a double field at %s[%d].", currentScope, i));
          }
        }
        break;
      case MESSAGE:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            Message.Builder message = protoMsg.newBuilderForField(fieldDescriptor);
            protoMsg.addRepeatedField(
                fieldDescriptor,
                convertJsonToProtoMessageImpl(
                    fieldDescriptor.getMessageType(),
                    jsonArray.getJSONObject(i),
                    currentScope,
                    /*topLevel =*/ false,
                    allowUnknownFields));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                String.format(
                    "JSONObject does not have a object field at %s[%d].", currentScope, i));
          }
        }
        break;
    }
  }
}
