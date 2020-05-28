/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.bigquery.storage.v1.stub.readrows;

import static com.google.cloud.bigquery.storage.v1.stub.readrows.BigQueryStorageExceptionFactory.newBigQueryStorageException;

import com.google.api.core.InternalApi;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.bigquery.storage.v1.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1.ReadRowsResponse;
import io.grpc.Status;

@InternalApi
public class ReadRowsRetryCallable
    extends ServerStreamingCallable<ReadRowsRequest, ReadRowsResponse> {
  private final ServerStreamingCallable<ReadRowsRequest, ReadRowsResponse> streamingCallable;

  public ReadRowsRetryCallable(
      ServerStreamingCallable<ReadRowsRequest, ReadRowsResponse> streamingCallable) {
    this.streamingCallable = streamingCallable;
  }

  @Override
  public void call(
      ReadRowsRequest request,
      ResponseObserver<ReadRowsResponse> responseObserver,
      ApiCallContext context) {
    this.streamingCallable.call(request, new ReadRowsResponseObserver(responseObserver), context);
  }

  private static class ReadRowsResponseObserver implements ResponseObserver<ReadRowsResponse> {

    private final ResponseObserver<ReadRowsResponse> responseObserver;

    ReadRowsResponseObserver(ResponseObserver<ReadRowsResponse> responseObserver) {
      this.responseObserver = responseObserver;
    }

    @Override
    public void onStart(StreamController controller) {
      this.responseObserver.onStart(controller);
    }

    @Override
    public void onResponse(ReadRowsResponse response) {
      this.responseObserver.onResponse(response);
    }

    @Override
    public void onError(Throwable t) {
      Status status = Status.fromThrowable(t);
      if (status.getCode() == Status.Code.INTERNAL) {
        this.responseObserver.onError(newBigQueryStorageException(t));
      } else {
        this.responseObserver.onError(t);
      }
    }

    @Override
    public void onComplete() {
      this.responseObserver.onComplete();
    }
  }
}
