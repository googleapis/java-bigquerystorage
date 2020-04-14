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
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
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
  private LRUCache<String, LRUCache<Descriptors.Descriptor, StreamWriter>> writerCache;

  // Maximum number of tables to hold in the cache, once the maxium exceeded, the cache will be
  // evicted based on least recent used.
  private static final int MAX_TABLE_ENTRY = 100;
  private static final int MAX_WRITERS_PER_TABLE = 2;

  private final BigQueryWriteClient stub;
  private final SchemaCompact compact;

  private WriterCache(BigQueryWriteClient stub, int maxTableEntry, SchemaCompact compact) {
    this.stub = stub;
    this.compact = compact;
    writerCache =
        new LRUCache<String, LRUCache<Descriptors.Descriptor, StreamWriter>>(maxTableEntry);
  }

  public static WriterCache getInstance() throws IOException {
    if (instance == null) {
      BigQueryWriteSettings stubSettings = BigQueryWriteSettings.newBuilder().build();
      BigQueryWriteClient stub = BigQueryWriteClient.create(stubSettings);
      instance = new WriterCache(stub, MAX_TABLE_ENTRY, SchemaCompact.getInstance());
    }
    return instance;
  }

  /** Returns a cache with custom stub used by test. */
  @VisibleForTesting
  public static WriterCache getTestInstance(
      BigQueryWriteClient stub, int maxTableEntry, SchemaCompact compact) {
    return new WriterCache(stub, maxTableEntry, compact);
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
  public StreamWriter getTableWriter(String tableName, Descriptors.Descriptor userSchema)
      throws IllegalArgumentException, IOException, InterruptedException {
    Matcher matcher = tablePattern.matcher(tableName);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }

    String streamName = null;
    Boolean streamExpired = false;
    StreamWriter writer = null;
    LRUCache<Descriptors.Descriptor, StreamWriter> tableEntry = null;

    synchronized (this) {
      tableEntry = writerCache.get(tableName);
      if (tableEntry != null) {
        writer = tableEntry.get(userSchema);
        if (writer != null && !writer.expired()) {
          return writer;
        } else {
          compact.check(tableName, userSchema);

          if (writer != null && writer.expired()) {
            writer.close();
          }
          streamName = CreateNewStream(tableName);
          writer = CreateNewWriter(streamName);
          tableEntry.put(userSchema, writer);
        }
      } else {
        compact.check(tableName, userSchema);

        streamName = CreateNewStream(tableName);
        tableEntry = new LRUCache<Descriptors.Descriptor, StreamWriter>(MAX_WRITERS_PER_TABLE);
        writer = CreateNewWriter(streamName);
        tableEntry.put(userSchema, writer);
        writerCache.put(tableName, tableEntry);
      }
    }

    return writer;
  }

  @VisibleForTesting
  public int cachedTableCount() {
    synchronized (writerCache) {
      return writerCache.getCurrentSize();
    }
  }

  private static class LRUCache<K, V> {
    class Node<T, U> {
      Node<T, U> previous;
      Node<T, U> next;
      T key;
      U value;

      public Node(Node<T, U> previous, Node<T, U> next, T key, U value) {
        this.previous = previous;
        this.next = next;
        this.key = key;
        this.value = value;
      }
    }

    private ConcurrentHashMap<K, Node<K, V>> cache;
    private Node<K, V> leastRecentlyUsed;
    private Node<K, V> mostRecentlyUsed;
    private int maxSize;
    private int currentSize;

    public LRUCache(int maxSize) {
      this.maxSize = maxSize;
      this.currentSize = 0;
      leastRecentlyUsed = new Node<K, V>(null, null, null, null);
      mostRecentlyUsed = leastRecentlyUsed;
      cache = new ConcurrentHashMap<K, Node<K, V>>();
    }

    public int getCurrentSize() {
      return cache.keySet().size();
    }

    public V get(K key) {
      Node<K, V> tempNode = cache.get(key);
      if (tempNode == null) {
        return null;
      }
      // If MRU leave the list as it is
      else if (tempNode.key == mostRecentlyUsed.key) {
        return mostRecentlyUsed.value;
      }

      // Get the next and previous nodes
      Node<K, V> nextNode = tempNode.next;
      Node<K, V> previousNode = tempNode.previous;

      // If at the left-most, we update LRU
      if (tempNode.key == leastRecentlyUsed.key) {
        nextNode.previous = null;
        leastRecentlyUsed = nextNode;
      }

      // If we are in the middle, we need to update the items before and after our item
      else if (tempNode.key != mostRecentlyUsed.key) {
        previousNode.next = nextNode;
        nextNode.previous = previousNode;
      }

      // Finally move our item to the MRU
      tempNode.previous = mostRecentlyUsed;
      mostRecentlyUsed.next = tempNode;
      mostRecentlyUsed = tempNode;
      mostRecentlyUsed.next = null;

      return tempNode.value;
    }

    public void put(K key, V value) {
      if (cache.containsKey(key)) {
        return;
      }

      // Put the new node at the right-most end of the linked-list
      Node<K, V> myNode = new Node<K, V>(mostRecentlyUsed, null, key, value);
      mostRecentlyUsed.next = myNode;
      cache.put(key, myNode);
      mostRecentlyUsed = myNode;

      // Delete the left-most entry and update the LRU pointer
      if (currentSize == maxSize) {
        cache.remove(leastRecentlyUsed.key);
        leastRecentlyUsed = leastRecentlyUsed.next;
        leastRecentlyUsed.previous = null;
      }

      // Update cache size, for the first added entry update the LRU pointer
      else if (currentSize < maxSize) {
        if (currentSize == 0) {
          leastRecentlyUsed = myNode;
        }
        currentSize++;
      }
    }
  }
}
