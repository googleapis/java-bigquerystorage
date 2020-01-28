/*
 * Copyright 2020 Google LLC
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
package com.google.cloud.bigquery.storage.v1beta2.stub;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ServerStreamingCallSettings;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.api.gax.rpc.WatchdogProvider;
import com.google.cloud.bigquery.storage.v1beta2.CreateReadSessionRequest;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.ReadSession;
import com.google.cloud.bigquery.storage.v1beta2.SplitReadStreamRequest;
import com.google.cloud.bigquery.storage.v1beta2.SplitReadStreamResponse;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.threeten.bp.Duration;

@RunWith(JUnit4.class)
public class EnhancedBigQueryReadStubSettingsTest {

  @Test
  public void testSettingsArePreserved() {
    String endpoint = "some.other.host:123";
    CredentialsProvider credentialsProvider = Mockito.mock(CredentialsProvider.class);
    Duration watchdogInterval = Duration.ofSeconds(12);
    WatchdogProvider watchdogProvider = Mockito.mock(WatchdogProvider.class);

    EnhancedBigQueryReadStubSettings.Builder builder =
        EnhancedBigQueryReadStubSettings.newBuilder()
            .setEndpoint(endpoint)
            .setCredentialsProvider(credentialsProvider)
            .setStreamWatchdogCheckInterval(watchdogInterval)
            .setStreamWatchdogProvider(watchdogProvider);

    verifyBuilder(builder, endpoint, credentialsProvider, watchdogInterval, watchdogProvider);

    verifySettings(
        builder.build(), endpoint, credentialsProvider, watchdogInterval, watchdogProvider);

    verifyBuilder(
        builder.build().toBuilder(),
        endpoint,
        credentialsProvider,
        watchdogInterval,
        watchdogProvider);
  }

  private void verifyBuilder(
      EnhancedBigQueryReadStubSettings.Builder builder,
      String endpoint,
      CredentialsProvider credentialsProvider,
      Duration watchdogInterval,
      WatchdogProvider watchdogProvider) {
    assertThat(builder.getEndpoint()).isEqualTo(endpoint);
    assertThat(builder.getCredentialsProvider()).isEqualTo(credentialsProvider);
    assertThat(builder.getStreamWatchdogCheckInterval()).isEqualTo(watchdogInterval);
    assertThat(builder.getStreamWatchdogProvider()).isEqualTo(watchdogProvider);

    InstantiatingGrpcChannelProvider channelProvider =
        (InstantiatingGrpcChannelProvider) builder.getTransportChannelProvider();
    assertThat(channelProvider.toBuilder().getMaxInboundMessageSize()).isEqualTo(Integer.MAX_VALUE);
  }

  private void verifySettings(
      EnhancedBigQueryReadStubSettings settings,
      String endpoint,
      CredentialsProvider credentialsProvider,
      Duration watchdogInterval,
      WatchdogProvider watchdogProvider) {
    assertThat(settings.getEndpoint()).isEqualTo(endpoint);
    assertThat(settings.getCredentialsProvider()).isEqualTo(credentialsProvider);
    assertThat(settings.getStreamWatchdogCheckInterval()).isEqualTo(watchdogInterval);
    assertThat(settings.getStreamWatchdogProvider()).isEqualTo(watchdogProvider);

    InstantiatingGrpcChannelProvider channelProvider =
        (InstantiatingGrpcChannelProvider) settings.getTransportChannelProvider();
    assertThat(channelProvider.toBuilder().getMaxInboundMessageSize()).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void testCreateReadSessionSettings() {
    UnaryCallSettings.Builder<CreateReadSessionRequest, ReadSession> builder =
        EnhancedBigQueryReadStubSettings.newBuilder().createReadSessionSettings();
    verifyRetrySettings(builder.getRetryableCodes(), builder.getRetrySettings());
  }

  @Test
  public void testReadRowsSettings() {
    ServerStreamingCallSettings.Builder<ReadRowsRequest, ReadRowsResponse> builder =
        EnhancedBigQueryReadStubSettings.newBuilder().readRowsSettings();
    assertThat(builder.getRetryableCodes()).contains(Code.UNAVAILABLE);
    RetrySettings retrySettings = builder.getRetrySettings();
    assertThat(retrySettings.getInitialRetryDelay()).isEqualTo(Duration.ofMillis(100L));
    assertThat(retrySettings.getRetryDelayMultiplier()).isWithin(1e-6).of(1.3);
    assertThat(retrySettings.getMaxRetryDelay()).isEqualTo(Duration.ofMinutes(1L));
    assertThat(retrySettings.getInitialRpcTimeout()).isEqualTo(Duration.ofDays(1L));
    assertThat(retrySettings.getRpcTimeoutMultiplier()).isWithin(1e-6).of(1.0);
    assertThat(retrySettings.getMaxRpcTimeout()).isEqualTo(Duration.ofDays(1L));
    assertThat(retrySettings.getTotalTimeout()).isEqualTo(Duration.ofDays(1L));
    assertThat(builder.getIdleTimeout()).isEqualTo(Duration.ZERO);
  }

  @Test
  public void testSplitReadStreamSettings() {
    UnaryCallSettings.Builder<SplitReadStreamRequest, SplitReadStreamResponse> builder =
        EnhancedBigQueryReadStubSettings.newBuilder().splitReadStreamSettings();
    verifyRetrySettings(builder.getRetryableCodes(), builder.getRetrySettings());
  }

  private void verifyRetrySettings(Set<Code> retryCodes, RetrySettings retrySettings) {
    assertThat(retryCodes).contains(Code.UNAVAILABLE);
    assertThat(retrySettings.getTotalTimeout()).isGreaterThan(Duration.ZERO);
    assertThat(retrySettings.getInitialRetryDelay()).isGreaterThan(Duration.ZERO);
    assertThat(retrySettings.getRetryDelayMultiplier()).isAtLeast(1.0);
    assertThat(retrySettings.getMaxRetryDelay()).isGreaterThan(Duration.ZERO);
    assertThat(retrySettings.getInitialRpcTimeout()).isGreaterThan(Duration.ZERO);
    assertThat(retrySettings.getRpcTimeoutMultiplier()).isAtLeast(1.0);
    assertThat(retrySettings.getMaxRpcTimeout()).isGreaterThan(Duration.ZERO);
  }
}
