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
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/bigquery/storage/v1/arrow.proto

package com.google.cloud.bigquery.storage.v1;

/**
 *
 *
 * <pre>
 * Arrow RecordBatch.
 * </pre>
 *
 * Protobuf type {@code google.cloud.bigquery.storage.v1.ArrowRecordBatch}
 */
public final class ArrowRecordBatch extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.bigquery.storage.v1.ArrowRecordBatch)
    ArrowRecordBatchOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ArrowRecordBatch.newBuilder() to construct.
  private ArrowRecordBatch(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private ArrowRecordBatch() {
    serializedRecordBatch_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ArrowRecordBatch();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  private ArrowRecordBatch(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
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
              serializedRecordBatch_ = input.readBytes();
              break;
            }
          case 16:
            {
              rowCount_ = input.readInt64();
              break;
            }
          default:
            {
              if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.bigquery.storage.v1.ArrowProto
        .internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.bigquery.storage.v1.ArrowProto
        .internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.bigquery.storage.v1.ArrowRecordBatch.class,
            com.google.cloud.bigquery.storage.v1.ArrowRecordBatch.Builder.class);
  }

  public static final int SERIALIZED_RECORD_BATCH_FIELD_NUMBER = 1;
  private com.google.protobuf.ByteString serializedRecordBatch_;
  /**
   *
   *
   * <pre>
   * IPC-serialized Arrow RecordBatch.
   * </pre>
   *
   * <code>bytes serialized_record_batch = 1;</code>
   *
   * @return The serializedRecordBatch.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getSerializedRecordBatch() {
    return serializedRecordBatch_;
  }

  public static final int ROW_COUNT_FIELD_NUMBER = 2;
  private long rowCount_;
  /**
   *
   *
   * <pre>
   * [Deprecated] The count of rows in `serialized_record_batch`.
   * Please use the format-independent ReadRowsResponse.row_count instead.
   * </pre>
   *
   * <code>int64 row_count = 2 [deprecated = true];</code>
   *
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
    if (!serializedRecordBatch_.isEmpty()) {
      output.writeBytes(1, serializedRecordBatch_);
    }
    if (rowCount_ != 0L) {
      output.writeInt64(2, rowCount_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!serializedRecordBatch_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream.computeBytesSize(1, serializedRecordBatch_);
    }
    if (rowCount_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(2, rowCount_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof com.google.cloud.bigquery.storage.v1.ArrowRecordBatch)) {
      return super.equals(obj);
    }
    com.google.cloud.bigquery.storage.v1.ArrowRecordBatch other =
        (com.google.cloud.bigquery.storage.v1.ArrowRecordBatch) obj;

    if (!getSerializedRecordBatch().equals(other.getSerializedRecordBatch())) return false;
    if (getRowCount() != other.getRowCount()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SERIALIZED_RECORD_BATCH_FIELD_NUMBER;
    hash = (53 * hash) + getSerializedRecordBatch().hashCode();
    hash = (37 * hash) + ROW_COUNT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getRowCount());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parseFrom(
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

  public static Builder newBuilder(
      com.google.cloud.bigquery.storage.v1.ArrowRecordBatch prototype) {
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
   * Arrow RecordBatch.
   * </pre>
   *
   * Protobuf type {@code google.cloud.bigquery.storage.v1.ArrowRecordBatch}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.bigquery.storage.v1.ArrowRecordBatch)
      com.google.cloud.bigquery.storage.v1.ArrowRecordBatchOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.bigquery.storage.v1.ArrowProto
          .internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.bigquery.storage.v1.ArrowProto
          .internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.bigquery.storage.v1.ArrowRecordBatch.class,
              com.google.cloud.bigquery.storage.v1.ArrowRecordBatch.Builder.class);
    }

    // Construct using com.google.cloud.bigquery.storage.v1.ArrowRecordBatch.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {}
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      serializedRecordBatch_ = com.google.protobuf.ByteString.EMPTY;

      rowCount_ = 0L;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.bigquery.storage.v1.ArrowProto
          .internal_static_google_cloud_bigquery_storage_v1_ArrowRecordBatch_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.ArrowRecordBatch getDefaultInstanceForType() {
      return com.google.cloud.bigquery.storage.v1.ArrowRecordBatch.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.ArrowRecordBatch build() {
      com.google.cloud.bigquery.storage.v1.ArrowRecordBatch result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.ArrowRecordBatch buildPartial() {
      com.google.cloud.bigquery.storage.v1.ArrowRecordBatch result =
          new com.google.cloud.bigquery.storage.v1.ArrowRecordBatch(this);
      result.serializedRecordBatch_ = serializedRecordBatch_;
      result.rowCount_ = rowCount_;
      onBuilt();
      return result;
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
      if (other instanceof com.google.cloud.bigquery.storage.v1.ArrowRecordBatch) {
        return mergeFrom((com.google.cloud.bigquery.storage.v1.ArrowRecordBatch) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.bigquery.storage.v1.ArrowRecordBatch other) {
      if (other == com.google.cloud.bigquery.storage.v1.ArrowRecordBatch.getDefaultInstance())
        return this;
      if (other.getSerializedRecordBatch() != com.google.protobuf.ByteString.EMPTY) {
        setSerializedRecordBatch(other.getSerializedRecordBatch());
      }
      if (other.getRowCount() != 0L) {
        setRowCount(other.getRowCount());
      }
      this.mergeUnknownFields(other.unknownFields);
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
      com.google.cloud.bigquery.storage.v1.ArrowRecordBatch parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage =
            (com.google.cloud.bigquery.storage.v1.ArrowRecordBatch) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.google.protobuf.ByteString serializedRecordBatch_ =
        com.google.protobuf.ByteString.EMPTY;
    /**
     *
     *
     * <pre>
     * IPC-serialized Arrow RecordBatch.
     * </pre>
     *
     * <code>bytes serialized_record_batch = 1;</code>
     *
     * @return The serializedRecordBatch.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getSerializedRecordBatch() {
      return serializedRecordBatch_;
    }
    /**
     *
     *
     * <pre>
     * IPC-serialized Arrow RecordBatch.
     * </pre>
     *
     * <code>bytes serialized_record_batch = 1;</code>
     *
     * @param value The serializedRecordBatch to set.
     * @return This builder for chaining.
     */
    public Builder setSerializedRecordBatch(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }

      serializedRecordBatch_ = value;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * IPC-serialized Arrow RecordBatch.
     * </pre>
     *
     * <code>bytes serialized_record_batch = 1;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearSerializedRecordBatch() {

      serializedRecordBatch_ = getDefaultInstance().getSerializedRecordBatch();
      onChanged();
      return this;
    }

    private long rowCount_;
    /**
     *
     *
     * <pre>
     * [Deprecated] The count of rows in `serialized_record_batch`.
     * Please use the format-independent ReadRowsResponse.row_count instead.
     * </pre>
     *
     * <code>int64 row_count = 2 [deprecated = true];</code>
     *
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
     * [Deprecated] The count of rows in `serialized_record_batch`.
     * Please use the format-independent ReadRowsResponse.row_count instead.
     * </pre>
     *
     * <code>int64 row_count = 2 [deprecated = true];</code>
     *
     * @param value The rowCount to set.
     * @return This builder for chaining.
     */
    @java.lang.Deprecated
    public Builder setRowCount(long value) {

      rowCount_ = value;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * [Deprecated] The count of rows in `serialized_record_batch`.
     * Please use the format-independent ReadRowsResponse.row_count instead.
     * </pre>
     *
     * <code>int64 row_count = 2 [deprecated = true];</code>
     *
     * @return This builder for chaining.
     */
    @java.lang.Deprecated
    public Builder clearRowCount() {

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

    // @@protoc_insertion_point(builder_scope:google.cloud.bigquery.storage.v1.ArrowRecordBatch)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.bigquery.storage.v1.ArrowRecordBatch)
  private static final com.google.cloud.bigquery.storage.v1.ArrowRecordBatch DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.bigquery.storage.v1.ArrowRecordBatch();
  }

  public static com.google.cloud.bigquery.storage.v1.ArrowRecordBatch getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ArrowRecordBatch> PARSER =
      new com.google.protobuf.AbstractParser<ArrowRecordBatch>() {
        @java.lang.Override
        public ArrowRecordBatch parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new ArrowRecordBatch(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<ArrowRecordBatch> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ArrowRecordBatch> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1.ArrowRecordBatch getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
