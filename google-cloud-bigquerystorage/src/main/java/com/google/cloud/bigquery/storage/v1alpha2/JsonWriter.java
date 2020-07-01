/*
 * Copyright 2020 Google LLC
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

import com.google.api.core.*;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoRows;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsRequest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonWriter {
  private static final Logger LOG = Logger.getLogger(JsonWriter.class.getName());
  private static String tablePatternString = "projects/([^/]+)/datasets/([^/]+)/tables/([^/]+)";
  private static Pattern tablePattern = Pattern.compile(tablePatternString);
  private com.google.cloud.bigquery.storage.v1alpha2.Table.TableSchema BQTableSchema;
  private Schema BQTableInitialSchema;
  private StreamWriter streamWriter;
  private Descriptors.Descriptor descriptor;

  public StreamWriter getStreamWriter() {
    return this.streamWriter;
  }

  public JsonWriter(StreamWriter streamWriter, BigQuery bigQuery)
      throws Descriptors.DescriptorValidationException {
    this.streamWriter = streamWriter;
    TableId tableId = getTableId(streamWriter.getTableNameString());
    Table table = bigQuery.getTable(tableId);
    BQTableInitialSchema = table.getDefinition().getSchema();
    descriptor = JsonToProtoConverter.BQSchemaToProtoSchema(BQTableInitialSchema);
  }

  public ApiFuture<AppendRowsResponse> append(JSONArray jsonRows, long offset)
      throws IllegalArgumentException {
    List<DynamicMessage> protoRows = new ArrayList<DynamicMessage>();
    for (int i = 0; i < jsonRows.length(); i++) {
      java.lang.Object jsonObject = jsonRows.get(i);
      if (jsonObject instanceof JSONObject) {
        DynamicMessage msg =
            JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, (JSONObject) jsonObject);
        protoRows.add(msg);
      } else {
        throw new IllegalArgumentException(
            "Illegal JSON Formatting: JSON input is not a JSONArray of JSONObjects.");
      }
    }

    ProtoRows.Builder rowsBuilder = ProtoRows.newBuilder();
    Descriptors.Descriptor descriptor = null;
    for (Message protoRow : protoRows) {
      rowsBuilder.addSerializedRows(protoRow.toByteString());
    }

    AppendRowsRequest.ProtoData.Builder data = AppendRowsRequest.ProtoData.newBuilder();
    data.setWriterSchema(ProtoSchemaConverter.convert(protoRows.get(0).getDescriptorForType()));
    data.setRows(rowsBuilder.build());
    if (offset > 0) {
      return streamWriter.append(
          AppendRowsRequest.newBuilder()
              .setProtoRows(data.build())
              .setOffset(Int64Value.of(offset))
              .build());
    }
    return streamWriter.append(AppendRowsRequest.newBuilder().setProtoRows(data.build()).build());
  }

  public ApiFuture<AppendRowsResponse> append(JSONArray jsonRows) throws IllegalArgumentException {
    return append(jsonRows, -1);
  }

  private TableId getTableId(String tableName) {
    Matcher matcher = tablePattern.matcher(tableName);
    if (!matcher.matches() || matcher.groupCount() != 3) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
    return TableId.of(matcher.group(1), matcher.group(2), matcher.group(3));
  }

  public void close() {
    this.streamWriter.close();
  }
}
