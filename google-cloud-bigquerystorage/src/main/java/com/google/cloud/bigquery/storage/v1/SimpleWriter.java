package com.google.cloud.bigquery.storage.v1;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController.LimitExceededBehavior;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;

/**
 * A SimpleWriter provides a way to write to BigQuery in a unary fashion. Underneath, it still uses
 * the streaming API through JsonStreamWriter. There is a cache that manages different table writes.
 * Default max table destination cached is 100, you can adjust it through the Builder.
 *
 * <p>Currently, it only supports AT_LEAST_ONCE writes.
 *
 * <p>This class is still in development, DO NO USE IT.
 *
 * <p>TODOS: 1. Make the class thread safe 2. Handle failed writer case
 */
public class SimpleWriter {
  private BigQueryWriteClient client;
  private LoadingCache<String, JsonStreamWriter> writerCache;
  private String traceId;
  private static String tableNamePatternString = "projects/[^/]+/datasets/[^/]+/tables/[^/]+";
  private static Pattern tableNamePattern = Pattern.compile(tableNamePatternString);
  private boolean ignoreUnkownFields;

  /** Constructs a new {@link SimpleWriter.Builder} using the given bigquery write client. */
  public static SimpleWriter.Builder newBuilder(BigQueryWriteClient client) {
    return new SimpleWriter.Builder(client);
  }

  private SimpleWriter(SimpleWriter.Builder builder) {
    this.client = builder.client;
    this.traceId = builder.traceId;
    this.ignoreUnkownFields = builder.ignoreUnknownField;
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
                        .setIgnoreUnknownFields(ignoreUnkownFields)
                        .setFlowControlSettings(
                            FlowControlSettings.newBuilder()
                                .setLimitExceededBehavior(LimitExceededBehavior.ThrowException)
                                .build())
                        .build();
                  }
                });
  }

  public static final class Builder {
    BigQueryWriteClient client;
    // Cache expriration time is set to the same as connection timeout time. After a connection
    // is cut, we might be missing schema updates to the object, so we will just let the cache
    // expire so that a fresh table schema will be retrieved.
    String cacheSpec = "maximumSize=100,expireAfterWrite=10m";
    String traceId = "SimpleWriter:null";
    boolean ignoreUnknownField = false;

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

    /**
     * One time set ignoreUnknown field. If true, then if the input has unknown fields to bigquery
     * table, the append will not fail. By default, the setting is false.
     */
    public SimpleWriter.Builder setIgnoreUnknownField(boolean ignoreUnknownField) {
      this.ignoreUnknownField = ignoreUnknownField;
      return this;
    }

    public SimpleWriter build() {
      return new SimpleWriter(this);
    }
  }

  /**
   * Appends data to a BigQuery Table. Rows will appear AT_LEAST_ONCE in BigQuery.
   *
   * @param rows the rows in serialized format to write to BigQuery.
   * @return the append response wrapped in a future.
   */
  public ApiFuture<AppendRowsResponse> append(String tableName, JSONArray data)
      throws ExecutionException, DescriptorValidationException, IOException {
    Matcher tableNameMatcher = tableNamePattern.matcher(tableName);
    if (!tableNameMatcher.matches()) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
    return writerCache.get(tableName).append(data);
  }
}
