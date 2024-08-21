/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.bigquery.storage.v1alpha;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.httpjson.GaxHttpJsonProperties;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.testing.FakeStatusCode;
import com.google.cloud.bigquery.storage.v1alpha.stub.HttpJsonMetastorePartitionServiceStub;
import com.google.protobuf.Empty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class MetastorePartitionServiceClientHttpJsonTest {
  private static MockHttpService mockService;
  private static MetastorePartitionServiceClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonMetastorePartitionServiceStub.getMethodDescriptors(),
            MetastorePartitionServiceSettings.getDefaultEndpoint());
    MetastorePartitionServiceSettings settings =
        MetastorePartitionServiceSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                MetastorePartitionServiceSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = MetastorePartitionServiceClient.create(settings);
  }

  @AfterClass
  public static void stopServer() {
    client.close();
  }

  @Before
  public void setUp() {}

  @After
  public void tearDown() throws Exception {
    mockService.reset();
  }

  @Test
  public void batchCreateMetastorePartitionsTest() throws Exception {
    BatchCreateMetastorePartitionsResponse expectedResponse =
        BatchCreateMetastorePartitionsResponse.newBuilder()
            .addAllPartitions(new ArrayList<MetastorePartition>())
            .build();
    mockService.addResponse(expectedResponse);

    BatchCreateMetastorePartitionsRequest request =
        BatchCreateMetastorePartitionsRequest.newBuilder()
            .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
            .addAllRequests(new ArrayList<CreateMetastorePartitionRequest>())
            .setSkipExistingPartitions(true)
            .build();

    BatchCreateMetastorePartitionsResponse actualResponse =
        client.batchCreateMetastorePartitions(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void batchCreateMetastorePartitionsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      BatchCreateMetastorePartitionsRequest request =
          BatchCreateMetastorePartitionsRequest.newBuilder()
              .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
              .addAllRequests(new ArrayList<CreateMetastorePartitionRequest>())
              .setSkipExistingPartitions(true)
              .build();
      client.batchCreateMetastorePartitions(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void batchDeleteMetastorePartitionsTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockService.addResponse(expectedResponse);

    BatchDeleteMetastorePartitionsRequest request =
        BatchDeleteMetastorePartitionsRequest.newBuilder()
            .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
            .addAllPartitionValues(new ArrayList<MetastorePartitionValues>())
            .build();

    client.batchDeleteMetastorePartitions(request);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void batchDeleteMetastorePartitionsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      BatchDeleteMetastorePartitionsRequest request =
          BatchDeleteMetastorePartitionsRequest.newBuilder()
              .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
              .addAllPartitionValues(new ArrayList<MetastorePartitionValues>())
              .build();
      client.batchDeleteMetastorePartitions(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void batchUpdateMetastorePartitionsTest() throws Exception {
    BatchUpdateMetastorePartitionsResponse expectedResponse =
        BatchUpdateMetastorePartitionsResponse.newBuilder()
            .addAllPartitions(new ArrayList<MetastorePartition>())
            .build();
    mockService.addResponse(expectedResponse);

    BatchUpdateMetastorePartitionsRequest request =
        BatchUpdateMetastorePartitionsRequest.newBuilder()
            .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
            .addAllRequests(new ArrayList<UpdateMetastorePartitionRequest>())
            .build();

    BatchUpdateMetastorePartitionsResponse actualResponse =
        client.batchUpdateMetastorePartitions(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void batchUpdateMetastorePartitionsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      BatchUpdateMetastorePartitionsRequest request =
          BatchUpdateMetastorePartitionsRequest.newBuilder()
              .setParent(TableName.of("[PROJECT]", "[DATASET]", "[TABLE]").toString())
              .addAllRequests(new ArrayList<UpdateMetastorePartitionRequest>())
              .build();
      client.batchUpdateMetastorePartitions(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listMetastorePartitionsTest() throws Exception {
    ListMetastorePartitionsResponse expectedResponse =
        ListMetastorePartitionsResponse.newBuilder().build();
    mockService.addResponse(expectedResponse);

    TableName parent = TableName.of("[PROJECT]", "[DATASET]", "[TABLE]");

    ListMetastorePartitionsResponse actualResponse = client.listMetastorePartitions(parent);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listMetastorePartitionsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      TableName parent = TableName.of("[PROJECT]", "[DATASET]", "[TABLE]");
      client.listMetastorePartitions(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listMetastorePartitionsTest2() throws Exception {
    ListMetastorePartitionsResponse expectedResponse =
        ListMetastorePartitionsResponse.newBuilder().build();
    mockService.addResponse(expectedResponse);

    String parent =
        "projects/project-5331/locations/location-5331/datasets/dataset-5331/tables/table-5331";

    ListMetastorePartitionsResponse actualResponse = client.listMetastorePartitions(parent);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listMetastorePartitionsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent =
          "projects/project-5331/locations/location-5331/datasets/dataset-5331/tables/table-5331";
      client.listMetastorePartitions(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void streamMetastorePartitionsUnsupportedMethodTest() throws Exception {
    // The streamMetastorePartitions() method is not supported in REST transport.
    // This empty test is generated for technical reasons.
  }
}
