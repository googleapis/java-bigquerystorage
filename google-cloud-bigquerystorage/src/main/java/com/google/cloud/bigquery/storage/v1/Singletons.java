/*
 * Copyright 2024 Google LLC
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
package com.google.cloud.bigquery.storage.v1;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import java.util.logging.Logger;

/** Container for global singleton objects. */
class Singletons {

  private static final Logger log = Logger.getLogger(Singletons.class.getName());

  // Global OpenTelemetry instance
  private static OpenTelemetry openTelemetry = null;

  static OpenTelemetry getOpenTelemetry() {
    if (openTelemetry == null) {
      openTelemetry = GlobalOpenTelemetry.get();
    }
    return openTelemetry;
  }
}
