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

  private Exceptions() {}
}
