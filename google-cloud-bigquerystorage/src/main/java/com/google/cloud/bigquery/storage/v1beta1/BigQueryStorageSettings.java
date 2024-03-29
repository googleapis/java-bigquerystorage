/*
 * Copyright 2018 Google LLC
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
package com.google.cloud.bigquery.storage.v1beta1;

import com.google.api.core.ApiFunction;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.ServerStreamingCallSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.cloud.bigquery.storage.v1beta1.Storage.BatchCreateReadSessionStreamsRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.BatchCreateReadSessionStreamsResponse;
import com.google.cloud.bigquery.storage.v1beta1.Storage.CreateReadSessionRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.FinalizeStreamRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadRowsResponse;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadSession;
import com.google.cloud.bigquery.storage.v1beta1.Storage.SplitReadStreamRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.SplitReadStreamResponse;
import com.google.cloud.bigquery.storage.v1beta1.stub.EnhancedBigQueryStorageStubSettings;
import com.google.protobuf.Empty;
import io.grpc.Metadata;
import io.grpc.Status;
import java.io.IOException;
import java.util.List;

/**
 * Settings class to configure an instance of {@link BigQueryStorageClient}.
 *
 * <p>The default instance has everything set to sensible defaults:
 *
 * <ul>
 *   <li>The default service address (bigquerystorage.googleapis.com) and default port (443) are
 *       used.
 *   <li>Credentials are acquired automatically through Application Default Credentials.
 *   <li>Retries are configured for idempotent methods but not for non-idempotent methods.
 * </ul>
 *
 * <p>The builder of this class is recursive, so contained classes are themselves builders. When
 * build() is called, the tree of builders is called to create the complete settings object. For
 * example, to set the total timeout of createReadSession to 30 seconds:
 *
 * <pre>
 * <code>
 * BigQueryStorageSettings.Builder settingsBuilder = BaseBigQueryStorageSettings.newBuilder();
 * settingsBuilder.createReadSessionSettings().getRetrySettings().toBuilder()
 *     .setTotalTimeout(Duration.ofSeconds(30));
 * BaseBigQueryStorageSettings settings = settingsBuilder.build();
 * </code>
 * </pre>
 */
@BetaApi
public class BigQueryStorageSettings extends ClientSettings<BigQueryStorageSettings> {

  /** Returns the object with the settings used for calls to createReadSession. */
  public UnaryCallSettings<CreateReadSessionRequest, ReadSession> createReadSessionSettings() {
    return getTypedStubSettings().createReadSessionSettings();
  }

  /** Returns the object with the settings used for calls to readRows. */
  public ServerStreamingCallSettings<ReadRowsRequest, ReadRowsResponse> readRowsSettings() {
    return getTypedStubSettings().readRowsSettings();
  }

  public static interface RetryAttemptListener {
    public void onRetryAttempt(Status prevStatus, Metadata prevMetadata);
  }

  private RetryAttemptListener readRowsRetryAttemptListener = null;

  /**
   * If a non null readRowsRetryAttemptListener is provided, client will call onRetryAttempt
   * function before a failed ReadRows request is retried. This can be used as negative feedback
   * mechanism for future decision to split read streams because some retried failures are due to
   * resource exhaustion that increased parallelism only makes it worse.
   */
  public void setReadRowsRetryAttemptListener(RetryAttemptListener readRowsRetryAttemptListener) {
    this.readRowsRetryAttemptListener = readRowsRetryAttemptListener;
  }

  public RetryAttemptListener getReadRowsRetryAttemptListener() {
    return readRowsRetryAttemptListener;
  }

  /** Returns the object with the settings used for calls to batchCreateReadSessionStreams. */
  public UnaryCallSettings<
          BatchCreateReadSessionStreamsRequest, BatchCreateReadSessionStreamsResponse>
      batchCreateReadSessionStreamsSettings() {
    return getTypedStubSettings().batchCreateReadSessionStreamsSettings();
  }

  /** Returns the object with the settings used for calls to finalizeStream. */
  public UnaryCallSettings<FinalizeStreamRequest, Empty> finalizeStreamSettings() {
    return getTypedStubSettings().finalizeStreamSettings();
  }

  /** Returns the object with the settings used for calls to splitReadStream. */
  public UnaryCallSettings<SplitReadStreamRequest, SplitReadStreamResponse>
      splitReadStreamSettings() {
    return getTypedStubSettings().splitReadStreamSettings();
  }

  EnhancedBigQueryStorageStubSettings getTypedStubSettings() {
    return (EnhancedBigQueryStorageStubSettings) getStubSettings();
  }

