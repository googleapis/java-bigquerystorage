/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.bigquery.storage.v1beta1;

import com.google.api.core.BetaApi;
import com.google.cloud.bigquery.storage.v1beta1.BigQueryStorageGrpc.BigQueryStorageImplBase;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Generated;

@BetaApi
@Generated("by gapic-generator-java")
public class MockBigQueryStorageImpl extends BigQueryStorageImplBase {
  private List<AbstractMessage> requests;
  private Queue<Object> responses;

  public MockBigQueryStorageImpl() {
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
      Storage.CreateReadSessionRequest request,
      StreamObserver<Storage.ReadSession> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Storage.ReadSession) {
      requests.add(request);
      responseObserver.onNext(((Storage.ReadSession) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CreateReadSession, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Storage.ReadSession.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void readRows(
      Storage.ReadRowsRequest request, StreamObserver<Storage.ReadRowsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Storage.ReadRowsResponse) {
      requests.add(request);
      responseObserver.onNext(((Storage.ReadRowsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ReadRows, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Storage.ReadRowsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void batchCreateReadSessionStreams(
      Storage.BatchCreateReadSessionStreamsRequest request,
      StreamObserver<Storage.BatchCreateReadSessionStreamsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Storage.BatchCreateReadSessionStreamsResponse) {
      requests.add(request);
      responseObserver.onNext(((Storage.BatchCreateReadSessionStreamsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method BatchCreateReadSessionStreams, expected"
                      + " %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Storage.BatchCreateReadSessionStreamsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void finalizeStream(
      Storage.FinalizeStreamRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method FinalizeStream, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void splitReadStream(
      Storage.SplitReadStreamRequest request,
      StreamObserver<Storage.SplitReadStreamResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Storage.SplitReadStreamResponse) {
      requests.add(request);
      responseObserver.onNext(((Storage.SplitReadStreamResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method SplitReadStream, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Storage.SplitReadStreamResponse.class.getName(),
                  Exception.class.getName())));
    }
  }
}
