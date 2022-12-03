package com.google.cloud.bigquery.storage.v1;

import com.google.api.core.ApiFuture;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.IOException;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;

/**
 * A SimpleWriter provides a way to write to BigQuery without a Stream. 
 */
public class SimpleWriter {
  BigQueryWriteClient client;
  LoadingCache<String, JsonStreamWriter> writerCache;

  /**
   * Creates a SimpleWriter instance with provided BigQueryWriteClient. This object should be
   * reused to do as many append calls as possible.
   * @param client
   * @throws IOException
   */
  public SimpleWriter(BigQueryWriteClient client) throws IOException {
    new SimpleWriter(client, "maximumSize=100,expireAfterWrite=60m");
  }

  /**
   * Creates a SimpleWriter with custom caching policy for JsonStreamWriter.
   * @param client
   * @param cacheSpec
   * @throws IOException
   */
  public SimpleWriter(BigQueryWriteClient client, String cacheSpec) throws IOException {
      this.client = Preconditions.checkNotNull(client);
      LoadingCache<String, JsonStreamWriter> writerCache = CacheBuilder.from(cacheSpec)
          .removalListener(new RemovalListener<String, JsonStreamWriter>(){
            public void onRemoval(RemovalNotification<String, JsonStreamWriter> notification) {
              notification.getValue().close();
            }})
          .build(
              new CacheLoader<String, JsonStreamWriter>() {
                public JsonStreamWriter load(String key)
                    throws DescriptorValidationException, IOException, InterruptedException {
                  return JsonStreamWriter.newBuilder(key, client)
                      .setEnableConnectionPool(true).build();
                }
              });
  }

  /**
   * Appends data to a BigQuery Table.
   *
   * @param rows the rows in serialized format to write to BigQuery.
   * @return the append response wrapped in a future.
   */
  public ApiFuture<AppendRowsResponse> append(String tableName, JSONArray data)
      throws ExecutionException, DescriptorValidationException, IOException {
    return writerCache.get(tableName).append(data);
  }
}
