package com.google.cloud.bigquery.storage.v1;

import com.google.api.pathtemplate.ValidationException;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.UninitializedMessageException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.TemporalAccessor;

/**
 * This is equivalent of {@link JsonToProtoMessage} with GenericRecord instead of
 * JSONArray/JSONObject All conversions that JsonToProtoMessage does are kept exactly same
 *
 * <p>Goal here is to be extremely permisive and convert as much as possible to be able to store
 * records For example if we are writing into column of type INTEGER/INT64 and we have string "123"
 * we should convert it It would be nice to have a way to specify that we want to be strict and
 * throw exception if we can't convert or even have configurable conversion rules.
 *
 * <p>It is very hard to change field types in BigQuery so we should try to convert as much as
 * possible
 * https://cloud.google.com/bigquery/docs/reference/standard-sql/data-definition-language#details_21
 *
 * <p>Note: GenericRecord can be tricky Field values are different when it is deserialized from
 * binary form or when it is created from scratch. For example string values are Utf8 when read from
 * binary form and String when created from scratch. Both value types must be supported
 */
public class AvroToProtoConverter implements ToProtoConverter<GenericRecord> {

  public static final AvroToProtoConverter INSTANCE = new AvroToProtoConverter();

  private static final class ProtoFdAndBqSchema {

    public ProtoFdAndBqSchema(TableFieldSchema bqField, Descriptors.FieldDescriptor protoFd) {
      this.bqField = bqField;
      this.protoFd = protoFd;
    }

    TableFieldSchema bqField;
    Descriptors.FieldDescriptor protoFd;
  }

  @Override
  public List<DynamicMessage> convertToProtoMessage(
      Descriptors.Descriptor protoDescriptor,
      TableSchema tableSchema,
      Iterable<GenericRecord> records,
      boolean ignoreUnknownFields) {

    return convertToProtoMessage(
        protoDescriptor, tableSchema.getFieldsList(), records, "root", ignoreUnknownFields);
  }

  /**
   * protoDescriptor is generated from tableSchema in
   * BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor
   *
   * <p>Nothe that tableSchema/protoDescriptor can change during runtime as table schema is updated
   * in BQ
   *
   * @param protoDescriptor
   * @param bqTableFields
   * @param records
   * @param tracePath
   * @param ignoreUnknownFields
   * @return
   * @throws Exceptions.RowIndexToErrorException
   */
  private List<DynamicMessage> convertToProtoMessage(
      Descriptors.Descriptor protoDescriptor,
      List<TableFieldSchema> bqTableFields,
      Iterable<GenericRecord> records,
      String tracePath,
      boolean ignoreUnknownFields)
      throws Exceptions.RowIndexToErrorException {

    List<DynamicMessage> protoMessages = new ArrayList<>();
    Map<String, ProtoFdAndBqSchema> fieldsMetadata = new HashMap<>();
    Map<Integer, String> rowIndexErrors = new HashMap<>();

    boolean hasDataUnknownError = false;
    int index = 0;
    for (GenericRecord record : records) {
      try {
        DynamicMessage.Builder protoBldr =
            doRecord(
                record,
                protoDescriptor,
                bqTableFields,
                tracePath,
                ignoreUnknownFields,
                fieldsMetadata);
        protoMessages.add(protoBldr.build()); // throws UninitializedMessageException
      } catch (UninitializedMessageException umx) {
        rowIndexErrors.put(
            index,
            String.format("Required field %s.%s not present", tracePath, umx.getMissingFields()));
      } catch (IllegalArgumentException exception) {
        if (exception instanceof Exceptions.DataHasUnknownFieldException) {
          hasDataUnknownError = true;
        }
        if (exception instanceof FieldValueException) {
          FieldValueException fvx = (FieldValueException) exception;
          String message =
              String.format(
                  "Field %s value %s is not convertible to %s. Error: %s",
                  fvx.fieldPath,
                  fvx.value.getClass().getSimpleName(),
                  fvx.targetType,
                  fvx.getCause());
          rowIndexErrors.put(index, message);
        } else {
          rowIndexErrors.put(index, String.valueOf(exception));
        }
      }
      index++;
    }
    if (!rowIndexErrors.isEmpty()) {
      throw new Exceptions.RowIndexToErrorException(rowIndexErrors, hasDataUnknownError);
    }
    return protoMessages;
  }

