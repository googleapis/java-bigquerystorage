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

/**
 * A abstract class that implements the Runnable interface and provides access to the current
 * StreamWriter and updatedSchema. This runnable will only be called when a updated schema has been
 * passed back through the AppendRowsResponse. Users should only implement the run() function.
 */
public abstract class OnSchemaUpdateRunnable implements Runnable {
  private StreamWriter streamWriter;
  private Table.TableSchema updatedSchema;
  private boolean attachUpdatedTableSchema = false;

  /**
   * Setter for the updatedSchema
   *
   * @param updatedSchema
   */
  void setUpdatedSchema(Table.TableSchema updatedSchema) {
    this.updatedSchema = updatedSchema;
  }

  /**
   * Setter for the streamWriter
   *
   * @param streamWriter
   */
  void setStreamWriter(StreamWriter streamWriter) {
    this.streamWriter = streamWriter;
  }

  /**
   * Setter for attachUpdatedTableSchema. This is used to set the writer schema for the first append
   * calls after a refreshAppend() is called to refresh the write stream upon schema update.
   *
   * @param streamWriter
   */
  void setAttachUpdatedTableSchema(boolean attachUpdatedTableSchema) {
    this.attachUpdatedTableSchema = attachUpdatedTableSchema;
  }

  /** Getter for the updatedSchema */
  Table.TableSchema getUpdatedSchema() {
    return this.updatedSchema;
  }

  /** Getter for the streamWriter */
  StreamWriter getStreamWriter() {
    return this.streamWriter;
  }

  /** Getter for attachUpdatedTableSchema */
  boolean getAttachUpdatedTableSchema() {
    return this.attachUpdatedTableSchema;
  }
}
