/*
 * Copyright 2024 Google LLC
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
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/avro.proto

// Protobuf Java Version: 3.25.3
package com.google.cloud.bigquery.storage.v1;

/**
 *
 *
 * <pre>
 * Avro rows.
 * </pre>
 *
 * Protobuf type {@code google.cloud.bigquery.storage.v1.AvroRows}
 */
public final class AvroRows extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.bigquery.storage.v1.AvroRows)
    AvroRowsOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use AvroRows.newBuilder() to construct.
  private AvroRows(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private AvroRows() {
    serializedBinaryRows_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new AvroRows();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.bigquery.storage.v1.AvroProto
        .internal_static_google_cloud_bigquery_storage_v1_AvroRows_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.bigquery.storage.v1.AvroProto
        .internal_static_google_cloud_bigquery_storage_v1_AvroRows_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.bigquery.storage.v1.AvroRows.class,
            com.google.cloud.bigquery.storage.v1.AvroRows.Builder.class);
  }

  public static final int SERIALIZED_BINARY_ROWS_FIELD_NUMBER = 1;
  private com.google.protobuf.ByteString serializedBinaryRows_ =
      com.google.protobuf.ByteString.EMPTY;
  /**
   *
   *
   * <pre>
   * Binary serialized rows in a block.
   * </pre>
   *
   * <code>bytes serialized_binary_rows = 1;</code>
   *
   * @return The serializedBinaryRows.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getSerializedBinaryRows() {
    return serializedBinaryRows_;
  }

  public static final int ROW_COUNT_FIELD_NUMBER = 2;
  private long rowCount_ = 0L;
  /**
   *
   *
   * <pre>
   * [Deprecated] The count of rows in the returning block.
   * Please use the format-independent ReadRowsResponse.row_count instead.
   * </pre>
   *
   * <code>int64 row_count = 2 [deprecated = true];</code>
   *
   * @deprecated google.cloud.bigquery.storage.v1.AvroRows.row_count is deprecated. See
   *     google/cloud/bigquery/storage/v1/avro.proto;l=39
   * @return The rowCount.
   */
  @java.lang.Override
  @java.lang.Deprecated
  public long getRowCount() {
    return rowCount_;
  }

  private byte memoizedIsInitialized = -1;

  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
    if (!serializedBinaryRows_.isEmpty()) {
      output.writeBytes(1, serializedBinaryRows_);
    }
    if (rowCount_ != 0L) {
      output.writeInt64(2, rowCount_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!serializedBinaryRows_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream.computeBytesSize(1, serializedBinaryRows_);
    }
    if (rowCount_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(2, rowCount_);
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof com.google.cloud.bigquery.storage.v1.AvroRows)) {
      return super.equals(obj);
    }
    com.google.cloud.bigquery.storage.v1.AvroRows other =
        (com.google.cloud.bigquery.storage.v1.AvroRows) obj;

    if (!getSerializedBinaryRows().equals(other.getSerializedBinaryRows())) return false;
    if (getRowCount() != other.getRowCount()) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SERIALIZED_BINARY_ROWS_FIELD_NUMBER;
    hash = (53 * hash) + getSerializedBinaryRows().hashCode();
    hash = (37 * hash) + ROW_COUNT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getRowCount());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() {
    return newBuilder();
  }

  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }

  public static Builder newBuilder(com.google.cloud.bigquery.storage.v1.AvroRows prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }

  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   *
   *
   * <pre>
   * Avro rows.
   * </pre>
   *
   * Protobuf type {@code google.cloud.bigquery.storage.v1.AvroRows}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.bigquery.storage.v1.AvroRows)
      com.google.cloud.bigquery.storage.v1.AvroRowsOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.bigquery.storage.v1.AvroProto
          .internal_static_google_cloud_bigquery_storage_v1_AvroRows_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.bigquery.storage.v1.AvroProto
          .internal_static_google_cloud_bigquery_storage_v1_AvroRows_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.bigquery.storage.v1.AvroRows.class,
              com.google.cloud.bigquery.storage.v1.AvroRows.Builder.class);
    }

    // Construct using com.google.cloud.bigquery.storage.v1.AvroRows.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      serializedBinaryRows_ = com.google.protobuf.ByteString.EMPTY;
      rowCount_ = 0L;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.bigquery.storage.v1.AvroProto
          .internal_static_google_cloud_bigquery_storage_v1_AvroRows_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.AvroRows getDefaultInstanceForType() {
      return com.google.cloud.bigquery.storage.v1.AvroRows.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.AvroRows build() {
      com.google.cloud.bigquery.storage.v1.AvroRows result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.AvroRows buildPartial() {
      com.google.cloud.bigquery.storage.v1.AvroRows result =
          new com.google.cloud.bigquery.storage.v1.AvroRows(this);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.google.cloud.bigquery.storage.v1.AvroRows result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.serializedBinaryRows_ = serializedBinaryRows_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.rowCount_ = rowCount_;
      }
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }

    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.setField(field, value);
    }

    @java.lang.Override
    public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }

    @java.lang.Override
    public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }

    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }

    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.cloud.bigquery.storage.v1.AvroRows) {
        return mergeFrom((com.google.cloud.bigquery.storage.v1.AvroRows) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.bigquery.storage.v1.AvroRows other) {
      if (other == com.google.cloud.bigquery.storage.v1.AvroRows.getDefaultInstance()) return this;
      if (other.getSerializedBinaryRows() != com.google.protobuf.ByteString.EMPTY) {
        setSerializedBinaryRows(other.getSerializedBinaryRows());
      }
      if (other.getRowCount() != 0L) {
        setRowCount(other.getRowCount());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10:
              {
                serializedBinaryRows_ = input.readBytes();
                bitField0_ |= 0x00000001;
                break;
              } // case 10
            case 16:
              {
                rowCount_ = input.readInt64();
                bitField0_ |= 0x00000002;
                break;
              } // case 16
            default:
              {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }

    private int bitField0_;

    private com.google.protobuf.ByteString serializedBinaryRows_ =
        com.google.protobuf.ByteString.EMPTY;
    /**
     *
     *
     * <pre>
     * Binary serialized rows in a block.
     * </pre>
     *
     * <code>bytes serialized_binary_rows = 1;</code>
     *
     * @return The serializedBinaryRows.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getSerializedBinaryRows() {
      return serializedBinaryRows_;
    }
    /**
     *
     *
     * <pre>
     * Binary serialized rows in a block.
     * </pre>
     *
     * <code>bytes serialized_binary_rows = 1;</code>
     *
     * @param value The serializedBinaryRows to set.
     * @return This builder for chaining.
     */
    public Builder setSerializedBinaryRows(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      serializedBinaryRows_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Binary serialized rows in a block.
     * </pre>
     *
     * <code>bytes serialized_binary_rows = 1;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearSerializedBinaryRows() {
      bitField0_ = (bitField0_ & ~0x00000001);
      serializedBinaryRows_ = getDefaultInstance().getSerializedBinaryRows();
      onChanged();
      return this;
    }

    private long rowCount_;
    /**
     *
     *
     * <pre>
     * [Deprecated] The count of rows in the returning block.
     * Please use the format-independent ReadRowsResponse.row_count instead.
     * </pre>
     *
     * <code>int64 row_count = 2 [deprecated = true];</code>
     *
     * @deprecated google.cloud.bigquery.storage.v1.AvroRows.row_count is deprecated. See
     *     google/cloud/bigquery/storage/v1/avro.proto;l=39
     * @return The rowCount.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public long getRowCount() {
      return rowCount_;
    }
    /**
     *
     *
     * <pre>
     * [Deprecated] The count of rows in the returning block.
     * Please use the format-independent ReadRowsResponse.row_count instead.
     * </pre>
     *
     * <code>int64 row_count = 2 [deprecated = true];</code>
     *
     * @deprecated google.cloud.bigquery.storage.v1.AvroRows.row_count is deprecated. See
     *     google/cloud/bigquery/storage/v1/avro.proto;l=39
     * @param value The rowCount to set.
     * @return This builder for chaining.
     */
    @java.lang.Deprecated
    public Builder setRowCount(long value) {

      rowCount_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * [Deprecated] The count of rows in the returning block.
     * Please use the format-independent ReadRowsResponse.row_count instead.
     * </pre>
     *
     * <code>int64 row_count = 2 [deprecated = true];</code>
     *
     * @deprecated google.cloud.bigquery.storage.v1.AvroRows.row_count is deprecated. See
     *     google/cloud/bigquery/storage/v1/avro.proto;l=39
     * @return This builder for chaining.
     */
    @java.lang.Deprecated
    public Builder clearRowCount() {
      bitField0_ = (bitField0_ & ~0x00000002);
      rowCount_ = 0L;
      onChanged();
      return this;
    }

    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }

    // @@protoc_insertion_point(builder_scope:google.cloud.bigquery.storage.v1.AvroRows)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.bigquery.storage.v1.AvroRows)
  private static final com.google.cloud.bigquery.storage.v1.AvroRows DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.bigquery.storage.v1.AvroRows();
  }

  public static com.google.cloud.bigquery.storage.v1.AvroRows getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AvroRows> PARSER =
      new com.google.protobuf.AbstractParser<AvroRows>() {
        @java.lang.Override
        public AvroRows parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          Builder builder = newBuilder();
          try {
            builder.mergeFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(builder.buildPartial());
          } catch (com.google.protobuf.UninitializedMessageException e) {
            throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
          } catch (java.io.IOException e) {
            throw new com.google.protobuf.InvalidProtocolBufferException(e)
                .setUnfinishedMessage(builder.buildPartial());
          }
          return builder.buildPartial();
        }
      };

  public static com.google.protobuf.Parser<AvroRows> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<AvroRows> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1.AvroRows getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
