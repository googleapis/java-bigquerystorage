package com.google.cloud.bigquery.storage.v1beta2.st;

import static org.junit.Assert.assertEquals;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryReadClient;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1beta2.CreateWriteStreamRequest;
import com.google.cloud.bigquery.storage.v1beta2.DataFormat;
import com.google.cloud.bigquery.storage.v1beta2.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1beta2.ReadRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.ReadSession;
import com.google.cloud.bigquery.storage.v1beta2.ReadStream;
import com.google.cloud.bigquery.storage.v1beta2.StreamWriter;
import com.google.cloud.bigquery.storage.v1beta2.TableName;
import com.google.cloud.bigquery.storage.v1beta2.WriteStream;
import com.google.cloud.bigquery.storage.v1beta2.it.ITBigQueryStorageLongRunningTest;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.protobuf.Descriptors;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

public class STBigQueryStorageLongRunningWriteTest {
  private static final Logger LOG =
      Logger.getLogger(ITBigQueryStorageLongRunningTest.class.getName());

  private static final String LONG_TESTS_ENABLED_PROPERTY =
      "bigquery.storage.enable_long_running_tests";

  private static final String LONG_TESTS_DISABLED_MESSAGE =
      String.format(
          "BigQuery Storage long running tests are not enabled and will be skipped. "
              + "To enable them, set system property '%s' to true.",
          LONG_TESTS_ENABLED_PROPERTY);
  private static BigQueryWriteClient client;
  private static String parentProjectId;
  private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();
  private static final String TABLE = "testtable";
  private static final String TABLE2 = "testtable2";
  private static final String DESCRIPTION = "BigQuery Write Java long test dataset";

  private static TableInfo tableInfo;
  private static TableInfo tableInfo2;
  private static String tableId;
  private static String tableId2;
  private static BigQuery bigquery;

  private static JSONObject MakeJsonObject(int size) throws IOException {
    JSONObject object = new JSONObject();
    // size: (1, simple)(2,complex)()
    if (size == 1) {
      object.put("test_str", "aaa");
      object.put("test_numerics", new JSONArray(new String[]{"1234", "-900000"}));
      object.put("test_datetime", String.valueOf(LocalDateTime.now()));
    }
    else if (size == 2) {  // make it complicated and slow
      // test_str
      JSONObject test_str = new JSONObject();
      JSONObject test_str_nest = new JSONObject();
      test_str.put("testing", "lorem");
      test_str_nest.put("testing_testing", new JSONArray(new String[]{"ipsum","I","don't","know","this whole thing"}));
      test_str.put("testing2", test_str_nest);
      object.put("test_str", "testing testing");

      // test_numerics
      object.put("test_numerics", test_str);

      object.put("test_datetime", String.valueOf(LocalDateTime.now()));
    }
    return object;
  }

  @BeforeClass
  public static void beforeClass() throws IOException {
    Assume.assumeTrue(LONG_TESTS_DISABLED_MESSAGE, Boolean.getBoolean(LONG_TESTS_ENABLED_PROPERTY));
    client = BigQueryWriteClient.create();
    parentProjectId = String.format("projects/%s", ServiceOptions.getDefaultProjectId());
    RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
    bigquery = bigqueryHelper.getOptions().getService();
    DatasetInfo datasetInfo = DatasetInfo.newBuilder(/* datasetId = */ DATASET).setDescription(DESCRIPTION).build();
    bigquery.create(datasetInfo);
    LOG.info("Created test dataset: " + DATASET);
    tableInfo =
        TableInfo.newBuilder(
            TableId.of(DATASET, TABLE),
            StandardTableDefinition.of(
                Schema.of(
                    com.google.cloud.bigquery.Field.newBuilder("foo", LegacySQLTypeName.STRING)
                        .setMode(Field.Mode.NULLABLE)
                        .build())))
            .build();
    com.google.cloud.bigquery.Field.Builder innerTypeFieldBuilder =
        com.google.cloud.bigquery.Field.newBuilder(
            "inner_type",
            LegacySQLTypeName.RECORD,
            com.google.cloud.bigquery.Field.newBuilder("value", LegacySQLTypeName.STRING)
                .setMode(Field.Mode.REPEATED)
                .build());

    tableInfo2 =
        TableInfo.newBuilder(
            TableId.of(DATASET, TABLE2),
            StandardTableDefinition.of(
                Schema.of(
                    Field.newBuilder(
                        "nested_repeated_type",
                        LegacySQLTypeName.RECORD,
                        innerTypeFieldBuilder.setMode(Field.Mode.REPEATED).build())
                        .setMode(Field.Mode.REPEATED)
                        .build(),
                    innerTypeFieldBuilder.setMode(Field.Mode.NULLABLE).build())))
            .build();
    bigquery.create(tableInfo);
    bigquery.create(tableInfo2);
    tableId =
        String.format(
            "projects/%s/datasets/%s/tables/%s",
            ServiceOptions.getDefaultProjectId(), DATASET, TABLE);
    tableId2 =
        String.format(
            "projects/%s/datasets/%s/tables/%s",
            ServiceOptions.getDefaultProjectId(), DATASET, TABLE2);
    LOG.info(
        String.format(
            "%s tests running with parent project: %s",
            ITBigQueryStorageLongRunningTest.class.getSimpleName(), parentProjectId));
  }