  /**
   * Converts GenericRecord to DynamicMessage.Builder recursively for nested records It is
   * intentional that complete DynamicMessage is not returned because builder.build() throws
   * UninitializedMessageException for unfilled required fields, which we handle elsewhere
   *
   * @param record
   * @param protoDescriptor
   * @param bqTableFields
   * @param recordPath
   * @param ignoreUnknownFields
   * @param fieldsMetadata
   * @return
   * @throws IllegalArgumentException
   */
  private DynamicMessage.Builder doRecord(
      GenericRecord record,
      Descriptors.Descriptor protoDescriptor,
      List<TableFieldSchema> bqTableFields,
      String recordPath,
      boolean ignoreUnknownFields,
      Map<String, ProtoFdAndBqSchema> fieldsMetadata)
      throws IllegalArgumentException {
    DynamicMessage.Builder protoBldr = DynamicMessage.newBuilder(protoDescriptor);
    for (Schema.Field avroField : record.getSchema().getFields()) {
      String fieldName = avroField.name();
      String fieldPath = recordPath + "." + fieldName;
      ProtoFdAndBqSchema schemaPair =
          fieldsMetadata.computeIfAbsent(
              fieldPath,
              key ->
                  computeDescriptorAndSchema(fieldPath, fieldName, protoDescriptor, bqTableFields));
      if (schemaPair == null) {
        if (ignoreUnknownFields) continue; // we are allowed to skip unknown fields
        else {
          throw new Exceptions.DataHasUnknownFieldException(fieldPath);
        }
      }
      Object fieldValue = record.get(fieldName);
      if (fieldValue == null) {
        continue; // skip field as there is nothing to add
      }
      Descriptors.FieldDescriptor protoFd = schemaPair.protoFd;
      TableFieldSchema bqField = schemaPair.bqField;
      String extraPath = fieldPath; // Another variable is required to capture array index
      try {
        if (protoFd.isRepeated()) { // repeated field aka array
          if (fieldValue instanceof GenericArray) {
            GenericArray avroArray = (GenericArray) fieldValue;
            for (int index = 0; index < avroArray.size(); index++) {
              extraPath = fieldPath + "[" + index + "]";
              Object elementValue = avroArray.get(index);
              Object protoValue =
                  convert(
                      elementValue,
                      protoFd,
                      bqField,
                      extraPath,
                      ignoreUnknownFields,
                      fieldsMetadata);
              if (protoValue != null) {
                protoBldr.addRepeatedField(protoFd, protoValue);
              } else {
                throw mkFieldException(elementValue, protoFd, bqField, extraPath, null);
              }
            }
          } else {
            // We can create a single element array instead of failing
            throw mkFieldException(fieldValue, protoFd, bqField, extraPath, null);
          }

        } else { // simple non-repeated field
          Object protoValue =
              convert(fieldValue, protoFd, bqField, extraPath, ignoreUnknownFields, fieldsMetadata);
          if (protoValue != null) {
            protoBldr.setField(protoFd, protoValue);
          } else {
            throw mkFieldException(fieldValue, protoFd, bqField, extraPath, null);
          }
        }
      } catch (FieldValueException ex) {
        throw ex;
      } catch (Exception ex) {
        // This function is recursively called, so this throw will be caught and throw directly
        // out by the catch above.
        throw mkFieldException(fieldValue, protoFd, bqField, fieldPath, ex);
      }
    }
    return protoBldr;
  }

  static class FieldValueException extends IllegalArgumentException {
    final Object value;
    final String fieldPath;
    final String targetType;

