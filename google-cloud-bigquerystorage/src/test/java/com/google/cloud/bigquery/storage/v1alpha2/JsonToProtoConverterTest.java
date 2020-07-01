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
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonToProtoConverterTest {
  private static ImmutableMap<Table.TableFieldSchema.Type, Descriptor>
      BQTableTypeToProtoDescriptor =
          new ImmutableMap.Builder<Table.TableFieldSchema.Type, Descriptor>()
              .put(Table.TableFieldSchema.Type.BOOL, BoolType.getDescriptor())
              .put(Table.TableFieldSchema.Type.BYTES, BytesType.getDescriptor())
              .put(Table.TableFieldSchema.Type.DATE, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.DATETIME, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.DOUBLE, DoubleType.getDescriptor())
              .put(Table.TableFieldSchema.Type.GEOGRAPHY, BytesType.getDescriptor())
              .put(Table.TableFieldSchema.Type.INT64, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.NUMERIC, DoubleType.getDescriptor())
              .put(Table.TableFieldSchema.Type.STRING, StringType.getDescriptor())
              .put(Table.TableFieldSchema.Type.TIME, Int64Type.getDescriptor())
              .put(Table.TableFieldSchema.Type.TIMESTAMP, Int64Type.getDescriptor())
              .build();

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

  @Test
  public void testBQTableSchemaToProtoDescriptorSimpleTypes() throws Exception {
    for (Map.Entry<Table.TableFieldSchema.Type, Descriptor> entry :
        BQTableTypeToProtoDescriptor.entrySet()) {
      Table.TableFieldSchema tableFieldSchema =
          Table.TableFieldSchema.newBuilder()
              .setType(entry.getKey())
              .setMode(Table.TableFieldSchema.Mode.NULLABLE)
              .setName("test_field_type")
              .build();
      Table.TableSchema tableSchema =
          Table.TableSchema.newBuilder().addFields(0, tableFieldSchema).build();
      Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
      assertTrue(isDescriptorEqual(descriptor, entry.getValue()));
    }
  }

  @Test
  public void testBQTableSchemaToProtoDescriptorComplex() throws Exception {
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
    Descriptor descriptor = JsonToProtoConverter.BQTableSchemaToProtoSchema(tableSchema);
    assertTrue(isDescriptorEqual(descriptor, MessageType.getDescriptor()));
  }
}
