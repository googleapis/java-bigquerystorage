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

import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.test.JsonTest.*;
import com.google.cloud.bigquery.storage.test.SchemaTest.*;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.io.IOException;
import javax.annotation.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.json.JSONObject;
import org.json.JSONArray;

@RunWith(JUnit4.class)
public class JsonWriterTest {
  @Mock private BigQuery mockBigquery;
  @Mock private Table mockBigqueryTable;

  private Map<LegacySQLTypeName, Descriptor> BQTypeToProtoDescriptor = Collections.unmodifiableMap(new HashMap<LegacySQLTypeName, Descriptor>() {{
        put(LegacySQLTypeName.BOOLEAN, BoolType.getDescriptor());
        put(LegacySQLTypeName.BYTES, BytesType.getDescriptor());
        put(LegacySQLTypeName.DATE, Int64Type.getDescriptor());
        put(LegacySQLTypeName.DATETIME, Int64Type.getDescriptor());
        put(LegacySQLTypeName.FLOAT, DoubleType.getDescriptor());
        put(LegacySQLTypeName.GEOGRAPHY, BytesType.getDescriptor());
        put(LegacySQLTypeName.INTEGER, Int64Type.getDescriptor());
        put(LegacySQLTypeName.NUMERIC, DoubleType.getDescriptor());
        put(LegacySQLTypeName.STRING, StringType.getDescriptor());
        put(LegacySQLTypeName.TIME, Int64Type.getDescriptor());
        put(LegacySQLTypeName.TIMESTAMP, Int64Type.getDescriptor());
    }});

  private JSONObject[] simpleJSONObjects = {
    new JSONObject().put("test_field_type", 21474836470L),
    new JSONObject().put("test_field_type", 1.23),
    new JSONObject().put("test_field_type", true),
    new JSONObject().put("test_field_type", "test")
  };

  private LegacySQLTypeName[] simpleBQTypes = {
    LegacySQLTypeName.BOOLEAN,
    LegacySQLTypeName.BYTES,
    LegacySQLTypeName.DATE,
    LegacySQLTypeName.DATETIME,
    LegacySQLTypeName.FLOAT,
    LegacySQLTypeName.GEOGRAPHY,
    LegacySQLTypeName.INTEGER,
    LegacySQLTypeName.NUMERIC,
    LegacySQLTypeName.STRING,
    LegacySQLTypeName.TIME,
    LegacySQLTypeName.TIMESTAMP
  };

  // private Map<LegacySQLTypeName, java.lang.object> BQTypeToJsonValue = Collections.unmodifiableMap(new HashMap<LegacySQLTypeName, java.lang.object>() {{
  //       put(LegacySQLTypeName.BOOLEAN, true);
  //       put(LegacySQLTypeName.BYTES, "test");
  //       put(LegacySQLTypeName.DATE, 123);
  //       put(LegacySQLTypeName.DATETIME, 123);
  //       put(LegacySQLTypeName.FLOAT, 1.23);
  //       put(LegacySQLTypeName.GEOGRAPHY, "test");
  //       put(LegacySQLTypeName.INTEGER, 123);
  //       put(LegacySQLTypeName.NUMERIC, 1.23);
  //       put(LegacySQLTypeName.STRING, "test");
  //       put(LegacySQLTypeName.TIME, 123);
  //       put(LegacySQLTypeName.TIMESTAMP, 123);
  //   }});