    public FieldValueException(Object value, String fieldPath, String targetType, Exception cause) {
      super(null, cause);
      this.value = value;
      this.fieldPath = fieldPath;
      this.targetType = targetType;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
      return this; // no use for stack trace
    }
  }

  private FieldValueException mkFieldException(
      Object value,
      Descriptors.FieldDescriptor protoFd,
      TableFieldSchema bqField,
      String fieldPath,
      Exception cause) {
    String targetType =
        bqField != null
            ? bqField.getType().name() + "/" + protoFd.getType().name()
            : protoFd.getType().name();
    return new FieldValueException(value, fieldPath, targetType, cause);
  }

  /**
   * Method - converts Avro value to Protobuf value with respect to protoFd and bqField applying all
   * possible conversions - returns null when conversion is not possible - recursively calls
   * doRecord for nested records
   *
   * @return converted value for protobuf or null if conversion is not possible
   * @throws UninitializedMessageException when nested record fails to initialize due to missing
   *     required fields
   */
  private Object convert(
      Object avroValue,
      Descriptors.FieldDescriptor protoFd,
      TableFieldSchema bqField,
      String fieldPath,
      boolean ignoreUnknownFields,
      Map<String, ProtoFdAndBqSchema> fieldsMetadata)
      throws UninitializedMessageException {

    switch (protoFd.getType()) {
      case BOOL:
        if (avroValue instanceof Boolean) {
          return avroValue;
        } else if (avroValue instanceof String
            && ("true".equalsIgnoreCase(((String) avroValue))
                || "false".equalsIgnoreCase(((String) avroValue)))) {
          return Boolean.parseBoolean((String) avroValue);
        }
        break;
      case BYTES:
        if (bqField != null) {
          if (bqField.getType() == TableFieldSchema.Type.NUMERIC) {
            if (avroValue instanceof String) {
              return BigDecimalByteStringEncoder.encodeToNumericByteString(
                  new BigDecimal((String) avroValue));
            } else if (avroValue instanceof Short
                || avroValue instanceof Integer
                || avroValue instanceof Long) {
              return BigDecimalByteStringEncoder.encodeToNumericByteString(
                  new BigDecimal(((Number) avroValue).longValue()));
            } else if (avroValue instanceof Float || avroValue instanceof Double) {
              // In JSON, the precision passed in is machine dependent. We should round the number
              // before passing to backend.
              BigDecimal bigDecimal = new BigDecimal(String.valueOf(avroValue));
              if (bigDecimal.scale() > 9) {
                bigDecimal = bigDecimal.setScale(NUMERIC_SCALE, RoundingMode.HALF_UP);
              }
              return BigDecimalByteStringEncoder.encodeToNumericByteString(bigDecimal);
            } else if (avroValue instanceof BigDecimal) {
              return BigDecimalByteStringEncoder.encodeToNumericByteString((BigDecimal) avroValue);
            }
          } else if (bqField.getType() == TableFieldSchema.Type.BIGNUMERIC) {
            if (avroValue instanceof String) {
              return BigDecimalByteStringEncoder.encodeToBigNumericByteString(
                  new BigDecimal((String) avroValue));
            } else if (avroValue instanceof Short
                || avroValue instanceof Integer
                || avroValue instanceof Long) {
              return BigDecimalByteStringEncoder.encodeToBigNumericByteString(
                  new BigDecimal(((Number) avroValue).longValue()));
            } else if (avroValue instanceof Float || avroValue instanceof Double) {
              return BigDecimalByteStringEncoder.encodeToBigNumericByteString(
                  new BigDecimal(String.valueOf(avroValue)));
            } else if (avroValue instanceof BigDecimal) {
              return BigDecimalByteStringEncoder.encodeToBigNumericByteString(
                  (BigDecimal) avroValue);
            }
          }
        }
        if (avroValue instanceof ByteBuffer) {
          ByteBuffer buffer = ((ByteBuffer) avroValue);
          if (buffer.hasArray()) {
            return buffer.array();
          } else {
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return bytes;
          }
        } else if (avroValue instanceof ByteString) {
          return ((ByteString) avroValue).toByteArray();
        }
        //        } else if (val instanceof JSONArray) {
        //          byte[] bytes = new byte[((JSONArray) val).length()];
        //          for (int j = 0; j < ((JSONArray) val).length(); j++) {
        //            bytes[j] = (byte) ((JSONArray) val).getInt(j);
        //            if (bytes[j] != ((JSONArray) val).getInt(j)) {
        //              throw new IllegalArgumentException(
        //                String.format(
        //                  "Error: " + currentScope + "[" + j + "] could not be converted to
        // byte[]."));
        //            }
        //          }
        //          protoMsg.setField(fieldDescriptor, bytes);
        //          return;
        //        }
        break;
      case INT64:
        if (bqField != null) {
          if (bqField.getType() == TableFieldSchema.Type.DATETIME) {
            if (avroValue instanceof String) {
              return CivilTimeEncoder.encodePacked64DatetimeMicros(
                  LocalDateTime.parse((String) avroValue, DATETIME_FORMATTER));
            } else if (avroValue instanceof Long) {
              return avroValue;
            }
          } else if (bqField.getType() == TableFieldSchema.Type.TIME) {
            if (avroValue instanceof String) {
              return CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.parse((String) avroValue));
            } else if (avroValue instanceof Long) {
              return avroValue;
            }
          } else if (bqField.getType() == TableFieldSchema.Type.TIMESTAMP) {
            if (avroValue instanceof String) {
              Double parsed = Doubles.tryParse((String) avroValue);
              if (parsed != null) {
                return parsed.longValue();
              }
              TemporalAccessor parsedTime = TIMESTAMP_FORMATTER.parse((String) avroValue);
              return parsedTime.getLong(ChronoField.INSTANT_SECONDS) * 1000000
                  + parsedTime.getLong(ChronoField.MICRO_OF_SECOND);
            } else if (avroValue instanceof Long) {
              return avroValue;
            } else if (avroValue instanceof Integer) {
              return Long.valueOf((Integer) avroValue);
            }
          }
        }
        if (avroValue instanceof Integer) {
          return Long.valueOf((Integer) avroValue);
        } else if (avroValue instanceof Long) {
          return avroValue;
        } else if (avroValue instanceof String) {
          Long parsed = Longs.tryParse((String) avroValue);
          if (parsed != null) {
            return parsed;
          }
        }
        break;
      case INT32:
        if (bqField != null && bqField.getType() == TableFieldSchema.Type.DATE) {
          if (avroValue instanceof String) {
            return (int) LocalDate.parse((String) avroValue).toEpochDay();
          } else if (avroValue instanceof Integer || avroValue instanceof Long) {
            return ((Number) avroValue).intValue();
          }
        }
        if (avroValue instanceof Integer) {
          return avroValue;
        } else if (avroValue instanceof String) {
          Integer parsed = Ints.tryParse((String) avroValue);
          if (parsed != null) {
            return parsed;
          }
        }
        break;
      case STRING:
        if (avroValue instanceof Utf8) {
          return avroValue.toString();
        } else if (avroValue instanceof String) {
          return avroValue;
        } else if (avroValue instanceof Short
            || avroValue instanceof Integer
            || avroValue instanceof Long
            || avroValue instanceof Boolean) {
          return String.valueOf(avroValue);
        }
        break;
      case DOUBLE:
        if (avroValue instanceof Number) {
          return ((Number) avroValue).doubleValue();
        } else if (avroValue instanceof String) {
          Double parsed = Doubles.tryParse((String) avroValue);
          if (parsed != null) {
            return parsed;
          }
        }
        break;
      case MESSAGE:
        if (bqField != null && bqField.getType() == TableFieldSchema.Type.RANGE) {
          if (avroValue instanceof String) {
            String rangeString = (String) avroValue;
            return mkProtoRange(
                    rangeString, protoFd, bqField, fieldPath, ignoreUnknownFields, fieldsMetadata)
                .build();
          }
        } else if (avroValue instanceof GenericRecord) {
          DynamicMessage.Builder builder =
              doRecord(
                  (GenericRecord) avroValue,
                  protoFd.getMessageType(),
                  bqField == null ? null : bqField.getFieldsList(),
                  fieldPath,
                  ignoreUnknownFields,
                  fieldsMetadata);
          return builder.build(); // throws UninitializedMessageException
        }
        break;
      default:
        throw new IllegalArgumentException("Unsupported field type " + protoFd.getType());
    }
    return null;
  }

  /** https://cloud.google.com/bigquery/docs/reference/standard-sql/data-types#range_with_literal */
  private DynamicMessage.Builder mkProtoRange(
      String rangeString,
      Descriptors.FieldDescriptor protoFd,
      TableFieldSchema bqField,
      String fieldPath,
      boolean ignoreUnknownFields,
      Map<String, ProtoFdAndBqSchema> fieldsMetadata) {
    if (rangeString.charAt(0) != '[' || rangeString.charAt(rangeString.length() - 1) != ')') {
      throw new IllegalArgumentException(
          "RANGE literal must have format [lower_bound, upper_bound)");
    }
    String[] rangeSplits = rangeString.substring(1, rangeString.length() - 1).split(",");
    if (rangeSplits.length != 2) {
      throw new IllegalArgumentException(
          "RANGE literal must have exactly two values separated by comma");
    }
    String startString = rangeSplits[0].trim().toUpperCase();
    String endString = rangeSplits[1].trim().toUpperCase();

    Object startValue = null;
    if (!startString.equals("NULL") && !startString.equals("UNBOUNDED")) {
      startValue =
          convert(
              startString,
              protoFd.getMessageType().findFieldByName("start"),
              bqField.getFields(0),
              fieldPath,
              ignoreUnknownFields,
              fieldsMetadata);
    }
    Object endValue = null;
    if (!endString.equals("NULL") && !endString.equals("UNBOUNDED")) {
      endValue =
          convert(
              endString,
              protoFd.getMessageType().findFieldByName("end"),
              bqField.getFields(1),
              fieldPath,
              ignoreUnknownFields,
              fieldsMetadata);
    }

    DynamicMessage.Builder protoBldr = DynamicMessage.newBuilder(protoFd.getMessageType());
    if (startValue != null) {
      protoBldr.setField(protoBldr.getDescriptorForType().findFieldByName("start"), startValue);
    }
    if (endValue != null) {
      protoBldr.setField(protoBldr.getDescriptorForType().findFieldByName("end"), endValue);
    }
    return protoBldr;
  }

  // This is exacly same as JsonToProtoMessage,
  // Must be used as it depends on name shenanigans in
  // BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor
  private ProtoFdAndBqSchema computeDescriptorAndSchema(
      String currentScope,
      String inputFieldName,
      Descriptors.Descriptor protoDescr,
      List<TableFieldSchema> bqFields) {

    // We want lowercase here to support case-insensitive data writes.
    // The protobuf descriptor that is used is assumed to have all lowercased fields
    String inputFieldLocator = inputFieldName.toLowerCase();

    // If jsonName is not compatible with proto naming convention, we should look by its
    // placeholder name.
    if (!BigQuerySchemaUtil.isProtoCompatible(inputFieldLocator)) {
      inputFieldLocator = BigQuerySchemaUtil.generatePlaceholderFieldName(inputFieldLocator);
    }

    Descriptors.FieldDescriptor protoFd = protoDescr.findFieldByName(inputFieldLocator);
    if (protoFd == null) {
      return null; // Input field not exists in proto schema
    }
    TableFieldSchema bqField = null;
    if (bqFields != null) {
      // protoSchema is generated from tableSchema so their field ordering should match.
      bqField = bqFields.get(protoFd.getIndex());
      // For RANGE type, expliclitly add the fields start and end of the same FieldElementType as it
      // is not expliclity defined in the TableFieldSchema.
      if (bqField.getType() == TableFieldSchema.Type.RANGE) {
        switch (bqField.getRangeElementType().getType()) {
          case DATE:
          case DATETIME:
          case TIMESTAMP:
            bqField =
                bqField
                    .toBuilder()
                    .addFields(
                        TableFieldSchema.newBuilder()
                            .setName("start")
                            .setType(bqField.getRangeElementType().getType())
                            .build())
                    .addFields(
                        TableFieldSchema.newBuilder()
                            .setName("end")
                            .setType(bqField.getRangeElementType().getType())
                            .build())
                    .build();
            break;
          default:
            throw new ValidationException(
                "Field at index "
                    + protoFd.getIndex()
                    + " with name ("
                    + bqField.getName()
                    + ") with type (RANGE) has an unsupported range element type ("
                    + bqField.getRangeElementType()
                    + ")");
        }
      }

      if (!bqField.getName().toLowerCase().equals(BigQuerySchemaUtil.getFieldName(protoFd))) {
        throw new ValidationException(
            "Field at index "
                + protoFd.getIndex()
                + " has mismatch names ("
                + bqField.getName()
                + ") ("
                + protoFd.getName()
                + ")");
      }
    }
    return new ProtoFdAndBqSchema(bqField, protoFd);
  }

  private static final int NUMERIC_SCALE = 9;
  private static final ImmutableMap<Descriptors.FieldDescriptor.Type, String>
      FIELD_TYPE_TO_DEBUG_MESSAGE =
          new ImmutableMap.Builder<Descriptors.FieldDescriptor.Type, String>()
              .put(Descriptors.FieldDescriptor.Type.BOOL, "boolean")
              .put(Descriptors.FieldDescriptor.Type.BYTES, "bytes")
              .put(Descriptors.FieldDescriptor.Type.INT32, "int32")
              .put(Descriptors.FieldDescriptor.Type.DOUBLE, "double")
              .put(Descriptors.FieldDescriptor.Type.INT64, "int64")
              .put(Descriptors.FieldDescriptor.Type.STRING, "string")
              .put(Descriptors.FieldDescriptor.Type.MESSAGE, "object")
              .build();
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      new DateTimeFormatterBuilder()
          .parseLenient()
          .append(DateTimeFormatter.ofPattern("yyyy[/][-]MM[/][-]dd"))
          .optionalStart()
          .appendLiteral('T')
          .optionalEnd()
          .optionalStart()
          .appendLiteral(' ')
          .optionalEnd()
          .appendValue(ChronoField.HOUR_OF_DAY, 2)
          .appendLiteral(':')
          .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
          .optionalStart()
          .appendLiteral(':')
          .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
          .optionalEnd()
          .optionalStart()
          .appendValue(ChronoField.MILLI_OF_SECOND, 3)
          .optionalEnd()
          .optionalStart()
          .appendFraction(ChronoField.MICRO_OF_SECOND, 3, 6, true)
          .optionalEnd()
          .optionalStart()
          .appendFraction(ChronoField.NANO_OF_SECOND, 6, 9, true)
          .optionalEnd()
          .optionalStart()
          .appendLiteral(' ')
          .optionalEnd()
          .optionalStart()
          .appendOffset("+HH:MM", "+00:00")
          .optionalEnd()
          .optionalStart()
          .appendZoneText(TextStyle.SHORT)
          .optionalEnd()
          .optionalStart()
          .appendLiteral('Z')
          .optionalEnd()
          .toFormatter()
          .withZone(ZoneOffset.UTC);

  private static final DateTimeFormatter DATETIME_FORMATTER =
      new DateTimeFormatterBuilder()
          .parseLenient()
          .append(DateTimeFormatter.ISO_LOCAL_DATE)
          .optionalStart()
          .optionalStart()
          .parseCaseInsensitive()
          .appendLiteral('T')
          .optionalEnd()
          .optionalStart()
          .appendLiteral(' ')
          .optionalEnd()
          .append(DateTimeFormatter.ISO_LOCAL_TIME)
          .optionalEnd()
          .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
          .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
          .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
          .toFormatter();
}
