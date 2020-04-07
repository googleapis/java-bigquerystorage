package com.google.cloud.bigquery.storage.v1alpha2;

import com.google.api.core.*;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoRows;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoSchema;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.CreateWriteStreamRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Writer that can help user to write data to BigQuery. This is a simplified version of the Write API.
 * For users writing with COMMITTED stream and don't care about row deduplication, it is recommended to use this Writer.
 *
 * It supports message batching and flow control. It handles stream creation and schema update.
 *
 * <pre>{@code
 * DataProto data1;
 * DirectWriter dw =
 *   DirectWriter.newBuilder("projects/pid/datasets/did/tables/tid", DataProto.GetDescriptor()).build();
 * ApiFuture<Long> response = dw.append({data1});
 * DataProto2 data2; // new data with updated schema
 * dw.updateSchema(DataProto2.GetDescriptor());
 * ApiFuture<Long> response = dw.append({data2});
 * }</pre>
 *
 * <p>{@link DirectWriter} will use the credentials set on the channel, which uses application
 * default credentials through {@link GoogleCredentials#getApplicationDefault} by default.
 */
public class DirectWriter implements AutoCloseable {
  private static final Logger LOG = Logger.getLogger(DirectWriter.class.getName());

  private ProtoSchema userSchema;
  private final StreamWriter writer;
  private final WriterCache writerCache;

  /**
   * Constructor of DirectWriter.
   * @param tableName Name of the table for ingest in format of 'projects/{pid}/datasets/{did}/tables/{tid}'.
   * @param messageDescriptor The descriptor of the input message, to be used to interpret the input messages.
   */
  public DirectWriter(Builder builder) throws Exception {
    userSchema = ProtoSchemaConverter.convert(builder.userSchema);
    writerCache = WriterCache.getInstance();
    writer = writerCache.getWriter(builder.tableName);
  }

  @Override
  public void close() {
    writerCache.returnWriter(writer);
  }

  /**
   * The row is represented in proto buffer messages and it must be compatible to the table's schema in BigQuery.
   *
   * @param protoRows rows in proto buffer format. They must be compatible with the schema set on the writer.
   * @return A future that contains the offset at which the append happened. Only when the future returns with valid
   *         offset, then the append actually happened.
   * @throws Exception
   */
  public ApiFuture<Long> append(List<MessageLite> protoRows) throws Exception {
    ProtoRows.Builder rowsBuilder = ProtoRows.newBuilder();
    Descriptors.Descriptor descriptor = null;
    for (MessageLite protoRow : protoRows) {
      rowsBuilder.addSerializedRows(protoRow.toByteString());
    }

    AppendRowsRequest.ProtoData.Builder data = AppendRowsRequest.ProtoData.newBuilder();
    data.setWriterSchema(userSchema);
    data.setRows(rowsBuilder.build());

    return ApiFutures.<Storage.AppendRowsResponse, Long>transform(
        writer.append(AppendRowsRequest.newBuilder().setProtoRows(data.build()).build()),
        new ApiFunction<Storage.AppendRowsResponse, Long>(){
          @Override
          public Long apply(Storage.AppendRowsResponse appendRowsResponse) {
            return Long.valueOf(appendRowsResponse.getOffset());
          }
        },
        MoreExecutors.directExecutor());
  }

  /**
   * After this call, messages will be appended using the new schema. Note that user is responsible to keep
   * the schema here in sync with the table's actual schema. If they ran out of date, the append may fail.
   * User can keep trying, until the table's new schema is picked up.
   * @param newSchema
   * @throws IOException
   * @throws InterruptedException
   */
  public void updateSchema(Descriptors.Descriptor newSchema) throws IOException, InterruptedException {
    Preconditions.checkArgument(newSchema != null);
    writer.refreshAppend();
    userSchema = ProtoSchemaConverter.convert(newSchema);
  }

  public static DirectWriter.Builder newBuilder(String tableName, Descriptors.Descriptor userSchema) {
    return new DirectWriter.Builder(tableName, userSchema);
  }

  /** A builder of {@link DirectWriter}s. */
  public static final class Builder {
    private final String tableName;
    private final Descriptors.Descriptor userSchema;

    // Connection settings
    private static final int THREADS_PER_CPU = 5;
    ExecutorProvider executorProvider =
        InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(THREADS_PER_CPU * Runtime.getRuntime().availableProcessors())
        .build();
    private CredentialsProvider credentialsProvider =
        BigQueryWriteSettings.defaultCredentialsProviderBuilder().build();
    TransportChannelProvider channelProvider =
        BigQueryWriteSettings.defaultGrpcTransportProviderBuilder().setChannelsPerCpu(1).build();

    // {@code StreamWriter} settings, if null, default to the settings on {@code StreamWriter}.
    RetrySettings retrySettings = null;
    BatchingSettings batchingSettings = null;

    private Builder(String tableName, Descriptors.Descriptor userSchema) {
      this.tableName = Preconditions.checkNotNull(tableName);
      this.userSchema = Preconditions.checkNotNull(userSchema);
    }

    /**
     * {@code ChannelProvider} to use to create Channels, which must point at Cloud BigQuery Storage
     * API endpoint.
     *
     * <p>For performance, this client benefits from having multiple underlying connections. See
     * {@link com.google.api.gax.grpc.InstantiatingGrpcChannelProvider.Builder#setPoolSize(int)}.
     */
    public Builder setChannelProvider(TransportChannelProvider channelProvider) {
      this.channelProvider = Preconditions.checkNotNull(channelProvider);
      return this;
    }

    /** {@code CredentialsProvider} to use to create Credentials to authenticate calls. */
    public Builder setCredentialsProvider(CredentialsProvider credentialsProvider) {
      this.credentialsProvider = Preconditions.checkNotNull(credentialsProvider);
      return this;
    }

    /** Sets the {@code BatchSettings} on the writer. */
    public Builder setBatchingSettings(BatchingSettings batchingSettings) {
      this.batchingSettings = Preconditions.checkNotNull(batchingSettings);
      return this;
    }

    /** Sets the {@code RetrySettings} on the writer. */
    public Builder setRetrySettings(RetrySettings retrySettings) {
      this.retrySettings = Preconditions.checkNotNull(retrySettings);
      return this;
    }

    /** Gives the ability to set a custom executor to be used by the library. */
    public Builder setExecutorProvider(ExecutorProvider executorProvider) {
      this.executorProvider = Preconditions.checkNotNull(executorProvider);
      return this;
    }

    /** Builds the {@code DirectWriter}. */
    public DirectWriter build() throws Exception {
      return new DirectWriter(this);
    }
  }
}
