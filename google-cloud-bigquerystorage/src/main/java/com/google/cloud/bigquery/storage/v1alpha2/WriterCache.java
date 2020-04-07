package com.google.cloud.bigquery.storage.v1alpha2;

import com.google.common.base.Preconditions;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.threeten.bp.Duration;

/**
 * A cache of StreamWriters that can be looked up by Table Name. The entries will expire after 5
 * minutes if not used. Code sample: WriterCache cache = WriterCache.getInstance(); StreamWriter
 * writer = cache.getWriter(); // Use... cache.returnWriter(writer);
 */
public class StreamCache {
  private static final Logger LOG = Logger.getLogger(StreamCache.class.getName());

  private static StreamCache instance;

  private Duration expireTime = Duration.ofSeconds(300);
  private ConcurrentHashMap<String, Map<String, Pair<StreamWriter, Long>>> cacheWithTimeout;

  private final BigQueryWriteClient stub;
  private final BigQueryWriteSettings stubSettings;
  private final CleanerThread cleanerThread;

  private StreamCache() throws Exception {
    cacheWithTimeout = new ConcurrentHashMap<>();
    stubSettings = BigQueryWriteSettings.newBuilder().build();
    stub = BigQueryWriteClient.create(stubSettings);
    cleanerThread = new CleanerThread(expireTime.toMillis(), cacheWithTimeout);
    Executors.newSingleThreadExecutor()
        .execute(
            new Runnable() {
              @Override
              public void run() {
                cleanerThread.run();
              }
            });
  }

  public static StreamCache getInstance() throws Exception {
    if (instance == null) {
      instance = new StreamCache();
    }
    return instance;
  }

  StreamWriter CreateNewWriter(String tableName) throws Exception {
    Stream.WriteStream stream =
        Stream.WriteStream.newBuilder().setType(Stream.WriteStream.Type.COMMITTED).build();
    stream =
        stub.createWriteStream(
            Storage.CreateWriteStreamRequest.newBuilder()
                .setParent(tableName)
                .setWriteStream(stream)
                .build());
    LOG.info("Created Write Stream:" + stream.getName());
    return StreamWriter.newBuilder(stream.getName()).build();
  }

  void addWriterToCache(StreamWriter writer) {
    Date date = new Date();
    Pair<StreamWriter, Long> streamEntry = new Pair<>(writer, date.getTime());

    if (!cacheWithTimeout.contains(writer.getTableNameString())) {
      ConcurrentHashMap<String, Pair<StreamWriter, Long>> tableEntry =
          new ConcurrentHashMap<String, Pair<StreamWriter, Long>>();
      tableEntry.put(writer.getStreamNameString(), streamEntry);
      cacheWithTimeout.put(writer.getTableNameString(), tableEntry);
    } else {
      cacheWithTimeout
          .get(writer.getTableNameString())
          .put(writer.getStreamNameString(), streamEntry);
    }
  }

  /**
   * Gets a writer for a given table from global cache.
   *
   * @param tableName
   * @return
   * @throws Exception
   */
  public StreamWriter getWriter(String tableName) throws Exception {
    StreamWriter writer;
    synchronized (cacheWithTimeout) {
      if (cacheWithTimeout.contains(tableName)) {
        Map<String, Pair<StreamWriter, Long>> writersForTable = cacheWithTimeout.get(tableName);
        Preconditions.checkArgument(!writersForTable.isEmpty());
        writer = writersForTable.remove(0).getKey();
        if (writersForTable.isEmpty()) {
          cacheWithTimeout.remove(tableName);
        }
      }
      writer = CreateNewWriter(tableName);
      synchronized (cacheWithTimeout) {
        Date date = new Date();
        Pair<StreamWriter, Long> streamEntry = new Pair<>(writer, date.getTime());
        if (!cacheWithTimeout.contains(tableName)) {
          ConcurrentHashMap<String, Pair<StreamWriter, Long>> tableEntry =
              new ConcurrentHashMap<String, Pair<StreamWriter, Long>>();
          tableEntry.put(writer.getStreamNameString(), streamEntry);
          cacheWithTimeout.put(tableName, tableEntry);
        } else {
          cacheWithTimeout.get(tableName).put(writer.getStreamNameString(), streamEntry);
        }
      }
      return writer;
    }
  }

  /**
   * Returns the writer to the cache.
   *
   * @param writer
   */
  public void returnWriter(StreamWriter writer) {
    synchronized (cacheWithTimeout) {
      addWriterToCache(writer);
    }
  }

  private class CleanerThread extends Thread {
    private long expiryInMillis;
    private ConcurrentHashMap<String, Map<String, Pair<StreamWriter, Long>>> timeMap;

    public CleanerThread(
        long expirationMillis,
        ConcurrentHashMap<String, Map<String, Pair<StreamWriter, Long>>> timeMap) {
      this.expiryInMillis = expirationMillis;
      this.timeMap = timeMap;
    }

    @Override
    public void run() {
      while (true) {
        cleanMap();
        try {
          Thread.sleep(expiryInMillis / 2);
        } catch (InterruptedException ignored) {
        }
      }
    }

    private void cleanMap() {
      long currentTime = new Date().getTime();
      synchronized (timeMap) {
        for (String tableName : timeMap.keySet()) {
          Map<String, Pair<StreamWriter, Long>> tableEntry = timeMap.get(tableName);
          for (String streamName : tableEntry.keySet()) {
            if (currentTime > (tableEntry.get(streamName).getValue() + expiryInMillis)) {
              StreamWriter writer = tableEntry.get(streamName).getKey();
              writer.close();
              tableEntry.remove(streamName);
            }
          }
          if (tableEntry.isEmpty()) {
            timeMap.remove(tableName);
          }
        }
      }
    }
  }
}
