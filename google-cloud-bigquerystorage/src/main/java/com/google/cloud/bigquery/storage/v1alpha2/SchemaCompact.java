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

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that checks the schema compatibility between Proto schema in proto descriptor and
 * Bigquery table schema. If this check is passed, then user can write to BigQuery table using the
 * user schema, otherwise the write will fail.
 *
 * <p>The implementation as of now is not complete, which measn, if this check passed, there is
 * still a possbility of writing will fail.
 */
public class SchemaCompact {
  private BigQuery bigquery;
  private static SchemaCompact compact;
  private static String tablePatternString = "projects/([^/]+)/datasets/([^/]+)/tables/([^/]+)";
  private static Pattern tablePattern = Pattern.compile(tablePatternString);
  private static final HashSet<Descriptors.FieldDescriptor.Type> SupportedTypes =
      new HashSet<>(
          Arrays.asList(
              Descriptors.FieldDescriptor.Type.INT32,
              Descriptors.FieldDescriptor.Type.INT64,
              Descriptors.FieldDescriptor.Type.UINT32,
              Descriptors.FieldDescriptor.Type.UINT64,
              Descriptors.FieldDescriptor.Type.FIXED32,
              Descriptors.FieldDescriptor.Type.FIXED64,
              Descriptors.FieldDescriptor.Type.SFIXED32,
              Descriptors.FieldDescriptor.Type.SFIXED64,
              Descriptors.FieldDescriptor.Type.FLOAT,
              Descriptors.FieldDescriptor.Type.DOUBLE,
              Descriptors.FieldDescriptor.Type.BOOL,
              Descriptors.FieldDescriptor.Type.BYTES,
              Descriptors.FieldDescriptor.Type.STRING,
              Descriptors.FieldDescriptor.Type.MESSAGE,
              Descriptors.FieldDescriptor.Type.GROUP,
              Descriptors.FieldDescriptor.Type.ENUM));

  private SchemaCompact(BigQuery bigquery) {
    this.bigquery = bigquery;
  }

  /**
   * Gets a singleton {code SchemaCompact} object.
   *
   * @return
   */
  public static SchemaCompact getInstance() {
    if (compact == null) {
      RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
      compact = new SchemaCompact(bigqueryHelper.getOptions().getService());
    }
    return compact;
  }

  /**
   * Gets a {code SchemaCompact} object with custom BigQuery stub.
   *
   * @param bigquery
   * @return
   */
  @VisibleForTesting
  public static SchemaCompact getInstance(BigQuery bigquery) {
    return new SchemaCompact(bigquery);
  }

  private TableId getTableId(String tableName) {
    Matcher matcher = tablePattern.matcher(tableName);
    if (!matcher.matches() || matcher.groupCount() != 3) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
    return TableId.of(matcher.group(1), matcher.group(2), matcher.group(3));
  }

  /**
   * Checks if proto schema is supported.
   *
   * @param protoSchema
   * @return True if protoSchema is supported
   * @throws IllegalArgumentException if schema is invalid
   */
  public static void isSupported(Descriptors.Descriptor protoSchema)
      throws IllegalArgumentException {
    HashSet<Descriptors.Descriptor> allMessageTypes = new HashSet<>();
    allMessageTypes.add(protoSchema);
    String protoSchemaName = protoSchema.getName();
    if (!isSupportedImpl(protoSchema, allMessageTypes, protoSchemaName)) {
      throw new IllegalArgumentException(
          "Proto schema "
              + protoSchemaName
              + " is not supported: contains nested messages of more than 15 levels.");
    }
  }

