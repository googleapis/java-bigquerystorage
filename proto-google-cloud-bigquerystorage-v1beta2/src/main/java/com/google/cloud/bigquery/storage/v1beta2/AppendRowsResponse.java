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
// source: google/cloud/bigquery/storage/v1beta2/storage.proto

package com.google.cloud.bigquery.storage.v1beta2;

/**
 *
 *
 * <pre>
 * Response message for `AppendRows`.
 * </pre>
 *
 * Protobuf type {@code google.cloud.bigquery.storage.v1beta2.AppendRowsResponse}
 */
public final class AppendRowsResponse extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.bigquery.storage.v1beta2.AppendRowsResponse)
    AppendRowsResponseOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use AppendRowsResponse.newBuilder() to construct.
  private AppendRowsResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private AppendRowsResponse() {}

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new AppendRowsResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  private AppendRowsResponse(
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
          case 8:
            {
              responseCase_ = 1;
              response_ = input.readInt64();
              break;
            }
          case 18:
            {
              com.google.rpc.Status.Builder subBuilder = null;
              if (responseCase_ == 2) {
                subBuilder = ((com.google.rpc.Status) response_).toBuilder();
              }
              response_ = input.readMessage(com.google.rpc.Status.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom((com.google.rpc.Status) response_);
                response_ = subBuilder.buildPartial();
              }
              responseCase_ = 2;
              break;
            }
          case 26:
            {
              com.google.cloud.bigquery.storage.v1beta2.TableSchema.Builder subBuilder = null;
              if (updatedSchema_ != null) {
                subBuilder = updatedSchema_.toBuilder();
              }
              updatedSchema_ =
                  input.readMessage(
                      com.google.cloud.bigquery.storage.v1beta2.TableSchema.parser(),
                      extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(updatedSchema_);
                updatedSchema_ = subBuilder.buildPartial();
              }

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
    return com.google.cloud.bigquery.storage.v1beta2.StorageProto
        .internal_static_google_cloud_bigquery_storage_v1beta2_AppendRowsResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.bigquery.storage.v1beta2.StorageProto
        .internal_static_google_cloud_bigquery_storage_v1beta2_AppendRowsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.class,
            com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.Builder.class);
  }

  private int responseCase_ = 0;
  private java.lang.Object response_;

  public enum ResponseCase
      implements
          com.google.protobuf.Internal.EnumLite,
          com.google.protobuf.AbstractMessage.InternalOneOfEnum {
    OFFSET(1),
    ERROR(2),
    RESPONSE_NOT_SET(0);
    private final int value;

    private ResponseCase(int value) {
      this.value = value;
    }
    /**
     * @param value The number of the enum to look for.
     * @return The enum associated with the given number.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static ResponseCase valueOf(int value) {
      return forNumber(value);
    }

    public static ResponseCase forNumber(int value) {
      switch (value) {
        case 1:
          return OFFSET;
        case 2:
          return ERROR;
        case 0:
          return RESPONSE_NOT_SET;
        default:
          return null;
      }
    }

    public int getNumber() {
      return this.value;
    }
  };

  public ResponseCase getResponseCase() {
    return ResponseCase.forNumber(responseCase_);
  }

  public static final int OFFSET_FIELD_NUMBER = 1;
  /**
   *
   *
   * <pre>
   * The row offset at which the last append occurred.
   * </pre>
   *
   * <code>int64 offset = 1;</code>
   *
   * @return The offset.
   */
  @java.lang.Override
  public long getOffset() {
    if (responseCase_ == 1) {
      return (java.lang.Long) response_;
    }
    return 0L;
  }

  public static final int ERROR_FIELD_NUMBER = 2;
  /**
   *
   *
   * <pre>
   * Error in case of append failure. If set, it means rows are not accepted
   * into the system. Users can retry within the same connection.
   * </pre>
   *
   * <code>.google.rpc.Status error = 2;</code>
   *
   * @return Whether the error field is set.
   */
  @java.lang.Override
  public boolean hasError() {
    return responseCase_ == 2;
  }
  /**
   *
   *
   * <pre>
   * Error in case of append failure. If set, it means rows are not accepted
   * into the system. Users can retry within the same connection.
   * </pre>
   *
   * <code>.google.rpc.Status error = 2;</code>
   *
   * @return The error.
   */
  @java.lang.Override
  public com.google.rpc.Status getError() {
    if (responseCase_ == 2) {
      return (com.google.rpc.Status) response_;
    }
    return com.google.rpc.Status.getDefaultInstance();
  }
  /**
   *
   *
   * <pre>
   * Error in case of append failure. If set, it means rows are not accepted
   * into the system. Users can retry within the same connection.
   * </pre>
   *
   * <code>.google.rpc.Status error = 2;</code>
   */
  @java.lang.Override
  public com.google.rpc.StatusOrBuilder getErrorOrBuilder() {
    if (responseCase_ == 2) {
      return (com.google.rpc.Status) response_;
    }
    return com.google.rpc.Status.getDefaultInstance();
  }

  public static final int UPDATED_SCHEMA_FIELD_NUMBER = 3;
  private com.google.cloud.bigquery.storage.v1beta2.TableSchema updatedSchema_;
  /**
   *
   *
   * <pre>
   * If backend detects a schema update, pass it to user so that user can
   * use it to input new type of message. It will be empty when there is no
   * schema updates.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
   *
   * @return Whether the updatedSchema field is set.
   */
  @java.lang.Override
  public boolean hasUpdatedSchema() {
    return updatedSchema_ != null;
  }
  /**
   *
   *
   * <pre>
   * If backend detects a schema update, pass it to user so that user can
   * use it to input new type of message. It will be empty when there is no
   * schema updates.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
   *
   * @return The updatedSchema.
   */
  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1beta2.TableSchema getUpdatedSchema() {
    return updatedSchema_ == null
        ? com.google.cloud.bigquery.storage.v1beta2.TableSchema.getDefaultInstance()
        : updatedSchema_;
  }
  /**
   *
   *
   * <pre>
   * If backend detects a schema update, pass it to user so that user can
   * use it to input new type of message. It will be empty when there is no
   * schema updates.
   * </pre>
   *
   * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
   */
  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1beta2.TableSchemaOrBuilder
      getUpdatedSchemaOrBuilder() {
    return getUpdatedSchema();
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
    if (responseCase_ == 1) {
      output.writeInt64(1, (long) ((java.lang.Long) response_));
    }
    if (responseCase_ == 2) {
      output.writeMessage(2, (com.google.rpc.Status) response_);
    }
    if (updatedSchema_ != null) {
      output.writeMessage(3, getUpdatedSchema());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (responseCase_ == 1) {
      size +=
          com.google.protobuf.CodedOutputStream.computeInt64Size(
              1, (long) ((java.lang.Long) response_));
    }
    if (responseCase_ == 2) {
      size +=
          com.google.protobuf.CodedOutputStream.computeMessageSize(
              2, (com.google.rpc.Status) response_);
    }
    if (updatedSchema_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(3, getUpdatedSchema());
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
    if (!(obj instanceof com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse)) {
      return super.equals(obj);
    }
    com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse other =
        (com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse) obj;

    if (hasUpdatedSchema() != other.hasUpdatedSchema()) return false;
    if (hasUpdatedSchema()) {
      if (!getUpdatedSchema().equals(other.getUpdatedSchema())) return false;
    }
    if (!getResponseCase().equals(other.getResponseCase())) return false;
    switch (responseCase_) {
      case 1:
        if (getOffset() != other.getOffset()) return false;
        break;
      case 2:
        if (!getError().equals(other.getError())) return false;
        break;
      case 0:
      default:
    }
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
    if (hasUpdatedSchema()) {
      hash = (37 * hash) + UPDATED_SCHEMA_FIELD_NUMBER;
      hash = (53 * hash) + getUpdatedSchema().hashCode();
    }
    switch (responseCase_) {
      case 1:
        hash = (37 * hash) + OFFSET_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getOffset());
        break;
      case 2:
        hash = (37 * hash) + ERROR_FIELD_NUMBER;
        hash = (53 * hash) + getError().hashCode();
        break;
      case 0:
      default:
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parseFrom(
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
      com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse prototype) {
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
   * Response message for `AppendRows`.
   * </pre>
   *
   * Protobuf type {@code google.cloud.bigquery.storage.v1beta2.AppendRowsResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.bigquery.storage.v1beta2.AppendRowsResponse)
      com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.bigquery.storage.v1beta2.StorageProto
          .internal_static_google_cloud_bigquery_storage_v1beta2_AppendRowsResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.bigquery.storage.v1beta2.StorageProto
          .internal_static_google_cloud_bigquery_storage_v1beta2_AppendRowsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.class,
              com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.Builder.class);
    }

    // Construct using com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.newBuilder()
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
      if (updatedSchemaBuilder_ == null) {
        updatedSchema_ = null;
      } else {
        updatedSchema_ = null;
        updatedSchemaBuilder_ = null;
      }
      responseCase_ = 0;
      response_ = null;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.bigquery.storage.v1beta2.StorageProto
          .internal_static_google_cloud_bigquery_storage_v1beta2_AppendRowsResponse_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse
        getDefaultInstanceForType() {
      return com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse build() {
      com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse buildPartial() {
      com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse result =
          new com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse(this);
      if (responseCase_ == 1) {
        result.response_ = response_;
      }
      if (responseCase_ == 2) {
        if (errorBuilder_ == null) {
          result.response_ = response_;
        } else {
          result.response_ = errorBuilder_.build();
        }
      }
      if (updatedSchemaBuilder_ == null) {
        result.updatedSchema_ = updatedSchema_;
      } else {
        result.updatedSchema_ = updatedSchemaBuilder_.build();
      }
      result.responseCase_ = responseCase_;
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
      if (other instanceof com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse) {
        return mergeFrom((com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse other) {
      if (other
          == com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse.getDefaultInstance())
        return this;
      if (other.hasUpdatedSchema()) {
        mergeUpdatedSchema(other.getUpdatedSchema());
      }
      switch (other.getResponseCase()) {
        case OFFSET:
          {
            setOffset(other.getOffset());
            break;
          }
        case ERROR:
          {
            mergeError(other.getError());
            break;
          }
        case RESPONSE_NOT_SET:
          {
            break;
          }
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
      com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage =
            (com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int responseCase_ = 0;
    private java.lang.Object response_;

    public ResponseCase getResponseCase() {
      return ResponseCase.forNumber(responseCase_);
    }

    public Builder clearResponse() {
      responseCase_ = 0;
      response_ = null;
      onChanged();
      return this;
    }

    /**
     *
     *
     * <pre>
     * The row offset at which the last append occurred.
     * </pre>
     *
     * <code>int64 offset = 1;</code>
     *
     * @return The offset.
     */
    public long getOffset() {
      if (responseCase_ == 1) {
        return (java.lang.Long) response_;
      }
      return 0L;
    }
    /**
     *
     *
     * <pre>
     * The row offset at which the last append occurred.
     * </pre>
     *
     * <code>int64 offset = 1;</code>
     *
     * @param value The offset to set.
     * @return This builder for chaining.
     */
    public Builder setOffset(long value) {
      responseCase_ = 1;
      response_ = value;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The row offset at which the last append occurred.
     * </pre>
     *
     * <code>int64 offset = 1;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearOffset() {
      if (responseCase_ == 1) {
        responseCase_ = 0;
        response_ = null;
        onChanged();
      }
      return this;
    }

    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.rpc.Status, com.google.rpc.Status.Builder, com.google.rpc.StatusOrBuilder>
        errorBuilder_;
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     *
     * @return Whether the error field is set.
     */
    @java.lang.Override
    public boolean hasError() {
      return responseCase_ == 2;
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     *
     * @return The error.
     */
    @java.lang.Override
    public com.google.rpc.Status getError() {
      if (errorBuilder_ == null) {
        if (responseCase_ == 2) {
          return (com.google.rpc.Status) response_;
        }
        return com.google.rpc.Status.getDefaultInstance();
      } else {
        if (responseCase_ == 2) {
          return errorBuilder_.getMessage();
        }
        return com.google.rpc.Status.getDefaultInstance();
      }
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     */
    public Builder setError(com.google.rpc.Status value) {
      if (errorBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        response_ = value;
        onChanged();
      } else {
        errorBuilder_.setMessage(value);
      }
      responseCase_ = 2;
      return this;
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     */
    public Builder setError(com.google.rpc.Status.Builder builderForValue) {
      if (errorBuilder_ == null) {
        response_ = builderForValue.build();
        onChanged();
      } else {
        errorBuilder_.setMessage(builderForValue.build());
      }
      responseCase_ = 2;
      return this;
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     */
    public Builder mergeError(com.google.rpc.Status value) {
      if (errorBuilder_ == null) {
        if (responseCase_ == 2 && response_ != com.google.rpc.Status.getDefaultInstance()) {
          response_ =
              com.google.rpc.Status.newBuilder((com.google.rpc.Status) response_)
                  .mergeFrom(value)
                  .buildPartial();
        } else {
          response_ = value;
        }
        onChanged();
      } else {
        if (responseCase_ == 2) {
          errorBuilder_.mergeFrom(value);
        }
        errorBuilder_.setMessage(value);
      }
      responseCase_ = 2;
      return this;
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     */
    public Builder clearError() {
      if (errorBuilder_ == null) {
        if (responseCase_ == 2) {
          responseCase_ = 0;
          response_ = null;
          onChanged();
        }
      } else {
        if (responseCase_ == 2) {
          responseCase_ = 0;
          response_ = null;
        }
        errorBuilder_.clear();
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     */
    public com.google.rpc.Status.Builder getErrorBuilder() {
      return getErrorFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     */
    @java.lang.Override
    public com.google.rpc.StatusOrBuilder getErrorOrBuilder() {
      if ((responseCase_ == 2) && (errorBuilder_ != null)) {
        return errorBuilder_.getMessageOrBuilder();
      } else {
        if (responseCase_ == 2) {
          return (com.google.rpc.Status) response_;
        }
        return com.google.rpc.Status.getDefaultInstance();
      }
    }
    /**
     *
     *
     * <pre>
     * Error in case of append failure. If set, it means rows are not accepted
     * into the system. Users can retry within the same connection.
     * </pre>
     *
     * <code>.google.rpc.Status error = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.rpc.Status, com.google.rpc.Status.Builder, com.google.rpc.StatusOrBuilder>
        getErrorFieldBuilder() {
      if (errorBuilder_ == null) {
        if (!(responseCase_ == 2)) {
          response_ = com.google.rpc.Status.getDefaultInstance();
        }
        errorBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.rpc.Status,
                com.google.rpc.Status.Builder,
                com.google.rpc.StatusOrBuilder>(
                (com.google.rpc.Status) response_, getParentForChildren(), isClean());
        response_ = null;
      }
      responseCase_ = 2;
      onChanged();
      ;
      return errorBuilder_;
    }

    private com.google.cloud.bigquery.storage.v1beta2.TableSchema updatedSchema_;
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.cloud.bigquery.storage.v1beta2.TableSchema,
            com.google.cloud.bigquery.storage.v1beta2.TableSchema.Builder,
            com.google.cloud.bigquery.storage.v1beta2.TableSchemaOrBuilder>
        updatedSchemaBuilder_;
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     *
     * @return Whether the updatedSchema field is set.
     */
    public boolean hasUpdatedSchema() {
      return updatedSchemaBuilder_ != null || updatedSchema_ != null;
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     *
     * @return The updatedSchema.
     */
    public com.google.cloud.bigquery.storage.v1beta2.TableSchema getUpdatedSchema() {
      if (updatedSchemaBuilder_ == null) {
        return updatedSchema_ == null
            ? com.google.cloud.bigquery.storage.v1beta2.TableSchema.getDefaultInstance()
            : updatedSchema_;
      } else {
        return updatedSchemaBuilder_.getMessage();
      }
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     */
    public Builder setUpdatedSchema(com.google.cloud.bigquery.storage.v1beta2.TableSchema value) {
      if (updatedSchemaBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        updatedSchema_ = value;
        onChanged();
      } else {
        updatedSchemaBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     */
    public Builder setUpdatedSchema(
        com.google.cloud.bigquery.storage.v1beta2.TableSchema.Builder builderForValue) {
      if (updatedSchemaBuilder_ == null) {
        updatedSchema_ = builderForValue.build();
        onChanged();
      } else {
        updatedSchemaBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     */
    public Builder mergeUpdatedSchema(com.google.cloud.bigquery.storage.v1beta2.TableSchema value) {
      if (updatedSchemaBuilder_ == null) {
        if (updatedSchema_ != null) {
          updatedSchema_ =
              com.google.cloud.bigquery.storage.v1beta2.TableSchema.newBuilder(updatedSchema_)
                  .mergeFrom(value)
                  .buildPartial();
        } else {
          updatedSchema_ = value;
        }
        onChanged();
      } else {
        updatedSchemaBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     */
    public Builder clearUpdatedSchema() {
      if (updatedSchemaBuilder_ == null) {
        updatedSchema_ = null;
        onChanged();
      } else {
        updatedSchema_ = null;
        updatedSchemaBuilder_ = null;
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     */
    public com.google.cloud.bigquery.storage.v1beta2.TableSchema.Builder getUpdatedSchemaBuilder() {

      onChanged();
      return getUpdatedSchemaFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     */
    public com.google.cloud.bigquery.storage.v1beta2.TableSchemaOrBuilder
        getUpdatedSchemaOrBuilder() {
      if (updatedSchemaBuilder_ != null) {
        return updatedSchemaBuilder_.getMessageOrBuilder();
      } else {
        return updatedSchema_ == null
            ? com.google.cloud.bigquery.storage.v1beta2.TableSchema.getDefaultInstance()
            : updatedSchema_;
      }
    }
    /**
     *
     *
     * <pre>
     * If backend detects a schema update, pass it to user so that user can
     * use it to input new type of message. It will be empty when there is no
     * schema updates.
     * </pre>
     *
     * <code>.google.cloud.bigquery.storage.v1beta2.TableSchema updated_schema = 3;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.cloud.bigquery.storage.v1beta2.TableSchema,
            com.google.cloud.bigquery.storage.v1beta2.TableSchema.Builder,
            com.google.cloud.bigquery.storage.v1beta2.TableSchemaOrBuilder>
        getUpdatedSchemaFieldBuilder() {
      if (updatedSchemaBuilder_ == null) {
        updatedSchemaBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.cloud.bigquery.storage.v1beta2.TableSchema,
                com.google.cloud.bigquery.storage.v1beta2.TableSchema.Builder,
                com.google.cloud.bigquery.storage.v1beta2.TableSchemaOrBuilder>(
                getUpdatedSchema(), getParentForChildren(), isClean());
        updatedSchema_ = null;
      }
      return updatedSchemaBuilder_;
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

    // @@protoc_insertion_point(builder_scope:google.cloud.bigquery.storage.v1beta2.AppendRowsResponse)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.bigquery.storage.v1beta2.AppendRowsResponse)
  private static final com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse
      DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse();
  }

  public static com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AppendRowsResponse> PARSER =
      new com.google.protobuf.AbstractParser<AppendRowsResponse>() {
        @java.lang.Override
        public AppendRowsResponse parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new AppendRowsResponse(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<AppendRowsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<AppendRowsResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
