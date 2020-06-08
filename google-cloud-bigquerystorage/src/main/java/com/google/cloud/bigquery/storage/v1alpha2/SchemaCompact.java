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
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors;
import com.google.zetasql.TypeAnnotationProto;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that checks the schema compatibility between user schema in proto descriptor and Bigquery
 * table schema. If this check is passed, then user can write to BigQuery table using the user
 * schema, otherwise the write will fail.
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
   * Checks if the userSchema is compatible with the table's current schema for writing. The current
   * implementatoin is not complete. If the check failed, the write couldn't succeed.
   *
   * @param tableName The name of the table to write to.
   * @param userSchema The schema user uses to append data.
   * @throws IllegalArgumentException the check failed.
   */
  public void check(String tableName, Descriptors.Descriptor userSchema)
      throws IllegalArgumentException {
    Table table = bigquery.getTable(getTableId(tableName));
    Schema schema = table.getDefinition().getSchema();
    // TODO: We only have very limited check here. More checks to be added.
    if (schema.getFields().size() != userSchema.getFields().size()) {
      throw new IllegalArgumentException(
          "User schema doesn't have expected field number with BigQuery table schema, expected: "
              + schema.getFields().size()
              + " actual: "
              + userSchema.getFields().size());
    }
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
      Descriptors.Descriptor message, HashSet<Descriptors.Descriptor> allMessageTypes)
      throws IllegalArgumentException {

    if (allMessageTypes.size() > 15) {
      throw new IllegalArgumentException(
          "User schema " + message.getFullName() + " is not supported: contains nested messages of more than 15 levels.");
    }

    if (allMessageTypes.contains(message)) {
      return false;
    }
    allMessageTypes.add(message);
    boolean result = isSupportedImpl(message, allMessageTypes);
    allMessageTypes.remove(message);
    return result;
  }

  /**
   * Actual implementation that checks if a user schema is supported.
   *
   * @param field
   * @param level Keeps track of current level of nesting.
   * @param allMessageTypes Keeps track of all message types seen, and make sure they don't repeat
   *     as recursive protos are not supported.
   * @return True if field type and option is supported
   * @throws IllegalArgumentException if schema is invalid
   */
  private static boolean isSupportedImpl(
      Descriptors.Descriptor userSchema, HashSet<Descriptors.Descriptor> allMessageTypes)
      throws IllegalArgumentException {

    // List<Descriptors.OneofDescriptor> oneofs = userSchema.getOneofs();
    // if (oneofs.size() > 0) {
    //   for (Descriptors.OneofDescriptor oneof : oneofs) {
    //     for (Descriptors.FieldDescriptor field : oneof.getFields()) {
    //       System.out.println(field.getFullName() + ": "+ field.getType());
    //     }
    //   }
    // }

    for (Descriptors.FieldDescriptor field : userSchema.getFields()) {
      if (!isSupportedType(field)) {
        throw new IllegalArgumentException(
            "User schema "
                + userSchema.getFullName()
                + " is not supported: contains "
                + field.getType()
                + " field type.");
      }

      if (field.isMapField()) {
        throw new IllegalArgumentException(
            "User schema " + userSchema.getFullName() + " is not supported: contains map fields.");
      }

      if (field.getType().equals(Descriptors.FieldDescriptor.Type.MESSAGE)
          || field.getType().equals(Descriptors.FieldDescriptor.Type.GROUP)) {
        if (!isNestedMessageAccepted(field.getMessageType(), allMessageTypes)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Checks if userSchema is supported
   *
   * @param userSchema
   * @return True if userSchema is supported
   * @throws IllegalArgumentException if schema is invalid
   */
  public static boolean isSupported(Descriptors.Descriptor userSchema)
      throws IllegalArgumentException {
    HashSet<Descriptors.Descriptor> allMessageTypes = new HashSet<>();
    allMessageTypes.add(userSchema);
    if (!isSupportedImpl(userSchema, allMessageTypes)) {
      throw new IllegalArgumentException(
          "User schema "
              + userSchema.getFullName()
              + " is not supported: contains ill-formatted nesting messages.");
    }
    return true;
  }

  public static boolean isCompatibleWithBQInteger(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
      if (field == Descriptors.FieldDescriptor.Type.INT64 ||
          field == Descriptors.FieldDescriptor.Type.SFIXED64) {

          return true;
      }

      if (field == Descriptors.FieldDescriptor.Type.INT32 ||
          field == Descriptors.FieldDescriptor.Type.UINT32 ||
          field == Descriptors.FieldDescriptor.Type.FIXED32 ||
          field == Descriptors.FieldDescriptor.Type.SFIXED32 ||
          field == Descriptors.FieldDescriptor.Type.ENUM) {

          return true;
      }

      return false;
  }

  public static boolean isCompatibleWithBQFloat(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
      if (field == Descriptors.FieldDescriptor.Type.FLOAT) {

          return true;
      }

      if (field == Descriptors.FieldDescriptor.Type.DOUBLE) {

          return true;
      }

      return false;
  }


  public static boolean isCompatibleWithBQBool(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
      if (field == Descriptors.FieldDescriptor.Type.BOOL) {
          return true;
      }

      if (field == Descriptors.FieldDescriptor.Type.INT32 ||
          field == Descriptors.FieldDescriptor.Type.INT64 ||
          field == Descriptors.FieldDescriptor.Type.UINT32 ||
          field == Descriptors.FieldDescriptor.Type.UINT64 ||
          field == Descriptors.FieldDescriptor.Type.FIXED32 ||
          field == Descriptors.FieldDescriptor.Type.FIXED64 ||
          field == Descriptors.FieldDescriptor.Type.SFIXED32 ||
          field == Descriptors.FieldDescriptor.Type.SFIXED64) {
          return true;
      }
      return false;
  }

  public static boolean isCompatibleWithBQStringAndBytes(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
      if (field == Descriptors.FieldDescriptor.Type.BYTES ||
          field == Descriptors.FieldDescriptor.Type.STRING) {
        return true;
      }
      return false;
  }

  public static boolean isCompatibleWithBQNumeric(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
      if (field == Descriptors.FieldDescriptor.Type.INT32 ||
          field == Descriptors.FieldDescriptor.Type.INT64 ||
          field == Descriptors.FieldDescriptor.Type.UINT32 ||
          field == Descriptors.FieldDescriptor.Type.UINT64 ||
          field == Descriptors.FieldDescriptor.Type.FIXED32 ||
          field == Descriptors.FieldDescriptor.Type.FIXED64 ||
          field == Descriptors.FieldDescriptor.Type.SFIXED32 ||
          field == Descriptors.FieldDescriptor.Type.SFIXED64) {
          return true;
      }

      if (field == Descriptors.FieldDescriptor.Type.BYTES &&
          (format == TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT ||
           format == TypeAnnotationProto.FieldFormat.Format.NUMERIC)) {
          return true;
      }

      return false;
  }

  public static boolean isCompatibleWithBQTimeStamp(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
        if (isCompatibleWithBQInteger(field, format) &&
            (format == TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT ||
             format == TypeAnnotationProto.FieldFormat.Format.TIMESTAMP_MICROS ||
             format == TypeAnnotationProto.FieldFormat.Format.TIMESTAMP_MILLIS  ||
             format == TypeAnnotationProto.FieldFormat.Format.TIMESTAMP_SECONDS)) {

            return true;
        }
        return false;
  }

  public static boolean isCompatibleWithBQDate(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
        if ((field == Descriptors.FieldDescriptor.Type.INT32 ||
             field == Descriptors.FieldDescriptor.Type.INT64 ||
             field == Descriptors.FieldDescriptor.Type.SFIXED32 ||
             field == Descriptors.FieldDescriptor.Type.SFIXED64) &&
            (format == TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT ||
             format == TypeAnnotationProto.FieldFormat.Format.DATE ||
             format == TypeAnnotationProto.FieldFormat.Format.DATE_DECIMAL)) {

            return true;
        }
        return false;
  }

  public static boolean isCompatibleWithBQTime(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
        if ((field == Descriptors.FieldDescriptor.Type.INT64 ||
             field == Descriptors.FieldDescriptor.Type.SFIXED64) &&
            (format == TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT ||
             format == TypeAnnotationProto.FieldFormat.Format.TIME_MICROS)) {

            return true;
        }
        return false;
  }

  public static boolean isCompatibleWithBQDateTime(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
        if ((field == Descriptors.FieldDescriptor.Type.INT64 ||
             field == Descriptors.FieldDescriptor.Type.SFIXED64) &&
            (format == TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT ||
             format == TypeAnnotationProto.FieldFormat.Format.DATETIME_MICROS)) {
            return true;
        }
        return false;
  }

  public static boolean isCompatibleWithBQRecord(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
        if (field == Descriptors.FieldDescriptor.Type.MESSAGE ||
            field == Descriptors.FieldDescriptor.Type.GROUP) {
            // TODO: Check if the underlying message is supported
            return true;
        }
        return false;
  }

  public static boolean isCompatibleWithBQGeography(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
        if (field == Descriptors.FieldDescriptor.Type.BYTES &&
            (format == TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT ||
             format == TypeAnnotationProto.FieldFormat.Format.ST_GEOGRAPHY_ENCODED)) {
            return true;
        }
        return false;
  }

  public static boolean isCompatibleWithBQBigNumeric(
    Descriptors.FieldDescriptor.Type field, TypeAnnotationProto.FieldFormat.Format format) {
        // TODO: Not implemented in cpp
        return false;
  }

  public boolean isProtoCompatibleWithBQ (
    Descriptors.Descriptor protoSchema, Schema BQSchema, boolean allowUnknownFields)
    throws IllegalArgumentException {
      int matchedFields = 0;

      List<Descriptors.FieldDescriptor> protoFields = protoSchema.getFields();
      List<Field> BQFields = BQSchema.getFields();

      if (protoFields.size() > BQFields.size()) {
        if (!allowUnknownFields) {
          throw new IllegalArgumentException(
              "Proto schema has "
                  + protoFields.size()
                  + " fields, while BQ schema has "
                  + BQFields.size()
                  + " fields.");
        }
      }

      // for (Field field : BQFields) {
      //
      // }

      return true;
  }

}
