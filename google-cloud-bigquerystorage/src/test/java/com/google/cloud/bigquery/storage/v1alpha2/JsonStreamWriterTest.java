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

import com.google.cloud.bigquery.storage.test.Test.*;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.cloud.bigquery.storage.test.JsonTest.AllenTest;
import com.google.cloud.bigquery.storage.v1alpha2.Storage.AppendRowsResponse;
import com.google.api.core.*;
import com.google.cloud.bigquery.storage.v1alpha2.Stream.WriteStream;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class JsonStreamWriterTest {
  // private static final Logger LOG = Logger.getLogger(JsonStreamWriterTest.class.getName());

  @Test
  public void testStuff() throws Exception {
    try (BigQueryWriteClient client = BigQueryWriteClient.create()) {
      WriteStream response =
          client.createWriteStream(
              CreateWriteStreamRequest.newBuilder()
                  .setParent("projects/bigquerytestdefault/datasets/allenTest/tables/allenTable")
                  .setWriteStream(WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build())
                  .build());

      JsonStreamWriter jsonStreamWriter =
          JsonStreamWriter.newBuilder(
                  response.getName(),
                  response.getTableSchema(),
                  client)
              .build();
      System.out.println(response.getTableSchema());
      System.out.flush();
      TimeUnit.SECONDS.sleep(10);
      AllenTest allen1 = AllenTest.newBuilder().setFoo("hello").setTestConnection("hello again").build();
      AllenTest allen2 = AllenTest.newBuilder().setFoo("hello3").setTestConnection("hello again3").build();
      List<AllenTest> protoRows = new ArrayList<AllenTest>();
      protoRows.add(allen1);
      protoRows.add(allen2);
      ApiFuture<AppendRowsResponse> appendResponseFuture = jsonStreamWriter.append(protoRows);
      System.out.println("Not waiting for api response");
      System.out.println(appendResponseFuture.get());
      System.out.println("waiting for api response");
      // AppendRowsResponse appendResponse = appendResponseFuture.get();
      // System.out.println(appendResponse.hasUpdatedSchema());
      // System.out.println(appendResponse.hasError());
      // System.out.println(appendResponse.getUpdatedSchema());
      // System.out.println(appendResponse.getOffset());
      // System.out.println(appendResponse.getError());
    }
  }
}
