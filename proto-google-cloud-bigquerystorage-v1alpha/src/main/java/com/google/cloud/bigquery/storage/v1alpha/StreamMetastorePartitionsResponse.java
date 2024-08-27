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
// source: google/cloud/bigquery/storage/v1alpha/metastore_partition.proto

// Protobuf Java Version: 3.25.4
package com.google.cloud.bigquery.storage.v1alpha;

/**
 *
 *
 * <pre>
 * This is the response message sent by the server
 * to the client for the [Partitions.StreamMetastorePartitions]() method when
 * the commit is successful. Server will close the stream after sending this
 * message.
 * </pre>
 *
 * Protobuf type {@code google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse}
 */
public final class StreamMetastorePartitionsResponse extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse)
    StreamMetastorePartitionsResponseOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use StreamMetastorePartitionsResponse.newBuilder() to construct.
  private StreamMetastorePartitionsResponse(
      com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private StreamMetastorePartitionsResponse() {}

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new StreamMetastorePartitionsResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionServiceProto
        .internal_static_google_cloud_bigquery_storage_v1alpha_StreamMetastorePartitionsResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionServiceProto
        .internal_static_google_cloud_bigquery_storage_v1alpha_StreamMetastorePartitionsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse.class,
            com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse.Builder
                .class);
  }

  public static final int TOTAL_PARTITIONS_STREAMED_COUNT_FIELD_NUMBER = 2;
  private long totalPartitionsStreamedCount_ = 0L;
  /**
   *
   *
   * <pre>
   * Total count of partitions streamed by the client during the lifetime of the
   * stream. This is only set in the final response message before closing the
   * stream.
   * </pre>
   *
   * <code>int64 total_partitions_streamed_count = 2;</code>
   *
   * @return The totalPartitionsStreamedCount.
   */
  @java.lang.Override
  public long getTotalPartitionsStreamedCount() {
    return totalPartitionsStreamedCount_;
  }

  public static final int TOTAL_PARTITIONS_INSERTED_COUNT_FIELD_NUMBER = 3;
  private long totalPartitionsInsertedCount_ = 0L;
  /**
   *
   *
   * <pre>
   * Total count of partitions inserted by the server during the lifetime of the
   * stream. This is only set in the final response message before closing the
   * stream.
   * </pre>
   *
   * <code>int64 total_partitions_inserted_count = 3;</code>
   *
   * @return The totalPartitionsInsertedCount.
   */
  @java.lang.Override
  public long getTotalPartitionsInsertedCount() {
    return totalPartitionsInsertedCount_;
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
    if (totalPartitionsStreamedCount_ != 0L) {
      output.writeInt64(2, totalPartitionsStreamedCount_);
    }
    if (totalPartitionsInsertedCount_ != 0L) {
      output.writeInt64(3, totalPartitionsInsertedCount_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (totalPartitionsStreamedCount_ != 0L) {
      size +=
          com.google.protobuf.CodedOutputStream.computeInt64Size(2, totalPartitionsStreamedCount_);
    }
    if (totalPartitionsInsertedCount_ != 0L) {
      size +=
          com.google.protobuf.CodedOutputStream.computeInt64Size(3, totalPartitionsInsertedCount_);
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
    if (!(obj
        instanceof com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse)) {
      return super.equals(obj);
    }
    com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse other =
        (com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse) obj;

    if (getTotalPartitionsStreamedCount() != other.getTotalPartitionsStreamedCount()) return false;
    if (getTotalPartitionsInsertedCount() != other.getTotalPartitionsInsertedCount()) return false;
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
    hash = (37 * hash) + TOTAL_PARTITIONS_STREAMED_COUNT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getTotalPartitionsStreamedCount());
    hash = (37 * hash) + TOTAL_PARTITIONS_INSERTED_COUNT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getTotalPartitionsInsertedCount());
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(
          java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(
          java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseDelimitedFrom(
          java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      parseFrom(
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
      com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse prototype) {
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
   * This is the response message sent by the server
   * to the client for the [Partitions.StreamMetastorePartitions]() method when
   * the commit is successful. Server will close the stream after sending this
   * message.
   * </pre>
   *
   * Protobuf type {@code google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse)
      com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionServiceProto
          .internal_static_google_cloud_bigquery_storage_v1alpha_StreamMetastorePartitionsResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionServiceProto
          .internal_static_google_cloud_bigquery_storage_v1alpha_StreamMetastorePartitionsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse.class,
              com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse.Builder
                  .class);
    }

    // Construct using
    // com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      totalPartitionsStreamedCount_ = 0L;
      totalPartitionsInsertedCount_ = 0L;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.bigquery.storage.v1alpha.MetastorePartitionServiceProto
          .internal_static_google_cloud_bigquery_storage_v1alpha_StreamMetastorePartitionsResponse_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
        getDefaultInstanceForType() {
      return com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
          .getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse build() {
      com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse result =
          buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
        buildPartial() {
      com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse result =
          new com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse(this);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartial0(
        com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.totalPartitionsStreamedCount_ = totalPartitionsStreamedCount_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.totalPartitionsInsertedCount_ = totalPartitionsInsertedCount_;
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
      if (other
          instanceof com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse) {
        return mergeFrom(
            (com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(
        com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse other) {
      if (other
          == com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
              .getDefaultInstance()) return this;
      if (other.getTotalPartitionsStreamedCount() != 0L) {
        setTotalPartitionsStreamedCount(other.getTotalPartitionsStreamedCount());
      }
      if (other.getTotalPartitionsInsertedCount() != 0L) {
        setTotalPartitionsInsertedCount(other.getTotalPartitionsInsertedCount());
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
            case 16:
              {
                totalPartitionsStreamedCount_ = input.readInt64();
                bitField0_ |= 0x00000001;
                break;
              } // case 16
            case 24:
              {
                totalPartitionsInsertedCount_ = input.readInt64();
                bitField0_ |= 0x00000002;
                break;
              } // case 24
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

    private long totalPartitionsStreamedCount_;
    /**
     *
     *
     * <pre>
     * Total count of partitions streamed by the client during the lifetime of the
     * stream. This is only set in the final response message before closing the
     * stream.
     * </pre>
     *
     * <code>int64 total_partitions_streamed_count = 2;</code>
     *
     * @return The totalPartitionsStreamedCount.
     */
    @java.lang.Override
    public long getTotalPartitionsStreamedCount() {
      return totalPartitionsStreamedCount_;
    }
    /**
     *
     *
     * <pre>
     * Total count of partitions streamed by the client during the lifetime of the
     * stream. This is only set in the final response message before closing the
     * stream.
     * </pre>
     *
     * <code>int64 total_partitions_streamed_count = 2;</code>
     *
     * @param value The totalPartitionsStreamedCount to set.
     * @return This builder for chaining.
     */
    public Builder setTotalPartitionsStreamedCount(long value) {

      totalPartitionsStreamedCount_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Total count of partitions streamed by the client during the lifetime of the
     * stream. This is only set in the final response message before closing the
     * stream.
     * </pre>
     *
     * <code>int64 total_partitions_streamed_count = 2;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearTotalPartitionsStreamedCount() {
      bitField0_ = (bitField0_ & ~0x00000001);
      totalPartitionsStreamedCount_ = 0L;
      onChanged();
      return this;
    }

    private long totalPartitionsInsertedCount_;
    /**
     *
     *
     * <pre>
     * Total count of partitions inserted by the server during the lifetime of the
     * stream. This is only set in the final response message before closing the
     * stream.
     * </pre>
     *
     * <code>int64 total_partitions_inserted_count = 3;</code>
     *
     * @return The totalPartitionsInsertedCount.
     */
    @java.lang.Override
    public long getTotalPartitionsInsertedCount() {
      return totalPartitionsInsertedCount_;
    }
    /**
     *
     *
     * <pre>
     * Total count of partitions inserted by the server during the lifetime of the
     * stream. This is only set in the final response message before closing the
     * stream.
     * </pre>
     *
     * <code>int64 total_partitions_inserted_count = 3;</code>
     *
     * @param value The totalPartitionsInsertedCount to set.
     * @return This builder for chaining.
     */
    public Builder setTotalPartitionsInsertedCount(long value) {

      totalPartitionsInsertedCount_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Total count of partitions inserted by the server during the lifetime of the
     * stream. This is only set in the final response message before closing the
     * stream.
     * </pre>
     *
     * <code>int64 total_partitions_inserted_count = 3;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearTotalPartitionsInsertedCount() {
      bitField0_ = (bitField0_ & ~0x00000002);
      totalPartitionsInsertedCount_ = 0L;
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

    // @@protoc_insertion_point(builder_scope:google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse)
  private static final com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE =
        new com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse();
  }

  public static com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<StreamMetastorePartitionsResponse> PARSER =
      new com.google.protobuf.AbstractParser<StreamMetastorePartitionsResponse>() {
        @java.lang.Override
        public StreamMetastorePartitionsResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<StreamMetastorePartitionsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<StreamMetastorePartitionsResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.bigquery.storage.v1alpha.StreamMetastorePartitionsResponse
      getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
