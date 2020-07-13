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
import java.util.*;
import java.util.logging.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonStreamWriterTest {
  private static final Logger LOG = Logger.getLogger(LocalTest.class.getName());

  @Test
  public void testStuff() throws Exception {
    try (BigQueryWriteClient bigQueryWriteClient = BigQueryWriteClient.create()) {
      JsonStreamWriter jsonStreamWriter =
          JsonStreamWriter.newBuilder(
                  "projects/bigquerytestdefault/datasets/allenTest/tables/allenTable",
                  bigQueryWriteClient)
              .setStreamType(Stream.WriteStream.Type.COMMITTED)
              .build();
      System.out.println(jsonStreamWriter.getDescriptor().toProto());
    }
  }
}
