/*
 * Copyright 2022 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auto.value.AutoValue;
import com.google.cloud.bigquery.storage.v1.ConnectionWorker.Load;
import com.google.cloud.bigquery.storage.v1.ConnectionWorker.TableSchemaAndTimestamp;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/** Pool of connections to accept appends and distirbute to different connections. */
public class ConnectionWorkerPool {
  static final Pattern STREAM_NAME_PATTERN =
      Pattern.compile("projects/([^/]+)/datasets/([^/]+)/tables/([^/]+)/streams/([^/]+)");

  private static final Logger log = Logger.getLogger(ConnectionWorkerPool.class.getName());
  /*
   * Max allowed inflight requests in the stream.getInflightWaitSeconds Method append is blocked at this.
   */
  private final long maxInflightRequests;

  /*
   * Max allowed inflight bytes in the stream. Method append is blocked at this.
   */
  private final long maxInflightBytes;

  /*
   * Max retry duration for retryable errors.
   */
  private final java.time.Duration maxRetryDuration;

  private RetrySettings retrySettings;

  /*
   * Behavior when inflight queue is exceeded. Only supports Block or Throw, default is Block.
   */
  private final FlowController.LimitExceededBehavior limitExceededBehavior;

  /** Map from write stream to corresponding connection. */
  private final Map<StreamWriter, ConnectionWorker> streamWriterToConnection = new HashMap<>();

  /** Map from a connection to a set of write stream that have sent requests onto it. */
  private final Map<ConnectionWorker, Set<StreamWriter>> connectionToWriteStream = new HashMap<>();

  /** Collection of all the created connections. */
  private final Set<ConnectionWorker> connectionWorkerPool =
      Collections.synchronizedSet(new HashSet<>());

  /*
   * Contains the mapping from stream name to updated schema.
   */
  private Map<String, TableSchemaAndTimestamp> tableNameToUpdatedSchema = new ConcurrentHashMap<>();

  /** Enable test related logic. */
  private static boolean enableTesting = false;

  /*
   * TraceId for debugging purpose.
   */
  private final String traceId;
  /*
   * Sets the compression to use for the calls
   */
  private String compressorName;

  /** Used for test on the number of times createWorker is called. */
  private final AtomicInteger testValueCreateConnectionCount = new AtomicInteger(0);

  /*
   * Tracks current inflight requests in the stream.
   */
  @GuardedBy("lock")
  private long inflightRequests = 0;

  /*
   * Tracks current inflight bytes in the stream.
   */
  @GuardedBy("lock")
  private long inflightBytes = 0;

  /*
   * Tracks how often the stream was closed due to a retriable error. Streaming will stop when the
   * count hits a threshold. Streaming should only be halted, if it isn't possible to establish a
   * connection. Keep track of the number of reconnections in succession. This will be reset if
   * a row is successfully called back.
   */
  @GuardedBy("lock")
  private long conectionRetryCountWithoutCallback = 0;

  /*
   * If false, streamConnection needs to be reset.
   */
  @GuardedBy("lock")
  private boolean streamConnectionIsConnected = false;

  /*
   * A boolean to track if we cleaned up inflight queue.
   */
  @GuardedBy("lock")
  private boolean inflightCleanuped = false;

  /*
   * Indicates whether user has called Close() or not.
   */
  @GuardedBy("lock")
  private boolean userClosed = false;

  /*
   * The final status of connection. Set to nonnull when connection is permanently closed.
   */
  @GuardedBy("lock")
  private Throwable connectionFinalStatus = null;

  /*
   * Contains the updated TableSchema.
   */
  @GuardedBy("lock")
  private TableSchema updatedSchema;

  /*
   * A client used to interact with BigQuery.
   */
  private BigQueryWriteSettings clientSettings;

  /**
   * The current maximum connection count. This value is gradually increased till the user defined
   * maximum connection count.
   */
  private int currentMaxConnectionCount;

  /** Lock for controlling concurrent operation on add / delete connections. */
  private final Lock lock = new ReentrantLock();

