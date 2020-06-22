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

@RunWith(JUnit4.class)
public class JsonWriterTest {
  @Mock private BigQuery mockBigquery;
  @Mock private Table mockBigqueryTable;

  private static Map<LegacySQLTypeName, Descriptor> typeMap = Collections.unmodifiableMap(new HashMap<LegacySQLTypeName, Descriptor>() {{
        // put(LegacySQLTypeName.BOOLEAN, BoolType.getDescriptor());
        // put(LegacySQLTypeName.BYTES, BytesType.getDescriptor());
        // put(LegacySQLTypeName.DATE, Int64Type.getDescriptor());
        // put(LegacySQLTypeName.DATETIME, Int64Type.getDescriptor());
        // put(LegacySQLTypeName.FLOAT, DoubleType.getDescriptor());
        // put(LegacySQLTypeName.GEOGRAPHY, BytesType.getDescriptor());
        // put(LegacySQLTypeName.INTEGER, Int64Type.getDescriptor());
        put(LegacySQLTypeName.NUMERIC, DoubleType.getDescriptor());
        // put(LegacySQLTypeName.RECORD, FieldDescriptorProto.Type.TYPE_MESSAGE);
        // put(LegacySQLTypeName.STRING, StringType.getDescriptor());
        // put(LegacySQLTypeName.TIME, Int64Type.getDescriptor());
        // put(LegacySQLTypeName.TIMESTAMP, Int64Type.getDescriptor());
    }});

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

  private boolean descriptorsEqual(Descriptor convertedProto, Descriptor originalProto) {
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
      if (convertedType == FieldDescriptor.Type.MESSAGE && !descriptorsEqual(convertedField.getMessageType(), originalField.getMessageType())) {
        return false;
      }
    }
    return true;
  }

  @Test
  public void testBQToProtoSimpleTypes() throws Exception{
    for (Map.Entry<LegacySQLTypeName, Descriptor> entry : typeMap.entrySet()) {
      customizeSchema(
          Schema.of(Field.newBuilder("test_field_type", entry.getKey())
                         .setMode(Field.Mode.NULLABLE)
                         .build()));
        JsonWriter writer = JsonWriter.getInstance(mockBigquery);
        Descriptor descriptor = writer.BQSchemaToProtoSchema("projects/p/datasets/d/tables/t");
        SchemaCompatibility compact = SchemaCompatibility.getInstance(mockBigquery);
        try {
          compact.check("projects/p/datasets/d/tables/t", descriptor);
        } catch (IllegalArgumentException e) {
          // System.out.println(entry.getKey());
          // System.out.println(entry.getValue());
          System.out.println(e);
          testPrint(descriptor, "");
          testPrint(entry.getValue(), "");
        }

        // if (!descriptorsEqual(descriptor, entry.getValue())) {
        //   fail("Should match!");
        // }

    }
    verify(mockBigquery, times(2)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(2)).getDefinition();
  }
}
