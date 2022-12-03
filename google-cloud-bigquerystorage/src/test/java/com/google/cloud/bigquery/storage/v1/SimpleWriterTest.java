package com.google.cloud.bigquery.storage.v1;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimpleWriterTest {
  private static final Logger log =
      Logger.getLogger(com.google.cloud.bigquery.storage.v1.StreamWriterTest.class.getName());
  private static final String TEST_STREAM_1 = "projects/p/datasets/d1/tables/t1/streams/s1";
  private static final String TEST_STREAM_2 = "projects/p/datasets/d2/tables/t2/streams/s2";
  private static final String TEST_TRACE_ID = "DATAFLOW:job_id";
  private FakeScheduledExecutorService fakeExecutor;
  private FakeBigQueryWrite testBigQueryWrite;
  private static MockServiceHelper serviceHelper;
  private BigQueryWriteClient client;
  private final TableFieldSchema FOO =
      TableFieldSchema.newBuilder()
          .setType(TableFieldSchema.Type.STRING)
          .setMode(TableFieldSchema.Mode.NULLABLE)
          .setName("foo")
          .build();
  private final TableFieldSchema BAR =
      TableFieldSchema.newBuilder()
          .setType(TableFieldSchema.Type.STRING)
          .setMode(TableFieldSchema.Mode.NULLABLE)
          .setName("bar")
          .build();

  public SimpleWriterTest() throws DescriptorValidationException {}

  @Before
  public void setUp() throws Exception {
    testBigQueryWrite = new FakeBigQueryWrite();
    serviceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(testBigQueryWrite));
    serviceHelper.start();
    fakeExecutor = new FakeScheduledExecutorService();
    testBigQueryWrite.setExecutor(fakeExecutor);
    client =
        BigQueryWriteClient.create(
            BigQueryWriteSettings.newBuilder()
                .setCredentialsProvider(NoCredentialsProvider.create())
                .setTransportChannelProvider(serviceHelper.createChannelProvider())
                .build());
  }

  @After
  public void tearDown() throws Exception {
    log.info("tearDown called");
    client.close();
    serviceHelper.stop();
  }

  @Test
  public void testGoodWrites() throws Exception {}

  @Test
  public void testBadWrites() throws Exception {}

  @Test
  public void testWriterCacheExpired() throws Exception {}

  @Test
  public void testBuilderParams() throws Exception {}
}
