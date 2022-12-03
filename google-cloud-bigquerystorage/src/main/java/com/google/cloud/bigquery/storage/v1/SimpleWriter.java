package com.google.cloud.bigquery.storage.v1;

import com.google.api.core.ApiFuture;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;

/**
 * A SimpleWriter provides a way to write to BigQuery without a Stream. Underneath, it maintains a
 * cache of JsonStreamWriter based on talbe name.
 */
public class SimpleWriter {
  BigQueryWriteClient client;
  LoadingCache<String, JsonStreamWriter> writerCache;
  String traceId;

  /** Constructs a new {@link SimpleWriter.Builder} using the given bigquery write client. */
  public static SimpleWriter.Builder newBuilder(BigQueryWriteClient client) {
    return new SimpleWriter.Builder(client);
  }

  private SimpleWriter(SimpleWriter.Builder builder) throws IOException {
    this.client = builder.client;
    this.traceId = builder.traceId;
    this.writerCache =
        CacheBuilder.from(builder.cacheSpec)
            .removalListener(
                new RemovalListener<String, JsonStreamWriter>() {
                  public void onRemoval(
                      RemovalNotification<String, JsonStreamWriter> notification) {
                    notification.getValue().close();
                  }
                })
            .build(
                new CacheLoader<String, JsonStreamWriter>() {
                  public JsonStreamWriter load(String key)
                      throws DescriptorValidationException, IOException, InterruptedException {
                    return JsonStreamWriter.newBuilder(key, client)
                        .setEnableConnectionPool(true)
                        .setTraceId(traceId)
                        .build();
                  }
                });
  }

  public static final class Builder {
    BigQueryWriteClient client;
    String cacheSpec = "maximumSize=100,expireAfterWrite=60m";
    String traceId = "SimpleWriter:null";

    private Builder(BigQueryWriteClient client) {
      this.client = Preconditions.checkNotNull(client);
    }

    /** CacheSpec for the JsonStreamWriter cache. */
    public SimpleWriter.Builder setCacheSpec(String cacheSpec) {
      this.cacheSpec = cacheSpec;
      return this;
    }

    /** One time trace id to apply to all writes */
    public SimpleWriter.Builder setTraceId(String traceId) {
      this.traceId = "SimpleWriter_" + traceId;
      return this;
    }
  }

  /**
   * Appends data to a BigQuery Table.
   * Rows will appear AT_LEAST_ONCE in BigQuery.
   *
   * @param rows the rows in serialized format to write to BigQuery.
   * @return the append response wrapped in a future.
   */
  public ApiFuture<AppendRowsResponse> append(String tableName, JSONArray data)
      throws ExecutionException, DescriptorValidationException, IOException {
    return writerCache.get(tableName).append(data);
  }
}