  /** Settings for connection pool. */
  @AutoValue
  public abstract static class Settings {
    /**
     * The minimum connections each pool created before trying to reuse the previously created
     * connection in multiplexing mode.
     */
    abstract int minConnectionsPerRegion();

    /** The maximum connections per connection pool. */
    abstract int maxConnectionsPerRegion();

    public static Builder builder() {
      return new AutoValue_ConnectionWorkerPool_Settings.Builder()
          .setMinConnectionsPerRegion(2)
          .setMaxConnectionsPerRegion(20);
    }

    /** Builder for the options to config {@link ConnectionWorkerPool}. */
    @AutoValue.Builder
    public abstract static class Builder {
      // TODO(gaole) rename to per location for easier understanding.
      public abstract Builder setMinConnectionsPerRegion(int value);

      public abstract Builder setMaxConnectionsPerRegion(int value);

      public abstract Settings build();
    }
  }

  /** Static setting for connection pool. */
  private static Settings settings = Settings.builder().build();

  ConnectionWorkerPool(
      long maxInflightRequests,
      long maxInflightBytes,
      java.time.Duration maxRetryDuration,
      FlowController.LimitExceededBehavior limitExceededBehavior,
      String traceId,
      @Nullable String comperssorName,
      BigQueryWriteSettings clientSettings) {
    this.maxInflightRequests = maxInflightRequests;
    this.maxInflightBytes = maxInflightBytes;
    this.maxRetryDuration = maxRetryDuration;
    this.limitExceededBehavior = limitExceededBehavior;
    this.traceId = traceId;
    this.compressorName = comperssorName;
    this.clientSettings = clientSettings;
    this.currentMaxConnectionCount = settings.minConnectionsPerRegion();
    // In-stream retry is not enabled for multiplexing.
    this.retrySettings = null;
  }

  /**
   * Sets static connection pool options.
   *
   * <p>Note: this method should be triggered prior to the construction of connection pool.
   */
  public static void setOptions(Settings settings) {
    ConnectionWorkerPool.settings = settings;
  }

  /** Distributes the writing of a message to an underlying connection. */
  ApiFuture<AppendRowsResponse> append(StreamWriter streamWriter, ProtoRows rows) {
    return append(streamWriter, rows, -1);
  }