  @AfterClass
  public static void afterClass() {
    if (client != null) {
      client.close();
    }
    if (bigquery != null) {
      RemoteBigQueryHelper.forceDelete(bigquery, DATASET);
      LOG.info("Deleted test dataset: " + DATASET);
    }
  }

  @Test
  public void testDefaultStream() throws IOException, InterruptedException, ExecutionException,
      Descriptors.DescriptorValidationException {
    // Set up a default stream. Write to it for a long time, (a few minutes for now) and make
    // sure that everything goes well.
    String tableName = "JsonTableDefaultStream";
    TableInfo tableInfo =
        TableInfo.newBuilder(
            TableId.of(DATASET, tableName),
            StandardTableDefinition.of(
                Schema.of(
                    com.google.cloud.bigquery.Field.newBuilder(
                        "test_str", StandardSQLTypeName.STRING)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "test_numerics", StandardSQLTypeName.NUMERIC)
                        .setMode(Field.Mode.REPEATED)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "test_datetime", StandardSQLTypeName.DATETIME)
                        .build())))
            .build();
    bigquery.create(tableInfo);
    TableName parent = TableName.of(ServiceOptions.getDefaultProjectId(), DATASET, tableName);
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(parent.toString(), tableInfo.getDefinition().getSchema())
            .createDefaultStream()
            .setBatchingSettings(
                StreamWriter.Builder.DEFAULT_BATCHING_SETTINGS
                    .toBuilder()
                    .setRequestByteThreshold(1024 * 1024L) // 1 Mb
                    .setElementCountThreshold(2L)
                    .setDelayThreshold(Duration.ofSeconds(2))
                    .build())
            .build()) {
      // TODO: Instead of just sending one message and then two messages, send generated messages
      // for like a minute, we can scale this up later to as long as we want.
      for (int i = 0; i < 5; i++){
        LOG.info("Sending a message");
        // Ramping up the size increases the latency
        JSONObject row = MakeJsonObject(2);
        JSONArray jsonArr = new JSONArray(new JSONObject[] {row});

        LocalDateTime start = LocalDateTime.now();
        Date startTime = new Date();
        ApiFuture<AppendRowsResponse> response = jsonStreamWriter.append(jsonArr, -1);
        assertEquals(0, response.get().getAppendResult().getOffset().getValue());
        Assert.assertFalse(response.get().getAppendResult().hasOffset());
        Date finishTime = new Date();
        LOG.info("Latency: ".concat(String.valueOf(finishTime.getTime() - startTime.getTime())).concat(" ms"));
         // seems like 2 or 3 seconds on average
        //LOG.info(String.valueOf((Math.abs(finish.getSecond())-start.getSecond())));
      }

      TableResult result =
          bigquery.listTableData(
              tableInfo.getTableId(), BigQuery.TableDataListOption.startIndex(0L));
      Iterator<FieldValueList> iter = result.getValues().iterator();
//      FieldValueList currentRow;
//      for (int i = 0; i < 5; i++) {
//        currentRow = iter.next();
//        assertEquals("aaa", currentRow.get(0).getStringValue());
//      }
//      assertEquals(false, iter.hasNext());
    }
  }

  @Test
  public void testDedicatedStream() {
    WriteStream writeStream = client.createWriteStream(CreateWriteStreamRequest.newBuilder()
        .setParent(tableId)
        .setWriteStream(
            WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build())
        .build());
    // set up a dedicated stream. Write to it for a long time, (a few minutes) and make
    // sure that everything goes well.
  }

  @Test
  public void testBufferedStreamWithFlushOperation() {
    // Set up a buffered stream with an ongoing flush operation and write to it for a long time,
    // (a few minutes) and make sure that everything goes well.

  }
//  public void testLongRunningReadSession() throws InterruptedException, ExecutionException {
//    // This test writes a larger table with the goal of doing a simple validation of timeout settings
//    // for a longer running session.
//
//    String table =
//        BigQueryResource.FormatTableResource(
//            /* projectId = */ "bigquery-public-data",
//            /* datasetId = */ "samples",
//            /* tableId = */ "wikipedia");
//
//    WriteStream stream = client.createWriteStream();
//
//    ReadSession session =
//        client.createReadSession(
//            /* parent = */ parentProjectId,
//            /* readSession = */ ReadSession.newBuilder()
//                .setTable(table)
//                .setDataFormat(DataFormat.AVRO)
//                .build(),
//            /* maxStreamCount = */ 5);
//
//    assertEquals(
//        String.format(
//            "Did not receive expected number of streams for table '%s' CreateReadSession response:%n%s",
//            table, session.toString()),
//        5,
//        session.getStreamsCount());
//
//    List<Callable<Long>> tasks = new ArrayList<>(session.getStreamsCount());
//    for (final ReadStream stream : session.getStreamsList()) {
//      tasks.add(
//          new Callable<Long>() {
//            @Override
//            public Long call() throws Exception {
//              return readAllRowsFromStream(stream);
//            }
//          });
//    }
//
//    ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
//    List<Future<Long>> results = executor.invokeAll(tasks);
//
//    long rowCount = 0;
//    for (Future<Long> result : results) {
//      rowCount += result.get();
//    }
//
//    assertEquals(313_797_035, rowCount);
//  }
}
