/*
 * Copyright 2020 Google LLC
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

package com.google.cloud.bigquery.storage.v1beta2;

import com.google.api.core.BetaApi;
import com.google.cloud.bigquery.storage.v1beta2.BigQueryReadGrpc.BigQueryReadImplBase;
import com.google.protobuf.AbstractMessage;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Generated;

@BetaApi
@Generated("by gapic-generator-java")
public class MockBigQueryReadImpl extends BigQueryReadImplBase {
  private List<AbstractMessage> requests;
  private Queue<Object> responses;

  public MockBigQueryReadImpl() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  public List<AbstractMessage> getRequests() {
    return requests;
  }

  public void addResponse(AbstractMessage response) {
    responses.add(response);
  }

  public void setResponses(List<AbstractMessage> responses) {
    this.responses = new LinkedList<Object>(responses);
  }

  public void addException(Exception exception) {
    responses.add(exception);
  }

  public void reset() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  @Override
  public void createReadSession(
      CreateReadSessionRequest request, StreamObserver<ReadSession> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ReadSession) {
      requests.add(request);
      responseObserver.onNext(((ReadSession) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CreateReadSession, expected %s or %s",
                  response.getClass().getName(),
                  ReadSession.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void readRows(ReadRowsRequest request, StreamObserver<ReadRowsResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof ReadRowsResponse) {
      requests.add(request);
      responseObserver.onNext(((ReadRowsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ReadRows, expected %s or %s",
                  response.getClass().getName(),
                  ReadRowsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void splitReadStream(
      SplitReadStreamRequest request, StreamObserver<SplitReadStreamResponse> responseObserver) {
    Object response = responses.remove();
    if (response instanceof SplitReadStreamResponse) {
      requests.add(request);
      responseObserver.onNext(((SplitReadStreamResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method SplitReadStream, expected %s or %s",
                  response.getClass().getName(),
                  SplitReadStreamResponse.class.getName(),
                  Exception.class.getName())));
    }
  }
}
