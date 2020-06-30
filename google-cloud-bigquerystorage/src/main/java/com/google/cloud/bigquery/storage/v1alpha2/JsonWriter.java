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

public class JsonWriter {
  // private static final Logger LOG = Logger.getLogger(JsonWriter.class.getName());
  // private Table.TableSchema BQTableSchema;
  // private StreamWriter streamWriter;
  // private Descriptor descriptor;
  //
  // public JsonWriter(StreamWriter.Builder builder) {
  //   this.streamWriter = builder.build();
  //   this.BQSchema = streamWriter.getTableSchema();
  //   descriptor = JsonToProtoConverter.BQSchemaToProtoSchema(BQSchema);
  // }
  //
  // public JsonWriter(StreamWriter streamWriter) {
  //   this.streamWriter = streamWriter;
  //   this.BQSchema = streamWriter.getTableSchema();
  //   descriptor = JsonToProtoConverter.BQSchemaToProtoSchema(BQSchema);
  // }
  //
  // public ApiFuture<AppendRowsResponse> append(JSONArray jsonRows) {
  //   List<DynamicMessage> protoRows = new ArrayList<DynamicMessage>();
  //   for (int i = 0; i < jsonRows.size(); i++) {
  //     JSONObject jsonObject = jsonRows.get(i);
  //     protoRows.add(JsonToProtoConverter.protoSchemaToProtoMessage(descriptor, jsonObject));
  //   }
  //
  //   ProtoRows.Builder rowsBuilder = ProtoRows.newBuilder();
  //   Descriptors.Descriptor descriptor = null;
  //   for (Message protoRow : protoRows) {
  //     rowsBuilder.addSerializedRows(protoRow.toByteString());
  //   }
  //
  //   AppendRowsRequest.ProtoData.Builder data = AppendRowsRequest.ProtoData.newBuilder();
  //   data.setWriterSchema(ProtoSchemaConverter.convert(protoRows.get(0).getDescriptorForType()));
  //   data.setRows(rowsBuilder.build());
  //
  //   return ApiFutures.<Storage.AppendRowsResponse, Long>transform(
  //       streamWriter.append(AppendRowsRequest.newBuilder().setProtoRows(data.build()).build()),
  //       new ApiFunction<Storage.AppendRowsResponse, Long>() {
  //         @Override
  //         public Long apply(Storage.AppendRowsResponse appendRowsResponse) {
  //           return Long.valueOf(appendRowsResponse.getOffset());
  //         }
  //       },
  //       MoreExecutors.directExecutor());
  //
  // }
}
