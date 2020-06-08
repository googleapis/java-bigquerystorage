/*
 * Copyright 2016 Google LLC
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
import com.google.cloud.bigquery.storage.test.SchemaTest.*;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import javax.annotation.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class SchemaCompactTest {
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

  // @Test
  // public void testSuccess() throws Exception {
  //   TableDefinition definition =
  //       new TableDefinition() {
  //         @Override
  //         public Type getType() {
  //           return null;
  //         }
  //
  //         @Nullable
  //         @Override
  //         public Schema getSchema() {
  //           return Schema.of(Field.of("Foo", LegacySQLTypeName.STRING));
  //         }
  //
  //         @Override
  //         public Builder toBuilder() {
  //           return null;
  //         }
  //       };
  //   when(mockBigqueryTable.getDefinition()).thenReturn(definition);
  //   SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
  //   compact.check("projects/p/datasets/d/tables/t", FooType.getDescriptor());
  //   verify(mockBigquery, times(1)).getTable(any(TableId.class));
  //   verify(mockBigqueryTable, times(1)).getDefinition();
  // }
  //
  // @Test
  // public void testFailed() throws Exception {
  //   TableDefinition definition =
  //       new TableDefinition() {
  //         @Override
  //         public Type getType() {
  //           return null;
  //         }
  //
  //         @Nullable
  //         @Override
  //         public Schema getSchema() {
  //           return Schema.of(
  //               Field.of("Foo", LegacySQLTypeName.STRING),
  //               Field.of("Bar", LegacySQLTypeName.STRING));
  //         }
  //
  //         @Override
  //         public Builder toBuilder() {
  //           return null;
  //         }
  //       };
  //   when(mockBigqueryTable.getDefinition()).thenReturn(definition);
  //   SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
  //   try {
  //     compact.check("projects/p/datasets/d/tables/t", FooType.getDescriptor());
  //     fail("should fail");
  //   } catch (IllegalArgumentException expected) {
  //     assertEquals(
  //         "User schema doesn't have expected field number with BigQuery table schema, expected: 2 actual: 1",
  //         expected.getMessage());
  //   }
  //   verify(mockBigquery, times(1)).getTable(any(TableId.class));
  //   verify(mockBigqueryTable, times(1)).getDefinition();
  // }
  //
  // @Test
  // public void testBadTableName() throws Exception {
  //   try {
  //     SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
  //     compact.check("blah", FooType.getDescriptor());
  //     fail("should fail");
  //   } catch (IllegalArgumentException expected) {
  //     assertEquals("Invalid table name: blah", expected.getMessage());
  //   }
  // }
  //
  // @Test
  // public void testSupportedTypes() {
  //   SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
  //
  //   for (Descriptors.FieldDescriptor field : SupportedTypes.getDescriptor().getFields()) {
  //     assertTrue(compact.isSupportedType(field));
  //   }
  //
  //   for (Descriptors.FieldDescriptor field : NonSupportedTypes.getDescriptor().getFields()) {
  //     assertFalse(compact.isSupportedType(field));
  //   }
  // }

  @Test
  public void testMap() {
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    Descriptors.Descriptor testMap = NonSupportedMap.getDescriptor();
    try {
      compact.isSupported(testMap);
      fail("Should not be supported: field contains map");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "User schema " + testMap.getFullName() + " is not supported: contains map fields.",
          expected.getMessage());
    }
  }

  @Test
  public void testNestingGood() {
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    try {
      compact.isSupported(SupportedNestingLvl1.getDescriptor());
      compact.isSupported(SupportedNestingStacked.getDescriptor());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testNestingRecursive() {
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    Descriptors.Descriptor testNesting = NonSupportedNestingRecursive.getDescriptor();
    try {
      compact.isSupported(testNesting);
      fail("Should not be supported: field contains invalid nesting");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "User schema "
              + testNesting.getFullName()
              + " is not supported: contains recursively nested messages.",
          expected.getMessage());
    }
  }

  @Test
  public void testNestingContainsRecursive() {
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    Descriptors.Descriptor testNesting = NonSupportedNestingContainsRecursive.getDescriptor();
    try {
      compact.isSupported(testNesting);
      fail("Should not be supported: field contains invalid nesting");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "User schema " +
          NonSupportedNestingRecursive.getDescriptor().getFullName() +
          " is not supported: contains recursively nested messages.",
          expected.getMessage());
    }
  }

  @Test
  public void testNestingRecursiveLimit() {
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    Descriptors.Descriptor testNesting = NonSupportedNestingLvl0.getDescriptor();
    try {
      compact.isSupported(testNesting);
      fail("Should not be supported: contains nested messages of more than 15 levels.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "User schema "
              + testNesting.getFullName()
              + " is not supported: contains nested messages of more than 15 levels.",
          expected.getMessage());
    }
  }

  @Test
  public void testProtoMoreFields() {
    TableDefinition definition =
        new TableDefinition() {
          @Override
          public Type getType() {
            return null;
          }

          @Nullable
          @Override
          public Schema getSchema() {
            return Schema.of(Field.of("int32_value", LegacySQLTypeName.INTEGER));
          }

          @Override
          public Builder toBuilder() {
            return null;
          }
        };
    when(mockBigqueryTable.getDefinition()).thenReturn(definition);
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);

    try {
      compact.isProtoCompatibleWithBQ(SupportedTypes.getDescriptor(), "projects/p/datasets/d/tables/t", false);
      fail("Should fail: proto has more fields and allowUnknownFields flag is false.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "Proto schema has "
            + SupportedTypes.getDescriptor().getFields().size()
            + " fields, while BQ schema has "
            + 1
            + " fields.",
          expected.getMessage());
    }
    verify(mockBigquery, times(1)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(1)).getDefinition();
  }

  @Test
  public void testProtoFieldOptionsRepeated() {
    TableDefinition definition =
        new TableDefinition() {
          @Override
          public Type getType() {
            return null;
          }

          @Nullable
          @Override
          public Schema getSchema() {
            return Schema.of(Field.newBuilder("repeated_mode", LegacySQLTypeName.INTEGER).setMode(Field.Mode.REPEATED).build());
          }

          @Override
          public Builder toBuilder() {
            return null;
          }
        };
    when(mockBigqueryTable.getDefinition()).thenReturn(definition);
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    assertTrue(compact.isProtoCompatibleWithBQ(ProtoRepeatedBQRepeated.getDescriptor(), "projects/p/datasets/d/tables/t", false));

    try {
      compact.isProtoCompatibleWithBQ(ProtoOptionalBQRepeated.getDescriptor(), "projects/p/datasets/d/tables/t", false);
      fail("Should fail: BQ schema is repeated, but proto is optional.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
            "Given proto field repeated_mode is not repeated but Big Query field repeated_mode is.",
            expected.getMessage());
    }


    try {
      compact.isProtoCompatibleWithBQ(ProtoRequiredBQRepeated.getDescriptor(), "projects/p/datasets/d/tables/t", false);
      fail("Should fail: BQ schema is repeated, but proto is required.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
            "Given proto field repeated_mode is not repeated but Big Query field repeated_mode is.",
            expected.getMessage());
    }
    verify(mockBigquery, times(3)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(3)).getDefinition();
  }

  @Test
  public void testProtoFieldOptionsRequired() {
    TableDefinition definition =
        new TableDefinition() {
          @Override
          public Type getType() {
            return null;
          }

          @Nullable
          @Override
          public Schema getSchema() {
            return Schema.of(Field.newBuilder("required_mode", LegacySQLTypeName.INTEGER).setMode(Field.Mode.REQUIRED).build());
          }

          @Override
          public Builder toBuilder() {
            return null;
          }
        };
    when(mockBigqueryTable.getDefinition()).thenReturn(definition);
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    assertTrue(compact.isProtoCompatibleWithBQ(ProtoRequiredBQRequired.getDescriptor(), "projects/p/datasets/d/tables/t", false));

    try {
      compact.isProtoCompatibleWithBQ(ProtoNoneBQRequired.getDescriptor(), "projects/p/datasets/d/tables/t", false);
      fail("Should fail: BQ schema is required, but proto does not have this field.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
            "The required Big Query field required_mode is missing in the proto schema.",
            expected.getMessage());
    }

    try {
      compact.isProtoCompatibleWithBQ(ProtoOptionalBQRequired.getDescriptor(), "projects/p/datasets/d/tables/t", false);
      fail("Should fail: BQ schema is required, but proto is optional.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
            "Given proto field required_mode is not required but Big Query field required_mode is.",
            expected.getMessage());
    }


    try {
      compact.isProtoCompatibleWithBQ(ProtoRepeatedBQRequired.getDescriptor(), "projects/p/datasets/d/tables/t", false);
      fail("Should fail: BQ schema is required, but proto is repeated.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
            "Given proto field required_mode is not required but Big Query field required_mode is.",
            expected.getMessage());
    }
    verify(mockBigquery, times(4)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(4)).getDefinition();
  }

  @Test
  public void testProtoFieldOptionsOptional() {
    TableDefinition definition =
        new TableDefinition() {
          @Override
          public Type getType() {
            return null;
          }

          @Nullable
          @Override
          public Schema getSchema() {
            return Schema.of(Field.newBuilder("optional_mode", LegacySQLTypeName.INTEGER).setMode(Field.Mode.NULLABLE).build());
          }

          @Override
          public Builder toBuilder() {
            return null;
          }
        };
    when(mockBigqueryTable.getDefinition()).thenReturn(definition);
    SchemaCompact compact = SchemaCompact.getInstance(mockBigquery);
    assertTrue(compact.isProtoCompatibleWithBQ(ProtoOptionalBQOptional.getDescriptor(), "projects/p/datasets/d/tables/t", false));
    assertTrue(compact.isProtoCompatibleWithBQ(ProtoRequiredBQOptional.getDescriptor(), "projects/p/datasets/d/tables/t", false));

    try {
      compact.isProtoCompatibleWithBQ(ProtoRepeatedBQOptional.getDescriptor(), "projects/p/datasets/d/tables/t", false);
      fail("Should fail: BQ schema is nullable, but proto field is repeated.");
    } catch (IllegalArgumentException expected) {
      assertEquals(
            "Given proto field optional_mode is repeated but Big Query field optional_mode is optional.",
            expected.getMessage());
    }

    verify(mockBigquery, times(3)).getTable(any(TableId.class));
    verify(mockBigqueryTable, times(3)).getDefinition();
  }


}
