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
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BQTableSchemaToProtoDescriptorTest {
  // This is a map between the Table.TableFieldSchema.Type and the descriptor it is supposed to
  // produce. The produced descriptor will be used to check against the entry values here.
  private static ImmutableMap<Table.TableFieldSchema.Type, Descriptor>
      BQTableTypeToCorrectProtoDescriptorTest =
          new ImmutableMap.Builder<Table.TableFieldSchema.Type, Descriptor>()
              .put(Table.TableFieldSchema.Type.BOOL, BoolType.getDescriptor())
              .put(Table.TableFieldSchema.Type.BYTES, BytesType.getDescriptor())
              .put(Table.TableFieldSchema.Type.DATE, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.DATETIME, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.DOUBLE, DoubleType.getDescriptor())
              .put(Table.TableFieldSchema.Type.GEOGRAPHY, BytesType.getDescriptor())
              .put(Table.TableFieldSchema.Type.INT64, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.NUMERIC, BytesType.getDescriptor())
              .put(Table.TableFieldSchema.Type.STRING, StringType.getDescriptor())
              .put(Table.TableFieldSchema.Type.TIME, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.TIMESTAMP, Int64Type.getDescriptor())
              .build();

  private void isDescriptorEqual(Descriptor convertedProto, Descriptor originalProto) {
    // Check same number of fields
    assertEquals(convertedProto.getFields().size(), originalProto.getFields().size());
    for (FieldDescriptor convertedField : convertedProto.getFields()) {
      FieldDescriptor originalField = originalProto.findFieldByName(convertedField.getName());
      // Check name
      assertNotNull(originalField);
      FieldDescriptor.Type convertedType = convertedField.getType();
      FieldDescriptor.Type originalType = originalField.getType();
      // Check type
      assertEquals(convertedType, originalType);
      // Check mode
      assertTrue(
          (originalField.isRepeated() == convertedField.isRepeated())
              || (originalField.isRequired() == convertedField.isRequired())
              || (originalField.isOptional() == convertedField.isOptional()));
      if (convertedType == FieldDescriptor.Type.MESSAGE) {
        // Recursively check nested messages
        isDescriptorEqual(convertedField.getMessageType(), originalField.getMessageType());
      }
    }
  }

  @Test
  public void testBQTableSchemaToProtoDescriptorSimpleTypes() throws Exception {
    for (Map.Entry<Table.TableFieldSchema.Type, Descriptor> entry :
        BQTableTypeToCorrectProtoDescriptorTest.entrySet()) {
      Table.TableFieldSchema tableFieldSchema =
          Table.TableFieldSchema.newBuilder()
              .setType(entry.getKey())
              .setMode(Table.TableFieldSchema.Mode.NULLABLE)
              .setName("test_field_type")
              .build();
      Table.TableSchema tableSchema =
          Table.TableSchema.newBuilder().addFields(0, tableFieldSchema).build();
      Descriptor descriptor =
          BQTableSchemaToProtoDescriptor.ConvertBQTableSchemaToProtoDescriptor(tableSchema);
      isDescriptorEqual(descriptor, entry.getValue());
    }
  }

  @Test
  public void testBQTableSchemaToProtoDescriptorStructSimple() throws Exception {
    Table.TableFieldSchema StringType =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.STRING)
            .setMode(Table.TableFieldSchema.Mode.NULLABLE)
            .setName("test_field_type")
            .build();
    Table.TableFieldSchema tableFieldSchema =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.STRUCT)
            .setMode(Table.TableFieldSchema.Mode.NULLABLE)
            .setName("test_field_type")
            .addFields(0, StringType)
            .build();
    Table.TableSchema tableSchema =
        Table.TableSchema.newBuilder().addFields(0, tableFieldSchema).build();
    Descriptor descriptor =
        BQTableSchemaToProtoDescriptor.ConvertBQTableSchemaToProtoDescriptor(tableSchema);
    isDescriptorEqual(descriptor, MessageType.getDescriptor());
  }

  @Test
  public void testBQTableSchemaToProtoDescriptorStructComplex() throws Exception {
    Table.TableFieldSchema test_int =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.INT64)
            .setMode(Table.TableFieldSchema.Mode.NULLABLE)
            .setName("test_int")
            .build();
    Table.TableFieldSchema NestingLvl2 =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.STRUCT)
            .setMode(Table.TableFieldSchema.Mode.NULLABLE)
            .addFields(0, test_int)
            .setName("nesting_value")
            .build();
    Table.TableFieldSchema NestingLvl1 =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.STRUCT)
            .setMode(Table.TableFieldSchema.Mode.REPEATED)
            .addFields(0, test_int)
            .addFields(1, NestingLvl2)
            .setName("nesting_value1")
            .build();
    Table.TableSchema tableSchema =
        Table.TableSchema.newBuilder()
            .addFields(0, test_int)
            .addFields(1, NestingLvl1)
            .addFields(2, NestingLvl2)
            .build();
    Descriptor descriptor =
        BQTableSchemaToProtoDescriptor.ConvertBQTableSchemaToProtoDescriptor(tableSchema);
    isDescriptorEqual(descriptor, NestingStackedLvl0.getDescriptor());
  }

  @Test
  public void testBQTableSchemaToProtoDescriptorOptions() throws Exception {
    Table.TableFieldSchema required =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.INT64)
            .setMode(Table.TableFieldSchema.Mode.REQUIRED)
            .setName("test_required")
            .build();
    Table.TableFieldSchema repeated =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.INT64)
            .setMode(Table.TableFieldSchema.Mode.REPEATED)
            .setName("test_repeated")
            .build();
    Table.TableFieldSchema optional =
        Table.TableFieldSchema.newBuilder()
            .setType(Table.TableFieldSchema.Type.INT64)
            .setMode(Table.TableFieldSchema.Mode.NULLABLE)
            .setName("test_optional")
            .build();
    Table.TableSchema tableSchema =
        Table.TableSchema.newBuilder()
            .addFields(0, required)
            .addFields(1, repeated)
            .addFields(2, optional)
            .build();
    Descriptor descriptor =
        BQTableSchemaToProtoDescriptor.ConvertBQTableSchemaToProtoDescriptor(tableSchema);
    isDescriptorEqual(descriptor, OptionTest.getDescriptor());
  }
}
