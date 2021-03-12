package com.google.cloud.bigquery.storage.v1beta2.it;

import static org.junit.Assert.assertEquals;

import com.google.api.core.ApiFuture;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.test.Test.FooType;
import com.google.cloud.bigquery.storage.v1beta2.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1beta2.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1beta2.CivilTimeEncoder;
import com.google.cloud.bigquery.storage.v1beta2.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1beta2.ProtoRows;
import com.google.cloud.bigquery.storage.v1beta2.ProtoSchemaConverter;
import com.google.cloud.bigquery.storage.v1beta2.TableName;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITBigQueryTimeEncoderTest {
  private static final Logger LOG =
      Logger.getLogger(ITBigQueryWriteManualClientTest.class.getName());
  private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();
  private static final String TABLE = "testtable";
  private static final String DESCRIPTION = "BigQuery Write Java manual client test dataset";

  private static BigQueryWriteClient client;
  private static TableInfo tableInfo;
  private static String tableId;
  private static BigQuery bigquery;

  private static AppendRowsRequest.Builder createAppendRequest(String streamName, String[] messages) {
    AppendRowsRequest.Builder requestBuilder = AppendRowsRequest.newBuilder();

    AppendRowsRequest.ProtoData.Builder dataBuilder = AppendRowsRequest.ProtoData.newBuilder();
    dataBuilder.setWriterSchema(ProtoSchemaConverter.convert(FooType.getDescriptor()));

    ProtoRows.Builder rows = ProtoRows.newBuilder();
    for (String message : messages) {
      FooType foo = FooType.newBuilder().setFoo(message).build();
      rows.addSerializedRows(foo.toByteString());
    }
    dataBuilder.setRows(rows.build());
    return requestBuilder.setProtoRows(dataBuilder.build()).setWriteStream(streamName);
  }

  @BeforeClass
  public static void beforeClass() throws IOException {
    client = BigQueryWriteClient.create();

    RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
    bigquery = bigqueryHelper.getOptions().getService();
    DatasetInfo datasetInfo =
        DatasetInfo.newBuilder(/* datasetId = */ DATASET).setDescription(DESCRIPTION).build();
    bigquery.create(datasetInfo);
    tableInfo =
        TableInfo.newBuilder(
            TableId.of(DATASET, TABLE),
            StandardTableDefinition.of(
                Schema.of(
                    com.google.cloud.bigquery.Field.newBuilder(
                        "test_str", StandardSQLTypeName.STRING)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "e32_time_seconds", StandardSQLTypeName.INT64)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "e64_time_micros", StandardSQLTypeName.INT64)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "e64_time_nanos", StandardSQLTypeName.INT64)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "e64_datetime_seconds", StandardSQLTypeName.INT64)
                        .build(),
                    com.google.cloud.bigquery.Field.newBuilder(
                        "e64_datetime_micros", StandardSQLTypeName.INT64)
                        .build()
                )))
            .build();
    bigquery.create(tableInfo);
  }

  @AfterClass
  public static void afterClass() {
    if (client != null) {
      client.close();
    }
    if (bigquery != null) {
      RemoteBigQueryHelper.forceDelete(bigquery, DATASET);
    }
  }

  @Test
  public void TestTimeEncoding() throws IOException, InterruptedException, ExecutionException,
      Descriptors.DescriptorValidationException{
    TableName parent = TableName.of(ServiceOptions.getDefaultProjectId(), DATASET, TABLE);
    try (JsonStreamWriter jsonStreamWriter =
        JsonStreamWriter.newBuilder(parent.toString(), tableInfo.getDefinition().getSchema())
            .createDefaultStream()
            .build()) {
      JSONObject row = new JSONObject();
      row.put("test_str", "Start of the day");
      row.put("e32_time_seconds", CivilTimeEncoder.encodePacked32TimeSeconds(LocalTime.of(1,1,0)));
      row.put("e64_time_micros", CivilTimeEncoder.encodePacked64TimeMicros(LocalTime.of(1,1,0)));
      row.put("e64_time_nanos", CivilTimeEncoder.encodePacked64TimeNanos(LocalTime.of(1,1,0)));
      row.put("e64_datetime_seconds", CivilTimeEncoder.encodePacked64DatetimeSeconds(
          LocalDateTime.of(1,1,1,1,1,1)));
      row.put("e64_datetime_micros", CivilTimeEncoder.encodePacked64DatetimeMicros(
          LocalDateTime.of(1,1,1,1,1, 1,1_000_000)));
      JSONArray jsonArr = new JSONArray(new JSONObject[] {row});
      ApiFuture<AppendRowsResponse> response = jsonStreamWriter.append(jsonArr, -1);
      Assert.assertFalse(response.get().getAppendResult().hasOffset());
      TableResult result =
          bigquery.listTableData(
              tableInfo.getTableId(), BigQuery.TableDataListOption.startIndex(0L));
      Iterator<FieldValueList> iter = result.getValues().iterator();
      FieldValueList currentRow;
      currentRow = iter.next();
      assertEquals("Start of the day", currentRow.get(0).getValue());
      assertEquals(LocalTime.of(1,1,0),
          CivilTimeEncoder.decodePacked32TimeSeconds(
              Integer.parseInt(currentRow.get(1).getStringValue())));
      assertEquals(LocalTime.of(1,1,0),
          CivilTimeEncoder.decodePacked64TimeMicros(
              Long.parseLong(currentRow.get(2).getStringValue())));
      assertEquals(LocalTime.of(1,1,0),
          CivilTimeEncoder.decodePacked64TimeNanos(
              Long.parseLong(currentRow.get(3).getStringValue())));
      assertEquals(LocalDateTime.of(1,1,1,1,1,1),
          CivilTimeEncoder.decodePacked64DatetimeSeconds(
              Long.parseLong(currentRow.get(4).getStringValue())));
      assertEquals(LocalDateTime.of(1,1,1,1,1, 1,1_000_000),
          CivilTimeEncoder.decodePacked64DatetimeMicros(
              Long.parseLong(currentRow.get(5).getStringValue())));
    }
  }
}
