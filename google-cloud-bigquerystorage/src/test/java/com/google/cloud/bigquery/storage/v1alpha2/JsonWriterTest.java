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

  private static Map<LegacySQLTypeName, Descriptor> typeMap = Collections.unmodifiableMap(new HashMap<LegacySQLTypeName, Descriptor>() {{
        put(LegacySQLTypeName.BOOLEAN, BoolType.getDescriptor());
        put(LegacySQLTypeName.BYTES, BytesType.getDescriptor());
        put(LegacySQLTypeName.DATE, Int64Type.getDescriptor());
        put(LegacySQLTypeName.DATETIME, Int64Type.getDescriptor());
        put(LegacySQLTypeName.FLOAT, DoubleType.getDescriptor());
        put(LegacySQLTypeName.GEOGRAPHY, BytesType.getDescriptor());
        put(LegacySQLTypeName.INTEGER, Int64Type.getDescriptor());
        // put(LegacySQLTypeName.NUMERIC, DoubleType.getDescriptor());
        put(LegacySQLTypeName.STRING, StringType.getDescriptor());
        put(LegacySQLTypeName.TIME, Int64Type.getDescriptor());
        put(LegacySQLTypeName.TIMESTAMP, Int64Type.getDescriptor());
    }});

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

  private void descriptorsEqual(Descriptor convertedProto, Descriptor originalProto)
    throws IllegalArgumentException {
    for (FieldDescriptor convertedField : convertedProto.getFields()) {
      FieldDescriptor originalField = originalProto.findFieldByName(convertedField.getName());
      if (originalField == null) {
        throw new IllegalArgumentException("Descriptors are not equal! Field not found.");
      }
      FieldDescriptor.Type convertedType = convertedField.getType();
      FieldDescriptor.Type originalType = originalField.getType();
      if (convertedType != originalType) {
        throw new IllegalArgumentException("Descriptors are not equal! Type not equal.");
      }
      if (convertedType == FieldDescriptor.Type.MESSAGE)
        try {
          descriptorsEqual(convertedField.getMessageType(), originalField.getMessageType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
      }
  }

  @Test
  public void testBQToProtoSimpleTypes() throws Exception {
    // TODO: fix numeric when PR is accepted
    for (Map.Entry<LegacySQLTypeName, Descriptor> entry : typeMap.entrySet()) {
      customizeSchema(
          Schema.of(Field.newBuilder("test_field_type", entry.getKey())
                         .setMode(Field.Mode.NULLABLE)
                         .build()));
        JsonWriter writer = JsonWriter.getInstance(mockBigquery);
        Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
        SchemaCompatibility compact = SchemaCompatibility.getInstance(mockBigquery);
        compact.check("projects/p/datasets/d/tables/t", descriptor);
        descriptorsEqual(descriptor, entry.getValue());
    }
    verify(mockBigquery, times(20)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(20)).getDefinition();
  }

  @Test
  public void testBQBoolean() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.BOOLEAN)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != BoolType.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQBytesDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.BYTES)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != BytesType.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQDateDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.DATE)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != Int64Type.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQDatetimeDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.DATETIME)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != Int64Type.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQFloatDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.FLOAT)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != DoubleType.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQGeographyDescriptor() throws Exception {

    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.GEOGRAPHY)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != BytesType.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQIntegerDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.INTEGER)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != Int64Type.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQStringDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.STRING)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != StringType.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQTimeDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.TIME)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != Int64Type.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQTimestampDescriptor() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.TIMESTAMP)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != Int64Type.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testBQRecordDescriptor() throws Exception {
    Field StringType =
        Field.newBuilder("test_field_type", LegacySQLTypeName.STRING)
            .setMode(Field.Mode.NULLABLE)
            .build();
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.RECORD, StringType)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    for (int i = 0; i < protoDescriptors.length; i++) {
        Descriptor protoDescriptor = protoDescriptors[i];
        if (protoDescriptor != MessageType.getDescriptor()) {
          try {
            descriptorsEqual(descriptor, protoDescriptors[i]);
          } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Descriptors are not equal! Type not equal.");
          }
        }
        else {
          descriptorsEqual(descriptor, protoDescriptors[i]);
        }
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  private boolean testProtobufJsonEquality(DynamicMessage proto, JSONObject json) {
    for (Map.Entry<FieldDescriptor, java.lang.Object> entry : proto.getAllFields().entrySet()) {
      FieldDescriptor key = entry.getKey();
      String fieldName = key.getName();
      java.lang.Object value = entry.getValue();
      if (value instanceof DynamicMessage) {
        if (!testProtobufJsonEquality((DynamicMessage) value, json.getJSONObject(fieldName))) {
          return false;
        }
      }
      boolean match = true;
      switch (key.getType()) {
        case BOOL:
          match = (Boolean)value == json.getBoolean(fieldName);
          break;
        case BYTES:
          match = Arrays.equals((byte[])value, json.getString(fieldName).getBytes());
          break;
        case INT64:
          match = (long)value == json.getInt(fieldName);
          break;
        case STRING:
          match = ((String)value).equals(json.getString(fieldName));
          break;
        case DOUBLE:
          match = (double)value == json.getNumber(fieldName).doubleValue();
          break;
      }
      if (!match) {
        return false;
      }
    }
    return true;
  }

  @Test
  public void testBQRecordJsonSimple() throws Exception {
    Field StringType =
        Field.newBuilder("test_field_type", LegacySQLTypeName.STRING)
            .setMode(Field.Mode.NULLABLE)
            .build();
    customizeSchema(
        Schema.of(Field.newBuilder("test_field_type", LegacySQLTypeName.RECORD, StringType)
                       .setMode(Field.Mode.NULLABLE)
                       .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
    JSONObject json = new JSONObject();
    JSONObject stringType = new JSONObject();
    stringType.put("test_field_type", "hello");
    json.put("test_field_type", stringType);

    DynamicMessage msg = writer.append("projects/p/datasets/d/tables/t", json);
    assertTrue(testProtobufJsonEquality(msg, json));
    verify(mockBigquery, times(2)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(2)).getDefinition();
  }

  @Test
  public void testBQRecordJsonComplex() throws Exception {
    Field bqBytes = Field.newBuilder("bytes", LegacySQLTypeName.BYTES)
          .setMode(Field.Mode.NULLABLE)
          .build();
    Field bqInt = Field.newBuilder("int", LegacySQLTypeName.INTEGER)
          .setMode(Field.Mode.NULLABLE)
          .build();
    Field record1 = Field.newBuilder("record1", LegacySQLTypeName.RECORD, bqInt)
          .setMode(Field.Mode.NULLABLE)
          .build();

    Field record = Field.newBuilder("record", LegacySQLTypeName.RECORD, bqInt, bqBytes, record1)
            .setMode(Field.Mode.NULLABLE)
            .build();
    customizeSchema(
        Schema.of(record,
                  Field.newBuilder("float", LegacySQLTypeName.FLOAT)
                        .setMode(Field.Mode.NULLABLE)
                        .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");

    JSONObject jsonRecord1 = new JSONObject();
    jsonRecord1.put("int", 2048);
    JSONObject jsonRecord = new JSONObject();
    jsonRecord.put("int", 1024);
    jsonRecord.put("bytes", "testing");
    jsonRecord.put("record1", jsonRecord1);
    JSONObject json = new JSONObject();
    json.put("record", jsonRecord);
    json.put("float", 1.23);

    DynamicMessage msg = writer.append("projects/p/datasets/d/tables/t", json);
    assertTrue(testProtobufJsonEquality(msg, json));
    verify(mockBigquery, times(2)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(2)).getDefinition();
  }

  @Test
  public void testBQRecordJsonRepeatedSimple() throws Exception {
    customizeSchema(
        Schema.of(Field.newBuilder("float", LegacySQLTypeName.FLOAT)
                        .setMode(Field.Mode.REPEATED)
                        .build()));
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");

    JSONObject json = new JSONObject();
    double[] doubleArr = {1.1, 2.2, 3.3, 4.4, 5.5};
    json.put("float", new JSONArray(doubleArr));
    DynamicMessage msg = writer.append("projects/p/datasets/d/tables/t", json);
    assertTrue(testProtobufJsonEquality(msg, json));
    verify(mockBigquery, times(2)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(2)).getDefinition();
  }
}
