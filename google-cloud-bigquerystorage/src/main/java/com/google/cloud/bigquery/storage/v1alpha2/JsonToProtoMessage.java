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
            "JSONObject has unknown fields. Set allowUnknownFields to True to ignore unknown fields.");
      }
    }

    for (FieldDescriptor field : protoFields) {
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
      matchedFields++;
      if (!field.isRepeated()) {
        fillField(protoMsg, field, json, currentScope, allowUnknownFields);
      } else {
        fillRepeatedField(protoMsg, field, json, currentScope, allowUnknownFields);
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
   * @param protoMsg
   * @param field
   * @param json
   * @param currentScope Debugging purposes
   * @param allowUnknownFields Ignores unknown JSON fields.
   * @throws IllegalArgumentException when JSON data is not compatible with proto descriptor.
   */
  private static void fillField(
      DynamicMessage.Builder protoMsg,
      FieldDescriptor field,
      JSONObject json,
      String currentScope,
      boolean allowUnknownFields)
      throws IllegalArgumentException {

    String fieldName = field.getName();
    switch (field.getType()) {
      case BOOL:
        try {
          protoMsg.setField(field, new Boolean(json.getBoolean(fieldName)));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a boolean field at " + currentScope + ".");
        }
        break;
      case BYTES:
        try {
          protoMsg.setField(field, json.getString(fieldName).getBytes());
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a string field at " + currentScope + ".");
        }
        break;
      case INT64:
        try {
          java.lang.Object val = json.get(fieldName);
          if (val instanceof Byte) {
            protoMsg.setField(field, new Long((Byte) val));
          } else if (val instanceof Short) {
            protoMsg.setField(field, new Long((Short) val));
          } else if (val instanceof Integer) {
            protoMsg.setField(field, new Long((Integer) val));
          } else if (val instanceof Long) {
            protoMsg.setField(field, new Long((Long) val));
          } else {
            throw new IllegalArgumentException(
                "JSONObject does not have a int64 field at " + currentScope + ".");
          }
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a int64 field at " + currentScope + ".");
        }
        break;
      case STRING:
        try {
          protoMsg.setField(field, json.getString(fieldName));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have a string field at " + currentScope + ".");
        }
        break;
      case DOUBLE:
        try {
          java.lang.Object val = json.get(fieldName);
          if (val instanceof Double) {
            protoMsg.setField(field, new Double((double) val));
          } else if (val instanceof Float) {
            protoMsg.setField(field, new Double((float) val));
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
        Message.Builder message = protoMsg.newBuilderForField(field);
        try {
          protoMsg.setField(
              field,
              convertJsonToProtoMessageImpl(
                  field.getMessageType(),
                  json.getJSONObject(fieldName),
                  currentScope,
                  false,
                  allowUnknownFields));
        } catch (JSONException e) {
          throw new IllegalArgumentException(
              "JSONObject does not have an object field at " + currentScope + ".");
        }
        break;
    }
  }

  /**
   * Fills a repeated protoField with the json data.
   *
   * @param protoMsg
   * @param field
   * @param json If root level has no matching fields, throws exception.
   * @param currentScope Debugging purposes
   * @param allowUnknownFields Ignores unknown JSON fields.
   * @throws IllegalArgumentException when JSON data is not compatible with proto descriptor.
   */
  private static void fillRepeatedField(
      DynamicMessage.Builder protoMsg,
      FieldDescriptor field,
      JSONObject json,
      String currentScope,
      boolean allowUnknownFields)
      throws IllegalArgumentException {
    String fieldName = field.getName();
    JSONArray jsonArray;
    try {
      jsonArray = json.getJSONArray(fieldName);
    } catch (JSONException e) {
      throw new IllegalArgumentException(
          "JSONObject does not have an array field at " + currentScope + ".");
    }

    switch (field.getType()) {
      case BOOL:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, new Boolean(jsonArray.getBoolean(i)));
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
            protoMsg.addRepeatedField(field, jsonArray.getString(i).getBytes());
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
              protoMsg.addRepeatedField(field, new Long((Byte) val));
            } else if (val instanceof Short) {
              protoMsg.addRepeatedField(field, new Long((Short) val));
            } else if (val instanceof Integer) {
              protoMsg.addRepeatedField(field, new Long((Integer) val));
            } else if (val instanceof Long) {
              protoMsg.addRepeatedField(field, new Long((Long) val));
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
      case STRING:
        for (int i = 0; i < jsonArray.length(); i++) {
          try {
            protoMsg.addRepeatedField(field, jsonArray.getString(i));
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
              protoMsg.addRepeatedField(field, new Double((double) val));
            } else if (val instanceof Float) {
              protoMsg.addRepeatedField(field, new Double((float) val));
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
            Message.Builder message = protoMsg.newBuilderForField(field);
            protoMsg.addRepeatedField(
                field,
                convertJsonToProtoMessageImpl(
                    field.getMessageType(),
                    jsonArray.getJSONObject(i),
                    currentScope,
                    false,
                    allowUnknownFields));
          } catch (JSONException e) {
            throw new IllegalArgumentException(
                "JSONObject does not have an object field at "
                    + currentScope
                    + "["
                    + i
                    + "]"
                    + ".");
          }
        }
        break;
    }
  }
}