  /** Distributes the writing of a message to an underlying connection. */
  ApiFuture<AppendRowsResponse> append(StreamWriter streamWriter, ProtoRows rows, long offset) {
    // We are in multiplexing mode after entering the following logic.
    ConnectionWorker connectionWorker;
    lock.lock();
    try {
      connectionWorker =
          streamWriterToConnection.compute(
              streamWriter,
              (key, existingStream) -> {
                // Stick to the existing stream if it's not overwhelmed.
                if (existingStream != null
                    && !existingStream.getLoad().isOverwhelmed()
                    && !existingStream.isConnectionInUnrecoverableState()) {
                  return existingStream;
                }
                if (existingStream != null && existingStream.isConnectionInUnrecoverableState()) {
                  existingStream = null;
                }
                // Before search for the next connection to attach, clear the finalized connections
                // first so that they will not be selected.
                clearFinalizedConnectionWorker();
                // Try to create or find another existing stream to reuse.
                ConnectionWorker createdOrExistingConnection = null;
                try {
                  createdOrExistingConnection =
                      createOrReuseConnectionWorker(streamWriter, existingStream);
                } catch (IOException e) {
                  throw new IllegalStateException(e);
                }
                // Update connection to write stream relationship.
                connectionToWriteStream.computeIfAbsent(
                    createdOrExistingConnection, (ConnectionWorker k) -> new HashSet<>());
                connectionToWriteStream.get(createdOrExistingConnection).add(streamWriter);
                return createdOrExistingConnection;
              });
    } finally {
      lock.unlock();
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    ApiFuture<AppendRowsResponse> responseFuture =
        connectionWorker.append(streamWriter, rows, offset);
    return ApiFutures.transform(
        responseFuture,
        // Add callback for update schema
        (response) -> {
          if (response.getWriteStream() != "" && response.hasUpdatedSchema()) {
            tableNameToUpdatedSchema.put(
                response.getWriteStream(),
                TableSchemaAndTimestamp.create(System.nanoTime(), response.getUpdatedSchema()));
          }
          return response;
        },
        MoreExecutors.directExecutor());
  }

  /**
   * Create a new connection if we haven't reached current maximum, or reuse an existing connection
   * with least load.
   */
  private ConnectionWorker createOrReuseConnectionWorker(
      StreamWriter streamWriter, ConnectionWorker existingConnectionWorker) throws IOException {
    String streamReference = streamWriter.getStreamName();
    if (connectionWorkerPool.size() < currentMaxConnectionCount) {
      // Always create a new connection if we haven't reached current maximum.
      return createConnectionWorker(
          streamWriter.getStreamName(), streamWriter.getLocation(), streamWriter.getProtoSchema());
    } else {
      ConnectionWorker existingBestConnection =
          pickBestLoadConnection(
              enableTesting ? Load.TEST_LOAD_COMPARATOR : Load.LOAD_COMPARATOR,
              ImmutableList.copyOf(connectionWorkerPool));
      if (!existingBestConnection.getLoad().isOverwhelmed()) {
        return existingBestConnection;
      } else if (currentMaxConnectionCount < settings.maxConnectionsPerRegion()) {
        // At this point, we have reached the connection cap and the selected connection is
        // overwhelmed, we can try scale up the connection pool.
        // The connection count will go up one by one until `maxConnectionsPerPool` is reached.
        currentMaxConnectionCount += 1;
        if (currentMaxConnectionCount > settings.maxConnectionsPerRegion()) {
          currentMaxConnectionCount = settings.maxConnectionsPerRegion();
        }
        return createConnectionWorker(
            streamWriter.getStreamName(),
            streamWriter.getLocation(),
            streamWriter.getProtoSchema());
      } else {
        // Stick to the original connection if all the connections are overwhelmed.
        if (existingConnectionWorker != null) {
          return existingConnectionWorker;
        }
        // If we are at this branch, it means we reached the maximum connections.
        return existingBestConnection;
      }
    }
  }

  private void clearFinalizedConnectionWorker() {
    Set<ConnectionWorker> connectionWorkerSet = new HashSet<>();
    for (ConnectionWorker existingWorker : connectionWorkerPool) {
      if (existingWorker.isConnectionInUnrecoverableState()) {
        connectionWorkerSet.add(existingWorker);
      }
    }
    for (ConnectionWorker workerToRemove : connectionWorkerSet) {
      connectionWorkerPool.remove(workerToRemove);
    }
  }

  /** Select out the best connection worker among the given connection workers. */
  static ConnectionWorker pickBestLoadConnection(
      Comparator<Load> comparator, List<ConnectionWorker> connectionWorkerList) {
    if (connectionWorkerList.isEmpty()) {
      throw new IllegalStateException(
          String.format(
              "Bug in code! At least one connection worker should be passed in "
                  + "pickSemiBestLoadConnection(...)"));
    }
    // Compare all connection workers to find the connection worker with the smallest load.
    // Loop and find the connection with the least load.
    // The load comparision and computation process
    int currentBestIndex = 0;
    Load currentBestLoad = connectionWorkerList.get(currentBestIndex).getLoad();
    for (int i = 1; i < connectionWorkerList.size(); i++) {
      Load loadToCompare = connectionWorkerList.get(i).getLoad();
      if (comparator.compare(loadToCompare, currentBestLoad) <= 0) {
        currentBestIndex = i;
        currentBestLoad = loadToCompare;
      }
    }
    return connectionWorkerList.get(currentBestIndex);
  }

  /**
   * Creates a single connection worker.
   *
   * <p>Note this function need to be thread-safe across different stream reference but no need for
   * a single stream reference. This is because createConnectionWorker(...) is called via
   * computeIfAbsent(...) which is at most once per key.
   */
  private ConnectionWorker createConnectionWorker(
      String streamName, String location, ProtoSchema writeSchema) throws IOException {
    if (enableTesting) {
      // Though atomic integer is super lightweight, add extra if check in case adding future logic.
      testValueCreateConnectionCount.getAndIncrement();
    }
    ConnectionWorker connectionWorker =
        new ConnectionWorker(
            streamName,
            location,
            writeSchema,
            maxInflightRequests,
            maxInflightBytes,
            maxRetryDuration,
            limitExceededBehavior,
            traceId,
            compressorName,
            clientSettings,
            retrySettings);
    connectionWorkerPool.add(connectionWorker);
    log.info(
        String.format(
            "Scaling up new connection for stream name: %s, pool size after scaling up %d",
            streamName, connectionWorkerPool.size()));
    return connectionWorker;
  }

  /**
   * Reports the close of the given write stream.
   *
   * <p>The corresponding worker is not closed until there is no stream reference is targeting to
   * that worker.
   */
  void close(StreamWriter streamWriter) {
    lock.lock();
    try {
      streamWriterToConnection.remove(streamWriter);
      // Since it's possible some other connections may have served this writeStream, we
      // iterate and see whether it's also fine to close other connections.
      Set<ConnectionWorker> connectionToRemove = new HashSet<>();
      for (ConnectionWorker connectionWorker : connectionToWriteStream.keySet()) {
        if (connectionToWriteStream.containsKey(connectionWorker)) {
          connectionToWriteStream.get(connectionWorker).remove(streamWriter);
          if (connectionToWriteStream.get(connectionWorker).isEmpty()) {
            connectionWorker.close();
            connectionWorkerPool.remove(connectionWorker);
            connectionToRemove.add(connectionWorker);
          }
        }
      }
      log.info(
          String.format(
              "During closing of writeStream for %s with writer id %s, we decided to close %s "
                  + "connections, pool size after removal $s",
              streamWriter.getStreamName(),
              streamWriter.getWriterId(),
              connectionToRemove.size(),
              connectionToWriteStream.size() - 1));
      connectionToWriteStream.keySet().removeAll(connectionToRemove);
    } finally {
      lock.unlock();
    }
  }

  /** Fetch the wait seconds from corresponding worker. */
  long getInflightWaitSeconds(StreamWriter streamWriter) {
    lock.lock();
    try {
      ConnectionWorker connectionWorker = streamWriterToConnection.get(streamWriter);
      if (connectionWorker == null) {
        return 0;
      } else {
        return connectionWorker.getInflightWaitSeconds();
      }
    } finally {
      lock.unlock();
    }
  }

  TableSchemaAndTimestamp getUpdatedSchema(StreamWriter streamWriter) {
    return tableNameToUpdatedSchema.getOrDefault(streamWriter.getStreamName(), null);
  }

  /** Enable Test related logic. */
  @VisibleForTesting
  static void enableTestingLogic() {
    enableTesting = true;
  }

  /** Returns how many times createConnectionWorker(...) is called. */
  int getCreateConnectionCount() {
    return testValueCreateConnectionCount.get();
  }

  int getTotalConnectionCount() {
    return connectionWorkerPool.size();
  }

  String getTraceId() {
    return traceId;
  }

  FlowController.LimitExceededBehavior limitExceededBehavior() {
    return limitExceededBehavior;
  }

  BigQueryWriteSettings bigQueryWriteSettings() {
    return clientSettings;
  }

  static String toTableName(String streamName) {
    Matcher matcher = STREAM_NAME_PATTERN.matcher(streamName);
    Preconditions.checkArgument(matcher.matches(), "Invalid stream name: %s.", streamName);
    return "projects/"
        + matcher.group(1)
        + "/datasets/"
        + matcher.group(2)
        + "/tables/"
        + matcher.group(3);
  }
}
