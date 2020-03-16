/*
 * Copyright 2016 Google LLC
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

import com.google.api.core.InternalApi;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import java.util.logging.Logger;

/**
 * A barrier kind of object that helps keep track of pending actions and synchronously wait until
 * all have completed.
 */
class Waiter {
  private static final Logger LOG = Logger.getLogger(Waiter.class.getName());

  private int pendingCount;
  private int pendingSize;
  FlowControlSettings flowControlSettings;

  Waiter(FlowControlSettings flowControlSettings) {
    pendingCount = 0;
    pendingSize = 0;
    this.flowControlSettings = flowControlSettings;
  }

  public synchronized void incrementPendingCount(int delta) {
    this.pendingCount += delta;
    if (pendingCount == 0) {
      notifyAll();
    }
  }

  public synchronized void incrementPendingSize(int delta) {
    this.pendingSize += delta;
  }

  private void overLimit(String message) {
    boolean interrupted = false;
    try {
      if (this.flowControlSettings.getLimitExceededBehavior()
          == FlowController.LimitExceededBehavior.Block) {
        try {
          LOG.fine("Wait on: " + message);
          wait();
        } catch (InterruptedException e) {
          // Ignored, uninterruptibly.
          interrupted = true;
        }
      } else if (this.flowControlSettings.getLimitExceededBehavior()
          == FlowController.LimitExceededBehavior.ThrowException) {
        throw new IllegalStateException("FlowControl limit exceeded: " + message);
      } else if (this.flowControlSettings.getLimitExceededBehavior()
          == FlowController.LimitExceededBehavior.Ignore) {
        return;
      } else {
        throw new IllegalStateException(
            "Unknown behavior setting: "
                + this.flowControlSettings.getLimitExceededBehavior().toString());
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public synchronized void waitOnElementCount() {
    while (this.pendingCount >= this.flowControlSettings.getMaxOutstandingElementCount()) {
      overLimit("Element count");
    }
  }

  public synchronized void waitOnSizeLimit(int incomingSize) {
    while (this.pendingSize + incomingSize
        >= this.flowControlSettings.getMaxOutstandingRequestBytes()) {
      overLimit("Byte size");
    }
  }

  public synchronized void waitComplete() {
    boolean interrupted = false;
    try {
      while (pendingCount > 0) {
        try {
          wait();
        } catch (InterruptedException e) {
          // Ignored, uninterruptibly.
          interrupted = true;
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @InternalApi
  public int pendingCount() {
    return pendingCount;
  }

  @InternalApi
  public int pendingSize() {
    return pendingSize;
  }
}
