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

import java.util.logging.Logger;

/**
 * A abstract class that implements the Runnable interface and provides access to the current
 * JsonStreamWriter, StreamWriter, and updatedSchema. This runnable will only be called when a
 * updated schema has been passed back through the AppendRowsResponse. Users should only implement
 * the run() function. The following example performs a simple schema update.
 *
 * <p>Performing a schema update requires 3 steps: making a new connection with the same
 * WriteStream, updating StreamWriter's stored Table.TableSchema, and updating JsonStreamWriter's
 * stored descriptor. By default, the StreamWriter will always refresh the connection upon schema
 * update, but the rest is up to the users. In the example below, the run() method updates the
 * Table.TableSchema for the StreamWriter, and generates a new Descriptor based on the updated
 * schema for the JsonStreamWriter.
 *
 * <pre>
 * <code>
 * public void run() {
 * this.streamWriter.setUpdatedSchema(this.updatedSchema);
 * try {
 * this.jsonStreamWriter.setDescriptor(this.updatedSchema);
 * } catch (Descriptors.DescriptorValidationException e) {
 * LOG.severe(
 * "Schema update fail: updated schema could not be converted to a valid descriptor.");
 * return;
 * }
 *
 * LOG.info("Successfully updated schema: " + this.updatedSchema);
 * }
 * </code>
 * </pre>
 */
public abstract class OnSchemaUpdateRunnable implements Runnable {
  private JsonStreamWriter jsonStreamWriter;
  private StreamWriter streamWriter;
  private Table.TableSchema updatedSchema;
  private static final Logger LOG = Logger.getLogger(OnSchemaUpdateRunnable.class.getName());

  /**
   * Setter for the updatedSchema
   *
   * @param updatedSchema
   */
  public void setUpdatedSchema(Table.TableSchema updatedSchema) {
    this.updatedSchema = updatedSchema;
  }

  /**
   * Setter for the streamWriter
   *
   * @param streamWriter
   */
  public void setStreamWriter(StreamWriter streamWriter) {
    this.streamWriter = streamWriter;
  }

  /**
   * Setter for the jsonStreamWriter
   *
   * @param jsonStreamWriter
   */
  public void setJsonStreamWriter(JsonStreamWriter jsonStreamWriter) {
    this.jsonStreamWriter = jsonStreamWriter;
  }

  /** Getter for the updatedSchema */
  public Table.TableSchema getUpdatedSchema() {
    return this.updatedSchema;
  }

  /** Getter for the streamWriter */
  public StreamWriter getStreamWriter() {
    return this.streamWriter;
  }

  /** Getter for the jsonStreamWriter */
  public JsonStreamWriter getJsonStreamWriter() {
    return this.jsonStreamWriter;
  }
}
