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
import com.google.protobuf.Descriptors.Descriptor;
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

  @Test
  public void testSuccess() throws Exception {
    Field NonSupportedNestingLvl16 =
        Field.newBuilder("test1", LegacySQLTypeName.INTEGER).setMode(Field.Mode.NULLABLE).build();
    Field NonSupportedNestingLvl14 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl16)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl13 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl14)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl12 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl13)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl11 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl12)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl10 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl11)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl9 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl10)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl8 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl9)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl7 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl8)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl6 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl7)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl5 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl6)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl4 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl5)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl3 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl4)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl2 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl3)
            .setMode(Field.Mode.NULLABLE)
            .build();
    Field NonSupportedNestingLvl1 =
        Field.newBuilder("test1", LegacySQLTypeName.RECORD, NonSupportedNestingLvl2)
            .setMode(Field.Mode.NULLABLE)
            .build();
    customizeSchema(
        Schema.of(
            Field.newBuilder("test100", LegacySQLTypeName.RECORD, NonSupportedNestingLvl1)
                .setMode(Field.Mode.NULLABLE)
                .build()));
    JSONObject json = new JSONObject();
    json.put("str", "Allen");
    json.put("int", 100);
    json.put("bool", true);
    json.put("bytes", "Bytearray");
    JsonWriter writer = JsonWriter.getInstance(mockBigquery);
    Descriptor descriptor = writer.append("projects/p/datasets/d/tables/t", json);
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    compact.check("projects/p/datasets/d/tables/t", descriptor);
    verify(mockBigquery, times(2)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(2)).getDefinition();
  }
}