  public static final BigQueryStorageSettings create(EnhancedBigQueryStorageStubSettings settings)
      throws IOException {
    return new BigQueryStorageSettings.Builder(settings.toBuilder()).build();
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return EnhancedBigQueryStorageStubSettings.defaultExecutorProviderBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return EnhancedBigQueryStorageStubSettings.getDefaultEndpoint();
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return EnhancedBigQueryStorageStubSettings.getDefaultServiceScopes();
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return EnhancedBigQueryStorageStubSettings.defaultCredentialsProviderBuilder();
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return EnhancedBigQueryStorageStubSettings.defaultGrpcTransportProviderBuilder();
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return EnhancedBigQueryStorageStubSettings.defaultTransportChannelProvider();
  }

  @BetaApi("The surface for customizing headers is not stable yet and may change in the future.")
  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return EnhancedBigQueryStorageStubSettings.defaultApiClientHeaderProviderBuilder();
  }

  /** Returns a new builder for this class. */
  public static Builder newBuilder() {
    return Builder.createDefault();
  }

  /** Returns a new builder for this class. */
  public static Builder newBuilder(ClientContext clientContext) {
    return new Builder(clientContext);
  }

  /** Returns a builder containing all the values of this settings class. */
  public Builder toBuilder() {
    return new Builder(this);
  }

  protected BigQueryStorageSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);
  }

  /** Builder for BigQueryStorageSettings. */
  public static class Builder extends ClientSettings.Builder<BigQueryStorageSettings, Builder> {
    protected Builder() throws IOException {
      this((ClientContext) null);
    }

    protected Builder(ClientContext clientContext) {
      super(EnhancedBigQueryStorageStubSettings.newBuilder(clientContext));
    }

    private static Builder createDefault() {
      return new Builder(EnhancedBigQueryStorageStubSettings.newBuilder());
    }

    protected Builder(BigQueryStorageSettings settings) {
      super(settings.getStubSettings().toBuilder());
    }

    protected Builder(EnhancedBigQueryStorageStubSettings.Builder stubSettings) {
      super(stubSettings);
    }

    public EnhancedBigQueryStorageStubSettings.Builder getStubSettingsBuilder() {
      return ((EnhancedBigQueryStorageStubSettings.Builder) getStubSettings());
    }

    // NEXT_MAJOR_VER: remove 'throws Exception'
    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) throws Exception {
      super.applyToAllUnaryMethods(
          getStubSettingsBuilder().unaryMethodSettingsBuilders(), settingsUpdater);
      return this;
    }

    private RetryAttemptListener readRowsRetryAttemptListener = null;

    public Builder setReadRowsRetryAttemptListener(
        RetryAttemptListener readRowsRetryAttemptListener) {
      this.readRowsRetryAttemptListener = readRowsRetryAttemptListener;
      return this;
    }

    /** Returns the builder for the settings used for calls to createReadSession. */
    public UnaryCallSettings.Builder<CreateReadSessionRequest, ReadSession>
        createReadSessionSettings() {
      return getStubSettingsBuilder().createReadSessionSettings();
    }

    /** Returns the builder for the settings used for calls to readRows. */
    public ServerStreamingCallSettings.Builder<ReadRowsRequest, ReadRowsResponse>
        readRowsSettings() {
      return getStubSettingsBuilder().readRowsSettings();
    }

    /** Returns the builder for the settings used for calls to batchCreateReadSessionStreams. */
    public UnaryCallSettings.Builder<
            BatchCreateReadSessionStreamsRequest, BatchCreateReadSessionStreamsResponse>
        batchCreateReadSessionStreamsSettings() {
      return getStubSettingsBuilder().batchCreateReadSessionStreamsSettings();
    }

    /** Returns the builder for the settings used for calls to finalizeStream. */
    public UnaryCallSettings.Builder<FinalizeStreamRequest, Empty> finalizeStreamSettings() {
      return getStubSettingsBuilder().finalizeStreamSettings();
    }

    /** Returns the builder for the settings used for calls to splitReadStream. */
    public UnaryCallSettings.Builder<SplitReadStreamRequest, SplitReadStreamResponse>
        splitReadStreamSettings() {
      return getStubSettingsBuilder().splitReadStreamSettings();
    }

    @Override
    public BigQueryStorageSettings build() throws IOException {
      BigQueryStorageSettings settings = new BigQueryStorageSettings(this);
      settings.setReadRowsRetryAttemptListener(readRowsRetryAttemptListener);
      return settings;
    }
  }
}
