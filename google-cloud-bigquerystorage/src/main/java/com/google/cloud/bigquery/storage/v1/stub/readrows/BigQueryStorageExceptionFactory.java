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

package com.google.cloud.bigquery.storage.v1.stub.readrows;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Map;
import javax.annotation.Nullable;
import javax.net.ssl.SSLHandshakeException;

public class BigQueryStorageExceptionFactory {

  public enum ErrorCode {
    CANCELLED(Status.CANCELLED),
    UNKNOWN(Status.UNKNOWN),
    INVALID_ARGUMENT(Status.INVALID_ARGUMENT),
    DEADLINE_EXCEEDED(Status.DEADLINE_EXCEEDED),
    NOT_FOUND(Status.NOT_FOUND),
    ALREADY_EXISTS(Status.ALREADY_EXISTS),
    PERMISSION_DENIED(Status.PERMISSION_DENIED),
    UNAUTHENTICATED(Status.UNAUTHENTICATED),
    RESOURCE_EXHAUSTED(Status.RESOURCE_EXHAUSTED),
    FAILED_PRECONDITION(Status.FAILED_PRECONDITION),
    ABORTED(Status.ABORTED),
    OUT_OF_RANGE(Status.OUT_OF_RANGE),
    UNIMPLEMENTED(Status.UNIMPLEMENTED),
    INTERNAL(Status.INTERNAL),
    UNAVAILABLE(Status.UNAVAILABLE),
    DATA_LOSS(Status.DATA_LOSS),
    ;

    private static final Map<Integer, ErrorCode> errorByRpcCode;

    static {
      ImmutableMap.Builder<Integer, ErrorCode> builder = ImmutableMap.builder();
      for (ErrorCode errorCode : ErrorCode.values()) {
        builder.put(errorCode.getCode(), errorCode);
      }
      errorByRpcCode = builder.build();
    }

    private final Status.Code code;

    ErrorCode(Status status) {
      this.code = status.getCode();
    }

    int getCode() {
      return this.code.value();
    }

    Status getGrpcStatus() {
      return this.code.toStatus();
    }

    /** @return the corresponding gRPC status code of this {@link ErrorCode}. */
    public Status.Code getGrpcStatusCode() {
      return this.code;
    }

    /**
     * Returns the error code represents by {@code name}, or {@code defaultValue} if {@code name}
     * does not map to a known code.
     */
    static ErrorCode valueOf(String name, ErrorCode defaultValue) {
      try {
        return ErrorCode.valueOf(name);
      } catch (IllegalArgumentException e) {
        return defaultValue;
      }
    }

    /**
     * Returns the error code corresponding to a gRPC status, or {@code UNKNOWN} if not recognized.
     */
    public static ErrorCode fromGrpcStatus(Status status) {
      ErrorCode code = errorByRpcCode.get(status.getCode().value());
      return code == null ? UNKNOWN : code;
    }

    /**
     * Returns the error code corresponding to the given status, or {@code UNKNOWN} if not
     * recognized.
     */
    static ErrorCode fromRpcStatus(com.google.rpc.Status status) {
      ErrorCode code = errorByRpcCode.get(status.getCode());
      return code == null ? UNKNOWN : code;
    }
  }

  public static BigQueryStorageException newBigQueryStorageException(Throwable throwable) {
    Status status = Status.fromThrowable(throwable);
    return new BigQueryStorageException(
        status.getDescription(),
        throwable.getCause(),
        status.getCode().value(),
        isRetryable(ErrorCode.fromGrpcStatus(status), throwable.getCause()));
  }

  private static boolean isRetryable(ErrorCode code, @Nullable Throwable cause) {
    switch (code) {
      case INTERNAL:
        return hasCauseMatching(cause, Matchers.isRetryableInternalError);
      case UNAVAILABLE:
        // SSLHandshakeException is (probably) not retryable, as it is an indication that the server
        // certificate was not accepted by the client.
        return !hasCauseMatching(cause, Matchers.isSSLHandshakeException);
      default:
        return false;
    }
  }

  private static boolean hasCauseMatching(
      @Nullable Throwable cause, Predicate<? super Throwable> matcher) {
    while (cause != null) {
      if (matcher.apply(cause)) {
        return true;
      }
      cause = cause.getCause();
    }
    return false;
  }

  private static class Matchers {
    static final Predicate<Throwable> isRetryableInternalError =
        new Predicate<Throwable>() {
          @Override
          public boolean apply(Throwable cause) {
            if (cause instanceof StatusRuntimeException
                && ((StatusRuntimeException) cause).getStatus().getCode() == Status.Code.INTERNAL) {
              if (cause.getMessage().contains("HTTP/2 error code: INTERNAL_ERROR")) {
                // See b/25451313.
                return true;
              }
              if (cause.getMessage().contains("Connection closed with unknown cause")) {
                // See b/27794742.
                return true;
              }
              if (cause
                  .getMessage()
                  .contains("Received unexpected EOS on DATA frame from server")) {
                return true;
              }
            }
            return false;
          }
        };
    static final Predicate<Throwable> isSSLHandshakeException =
        new Predicate<Throwable>() {
          @Override
          public boolean apply(Throwable input) {
            return input instanceof SSLHandshakeException;
          }
        };
  }
}
