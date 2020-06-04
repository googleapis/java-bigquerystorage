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
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.common.annotations.VisibleForTesting;
import com.google.cloud.bigquery.storage.test.TypeAnnotationProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DescriptorProtos;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

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
  private static final HashSet<Descriptors.FieldDescriptor.Type> SupportedTypes = new HashSet<>(Arrays.asList(
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
      Descriptors.FieldDescriptor.Type.ENUM
  ));
  private static HashSet<Descriptors.Descriptor> allMessageTypes = new HashSet<>();

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

  public static boolean hasFormatAnnotation(Descriptors.FieldDescriptor field) {
    return getFormatAnnotationImpl(field) != TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT;
  }

  private static TypeAnnotationProto.FieldFormat.Format getFormatAnnotationImpl(Descriptors.FieldDescriptor field) {
    // Read the format encoding, or if it doesn't exist, the type encoding.
    if (field.getOptions().hasExtension(TypeAnnotationProto.format)) {
      return field.getOptions().getExtension(TypeAnnotationProto.format);
    } else if (field.getOptions().hasExtension(TypeAnnotationProto.type)) {
      return field.getOptions().getExtension(TypeAnnotationProto.type);
    } else {
      return TypeAnnotationProto.FieldFormat.Format.DEFAULT_FORMAT;
    }
  }

  public static TypeAnnotationProto.FieldFormat.Format getFormatAnnotation(Descriptors.FieldDescriptor field) {
    // Read the format (or deprecated type) encoding.
    TypeAnnotationProto.FieldFormat.Format format = getFormatAnnotationImpl(field);

    TypeAnnotationProto.DeprecatedEncoding.Encoding encodingValue =
        field.getOptions().getExtension(TypeAnnotationProto.encoding);
    // If we also have a (valid) deprecated encoding annotation, merge that over
    // top of the type encoding.  Ignore any invalid encoding annotation.
    // This exists for backward compatability with existing .proto files only.
    if (encodingValue == TypeAnnotationProto.DeprecatedEncoding.Encoding.DATE_DECIMAL
        && format == TypeAnnotationProto.FieldFormat.Format.DATE) {
      return TypeAnnotationProto.FieldFormat.Format.DATE_DECIMAL;
    }
    return format;
  }

  /**
   *
   * @param field      A field of a proto schema
   * @return           True if fieldtype is supported by BQ Schema
   */
  public static boolean isSupportedType(Descriptors.FieldDescriptor field) {
    Descriptors.FieldDescriptor.Type fieldType = field.getType();
    if (!SupportedTypes.contains(fieldType)) {
      return false;
    }
    return true;
  }

  private static boolean isNestedMessageAccepted(Descriptors.Descriptor message, int level) throws IllegalArgumentException {

    if (level > 15) {
      throw new IllegalArgumentException("User schema " + message.getFullName() + " is not supported: contains nested messages of greater than 15 levels");
    }

    if (allMessageTypes.contains(message)) {
      throw new IllegalArgumentException("User schema " + message.getFullName() + " is not supported: contains nested messages of the same type.");
    }
    allMessageTypes.add(message);

    for (Descriptors.Descriptor submessage : message.getNestedTypes()) {
      if (!isSupportedImpl(submessage, level + 1)) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param field      A field of a proto schema
   * @return           True if field mode/option is supported
   */
  private static boolean isSupportedImpl(Descriptors.Descriptor userSchema, int level) throws IllegalArgumentException {
    List<Descriptors.OneofDescriptor> oneofs = userSchema.getOneofs();

    if (oneofs.size() > 0) {
      throw new IllegalArgumentException("User schema " + userSchema.getFullName() + " is not supported: contains oneof fields.");
    }

    for (Descriptors.FieldDescriptor field : userSchema.getFields()) {
      if (!isSupportedType(field)) {
        throw new IllegalArgumentException("User schema " + userSchema.getFullName() + " is not supported: contains " + field.getType() + " field type.");
      }

      if (field.isMapField()) {
        throw new IllegalArgumentException("User schema " + userSchema.getFullName() + " is not supported: contains map fields.");
      }
    }

    if (!isNestedMessageAccepted(userSchema, level)) {
      return false;
    }
    return true;
  }

  public static boolean isSupported(Descriptors.Descriptor userSchema) throws IllegalArgumentException {
    return isSupportedImpl(userSchema, 1);
  }
}
