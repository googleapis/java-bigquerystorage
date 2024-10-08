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

// Protobuf Java Version: 3.25.5
package com.google.cloud.bigquery.storage.v1;

/**
 *
 *
 * <pre>
 * Contains options specific to Avro Serialization.
 * </pre>
 *
 * Protobuf type {@code google.cloud.bigquery.storage.v1.AvroSerializationOptions}
 */
public final class AvroSerializationOptions extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.bigquery.storage.v1.AvroSerializationOptions)
    AvroSerializationOptionsOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use AvroSerializationOptions.newBuilder() to construct.
  private AvroSerializationOptions(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private AvroSerializationOptions() {}

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new AvroSerializationOptions();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.bigquery.storage.v1.AvroProto
        .internal_static_google_cloud_bigquery_storage_v1_AvroSerializationOptions_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.bigquery.storage.v1.AvroProto
        .internal_static_google_cloud_bigquery_storage_v1_AvroSerializationOptions_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.bigquery.storage.v1.AvroSerializationOptions.class,
            com.google.cloud.bigquery.storage.v1.AvroSerializationOptions.Builder.class);
  }

  public static final int ENABLE_DISPLAY_NAME_ATTRIBUTE_FIELD_NUMBER = 1;
  private boolean enableDisplayNameAttribute_ = false;
  /**
   *
   *
   * <pre>
   * Enable displayName attribute in Avro schema.
   *
   * The Avro specification requires field names to be alphanumeric.  By
   * default, in cases when column names do not conform to these requirements
   * (e.g. non-ascii unicode codepoints) and Avro is requested as an output
   * format, the CreateReadSession call will fail.
   *
   * Setting this field to true, populates avro field names with a placeholder
   * value and populates a "displayName" attribute for every avro field with the
   * original column name.
   * </pre>
   *
   * <code>bool enable_display_name_attribute = 1;</code>
   *
   * @return The enableDisplayNameAttribute.
   */
  @java.lang.Override
  public boolean getEnableDisplayNameAttribute() {
    return enableDisplayNameAttribute_;
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
    if (enableDisplayNameAttribute_ != false) {
      output.writeBool(1, enableDisplayNameAttribute_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (enableDisplayNameAttribute_ != false) {
      size += com.google.protobuf.CodedOutputStream.computeBoolSize(1, enableDisplayNameAttribute_);
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
    if (!(obj instanceof com.google.cloud.bigquery.storage.v1.AvroSerializationOptions)) {
      return super.equals(obj);
    }
    com.google.cloud.bigquery.storage.v1.AvroSerializationOptions other =
        (com.google.cloud.bigquery.storage.v1.AvroSerializationOptions) obj;

    if (getEnableDisplayNameAttribute() != other.getEnableDisplayNameAttribute()) return false;
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
    hash = (37 * hash) + ENABLE_DISPLAY_NAME_ATTRIBUTE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(getEnableDisplayNameAttribute());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions parseFrom(
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
      com.google.cloud.bigquery.storage.v1.AvroSerializationOptions prototype) {
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
   * Contains options specific to Avro Serialization.
   * </pre>
   *
   * Protobuf type {@code google.cloud.bigquery.storage.v1.AvroSerializationOptions}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.bigquery.storage.v1.AvroSerializationOptions)
      com.google.cloud.bigquery.storage.v1.AvroSerializationOptionsOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.bigquery.storage.v1.AvroProto
          .internal_static_google_cloud_bigquery_storage_v1_AvroSerializationOptions_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.bigquery.storage.v1.AvroProto
          .internal_static_google_cloud_bigquery_storage_v1_AvroSerializationOptions_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.bigquery.storage.v1.AvroSerializationOptions.class,
              com.google.cloud.bigquery.storage.v1.AvroSerializationOptions.Builder.class);
    }

    // Construct using com.google.cloud.bigquery.storage.v1.AvroSerializationOptions.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      enableDisplayNameAttribute_ = false;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.bigquery.storage.v1.AvroProto
          .internal_static_google_cloud_bigquery_storage_v1_AvroSerializationOptions_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.AvroSerializationOptions
        getDefaultInstanceForType() {
      return com.google.cloud.bigquery.storage.v1.AvroSerializationOptions.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.AvroSerializationOptions build() {
      com.google.cloud.bigquery.storage.v1.AvroSerializationOptions result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1.AvroSerializationOptions buildPartial() {
      com.google.cloud.bigquery.storage.v1.AvroSerializationOptions result =
          new com.google.cloud.bigquery.storage.v1.AvroSerializationOptions(this);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartial0(
        com.google.cloud.bigquery.storage.v1.AvroSerializationOptions result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.enableDisplayNameAttribute_ = enableDisplayNameAttribute_;
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
      if (other instanceof com.google.cloud.bigquery.storage.v1.AvroSerializationOptions) {
        return mergeFrom((com.google.cloud.bigquery.storage.v1.AvroSerializationOptions) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.bigquery.storage.v1.AvroSerializationOptions other) {
      if (other
          == com.google.cloud.bigquery.storage.v1.AvroSerializationOptions.getDefaultInstance())
        return this;
      if (other.getEnableDisplayNameAttribute() != false) {
        setEnableDisplayNameAttribute(other.getEnableDisplayNameAttribute());
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
            case 8:
              {
                enableDisplayNameAttribute_ = input.readBool();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
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

    private boolean enableDisplayNameAttribute_;
    /**
     *
     *
     * <pre>
     * Enable displayName attribute in Avro schema.
     *
     * The Avro specification requires field names to be alphanumeric.  By
     * default, in cases when column names do not conform to these requirements
     * (e.g. non-ascii unicode codepoints) and Avro is requested as an output
     * format, the CreateReadSession call will fail.
     *
     * Setting this field to true, populates avro field names with a placeholder
     * value and populates a "displayName" attribute for every avro field with the
     * original column name.
     * </pre>
     *
     * <code>bool enable_display_name_attribute = 1;</code>
     *
     * @return The enableDisplayNameAttribute.
     */
    @java.lang.Override
    public boolean getEnableDisplayNameAttribute() {
      return enableDisplayNameAttribute_;
    }
    /**
     *
     *
     * <pre>
     * Enable displayName attribute in Avro schema.
     *
     * The Avro specification requires field names to be alphanumeric.  By
     * default, in cases when column names do not conform to these requirements
     * (e.g. non-ascii unicode codepoints) and Avro is requested as an output
     * format, the CreateReadSession call will fail.
     *
     * Setting this field to true, populates avro field names with a placeholder
     * value and populates a "displayName" attribute for every avro field with the
     * original column name.
     * </pre>
     *
     * <code>bool enable_display_name_attribute = 1;</code>
     *
     * @param value The enableDisplayNameAttribute to set.
     * @return This builder for chaining.
     */
    public Builder setEnableDisplayNameAttribute(boolean value) {

      enableDisplayNameAttribute_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Enable displayName attribute in Avro schema.
     *
     * The Avro specification requires field names to be alphanumeric.  By
     * default, in cases when column names do not conform to these requirements
     * (e.g. non-ascii unicode codepoints) and Avro is requested as an output
     * format, the CreateReadSession call will fail.
     *
     * Setting this field to true, populates avro field names with a placeholder
     * value and populates a "displayName" attribute for every avro field with the
     * original column name.
     * </pre>
     *
     * <code>bool enable_display_name_attribute = 1;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearEnableDisplayNameAttribute() {
      bitField0_ = (bitField0_ & ~0x00000001);
      enableDisplayNameAttribute_ = false;
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

    // @@protoc_insertion_point(builder_scope:google.cloud.bigquery.storage.v1.AvroSerializationOptions)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.bigquery.storage.v1.AvroSerializationOptions)
  private static final com.google.cloud.bigquery.storage.v1.AvroSerializationOptions
      DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.bigquery.storage.v1.AvroSerializationOptions();
  }

  public static com.google.cloud.bigquery.storage.v1.AvroSerializationOptions getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AvroSerializationOptions> PARSER =
      new com.google.protobuf.AbstractParser<AvroSerializationOptions>() {
        @java.lang.Override
        public AvroSerializationOptions parsePartialFrom(
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

  public static com.google.protobuf.Parser<AvroSerializationOptions> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<AvroSerializationOptions> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1.AvroSerializationOptions getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
