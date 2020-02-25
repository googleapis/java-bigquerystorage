/*
 * Copyright 2016 Google LLC
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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.rpc.DataLossException;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessServerBuilder;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Duration;

@RunWith(JUnit4.class)
public class PublisherImplTest {

	private static final String TEST_STREAM = "";

	private static final ExecutorProvider SINGLE_THREAD_EXECUTOR =
			InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build();

	private static final TransportChannelProvider TEST_CHANNEL_PROVIDER =
			LocalChannelProvider.create("test-server");

	private FakeScheduledExecutorService fakeExecutor;

	private FakeBigQueryWriteImpl testBigQueryWriteImpl;

	private Server testServer;

	@Before
	public void setUp() throws Exception {
		testBigQueryWriteImpl = new FakeBigQueryWriteImpl();

		InProcessServerBuilder serverBuilder = InProcessServerBuilder.forName("test-server");
		serverBuilder.addService(testBigQueryWriteImpl);
		testServer = serverBuilder.build();
		testServer.start();

		fakeExecutor = new FakeScheduledExecutorService();
	}

	@After
	public void tearDown() throws Exception {
		testServer.shutdownNow().awaitTermination();
	}

	private ApiFuture<AppendRowsResponse> sendTestMessage(StreamWriter writer, String data) {
		return writer.append(
				AppendRowsRequest.newBuilder().setData(ByteString.copyFromUtf8(data)).build());
	}

	@Test
	public void testAppendByDuration() throws Exception {
		StreamWriter writer =
				getTestStreamWriterBuilder()
						// To demonstrate that reaching duration will trigger append
						.setBatchingSettings(
								StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
										.toBuilder()
										.setDelayThreshold(Duration.ofSeconds(5))
										.setElementCountThreshold(10L)
										.build())
						.build();

		testBigQueryWriteImpl.addResponse(
				Storage.AppendRowsResponse.newBuilder().build());

		ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, "A");
		ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, "B");

		assertFalse(appendFuture1.isDone());
		assertFalse(appendFuture2.isDone());

		fakeExecutor.advanceTime(Duration.ofSeconds(10));

		assertEquals("1", appendFuture1.get());
		assertEquals("2", appendFuture2.get());

		assertEquals(2, testBigQueryWriteImpl.getCapturedRequests().get(0).getOffset());
		writer.shutdown();
		writer.awaitTermination(1, TimeUnit.MINUTES);
	}

	@Test
	public void testAppendByNumBatchedMessages() throws Exception {
		StreamWriter writer =
				getTestStreamWriterBuilder()
						.setBatchingSettings(
								StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
										.toBuilder()
										.setElementCountThreshold(2L)
										.setDelayThreshold(Duration.ofSeconds(100))
										.build())
						.build();

		testBigQueryWriteImpl
				.addResponse(AppendRowsResponse.newBuilder().setOffset(0))
				.addResponse(AppendRowsResponse.newBuilder().setOffset(2));

		ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, "A");
		ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, "B");
		ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, "C");

		// Note we are not advancing time but message should still get published

		assertEquals(1L, appendFuture1.get().getOffset());
		assertEquals(2L, appendFuture2.get().getOffset());

		assertFalse(appendFuture3.isDone());

		ApiFuture<AppendRowsResponse> appendFuture4 =
				writer.append(AppendRowsRequest.newBuilder().setData(ByteString.copyFromUtf8("D")).build());

		assertEquals(3L, appendFuture3.get().getOffset());
		assertEquals(4L, appendFuture4.get().getOffset());

		assertEquals(2,
				testBigQueryWriteImpl.getCapturedRequests().get(0).getProtoRows().getRows().getSerializedRowsCount());
		assertEquals(2,
				testBigQueryWriteImpl.getCapturedRequests().get(1).getProtoRows().getRows().getSerializedRowsCount());
		writer.shutdown();
		writer.awaitTermination(1, TimeUnit.MINUTES);
	}

	@Test
	public void testSingleAppendByNumBytes() throws Exception {
		StreamWriter writer =
				getTestStreamWriterBuilder()
						.setBatchingSettings(
								StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
										.toBuilder()
										.setElementCountThreshold(2L)
										.setDelayThreshold(Duration.ofSeconds(100))
										.build())
						.build();

		testBigQueryWriteImpl
				.addResponse(AppendRowsResponse.newBuilder().setOffset(0))
				.addResponse(AppendRowsResponse.newBuilder().setOffset(2));

		ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(publisher, "A");
		ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(publisher, "B");
		ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(publisher, "C");

		// Note we are not advancing time but message should still get published

		assertEquals("1", appendFuture1.get());
		assertEquals("2", appendFuture2.get());
		assertFalse(appendFuture3.isDone());

		ApiFuture<AppendRowsResponse> appendFuture4 = sendTestMessage(publisher, "D");
		assertEquals("3", appendFuture3.get());
		assertEquals("4", appendFuture4.get());

		assertEquals(2, testBigQueryWriteImpl.getCapturedRequests().size());
		writer.shutdown();
		writer.awaitTermination(1, TimeUnit.MINUTES);
	}

	@Test
	public void testPublishByShutdown() throws Exception {
		StreamWriter publisher =
				getTestStreamWriterBuilder()
						.setBatchingSettings(
								StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
										.toBuilder()
										.setDelayThreshold(Duration.ofSeconds(100))
										.setElementCountThreshold(10L)
										.build())
						.build();

		testBigQueryWriteImpl.addResponse(
				AppendRowsResponse.newBuilder().addMessageIds("1").addMessageIds("2"));

		ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(publisher, "A");
		ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(publisher, "B");

		// Note we are not advancing time or reaching the count threshold but messages should
		// still get published by call to shutdown

		publisher.shutdown();
		publisher.awaitTermination(1, TimeUnit.MINUTES);

		// Verify the publishes completed
		assertTrue(appendFuture1.isDone());
		assertTrue(appendFuture2.isDone());
		assertEquals("1", appendFuture1.get());
		assertEquals("2", appendFuture2.get());
	}

	@Test
	public void testPublishMixedSizeAndDuration() throws Exception {
		StreamWriter writer =
				getTestStreamWriterBuilder()
						// To demonstrate that reaching duration will trigger publish
						.setBatchingSettings(
								StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
										.toBuilder()
										.setElementCountThreshold(2L)
										.setDelayThreshold(Duration.ofSeconds(5))
										.build())
						.build();

		testBigQueryWriteImpl.addResponse(
				AppendRowsResponse.newBuilder().setOffset(1L));
		testBigQueryWriteImpl.addResponse(AppendRowsResponse.newBuilder().setOffset(3L));

		ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, "A");

		fakeExecutor.advanceTime(Duration.ofSeconds(2));
		assertFalse(appendFuture1.isDone());

		ApiFuture<AppendRowsResponse> appendFuture2 = sendTestMessage(writer, "B");

		// Publishing triggered by batch size
		assertEquals("1", appendFuture1.get());
		assertEquals("2", appendFuture2.get());

		ApiFuture<AppendRowsResponse> appendFuture3 = sendTestMessage(writer, "C");

		assertFalse(appendFuture3.isDone());

		// Publishing triggered by time
		fakeExecutor.advanceTime(Duration.ofSeconds(5));

		assertEquals("3", appendFuture3.get());

		assertEquals(2, testBigQueryWriteImpl.getCapturedRequests().get(0).getOffset());
		assertEquals(1, testBigQueryWriteImpl.getCapturedRequests().get(1).getOffset());
		writer.shutdown();
		writer.awaitTermination(1, TimeUnit.MINUTES);
	}

	@Test
	/**
	 * Make sure that resume publishing works as expected:
	 *
	 * <ol>
	 *   <li>publish with key orderA which returns a failure.
	 *   <li>publish with key orderA again, which should fail immediately
	 *   <li>publish with key orderB, which should succeed
	 *   <li>resume publishing on key orderA
	 *   <li>publish with key orderA, which should now succeed
	 * </ol>
	 */
	public void testResumePublish() throws Exception {
		StreamWriter writer =
				getTestStreamWriterBuilder()
						.setBatchingSettings(
								StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
										.toBuilder()
										.setElementCountThreshold(2L)
										.build())
						.setEnableMessageOrdering(true)
						.build();

		ApiFuture<AppendRowsResponse> future1 = sendTestMessageWithOrderingKey(publisher, "m1", "orderA");
		ApiFuture<AppendRowsResponse> future2 = sendTestMessageWithOrderingKey(publisher, "m2", "orderA");

		fakeExecutor.advanceTime(Duration.ZERO);
		assertFalse(future1.isDone());
		assertFalse(future2.isDone());

		// This exception should stop future publishing to the same key
		testBigQueryWriteImpl.addPublishError(new StatusException(Status.INVALID_ARGUMENT));

		fakeExecutor.advanceTime(Duration.ZERO);

		try {
			future1.get();
			Assert.fail("This should fail.");
		} catch (ExecutionException e) {
		}

		try {
			future2.get();
			Assert.fail("This should fail.");
		} catch (ExecutionException e) {
		}

		// Submit new requests with orderA that should fail.
		ApiFuture<AppendRowsResponse> future3 = sendTestMessageWithOrderingKey(publisher, "m3", "orderA");
		ApiFuture<AppendRowsResponse> future4 = sendTestMessageWithOrderingKey(publisher, "m4", "orderA");

		try {
			future3.get();
			Assert.fail("This should fail.");
		} catch (ExecutionException e) {
			assertEquals(SequentialExecutorService.CallbackExecutor.CANCELLATION_EXCEPTION, e.getCause());
		}

		try {
			future4.get();
			Assert.fail("This should fail.");
		} catch (ExecutionException e) {
			assertEquals(SequentialExecutorService.CallbackExecutor.CANCELLATION_EXCEPTION, e.getCause());
		}

		// Submit a new request with orderB, which should succeed
		ApiFuture<AppendRowsResponse> future5 = sendTestMessageWithOrderingKey(publisher, "m5", "orderB");
		ApiFuture<AppendRowsResponse> future6 = sendTestMessageWithOrderingKey(publisher, "m6", "orderB");

		testBigQueryWriteImpl.addResponse(
				AppendRowsResponse.newBuilder().addMessageIds("5").addMessageIds("6"));

		Assert.assertEquals("5", future5.get());
		Assert.assertEquals("6", future6.get());

		// Resume publishing of "orderA", which should now succeed
		publisher.resumePublish("orderA");

		ApiFuture<AppendRowsResponse> future7 = sendTestMessageWithOrderingKey(publisher, "m7", "orderA");
		ApiFuture<AppendRowsResponse> future8 = sendTestMessageWithOrderingKey(publisher, "m8", "orderA");

		testBigQueryWriteImpl.addResponse(
				AppendRowsResponse.newBuilder().addMessageIds("7").addMessageIds("8"));

		Assert.assertEquals("7", future7.get());
		Assert.assertEquals("8", future8.get());

		publisher.shutdown();
	}

	@Test
	public void testErrorPropagation() throws Exception {
		Publisher publisher =
				getTestStreamWriterBuilder()
						.setExecutorProvider(SINGLE_THREAD_EXECUTOR)
						.setBatchingSettings(
								Publisher.Builder.DEFAULT_BATCHING_SETTINGS
										.toBuilder()
										.setElementCountThreshold(1L)
										.setDelayThreshold(Duration.ofSeconds(5))
										.build())
						.build();
		testBigQueryWriteImpl.addPublishError(Status.DATA_LOSS.asException());
		try {
			sendTestMessage(publisher, "A").get();
			fail("should throw exception");
		} catch (ExecutionException e) {
			assertThat(e.getCause()).isInstanceOf(DataLossException.class);
		}
	}

	@Test
	public void testWriterGetters() throws Exception {
		StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);
		builder.setChannelProvider(TEST_CHANNEL_PROVIDER);
		builder.setExecutorProvider(SINGLE_THREAD_EXECUTOR);
		builder.setBatchingSettings(
				BatchingSettings.newBuilder()
						.setRequestByteThreshold(10L)
						.setDelayThreshold(Duration.ofMillis(11))
						.setElementCountThreshold(12L)
						.build());
		builder.setCredentialsProvider(NoCredentialsProvider.create());
		StreamWriter writer = builder.build();

		assertEquals(TEST_STREAM, writer.getStreamNameString());
		assertEquals(10, (long) writer.getBatchingSettings().getRequestByteThreshold());
		assertEquals(Duration.ofMillis(11), writer.getBatchingSettings().getDelayThreshold());
		assertEquals(12, (long) writer.getBatchingSettings().getElementCountThreshold());
		writer.shutdown();
		writer.awaitTermination(1, TimeUnit.MINUTES);
	}

	@Test
	public void testBuilderParametersAndDefaults() {
		StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);
		assertEquals(TEST_STREAM.toString(), builder.streamName);
		assertEquals(StreamWriter.Builder.DEFAULT_EXECUTOR_PROVIDER, builder.executorProvider);
		assertEquals(
				StreamWriter.Builder.DEFAULT_REQUEST_BYTES_THRESHOLD,
				builder.batchingSettings.getRequestByteThreshold().longValue());
		assertEquals(
				StreamWriter.Builder.DEFAULT_DELAY_THRESHOLD, builder.batchingSettings.getDelayThreshold());
		assertEquals(
				StreamWriter.Builder.DEFAULT_ELEMENT_COUNT_THRESHOLD,
				builder.batchingSettings.getElementCountThreshold().longValue());
		assertEquals(StreamWriter.Builder.DEFAULT_RETRY_SETTINGS, builder.retrySettings);
	}

	@Test
	public void testBuilderInvalidArguments() {
		StreamWriter.Builder builder = StreamWriter.newBuilder(TEST_STREAM);

		try {
			builder.setChannelProvider(null);
			fail("Should have thrown an IllegalArgumentException");
		} catch (NullPointerException expected) {
			// Expected
		}

		try {
			builder.setExecutorProvider(null);
			fail("Should have thrown an IllegalArgumentException");
		} catch (NullPointerException expected) {
			// Expected
		}
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
							.toBuilder()
							.setRequestByteThreshold(null)
							.build());
			fail("Should have thrown an NullPointerException");
		} catch (NullPointerException expected) {
			// Expected
		}
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
							.toBuilder()
							.setRequestByteThreshold(0L)
							.build());
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// Expected
		}
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
							.toBuilder()
							.setRequestByteThreshold(-1L)
							.build());
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// Expected
		}

		builder.setBatchingSettings(
				StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
						.toBuilder()
						.setDelayThreshold(Duration.ofMillis(1))
						.build());
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS.toBuilder().setDelayThreshold(null).build());
			fail("Should have thrown an NullPointerException");
		} catch (NullPointerException expected) {
			// Expected
		}
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
							.toBuilder()
							.setDelayThreshold(Duration.ofMillis(-1))
							.build());
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// Expected
		}

		builder.setBatchingSettings(
				StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
						.toBuilder()
						.setElementCountThreshold(1L)
						.build());
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
							.toBuilder()
							.setElementCountThreshold(null)
							.build());
			fail("Should have thrown an NullPointerException");
		} catch (NullPointerException expected) {
			// Expected
		}
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
							.toBuilder()
							.setElementCountThreshold(0L)
							.build());
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// Expected
		}
		try {
			builder.setBatchingSettings(
					StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
							.toBuilder()
							.setElementCountThreshold(-1L)
							.build());
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// Expected
		}
	}

	@Test
	public void testAwaitTermination() throws Exception {
		StreamWriter writer =
				getTestStreamWriterBuilder()
						.setExecutorProvider(SINGLE_THREAD_EXECUTOR)
						.build();
		ApiFuture<AppendRowsResponse> appendFuture1 = sendTestMessage(writer, "A");
		writer.shutdown();
		assertTrue(writer.awaitTermination(1, TimeUnit.MINUTES));
	}

	@Test
	public void testShutDown() throws Exception {
		ApiFuture apiFuture = EasyMock.mock(ApiFuture.class);
		StreamWriter writer = EasyMock.mock(StreamWriter.class);
		EasyMock.expect(
				writer.append(
						Storage.AppendRowsRequest.newBuilder().setData(ByteString.copyFromUtf8("A")).build()))
				.andReturn(apiFuture);
		EasyMock.expect(writer.awaitTermination(1, TimeUnit.MINUTES)).andReturn(true);
		writer.shutdown();
		EasyMock.expectLastCall().once();
		EasyMock.replay(writer);
		sendTestMessage(writer, "A");
		writer.shutdown();
		assertTrue(writer.awaitTermination(1, TimeUnit.MINUTES));
	}

	private StreamWriter.Builder getTestStreamWriterBuilder() {
		return StreamWriter.newBuilder(TEST_STREAM)
				       .setExecutorProvider(FixedExecutorProvider.create(fakeExecutor))
				       .setChannelProvider(TEST_CHANNEL_PROVIDER)
				       .setCredentialsProvider(NoCredentialsProvider.create());
	}
}
