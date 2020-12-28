/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery.storage.v1beta2;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.storage.v1alpha2.Table;
import com.google.common.collect.ImmutableMap;

/** Converts structure from BigQuery v2 API to BigQueryStorage API */
public class BQV2ToBQStorageConverter {
  private static ImmutableMap<Field.Mode, TableFieldSchema.Mode> BQTableSchemaModeMap =
      ImmutableMap.of(
          Field.Mode.NULLABLE, TableFieldSchema.Mode.NULLABLE,
          Field.Mode.REPEATED, TableFieldSchema.Mode.REPEATED,
          Field.Mode.REQUIRED, TableFieldSchema.Mode.REQUIRED);

  private static ImmutableMap<StandardSQLTypeName, TableFieldSchema.Type> BQTableSchemaTypeMap =
      new ImmutableMap.Builder<StandardSQLTypeName, TableFieldSchema.Type>()
          .put(StandardSQLTypeName.BOOL, TableFieldSchema.Type.BOOL)
          .put(StandardSQLTypeName.BYTES, TableFieldSchema.Type.BYTES)
          .put(StandardSQLTypeName.DATE, TableFieldSchema.Type.DATE)
          .put(StandardSQLTypeName.DATETIME, TableFieldSchema.Type.DATETIME)
          .put(StandardSQLTypeName.FLOAT64, TableFieldSchema.Type.DOUBLE)
          .put(StandardSQLTypeName.GEOGRAPHY, TableFieldSchema.Type.GEOGRAPHY)
          .put(StandardSQLTypeName.INT64, TableFieldSchema.Type.INT64)
          .put(StandardSQLTypeName.NUMERIC, TableFieldSchema.Type.NUMERIC)
          .put(StandardSQLTypeName.STRING, TableFieldSchema.Type.STRING)
          .put(StandardSQLTypeName.STRUCT, TableFieldSchema.Type.STRUCT)
          .put(StandardSQLTypeName.TIME, TableFieldSchema.Type.TIME)
          .put(StandardSQLTypeName.TIMESTAMP, TableFieldSchema.Type.TIMESTAMP)
          .build();

  /**
   * Converts from bigquery v2 Table Schema to bigquery storage API Table Schema.
   *
   * @param schame the bigquery v2 Table Schema
   * @return the bigquery storage API Table Schema
   */
  public static TableSchema ConvertTableSchema(Schema schema) {
    TableSchema.Builder result = TableSchema.newBuilder();
    for (int i = 0; i < schema.getFields().size(); i++) {
      result.addFields(i, ConvertFieldSchema(schema.getFields().get(i)));
    }
    return result.build();
  }

  /**
   * Converts from bigquery v2 Field Schema to bigquery storage API Field Schema.
   *
   * @param schame the bigquery v2 Field Schema
   * @return the bigquery storage API Field Schema
   */
  public static TableFieldSchema ConvertFieldSchema(Field field) {
    TableFieldSchema.Builder result = TableFieldSchema.newBuilder();
    if (field.getMode() == null) {
      field = field.toBuilder().setMode(Field.Mode.NULLABLE).build();
    }
    result.setMode(BQTableSchemaModeMap.get(field.getMode()));
    result.setName(field.getName());
    result.setType(BQTableSchemaTypeMap.get(field.getType().getStandardType()));
    if (field.getDescription() != null) {
      result.setDescription(field.getDescription());
    }
    if (field.getSubFields() != null) {
      for (int i = 0; i < field.getSubFields().size(); i++) {
        result.addFields(i, ConvertFieldSchema(field.getSubFields().get(i)));
      }
    }
    return result.build();
  }
}
