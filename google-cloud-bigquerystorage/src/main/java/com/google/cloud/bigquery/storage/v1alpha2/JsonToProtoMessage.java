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

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Converts Json data to protocol buffer messages given the protocol buffer descriptor. */
public class JsonToProtoMessage {

  /**
   * Converts Json data to protocol buffer messages given the protocol buffer descriptor.
   *
   * @param protoSchema
   * @param json
   * @throws IllegalArgumentException when JSON data is not compatible with proto descriptor.
   */
  public static DynamicMessage convertJsonToProtoMessage(Descriptor protoSchema, JSONObject json)
      throws IllegalArgumentException {
    return convertJsonToProtoMessage(protoSchema, json, false);
  }

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
    return convertJsonToProtoMessageImpl(protoSchema, json, "root", true, allowUnknownFields);
  }

  /**
   * Converts Json data to protocol buffer messages given the protocol buffer descriptor.
   *
   * @param protoSchema
   * @param json
   * @param jsonScope Debugging purposes
   * @param topLevel If root level has no matching fields, throws exception.
   * @param allowUnknownFields Ignores unknown JSON fields.
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
    int matchedFields = 0;
    List<FieldDescriptor> protoFields = protoSchema.getFields();

    if (JSONObject.getNames(json).length > protoFields.size()) {
      if (!allowUnknownFields) {
        throw new IllegalArgumentException(
            "JSONObject has fields unknown to BigQuery: f1. Set allowUnknownFields to True to allow unknown fields.");
      }
    }
    HashMap<String, String> jsonLowercaseNameToActualName = new HashMap<String, String>();
    String[] actualNames = JSONObject.getNames(json);
    for (int i = 0; i < actualNames.length; i++) {
      jsonLowercaseNameToActualName.put(actualNames[i].toLowerCase(), actualNames[i]);
    }

    for (FieldDescriptor field : protoFields) {
      String fieldName = field.getName();
      String lowercaseFieldName = fieldName.toLowerCase();
      String currentScope = jsonScope + "." + fieldName;

      if (!jsonLowercaseNameToActualName.containsKey(lowercaseFieldName)) {
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
            jsonLowercaseNameToActualName.get(lowercaseFieldName),
            currentScope,
            allowUnknownFields);
      } else {
        fillRepeatedField(
            protoMsg,
            field,
            json,
            jsonLowercaseNameToActualName.get(lowercaseFieldName),
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

    switch (fieldDescriptor.getType()) {
      case BOOL:
        try {
          protoMsg.setField(fieldDescriptor, new Boolean(json.getBoolean(actualJsonKeyName)));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a boolean field at " + currentScope + ".");
        }
        break;
      case BYTES:
        try {
          protoMsg.setField(fieldDescriptor, json.getString(actualJsonKeyName).getBytes());
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a string field at " + currentScope + ".");
        }
        break;
      case INT64:
        try {
          java.lang.Object val = json.get(actualJsonKeyName);
          if (val instanceof Byte) {
            protoMsg.setField(fieldDescriptor, new Long((Byte) val));
          } else if (val instanceof Short) {
            protoMsg.setField(fieldDescriptor, new Long((Short) val));
          } else if (val instanceof Integer) {
            protoMsg.setField(fieldDescriptor, new Long((Integer) val));
          } else if (val instanceof Long) {
            protoMsg.setField(fieldDescriptor, new Long((Long) val));
          } else {
            throw new IllegalArgumentException(
                "JSONObject does not have a int64 field at " + currentScope + ".");
          }
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a int64 field at " + currentScope + ".");
        }
        break;
      case INT32:
        try {
          java.lang.Object val = json.get(actualJsonKeyName);
          if (val instanceof Byte) {
            protoMsg.setField(fieldDescriptor, new Integer((Byte) val));
          } else if (val instanceof Short) {
            protoMsg.setField(fieldDescriptor, new Integer((Short) val));
          } else if (val instanceof Integer) {
            protoMsg.setField(fieldDescriptor, new Integer((Integer) val));
          } else {
            throw new IllegalArgumentException(
                "JSONObject does not have a int32 field at " + currentScope + ".");
          }
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a int32 field at " + currentScope + ".");
        }
        break;
      case STRING:
        try {
          protoMsg.setField(fieldDescriptor, json.getString(actualJsonKeyName));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a string field at " + currentScope + ".");
        }
        break;
      case DOUBLE:
        try {
          java.lang.Object val = json.get(actualJsonKeyName);
          if (val instanceof Double) {
            protoMsg.setField(fieldDescriptor, new Double((double) val));
          } else if (val instanceof Float) {
            protoMsg.setField(fieldDescriptor, new Double((float) val));
          } else {
            throw new IllegalArgumentException(
                "JSONObject does not have a double field at " + currentScope + ".");
          }
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a double field at " + currentScope + ".");
        }
        break;
      case MESSAGE:
        Message.Builder message = protoMsg.newBuilderForField(fieldDescriptor);
        try {
          protoMsg.setField(
              fieldDescriptor,
              convertJsonToProtoMessageImpl(
                  fieldDescriptor.getMessageType(),
                  json.getJSONObject(actualJsonKeyName),
                  currentScope,
                  false,
                  allowUnknownFields));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a object field at " + currentScope + ".");
        }
        break;
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

    switch (fieldDescriptor.getType()) {
      case BOOL:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(fieldDescriptor, new Boolean(jsonArray.getBoolean(i)));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have a boolean field at "
                    + currentScope
                    + "["
                    + i
                    + "]"
                    + ".");
          }
        }
        break;
      case BYTES:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(fieldDescriptor, jsonArray.getString(i).getBytes());
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have a string field at " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case INT64:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            java.lang.Object val = jsonArray.get(i);
            if (val instanceof Byte) {
              protoMsg.addRepeatedField(fieldDescriptor, new Long((Byte) val));
            } else if (val instanceof Short) {
              protoMsg.addRepeatedField(fieldDescriptor, new Long((Short) val));
            } else if (val instanceof Integer) {
              protoMsg.addRepeatedField(fieldDescriptor, new Long((Integer) val));
            } else if (val instanceof Long) {
              protoMsg.addRepeatedField(fieldDescriptor, new Long((Long) val));
            } else {
              throw new IllegalArgumentException(
                  "JSONObject does not have a int64 field at "
                      + currentScope
                      + "["
                      + i
                      + "]"
                      + ".");
            }
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have a int64 field at " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case INT32:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            java.lang.Object val = jsonArray.get(i);
            if (val instanceof Byte) {
              protoMsg.addRepeatedField(fieldDescriptor, new Integer((Byte) val));
            } else if (val instanceof Short) {
              protoMsg.addRepeatedField(fieldDescriptor, new Integer((Short) val));
            } else if (val instanceof Integer) {
              protoMsg.addRepeatedField(fieldDescriptor, new Integer((Integer) val));
            } else {
              throw new IllegalArgumentException(
                  "JSONObject does not have a int32 field at "
                      + currentScope
                      + "["
                      + i
                      + "]"
                      + ".");
            }
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have a int32 field at " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case STRING:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(fieldDescriptor, jsonArray.getString(i));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have a string field at " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
      case DOUBLE:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            java.lang.Object val = jsonArray.get(i);
            if (val instanceof Double) {
              protoMsg.addRepeatedField(fieldDescriptor, new Double((double) val));
            } else if (val instanceof Float) {
              protoMsg.addRepeatedField(fieldDescriptor, new Double((float) val));
            } else {
              throw new IllegalArgumentException(
                  "JSONObject does not have a double field at "
                      + currentScope
                      + "["
                      + i
                      + "]"
                      + ".");
            }
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have a double field at " + currentScope + "[" + i + "]" + ".");
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
                    false,
                    allowUnknownFields));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have a object field at " + currentScope + "[" + i + "]" + ".");
          }
        }
        break;
    }
  }
}
