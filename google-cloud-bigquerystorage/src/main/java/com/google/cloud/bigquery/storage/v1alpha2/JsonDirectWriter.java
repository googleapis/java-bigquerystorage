/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery.storage.v1alpha2;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoRows;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import io.grpc.Status;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.json.JSONArray;

/**
 * Writer that can help user to write JSON data to BigQuery. This is a simplified version of the Write
 * API. For users writing with COMMITTED stream and don't care about row deduplication, it is
 * recommended to use this Writer.
 *
 * <pre>{@code
 * JSONArray json;
 * ApiFuture<Long> response = JsonDirectWriter.append("projects/pid/datasets/did/tables/tid", json);
 * }</pre>
 *
 * <p>{@link JsonDirectWriter} will use the credentials set on the channel, which uses application
 * default credentials through {@link GoogleCredentials#getApplicationDefault} by default.
 */
public class JsonDirectWriter {
  private static final Logger LOG = Logger.getLogger(JsonDirectWriter.class.getName());
  private static JsonWriterCache cache = null;
  private static Lock cacheLock = new ReentrantLock();

  /**
   * Append rows to the given table.
   *
   * @param tableName table name in the form of "projects/{pName}/datasets/{dName}/tables/{tName}"
   * @param json A JSONArray
   * @return A future that contains the offset at which the append happened. Only when the future
   *     returns with valid offset, then the append actually happened.
   * @throws IOException, InterruptedException, InvalidArgumentException, Descriptors.DescriptorValidationException
   */
  public static <T extends Message> ApiFuture<Long> append(String tableName, JSONArray json)
      throws IOException, InterruptedException, InvalidArgumentException, Descriptors.DescriptorValidationException {
    Preconditions.checkNotNull(tableName, "TableName is null.");
    Preconditions.checkNotNull(json, "JSONArray is null.");

    if (json.length() == 0) {
      throw new InvalidArgumentException(
          new Exception("Empty JSONArrays are not allowed"),
          GrpcStatusCode.of(Status.Code.INVALID_ARGUMENT),
          false);
    }
    try {
      cacheLock.lock();
      if (cache == null) {
        cache = JsonWriterCache.getInstance();
      }
    } finally {
      cacheLock.unlock();
    }

    JsonStreamWriter writer = cache.getTableWriter(tableName);
    return ApiFutures.<Storage.AppendRowsResponse, Long>transform(
        writer.append(json, /* offset = */ -1, /*allowUnknownFields = */ false),
        new ApiFunction<Storage.AppendRowsResponse, Long>() {
          @Override
          public Long apply(Storage.AppendRowsResponse appendRowsResponse) {
            return Long.valueOf(appendRowsResponse.getOffset());
          }
        },
        MoreExecutors.directExecutor());
  }

  @VisibleForTesting
  public static void testSetStub(
      BigQueryWriteClient stub, int maxTableEntry) {
    cache = JsonWriterCache.getTestInstance(stub, maxTableEntry);
  }

  /** Clears the underlying cache and all the transport connections. */
  public static void clearCache() {
    cache.clear();
  }
}
