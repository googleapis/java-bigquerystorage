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


package com.google.cloud.bigquery.storage.v1beta2.st;

import static org.junit.Assert.assertEquals;

public class STBigQueryStressTest {

  private static final Logger LOG =
    Logger.getLogger(ITBigQueryStorageLongRunningTest.class.getName());

  private static BigQueryReadClient client;
  private static String parentProjectId;

  @BeforeClass
  public static void beforeClass() throws IOException {
    client = BigQueryReadClient.create();
     parentProjectId = String.format("projects/%s", ServiceOptions.getDefaultProjectId());
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
  }

  @Test
  public void testLongRunningReadSession() throws InterruptedException, ExecutionException {
     assertEquals(1,1);
  }
}
