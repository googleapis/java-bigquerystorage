/*
 * Copyright 2019 Google LLC
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

package com.google.cloud.bigquery.storage.v1alpha2.it;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto;
import com.google.cloud.bigquery.storage.v1alpha2.StreamWriter;
import com.google.cloud.bigquery.storage.v1alpha2.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.cloud.bigquery.Schema;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.*;

import java.util.HashMap;

/**
 * ProtobufEnvelope - allows creating a protobuf message without the .proto file dynamically.
 *
 * @author Florian Leibert
 */
class ProtobufEnvelope {
	private HashMap<String, Object> values = new HashMap<String, Object>();
	private DescriptorProtos.DescriptorProto.Builder desBuilder;
	private int i = 1;

	public ProtobufEnvelope() {
		desBuilder = DescriptorProtos.DescriptorProto.newBuilder();
		i = 1;
	}

	public <T> void addField(String fieldName, T fieldValue, DescriptorProtos.FieldDescriptorProto.Type type) {
		DescriptorProtos.FieldDescriptorProto.Builder fd1Builder = DescriptorProtos.FieldDescriptorProto.newBuilder()
				                                                           .setName(fieldName).setNumber(i++).setType(type);
		desBuilder.addField(fd1Builder.build());
		values.put(fieldName, fieldValue);
	}

	public Message constructMessage(String messageName)
			throws Descriptors.DescriptorValidationException {
		desBuilder.setName(messageName);
		DescriptorProtos.DescriptorProto dsc = desBuilder.build();

		DescriptorProtos.FileDescriptorProto fileDescP = DescriptorProtos.FileDescriptorProto.newBuilder()
				                                                 .addMessageType(dsc).build();

		Descriptors.FileDescriptor[] fileDescs = new Descriptors.FileDescriptor[0];
		Descriptors.FileDescriptor dynamicDescriptor = Descriptors.FileDescriptor.buildFrom(fileDescP, fileDescs);
		Descriptors.Descriptor msgDescriptor = dynamicDescriptor.findMessageTypeByName(messageName);
		DynamicMessage.Builder dmBuilder =
				DynamicMessage.newBuilder(msgDescriptor);
		for (String name : values.keySet()) {
			dmBuilder.setField(msgDescriptor.findFieldByName(name), values.get(name));
		}
		return dmBuilder.build();
	}

	public void clear() {
		desBuilder = DescriptorProtos.DescriptorProto.newBuilder();
		i = 1;
		values.clear();
	}
}

/** Integration tests for BigQuery Storage API. */
public class ITBigQueryWriteManualClientTest {
	private static final Logger LOG = Logger.getLogger(ITBigQueryWriteManualClientTest.class.getName());
	private static final String DATASET = RemoteBigQueryHelper.generateDatasetName();
	private static final String TABLE = "testtable";
	private static final String DESCRIPTION = "BigQuery Write Java manual client test dataset";

	private static BigQueryWriteClient client;
	private static String tableId;
	private static BigQuery bigquery;

	@BeforeClass
	public static void beforeClass() throws IOException {
		client = BigQueryWriteClient.create();

		RemoteBigQueryHelper bigqueryHelper = RemoteBigQueryHelper.create();
		bigquery = bigqueryHelper.getOptions().getService();
		DatasetInfo datasetInfo =
				DatasetInfo.newBuilder(/* datasetId = */ DATASET).setDescription(DESCRIPTION).build();
		bigquery.create(datasetInfo);
		LOG.info("Created test dataset: " + DATASET);
		TableInfo tableInfo = TableInfo.newBuilder(TableId.of(DATASET, TABLE),
				StandardTableDefinition.of(
						Schema.of(com.google.cloud.bigquery.Field.newBuilder(
								"foo", LegacySQLTypeName.STRING).build()))).build();
		bigquery.create(tableInfo);
		tableId = String.format("projects/%s/datasets/%s/tables/%s",
				ServiceOptions.getDefaultProjectId(), DATASET, TABLE);
		LOG.info(
				String.format(
						"%s tests running with table: %s",
						ITBigQueryWriteManualClientTest.class.getSimpleName(), tableId));
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

	private AppendRowsRequest createAppendRequest(String streamName) {
		ProtobufEnvelope pe = new ProtobufEnvelope();
		pe.addField("foo", "aaa",
				DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
		AppendRowsRequest.Builder requestBuilder = AppendRowsRequest.newBuilder();

		AppendRowsRequest.ProtoData.Builder dataBuilder = AppendRowsRequest.ProtoData.newBuilder();
		dataBuilder.setWriterSchema(ProtoBufProto.ProtoSchema.newBuilder().setProtoDescriptor(
				DescriptorProtos.DescriptorProto.newBuilder().setName("Message").addField(
						DescriptorProtos.FieldDescriptorProto.newBuilder().setName("foo").setType(
								DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING).setNumber(1).build()).build()));

		ProtoBufProto.ProtoRows.Builder rows = ProtoBufProto.ProtoRows.newBuilder();
		try {
			rows.addSerializedRows(pe.constructMessage("t").toByteString());
		} catch (Descriptors.DescriptorValidationException e) {
			throw new RuntimeException(e);
		}
		pe.clear();
		dataBuilder.setRows(rows.build());
		return requestBuilder.setProtoRows(dataBuilder.build()).setWriteStream(streamName).build();
	}

	@Test
	public void testSimpleWrite() throws IOException, InterruptedException, ExecutionException {
		WriteStream writeStream =
				client.createWriteStream(
						CreateWriteStreamRequest.newBuilder().setParent(tableId).setWriteStream(
								WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build()).build());
		StreamWriter streamWriter = StreamWriter.newBuilder(writeStream.getName())
				                            .setBatchingSettings(
				                            		BatchingSettings.newBuilder().setElementCountThreshold(1L).build())
				                            .build();
		ApiFuture<AppendRowsResponse> response = streamWriter.append(createAppendRequest(writeStream.getName()));
		LOG.info("Test Got response: " + response.get().getOffset());
	}
}
