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
// source: google/cloud/bigquery/storage/v1/protobuf.proto

// Protobuf Java Version: 3.25.3
package com.google.cloud.bigquery.storage.v1;

/** Protobuf type {@code google.cloud.bigquery.storage.v1.ProtoRows} */
public final class ProtoRows extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.bigquery.storage.v1.ProtoRows)
    ProtoRowsOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ProtoRows.newBuilder() to construct.
  private ProtoRows(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private ProtoRows() {
    serializedRows_ = emptyList(com.google.protobuf.ByteString.class);
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ProtoRows();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.bigquery.storage.v1.ProtoBufProto
        .internal_static_google_cloud_bigquery_storage_v1_ProtoRows_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.bigquery.storage.v1.ProtoBufProto
        .internal_static_google_cloud_bigquery_storage_v1_ProtoRows_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.bigquery.storage.v1.ProtoRows.class,
            com.google.cloud.bigquery.storage.v1.ProtoRows.Builder.class);
  }

  public static final int SERIALIZED_ROWS_FIELD_NUMBER = 1;

  @SuppressWarnings("serial")
  private com.google.protobuf.Internal.ProtobufList<com.google.protobuf.ByteString>
      serializedRows_ = emptyList(com.google.protobuf.ByteString.class);
  /**
   *
   *
   * <pre>
   * A sequence of rows serialized as a Protocol Buffer.
   *
   * See https://developers.google.com/protocol-buffers/docs/overview for more
   * information on deserializing this field.
   * </pre>
   *
   * <code>repeated bytes serialized_rows = 1;</code>
   *
   * @return A list containing the serializedRows.
   */
  @java.lang.Override
  public java.util.List<com.google.protobuf.ByteString> getSerializedRowsList() {
    return serializedRows_;
  }
  /**
   *
   *
   * <pre>
   * A sequence of rows serialized as a Protocol Buffer.
   *
   * See https://developers.google.com/protocol-buffers/docs/overview for more
   * information on deserializing this field.
   * </pre>
   *
   * <code>repeated bytes serialized_rows = 1;</code>
   *
   * @return The count of serializedRows.
   */
  public int getSerializedRowsCount() {
    return serializedRows_.size();
  }
  /**
   *
   *
   * <pre>
   * A sequence of rows serialized as a Protocol Buffer.
   *
   * See https://developers.google.com/protocol-buffers/docs/overview for more
   * information on deserializing this field.
   * </pre>
   *
   * <code>repeated bytes serialized_rows = 1;</code>
   *
   * @param index The index of the element to return.
   * @return The serializedRows at the given index.
   */
  public com.google.protobuf.ByteString getSerializedRows(int index) {
    return serializedRows_.get(index);
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
    for (int i = 0; i < serializedRows_.size(); i++) {
      output.writeBytes(1, serializedRows_.get(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    {
      int dataSize = 0;
      for (int i = 0; i < serializedRows_.size(); i++) {
        dataSize +=
            com.google.protobuf.CodedOutputStream.computeBytesSizeNoTag(serializedRows_.get(i));
      }
      size += dataSize;
      size += 1 * getSerializedRowsList().size();
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
    if (!(obj instanceof com.google.cloud.bigquery.storage.v1.ProtoRows)) {
      return super.equals(obj);
    }
    com.google.cloud.bigquery.storage.v1.ProtoRows other =
        (com.google.cloud.bigquery.storage.v1.ProtoRows) obj;

    if (!getSerializedRowsList().equals(other.getSerializedRowsList())) return false;
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
    if (getSerializedRowsCount() > 0) {
      hash = (37 * hash) + SERIALIZED_ROWS_FIELD_NUMBER;
      hash = (53 * hash) + getSerializedRowsList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows parseFrom(
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

  public static Builder newBuilder(com.google.cloud.bigquery.storage.v1.ProtoRows prototype) {
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
  /** Protobuf type {@code google.cloud.bigquery.storage.v1.ProtoRows} */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.bigquery.storage.v1.ProtoRows)
      com.google.cloud.bigquery.storage.v1.ProtoRowsOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.bigquery.storage.v1.ProtoBufProto
          .internal_static_google_cloud_bigquery_storage_v1_ProtoRows_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.bigquery.storage.v1.ProtoBufProto
          .internal_static_google_cloud_bigquery_storage_v1_ProtoRows_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.bigquery.storage.v1.ProtoRows.class,
              com.google.cloud.bigquery.storage.v1.ProtoRows.Builder.class);
    }

    // Construct using com.google.cloud.bigquery.storage.v1.ProtoRows.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      serializedRows_ = emptyList(com.google.protobuf.ByteString.class);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.bigquery.storage.v1.ProtoBufProto
          .internal_static_google_cloud_bigquery_storage_v1_ProtoRows_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.ProtoRows getDefaultInstanceForType() {
      return com.google.cloud.bigquery.storage.v1.ProtoRows.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.ProtoRows build() {
      com.google.cloud.bigquery.storage.v1.ProtoRows result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.ProtoRows buildPartial() {
      com.google.cloud.bigquery.storage.v1.ProtoRows result =
          new com.google.cloud.bigquery.storage.v1.ProtoRows(this);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.google.cloud.bigquery.storage.v1.ProtoRows result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        serializedRows_.makeImmutable();
        result.serializedRows_ = serializedRows_;
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
      if (other instanceof com.google.cloud.bigquery.storage.v1.ProtoRows) {
        return mergeFrom((com.google.cloud.bigquery.storage.v1.ProtoRows) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.bigquery.storage.v1.ProtoRows other) {
      if (other == com.google.cloud.bigquery.storage.v1.ProtoRows.getDefaultInstance()) return this;
      if (!other.serializedRows_.isEmpty()) {
        if (serializedRows_.isEmpty()) {
          serializedRows_ = other.serializedRows_;
          serializedRows_.makeImmutable();
          bitField0_ |= 0x00000001;
        } else {
          ensureSerializedRowsIsMutable();
          serializedRows_.addAll(other.serializedRows_);
        }
        onChanged();
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
                com.google.protobuf.ByteString v = input.readBytes();
                ensureSerializedRowsIsMutable();
                serializedRows_.add(v);
                break;
              } // case 10
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

    private com.google.protobuf.Internal.ProtobufList<com.google.protobuf.ByteString>
        serializedRows_ = emptyList(com.google.protobuf.ByteString.class);

    private void ensureSerializedRowsIsMutable() {
      if (!serializedRows_.isModifiable()) {
        serializedRows_ = makeMutableCopy(serializedRows_);
      }
      bitField0_ |= 0x00000001;
    }
    /**
     *
     *
     * <pre>
     * A sequence of rows serialized as a Protocol Buffer.
     *
     * See https://developers.google.com/protocol-buffers/docs/overview for more
     * information on deserializing this field.
     * </pre>
     *
     * <code>repeated bytes serialized_rows = 1;</code>
     *
     * @return A list containing the serializedRows.
     */
    public java.util.List<com.google.protobuf.ByteString> getSerializedRowsList() {
      serializedRows_.makeImmutable();
      return serializedRows_;
    }
    /**
     *
     *
     * <pre>
     * A sequence of rows serialized as a Protocol Buffer.
     *
     * See https://developers.google.com/protocol-buffers/docs/overview for more
     * information on deserializing this field.
     * </pre>
     *
     * <code>repeated bytes serialized_rows = 1;</code>
     *
     * @return The count of serializedRows.
     */
    public int getSerializedRowsCount() {
      return serializedRows_.size();
    }
    /**
     *
     *
     * <pre>
     * A sequence of rows serialized as a Protocol Buffer.
     *
     * See https://developers.google.com/protocol-buffers/docs/overview for more
     * information on deserializing this field.
     * </pre>
     *
     * <code>repeated bytes serialized_rows = 1;</code>
     *
     * @param index The index of the element to return.
     * @return The serializedRows at the given index.
     */
    public com.google.protobuf.ByteString getSerializedRows(int index) {
      return serializedRows_.get(index);
    }
    /**
     *
     *
     * <pre>
     * A sequence of rows serialized as a Protocol Buffer.
     *
     * See https://developers.google.com/protocol-buffers/docs/overview for more
     * information on deserializing this field.
     * </pre>
     *
     * <code>repeated bytes serialized_rows = 1;</code>
     *
     * @param index The index to set the value at.
     * @param value The serializedRows to set.
     * @return This builder for chaining.
     */
    public Builder setSerializedRows(int index, com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      ensureSerializedRowsIsMutable();
      serializedRows_.set(index, value);
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A sequence of rows serialized as a Protocol Buffer.
     *
     * See https://developers.google.com/protocol-buffers/docs/overview for more
     * information on deserializing this field.
     * </pre>
     *
     * <code>repeated bytes serialized_rows = 1;</code>
     *
     * @param value The serializedRows to add.
     * @return This builder for chaining.
     */
    public Builder addSerializedRows(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      ensureSerializedRowsIsMutable();
      serializedRows_.add(value);
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A sequence of rows serialized as a Protocol Buffer.
     *
     * See https://developers.google.com/protocol-buffers/docs/overview for more
     * information on deserializing this field.
     * </pre>
     *
     * <code>repeated bytes serialized_rows = 1;</code>
     *
     * @param values The serializedRows to add.
     * @return This builder for chaining.
     */
    public Builder addAllSerializedRows(
        java.lang.Iterable<? extends com.google.protobuf.ByteString> values) {
      ensureSerializedRowsIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(values, serializedRows_);
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A sequence of rows serialized as a Protocol Buffer.
     *
     * See https://developers.google.com/protocol-buffers/docs/overview for more
     * information on deserializing this field.
     * </pre>
     *
     * <code>repeated bytes serialized_rows = 1;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearSerializedRows() {
      serializedRows_ = emptyList(com.google.protobuf.ByteString.class);
      bitField0_ = (bitField0_ & ~0x00000001);
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

    // @@protoc_insertion_point(builder_scope:google.cloud.bigquery.storage.v1.ProtoRows)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.bigquery.storage.v1.ProtoRows)
  private static final com.google.cloud.bigquery.storage.v1.ProtoRows DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.bigquery.storage.v1.ProtoRows();
  }

  public static com.google.cloud.bigquery.storage.v1.ProtoRows getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ProtoRows> PARSER =
      new com.google.protobuf.AbstractParser<ProtoRows>() {
        @java.lang.Override
        public ProtoRows parsePartialFrom(
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

  public static com.google.protobuf.Parser<ProtoRows> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ProtoRows> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1.ProtoRows getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
