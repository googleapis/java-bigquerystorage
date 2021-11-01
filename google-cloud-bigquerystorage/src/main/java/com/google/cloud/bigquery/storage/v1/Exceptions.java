/*
 * Copyright 2021 Google LLC
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
package com.google.cloud.bigquery.storage.v1;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import javax.annotation.Nullable;

/** Exceptions for Storage Client Libraries. */
public final class Exceptions {
  /** Main Storage Exception. Might contain map of streams to errors for that stream. */
  public static class StorageException extends RuntimeException {

    protected StorageException() {}

    protected StorageException(String message) {
      super(message);
    }

    protected StorageException(String message, Throwable cause) {
      super(message, cause);
    }

    protected StorageException(Throwable cause) {
      super(cause);
    }

    private ImmutableMap<String, GrpcStatusCode> errors;

    public ImmutableMap<String, GrpcStatusCode> getErrors() {
      return errors;
    }

    public void setErrors(ImmutableMap<String, GrpcStatusCode> value) {
      this.errors = value;
    }

    private String streamName;

    public String getStreamName() {
      return streamName;
    }

    protected void setStreamName(String value) {
      this.streamName = value;
    }
  }

  /** Stream has already been finalized. */
  public static class StreamFinalizedException extends StorageException {
    protected StreamFinalizedException() {}

    protected StreamFinalizedException(String name, String message, Throwable cause) {
      super(message, cause);
      setStreamName(name);
    }
  }

  /**
   * There was a schema mismatch due to bigquery table with fewer fields than the input message.
   * This can be resolved by updating the table's schema with the message schema.
   */
  public static class SchemaMismatchedException extends StorageException {
    protected SchemaMismatchedException() {}

    protected SchemaMismatchedException(String name, String message, Throwable cause) {
      super(message, cause);
      setStreamName(name);
    }
  }

  private static StorageError toStorageError(com.google.rpc.Status rpcStatus) {
    for (Any detail : rpcStatus.getDetailsList()) {
      if (detail.is(StorageError.class)) {
        try {
          return detail.unpack(StorageError.class);
        } catch (InvalidProtocolBufferException protoException) {
          throw new IllegalStateException(protoException);
        }
      }
    }
    return null;
  }

  /* Converts a c.g.rpc.Status into a StorageException, if possible.
   * Examines the embedded StorageError, and potentially returns a StreamFinalizedException or
   * SchemaMismatchedException (both derive from StorageException).
   * If there is no StorageError, or the StorageError is a different error it will return NULL.
   */
  @Nullable
  public static StorageException toStorageException(
      com.google.rpc.Status rpcStatus, Throwable exception) {
    StorageError error = toStorageError(rpcStatus);
    if (error == null) {
      return null;
    }
    switch (error.getCode()) {
      case STREAM_FINALIZED:
        return new StreamFinalizedException(error.getEntity(), error.getErrorMessage(), exception);

      case SCHEMA_MISMATCH_EXTRA_FIELDS:
        return new SchemaMismatchedException(error.getEntity(), error.getErrorMessage(), exception);

      default:
        return null;
    }
  }

  /* Converts a Throwable into a StorageException, if possible.
   * Examines the embedded error message, and potentially returns a StreamFinalizedException or
   * SchemaMismatchedException (both derive from StorageException).
   * If there is no StorageError, or the StorageError is a different error it will return NULL.
   */
  @Nullable
  public static StorageException toStorageException(Throwable exception) {
    String errorMsg = exception.getMessage();
    if (errorMsg == null) {
      return null;
    }
    if (errorMsg.toLowerCase().contains("finazlied")) {
      return new StreamFinalizedException(null, errorMsg, exception);
    }
    if (errorMsg.toLowerCase().contains("mismatch")) {
      return new SchemaMismatchedException(null, errorMsg, exception);
    }
    return null;
  }

  private Exceptions() {}
}
