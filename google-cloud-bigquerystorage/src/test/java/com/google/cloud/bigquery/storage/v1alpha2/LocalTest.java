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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.cloud.bigquery.storage.v1alpha2.*;
import com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import com.google.api.core.ApiFuture;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.cloud.bigquery.storage.test.Test.*;
import com.google.common.collect.Sets;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Timestamp;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.threeten.bp.Instant;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors;

@RunWith(JUnit4.class)
public class LocalTest {
  private static final Logger LOG =
      Logger.getLogger(LocalTest.class.getName());
  @Test
  public void testStuff() throws Exception {
    try (BigQueryWriteClient bigQueryWriteClient = BigQueryWriteClient.create()) {
       // TableName parent = TableName.of("bigquerytestdefault", "allenTest", "allenTable");
       // WriteStream writeStream = WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).setName("allenStream").build();
       // WriteStream response = bigQueryWriteClient.createWriteStream(parent, writeStream);
       // Table.TableSchema tableSchema = response.getTableSchema();
       // Descriptor descriptor = BQTableSchemaToProtoDescriptor.ConvertBQTableSchemaToProtoDescriptor(tableSchema);

       JsonStreamWriter jsonStreamWriter = JsonStreamWriter.newBuilder("projects/bigquerytestdefault/datasets/allenTest/tables/allenTable/streams/s", bigQueryWriteClient)
                                                           .setStreamType(Stream.WriteStream.Type.PENDING)
                                                           .build();
       System.out.println(jsonStreamWriter.getDescriptor().toProto());
     }
   }
}
