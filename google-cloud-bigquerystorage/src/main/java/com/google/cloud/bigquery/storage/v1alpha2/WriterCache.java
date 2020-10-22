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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.protobuf.Descriptors.Descriptor;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A cache of StreamWriters that can be looked up by Table Name. The entries will expire after 5
 * minutes if not used. Code sample: WriterCache cache = WriterCache.getInstance(); StreamWriter
 * writer = cache.getWriter(); // Use... cache.returnWriter(writer);
 */
public class WriterCache {
  private static final Logger LOG = Logger.getLogger(WriterCache.class.getName());

  private static String tablePatternString = "(projects/[^/]+/datasets/[^/]+/tables/[^/]+)";
  private static Pattern tablePattern = Pattern.compile(tablePatternString);

  private static WriterCache instance;
  private Cache<String, Cache<Descriptor, StreamWriter>> writerCache;

  // Maximum number of tables to hold in the cache, once the maxium exceeded, the cache will be
  // evicted based on least recent used.
  private static final int MAX_TABLE_ENTRY = 100;
  private static final int MAX_WRITERS_PER_TABLE = 2;

  private final BigQueryWriteClient stub;
  private final SchemaCompatibility compat;

  private WriterCache(BigQueryWriteClient stub, int maxTableEntry, SchemaCompatibility compat) {
    this.stub = stub;
    this.compat = compat;
    writerCache =
        CacheBuilder.newBuilder()
            .maximumSize(maxTableEntry)
            .removalListener(
                new RemovalListener<String, Cache<Descriptor, StreamWriter>>() {
                  @Override
                  public void onRemoval(
                      RemovalNotification<String, Cache<Descriptor, StreamWriter>>
                          removalNotification) {
                    removalNotification.getValue().invalidateAll();
                  }
                })
            .<String, Cache<Descriptor, StreamWriter>>build();
  }

  public static WriterCache getInstance() throws IOException {
    if (instance == null) {
      BigQueryWriteSettings stubSettings = BigQueryWriteSettings.newBuilder().build();
      BigQueryWriteClient stub = BigQueryWriteClient.create(stubSettings);
      instance = new WriterCache(stub, MAX_TABLE_ENTRY, SchemaCompatibility.getInstance());
    }
    return instance;
  }

  /** Returns a cache with custom stub used by test. */
  @VisibleForTesting
  public static WriterCache getTestInstance(
      BigQueryWriteClient stub, int maxTableEntry, SchemaCompatibility compat) {
    Preconditions.checkNotNull(stub, "Stub is null.");
    Preconditions.checkNotNull(stub, "Compat is null.");
    return new WriterCache(stub, maxTableEntry, compat);
  }

  /** Returns an entry with {@code StreamWriter} and expiration time in millis. */
  private String CreateNewStream(String tableName) {
    Stream.WriteStream stream =
        Stream.WriteStream.newBuilder().setType(Stream.WriteStream.Type.COMMITTED).build();
    stream =
        stub.createWriteStream(
            Storage.CreateWriteStreamRequest.newBuilder()
                .setParent(tableName)
                .setWriteStream(stream)
                .build());
    LOG.info("Created write stream:" + stream.getName());
    return stream.getName();
  }

  StreamWriter CreateNewWriter(String streamName)
      throws IllegalArgumentException, IOException, InterruptedException {
    return StreamWriter.newBuilder(streamName)
        .setChannelProvider(stub.getSettings().getTransportChannelProvider())
        .setCredentialsProvider(stub.getSettings().getCredentialsProvider())
        .setExecutorProvider(stub.getSettings().getExecutorProvider())
        .build();
  }
  /**
   * Gets a writer for a given table with a given user schema from global cache.
   *
   * @param tableName
   * @param userSchema
   * @return
   * @throws Exception
   */
  public StreamWriter getTableWriter(String tableName, Descriptor userSchema)
      throws IllegalArgumentException, IOException, InterruptedException {
    Preconditions.checkNotNull(tableName, "TableName is null.");
    Preconditions.checkNotNull(tableName, "UserSchema is null.");
    Matcher matcher = tablePattern.matcher(tableName);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }

    String streamName = null;
    Boolean streamExpired = false;
    StreamWriter writer = null;
    Cache<Descriptor, StreamWriter> tableEntry = null;

    synchronized (this) {
      tableEntry = writerCache.getIfPresent(tableName);
      if (tableEntry != null) {
        writer = tableEntry.getIfPresent(userSchema);
        if (writer != null) {
          return writer;
        }
        compat.check(tableName, userSchema);
        streamName = CreateNewStream(tableName);
        writer = CreateNewWriter(streamName);
        tableEntry.put(userSchema, writer);
      } else {
        compat.check(tableName, userSchema);
        streamName = CreateNewStream(tableName);
        tableEntry =
            CacheBuilder.newBuilder()
                .maximumSize(MAX_WRITERS_PER_TABLE)
                .removalListener(
                    new RemovalListener<Descriptor, StreamWriter>() {
                      @Override
                      public void onRemoval(
                          RemovalNotification<Descriptor, StreamWriter> removalNotification) {
                        removalNotification.getValue().close();
                      }
                    })
                .<Descriptor, StreamWriter>build();
        writer = CreateNewWriter(streamName);
        tableEntry.put(userSchema, writer);
        writerCache.put(tableName, tableEntry);
      }
    }

    return writer;
  }

  /** Clear the cache and close all the writers in the cache. */
  public void clear() {
    synchronized (this) {
      ConcurrentMap<String, Cache<Descriptor, StreamWriter>> map = writerCache.asMap();
      for (String key : map.keySet()) {
        Cache<Descriptor, StreamWriter> entry = writerCache.getIfPresent(key);
        ConcurrentMap<Descriptor, StreamWriter> entryMap = entry.asMap();
        for (Descriptor descriptor : entryMap.keySet()) {
          StreamWriter writer = entry.getIfPresent(descriptor);
          writer.close();
        }
      }
      writerCache.cleanUp();
    }
  }

  @VisibleForTesting
  public long cachedTableCount() {
    synchronized (writerCache) {
      return writerCache.size();
    }
  }
}