  private Descriptor[] protoDescriptors =  {
    BoolType.getDescriptor(),
    BytesType.getDescriptor(),
    Int64Type.getDescriptor(),
    DoubleType.getDescriptor(),
    StringType.getDescriptor(),
    MessageType.getDescriptor()
  };

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    when(mockBigquery.getTable(any(TableId.class))).thenReturn(mockBigqueryTable);
  }

  @After
  public void tearDown() {
    verifyNoMoreInteractions(mockBigquery);
    verifyNoMoreInteractions(mockBigqueryTable);
  }

  public void customizeSchema(final Schema schema) {
    TableDefinition definition =
        new TableDefinition() {
          @Override
          public Type getType() {
            return null;
          }

          @Nullable
          @Override
          public Schema getSchema() {
            return schema;
          }

          @Override
          public Builder toBuilder() {
            return null;
          }
        };
    when(mockBigqueryTable.getDefinition()).thenReturn(definition);
  }

  private void testPrint(Descriptor descriptor, String scope) {
   for (FieldDescriptor field : descriptor.getFields()) {
     if (field.getType() == FieldDescriptor.Type.MESSAGE) {
       System.out.println(field.getName());
       testPrint(field.getMessageType(), scope + field.getName());
     } else {
       System.out.println(field.getName() + ": " + field.getType());
     }
   }
 }

  private boolean isDescriptorEqual(Descriptor convertedProto, Descriptor originalProto) {
    for (FieldDescriptor convertedField : convertedProto.getFields()) {
      FieldDescriptor originalField = originalProto.findFieldByName(convertedField.getName());
      if (originalField == null) {
        return false;
      }
      FieldDescriptor.Type convertedType = convertedField.getType();
      FieldDescriptor.Type originalType = originalField.getType();
      if (convertedType != originalType) {
        return false;
      }
      if (convertedType == FieldDescriptor.Type.MESSAGE) {
        if (!isDescriptorEqual(convertedField.getMessageType(), originalField.getMessageType())) {
          return false;
        }
      }
    }
    return true;
  }
  private boolean isProtoJsonEqual(DynamicMessage proto, JSONObject json) {
    for (Map.Entry<FieldDescriptor, java.lang.Object> entry : proto.getAllFields().entrySet()) {
      FieldDescriptor key = entry.getKey();
      java.lang.Object value = entry.getValue();
      if (key.isRepeated()) {
        if (!isProtoArrayJsonArrayEqual(key, value, json)) {
          return false;
        }
      }
      else {
        if (!isProtoFieldJsonFieldEqual(key, value, json)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean isProtoFieldJsonFieldEqual(FieldDescriptor key, java.lang.Object value, JSONObject json) {
    String fieldName = key.getName();
    switch (key.getType()) {
      case BOOL:
        return (Boolean)value == json.getBoolean(fieldName);
      case BYTES:
        return Arrays.equals((byte[])value, json.getString(fieldName).getBytes());
      case INT64:
        return (long)value == json.getInt(fieldName);
      case STRING:
        return ((String)value).equals(json.getString(fieldName));
      case DOUBLE:
        return (double)value == json.getNumber(fieldName).doubleValue();
      case MESSAGE:
        return isProtoJsonEqual((DynamicMessage) value, json.getJSONObject(fieldName));
    }
    return false;
  }

  private boolean isProtoArrayJsonArrayEqual(FieldDescriptor key, java.lang.Object value, JSONObject json) {
    String fieldName = key.getName();
    JSONArray jsonArray = json.getJSONArray(fieldName);
    switch (key.getType()) {
      case BOOL:
        List<Boolean> boolArr = (List<Boolean>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!(boolArr.get(i) == jsonArray.getBoolean(i))) {
              return false;
            }
        }
        return true;
      case BYTES:
        List<byte[]> byteArr = (List<byte[]>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!Arrays.equals(byteArr.get(i), jsonArray.getString(i).getBytes())) {
              return false;
            }
        }
        return true;
      case INT64:
        List<Long> longArr = (List<Long>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!(longArr.get(i) == jsonArray.getInt(i))) {
              return false;
            }
        }
        return true;
      case STRING:
        List<String> stringArr = (List<String>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!stringArr.get(i).equals(jsonArray.getString(i))) {
              return false;
            }
        }
        return true;
      case DOUBLE:
        List<Double> doubleArr = (List<Double>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
          System.out.println(doubleArr.get(i));
            if (!(doubleArr.get(i) == jsonArray.getNumber(i).doubleValue())) {
              return false;
            }
        }
        return true;
      case MESSAGE:
        List<DynamicMessage> messageArr = (List<DynamicMessage>) value;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!isProtoJsonEqual(messageArr.get(i), jsonArray.getJSONObject(i))) {
              return false;
            }
        }
        return true;
    }
    return false;
  }

  @Test
  public void testBQSchemaToProtoDescriptorSimpleTypes() throws Exception {
    for (Map.Entry<LegacySQLTypeName, Descriptor> entry : BQTypeToProtoDescriptor.entrySet()) {
      customizeSchema(
          Schema.of(Field.newBuilder("test_field_type", entry.getKey())
                         .setMode(Field.Mode.NULLABLE)
                         .build()));
        JsonWriter writer = JsonWriter.getInstance(mockBigquery);
        Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
        SchemaCompatibility compact = SchemaCompatibility.getInstance(mockBigquery);
        compact.check("projects/p/datasets/d/tables/t", descriptor);
        assertTrue(isDescriptorEqual(descriptor, entry.getValue()));
    }
    verify(mockBigquery, times(22)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(22)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferBoolean() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.BOOLEAN)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the boolean field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferBytes() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.BYTES)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the string field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferDate() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.DATE)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the int64 field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferDatetime() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.DATETIME)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the int64 field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferFloat() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.FLOAT)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the double field .test_field_type.");
        }
    }
    assertEquals(2, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferGeography() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.GEOGRAPHY)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the string field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferInteger() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.INTEGER)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the int64 field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferNumeric() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.NUMERIC)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the double field .test_field_type.");
        }
    }
    assertEquals(2, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferString() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.STRING)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the string field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferTime() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.TIME)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the int64 field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testBQSchemaToProtobufferTimestamp() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.TIMESTAMP)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    int success = 0;
    for (JSONObject json : simpleJSONObjects) {
        try {
          DynamicMessage protoMsg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
          success += 1;
        } catch (IllegalArgumentException e) {
          assertEquals(e.getMessage(), "JSONObject does not have the int64 field .test_field_type.");
        }
    }
    assertEquals(1, success);
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  // @Test
  // public void testBQRecordJsonSimple() throws Exception {
  //   Field StringType =
  //       Field.newBuilder("test_field_type", LegacySQLTypeName.STRING)
  //           .setMode(Field.Mode.NULLABLE)
  //           .build();
  //   customizeSchema(
  //       Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.RECORD, StringType)
  //                      .setMode(Field.Mode.NULLABLE)
  //                      .build()));
  //   JsonWriter writer = JsonWriter.getInstance(mockBigquery);
  //   Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
  //   JSONObject json = new JSONObject();
  //   JSONObject stringType = new JSONObject();
  //   stringType.put("test_field_type", "hello");
  //   json.put("test_field_type", stringType);
  //
  //   DynamicMessage msg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
  //   assertTrue(isProtoJsonEqual(msg, json));
  //   verify(mockBigquery, times(2)).getTable(any(TableId.class));
  //   verify(mockBigqueryTable, times(2)).getDefinition();
  // }
  //
  // @Test
  // public void testBQRecordJsonComplex() throws Exception {
  //   Field bqBytes = Field.newBuilder("bytes", LegacySQLTypeName.BYTES)
  //         .setMode(Field.Mode.NULLABLE)
  //         .build();
  //   Field bqInt = Field.newBuilder("int", LegacySQLTypeName.INTEGER)
  //         .setMode(Field.Mode.NULLABLE)
  //         .build();
  //   Field record1 = Field.newBuilder("record1", LegacySQLTypeName.RECORD, bqInt)
  //         .setMode(Field.Mode.NULLABLE)
  //         .build();
  //
  //   Field record = Field.newBuilder("record", LegacySQLTypeName.RECORD, bqInt, bqBytes, record1)
  //           .setMode(Field.Mode.NULLABLE)
  //           .build();
  //   customizeSchema(
  //       Schema.of(record,
  //                 Field.newBuilder("float", LegacySQLTypeName.FLOAT)
  //                       .setMode(Field.Mode.NULLABLE)
  //                       .build()));
  //   JsonWriter writer = JsonWriter.getInstance(mockBigquery);
  //   Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
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
  //   DynamicMessage msg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
  //   assertTrue(isProtoJsonEqual(msg, json));
  //   verify(mockBigquery, times(2)).getTable(any(TableId.class));
  //   verify(mockBigqueryTable, times(2)).getDefinition();
  // }
  //
  // @Test
  // public void testBQRecordJsonRepeatedSimple() throws Exception {
  //   customizeSchema(
  //       Schema.of(Field.newBuilder("float", LegacySQLTypeName.FLOAT)
  //                       .setMode(Field.Mode.REPEATED)
  //                       .build(),
  //                       Field.newBuilder("hello", LegacySQLTypeName.STRING)
  //                                       .setMode(Field.Mode.NULLABLE)
  //                                       .build()));
  //   JsonWriter writer = JsonWriter.getInstance(mockBigquery);
  //   Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
  //
  //   JSONObject json = new JSONObject();
  //   double[] doubleArr = {1.1, 2.2, 3.3, 4.4, 5.5};
  //   json.put("float", new JSONArray(doubleArr));
  //   DynamicMessage msg = writer.BQSchemaToProtoMessage("projects/p/datasets/d/tables/t", json);
  //   assertTrue(isProtoJsonEqual(msg, json));
  //   verify(mockBigquery, times(2)).getTable(any(TableId.class));
  //   verify(mockBigqueryTable, times(2)).getDefinition();
  // }
}
