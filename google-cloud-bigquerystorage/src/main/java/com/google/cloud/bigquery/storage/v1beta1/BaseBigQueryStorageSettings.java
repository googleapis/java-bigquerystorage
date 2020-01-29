/*
 * Copyright 2019 Google LLC
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
import com.google.cloud.bigquery.storage.v1beta1.stub.BigQueryStorageStubSettings;
import com.google.protobuf.Empty;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * Settings class to configure an instance of {@link BaseBigQueryStorageClient}.
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
 * build() is called, the tree of builders is called to create the complete settings object.
 *
 * <p>For example, to set the total timeout of createReadSession to 30 seconds:
 *
 * <pre>
 * <code>
 * BaseBigQueryStorageSettings.Builder baseBigQueryStorageSettingsBuilder =
 *     BaseBigQueryStorageSettings.newBuilder();
 * baseBigQueryStorageSettingsBuilder.createReadSessionSettings().getRetrySettings().toBuilder()
 *     .setTotalTimeout(Duration.ofSeconds(30));
 * BaseBigQueryStorageSettings baseBigQueryStorageSettings = baseBigQueryStorageSettingsBuilder.build();
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
@BetaApi
public class BaseBigQueryStorageSettings extends ClientSettings<BaseBigQueryStorageSettings> {
  /** Returns the object with the settings used for calls to createReadSession. */
  public UnaryCallSettings<CreateReadSessionRequest, ReadSession> createReadSessionSettings() {
    return ((BigQueryStorageStubSettings) getStubSettings()).createReadSessionSettings();
  }

  /** Returns the object with the settings used for calls to readRows. */
  public ServerStreamingCallSettings<ReadRowsRequest, ReadRowsResponse> readRowsSettings() {
    return ((BigQueryStorageStubSettings) getStubSettings()).readRowsSettings();
  }

  /** Returns the object with the settings used for calls to batchCreateReadSessionStreams. */
  public UnaryCallSettings<
          BatchCreateReadSessionStreamsRequest, BatchCreateReadSessionStreamsResponse>
      batchCreateReadSessionStreamsSettings() {
    return ((BigQueryStorageStubSettings) getStubSettings())
        .batchCreateReadSessionStreamsSettings();
  }

  /** Returns the object with the settings used for calls to finalizeStream. */
  public UnaryCallSettings<FinalizeStreamRequest, Empty> finalizeStreamSettings() {
    return ((BigQueryStorageStubSettings) getStubSettings()).finalizeStreamSettings();
  }

  /** Returns the object with the settings used for calls to splitReadStream. */
  public UnaryCallSettings<SplitReadStreamRequest, SplitReadStreamResponse>
      splitReadStreamSettings() {
    return ((BigQueryStorageStubSettings) getStubSettings()).splitReadStreamSettings();
  }

  public static final BaseBigQueryStorageSettings create(BigQueryStorageStubSettings stub)
      throws IOException {
    return new BaseBigQueryStorageSettings.Builder(stub.toBuilder()).build();
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return BigQueryStorageStubSettings.defaultExecutorProviderBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return BigQueryStorageStubSettings.getDefaultEndpoint();
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return BigQueryStorageStubSettings.getDefaultServiceScopes();
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return BigQueryStorageStubSettings.defaultCredentialsProviderBuilder();
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return BigQueryStorageStubSettings.defaultGrpcTransportProviderBuilder();
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return BigQueryStorageStubSettings.defaultTransportChannelProvider();
  }

  @BetaApi("The surface for customizing headers is not stable yet and may change in the future.")
  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return BigQueryStorageStubSettings.defaultApiClientHeaderProviderBuilder();
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

  protected BaseBigQueryStorageSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);
  }

  /** Builder for BaseBigQueryStorageSettings. */
  public static class Builder extends ClientSettings.Builder<BaseBigQueryStorageSettings, Builder> {
    protected Builder() throws IOException {
      this((ClientContext) null);
    }

    protected Builder(ClientContext clientContext) {
      super(BigQueryStorageStubSettings.newBuilder(clientContext));
    }

    private static Builder createDefault() {
      return new Builder(BigQueryStorageStubSettings.newBuilder());
    }

    protected Builder(BaseBigQueryStorageSettings settings) {
      super(settings.getStubSettings().toBuilder());
    }

    protected Builder(BigQueryStorageStubSettings.Builder stubSettings) {
      super(stubSettings);
    }

    public BigQueryStorageStubSettings.Builder getStubSettingsBuilder() {
      return ((BigQueryStorageStubSettings.Builder) getStubSettings());
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
    public BaseBigQueryStorageSettings build() throws IOException {
      return new BaseBigQueryStorageSettings(this);
    }
  }
}