  /**
   * Actual implementation that checks if a proto schema is supported.
   *
   * @param protoSchema
   * @param allMessageTypes Keeps track of all message types seen, and make sure they don't repeat
   *     as recursive protos are not supported.
   * @param protoScope Debugging purposes to show error if messages are nested.
   * @return True if field type and option is supported
   * @throws IllegalArgumentException if schema is invalid
   */
  private static boolean isSupportedImpl(
      Descriptors.Descriptor protoSchema,
      HashSet<Descriptors.Descriptor> allMessageTypes,
      String protoScope)
      throws IllegalArgumentException {

    for (Descriptors.FieldDescriptor field : protoSchema.getFields()) {
      String currentProtoScope = protoScope + "." + field.getName();
      if (!isSupportedType(field)) {
        throw new IllegalArgumentException(
            "Proto schema "
                + currentProtoScope
                + " is not supported: contains "
                + field.getType()
                + " field type.");
      }

      if (field.isMapField()) {
        throw new IllegalArgumentException(
            "Proto schema " + currentProtoScope + " is not supported: is a map field.");
      }

      if (field.getType().equals(Descriptors.FieldDescriptor.Type.MESSAGE)
          || field.getType().equals(Descriptors.FieldDescriptor.Type.GROUP)) {
        if (!isNestedMessageAccepted(field.getMessageType(), allMessageTypes, currentProtoScope)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * @param field
   * @return True if fieldtype is supported by BQ Schema
   */
  public static boolean isSupportedType(Descriptors.FieldDescriptor field) {
    Descriptors.FieldDescriptor.Type fieldType = field.getType();
    if (!SupportedTypes.contains(fieldType)) {
      return false;
    }
    return true;
  }

  /**
   * Method that checks for proper nesting (no recursive protos) and supported types.
   *
   * @param message
   * @param level Keeps track of current level of nesting.
   * @param allMessageTypes Keeps track of all message types seen, and make sure they don't repeat
   *     as recursive protos are not supported.
   * @return True if fieldtype is supported by BQ Schema
   * @throws IllegalArgumentException if message is invalid
   */
  private static boolean isNestedMessageAccepted(
      Descriptors.Descriptor message,
      HashSet<Descriptors.Descriptor> allMessageTypes,
      String protoScope)
      throws IllegalArgumentException {

    if (allMessageTypes.size() > 15) {
      return false;
    }

    if (allMessageTypes.contains(message)) {
      throw new IllegalArgumentException(
          "Proto schema " + protoScope + " is not supported: is a recursively nested message.");
    }
    allMessageTypes.add(message);
    boolean result = isSupportedImpl(message, allMessageTypes, protoScope);
    allMessageTypes.remove(message);
    return result;
  }

  private static boolean isCompatibleWithBQBool(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.BOOL
        || field == Descriptors.FieldDescriptor.Type.INT32
        || field == Descriptors.FieldDescriptor.Type.INT64
        || field == Descriptors.FieldDescriptor.Type.UINT32
        || field == Descriptors.FieldDescriptor.Type.UINT64
        || field == Descriptors.FieldDescriptor.Type.FIXED32
        || field == Descriptors.FieldDescriptor.Type.FIXED64
        || field == Descriptors.FieldDescriptor.Type.SFIXED32
        || field == Descriptors.FieldDescriptor.Type.SFIXED64) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQBytes(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.BYTES) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQDate(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.INT32
        || field == Descriptors.FieldDescriptor.Type.INT64
        || field == Descriptors.FieldDescriptor.Type.SFIXED32
        || field == Descriptors.FieldDescriptor.Type.SFIXED64) {

      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQDatetime(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.INT64
        || field == Descriptors.FieldDescriptor.Type.SFIXED64) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQFloat(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.FLOAT) {
      return true;
    }
    if (field == Descriptors.FieldDescriptor.Type.DOUBLE) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQGeography(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.BYTES) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQInteger(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.INT64
        || field == Descriptors.FieldDescriptor.Type.SFIXED64
        || field == Descriptors.FieldDescriptor.Type.INT32
        || field == Descriptors.FieldDescriptor.Type.UINT32
        || field == Descriptors.FieldDescriptor.Type.FIXED32
        || field == Descriptors.FieldDescriptor.Type.SFIXED32
        || field == Descriptors.FieldDescriptor.Type.ENUM) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQNumeric(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.INT32
        || field == Descriptors.FieldDescriptor.Type.INT64
        || field == Descriptors.FieldDescriptor.Type.UINT32
        || field == Descriptors.FieldDescriptor.Type.UINT64
        || field == Descriptors.FieldDescriptor.Type.FIXED32
        || field == Descriptors.FieldDescriptor.Type.FIXED64
        || field == Descriptors.FieldDescriptor.Type.SFIXED32
        || field == Descriptors.FieldDescriptor.Type.SFIXED64) {
      return true;
    }

    if (field == Descriptors.FieldDescriptor.Type.BYTES) {
      return true;
    }

    return false;
  }

  private static boolean isCompatibleWithBQRecord(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.MESSAGE
        || field == Descriptors.FieldDescriptor.Type.GROUP) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQString(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.STRING
        || field == Descriptors.FieldDescriptor.Type.ENUM) {
      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQTime(Descriptors.FieldDescriptor.Type field) {
    if (field == Descriptors.FieldDescriptor.Type.INT64
        || field == Descriptors.FieldDescriptor.Type.SFIXED64) {

      return true;
    }
    return false;
  }

  private static boolean isCompatibleWithBQTimestamp(Descriptors.FieldDescriptor.Type field) {
    if (isCompatibleWithBQInteger(field)) {
      return true;
    }
    return false;
  }

  /**
   * Checks if proto field option is compatible with BQ field mode.
   *
   * @param protoField
   * @param BQField
   * @param protoScope Debugging purposes to show error if messages are nested.
   * @param BQScope Debugging purposes to show error if messages are nested.
   * @throws IllegalArgumentException if proto field type is incompatible with BQ field type.
   */
  private void protoFieldModeIsCompatibleWithBQFieldMode(
      Descriptors.FieldDescriptor protoField, Field BQField, String protoScope, String BQScope)
      throws IllegalArgumentException {
    if (BQField.getMode() == null) {
      throw new IllegalArgumentException(
          "Big query schema contains invalid field option for " + BQScope + ".");
    }
    switch (BQField.getMode()) {
      case REPEATED:
        if (!protoField.isRepeated()) {
          throw new IllegalArgumentException(
              "Given proto field "
                  + protoScope
                  + " is not repeated but Big Query field "
                  + BQScope
                  + " is.");
        }
        break;
      case REQUIRED:
        if (!protoField.isRequired()) {
          throw new IllegalArgumentException(
              "Given proto field "
                  + protoScope
                  + " is not required but Big Query field "
                  + BQScope
                  + " is.");
        }
        break;
      case NULLABLE:
        if (protoField.isRepeated()) {
          throw new IllegalArgumentException(
              "Given proto field "
                  + protoScope
                  + " is repeated but Big Query field "
                  + BQScope
                  + " is optional.");
        }
        break;
    }
  }
  /**
   * Checks if proto field type is compatible with BQ field type.
   *
   * @param protoField
   * @param BQField
   * @param allowUnknownFields
   * @param protoScope Debugging purposes to show error if messages are nested.
   * @param BQScope Debugging purposes to show error if messages are nested.
   * @throws IllegalArgumentException if proto field type is incompatible with BQ field type.
   */
  private void protoFieldTypeIsCompatibleWithBQFieldType(
      Descriptors.FieldDescriptor protoField,
      Field BQField,
      boolean allowUnknownFields,
      String protoScope,
      String BQScope)
      throws IllegalArgumentException {

    LegacySQLTypeName BQType = BQField.getType();
    Descriptors.FieldDescriptor.Type protoType = protoField.getType();

    boolean match = false;

    if (BQType == LegacySQLTypeName.BOOLEAN) {
      match = isCompatibleWithBQBool(protoType);
    } else if (BQType == LegacySQLTypeName.BYTES) {
      match = isCompatibleWithBQBytes(protoType);
    } else if (BQType == LegacySQLTypeName.DATE) {
      match = isCompatibleWithBQDate(protoType);
    } else if (BQType == LegacySQLTypeName.DATETIME) {
      match = isCompatibleWithBQDatetime(protoType);
    } else if (BQType == LegacySQLTypeName.FLOAT) {
      match = isCompatibleWithBQFloat(protoType);
    } else if (BQType == LegacySQLTypeName.GEOGRAPHY) {
      match = isCompatibleWithBQGeography(protoType);
    } else if (BQType == LegacySQLTypeName.INTEGER) {
      match = isCompatibleWithBQInteger(protoType);
    } else if (BQType == LegacySQLTypeName.NUMERIC) {
      match = isCompatibleWithBQNumeric(protoType);
    } else if (BQType == LegacySQLTypeName.RECORD) {
      match = isCompatibleWithBQRecord(protoType);
      if (match) {
        isProtoCompatibleWithBQ(
            protoField.getMessageType(),
            Schema.of(BQField.getSubFields()),
            allowUnknownFields,
            protoScope,
            BQScope,
            false);
      }
    } else if (BQType == LegacySQLTypeName.STRING) {
      match = isCompatibleWithBQString(protoType);
    } else if (BQType == LegacySQLTypeName.TIME) {
      match = isCompatibleWithBQTime(protoType);
    } else if (BQType == LegacySQLTypeName.TIMESTAMP) {
      match = isCompatibleWithBQTimestamp(protoType);
    }
    if (!match) {
      throw new IllegalArgumentException(
          "The proto field "
              + protoScope
              + " does not have a matching type with the big query field "
              + BQScope
              + ".");
    }
  }

  /**
   * Checks if proto schema is compatible with BQ schema.
   *
   * @param protoSchema
   * @param BQSchema
   * @param allowUnknownFields
   * @param protoScope Debugging purposes to show error if messages are nested.
   * @param BQScope Debugging purposes to show error if messages are nested.
   * @throws IllegalArgumentException if proto field type is incompatible with BQ field type.
   */
  private void isProtoCompatibleWithBQ(
      Descriptors.Descriptor protoSchema,
      Schema BQSchema,
      boolean allowUnknownFields,
      String protoScope,
      String BQScope,
      boolean topLevel)
      throws IllegalArgumentException {

    int matchedFields = 0;
    HashMap<String, Descriptors.FieldDescriptor> protoFieldMap = new HashMap<>();
    List<Descriptors.FieldDescriptor> protoFields = protoSchema.getFields();
    List<Field> BQFields = BQSchema.getFields();

    if (protoFields.size() > BQFields.size()) {
      if (!allowUnknownFields) {
        throw new IllegalArgumentException(
            "Proto schema "
                + protoScope
                + " has "
                + protoFields.size()
                + " fields, while BQ schema "
                + BQScope
                + " has "
                + BQFields.size()
                + " fields.");
      }
    }
    // Use hashmap to map from lowercased name to appropriate field to account for casing difference
    for (Descriptors.FieldDescriptor field : protoFields) {
      protoFieldMap.put(field.getName().toLowerCase(), field);
    }

    for (Field BQField : BQFields) {
      String fieldName = BQField.getName().toLowerCase();
      Descriptors.FieldDescriptor protoField = null;
      if (protoFieldMap.containsKey(fieldName)) {
        protoField = protoFieldMap.get(fieldName);
      }

      String currentBQScope = BQScope + "." + BQField.getName();
      if (protoField == null && BQField.getMode() == Field.Mode.REQUIRED) {
        throw new IllegalArgumentException(
            "The required Big Query field "
                + currentBQScope
                + " is missing in the proto schema "
                + protoScope
                + ".");
      }

      if (protoField == null) {
        continue;
      }
      String currentProtoScope = protoScope + "." + protoField.getName();
      protoFieldModeIsCompatibleWithBQFieldMode(
          protoField, BQField, currentProtoScope, currentBQScope);
      protoFieldTypeIsCompatibleWithBQFieldType(
          protoField, BQField, allowUnknownFields, currentProtoScope, currentBQScope);
      matchedFields++;
    }

    if (matchedFields == 0 && topLevel) {
      throw new IllegalArgumentException(
          "There is no matching fields found for the proto schema "
              + protoScope
              + " and the BQ table schema "
              + BQScope
              + ".");
    }
  }

  /**
   * Checks if proto schema is compatible with BQ schema after retrieving BQ schema by BQTableName.
   *
   * @param protoSchema
   * @param BQSchema
   * @param allowUnknownFields
   * @param protoScope Debugging purposes to show error if messages are nested.
   * @param BQScope Debugging purposes to show error if messages are nested.
   * @throws IllegalArgumentException if proto field type is incompatible with BQ field type.
   */
  public void check(
      String BQTableName, Descriptors.Descriptor protoSchema, boolean allowUnknownFields)
      throws IllegalArgumentException {

    Table table = bigquery.getTable(getTableId(BQTableName));
    Schema BQSchema = table.getDefinition().getSchema();
    String protoSchemaName = protoSchema.getName();
    isSupported(protoSchema);
    isProtoCompatibleWithBQ(
        protoSchema,
        BQSchema,
        allowUnknownFields,
        protoSchemaName,
        getTableId(BQTableName).getTable(),
        true);
  }
}
