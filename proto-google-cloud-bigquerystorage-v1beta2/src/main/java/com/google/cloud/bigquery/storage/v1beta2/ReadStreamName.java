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

package com.google.cloud.bigquery.storage.v1beta2;

import com.google.api.pathtemplate.PathTemplate;
import com.google.api.resourcenames.ResourceName;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** AUTO-GENERATED DOCUMENTATION AND CLASS */
@javax.annotation.Generated("by GAPIC protoc plugin")
public class ReadStreamName implements ResourceName {

  private static final PathTemplate PATH_TEMPLATE =
      PathTemplate.createWithoutUrlEncoding(
          "projects/{project}/locations/{location}/sessions/{session}/streams/{stream}");

  private volatile Map<String, String> fieldValuesMap;

  private final String project;
  private final String location;
  private final String session;
  private final String stream;

  public String getProject() {
    return project;
  }

  public String getLocation() {
    return location;
  }

  public String getSession() {
    return session;
  }

  public String getStream() {
    return stream;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  private ReadStreamName(Builder builder) {
    project = Preconditions.checkNotNull(builder.getProject());
    location = Preconditions.checkNotNull(builder.getLocation());
    session = Preconditions.checkNotNull(builder.getSession());
    stream = Preconditions.checkNotNull(builder.getStream());
  }

  public static ReadStreamName of(String project, String location, String session, String stream) {
    return newBuilder()
        .setProject(project)
        .setLocation(location)
        .setSession(session)
        .setStream(stream)
        .build();
  }

  public static String format(String project, String location, String session, String stream) {
    return newBuilder()
        .setProject(project)
        .setLocation(location)
        .setSession(session)
        .setStream(stream)
        .build()
        .toString();
  }

  public static ReadStreamName parse(String formattedString) {
    if (formattedString.isEmpty()) {
      return null;
    }
    Map<String, String> matchMap =
        PATH_TEMPLATE.validatedMatch(
            formattedString, "ReadStreamName.parse: formattedString not in valid format");
    return of(
        matchMap.get("project"),
        matchMap.get("location"),
        matchMap.get("session"),
        matchMap.get("stream"));
  }

  public static List<ReadStreamName> parseList(List<String> formattedStrings) {
    List<ReadStreamName> list = new ArrayList<>(formattedStrings.size());
    for (String formattedString : formattedStrings) {
      list.add(parse(formattedString));
    }
    return list;
  }

  public static List<String> toStringList(List<ReadStreamName> values) {
    List<String> list = new ArrayList<String>(values.size());
    for (ReadStreamName value : values) {
      if (value == null) {
        list.add("");
      } else {
        list.add(value.toString());
      }
    }
    return list;
  }

  public static boolean isParsableFrom(String formattedString) {
    return PATH_TEMPLATE.matches(formattedString);
  }

  public Map<String, String> getFieldValuesMap() {
    if (fieldValuesMap == null) {
      synchronized (this) {
        if (fieldValuesMap == null) {
          ImmutableMap.Builder<String, String> fieldMapBuilder = ImmutableMap.builder();
          fieldMapBuilder.put("project", project);
          fieldMapBuilder.put("location", location);
          fieldMapBuilder.put("session", session);
          fieldMapBuilder.put("stream", stream);
          fieldValuesMap = fieldMapBuilder.build();
        }
      }
    }
    return fieldValuesMap;
  }

  public String getFieldValue(String fieldName) {
    return getFieldValuesMap().get(fieldName);
  }

  @Override
  public String toString() {
    return PATH_TEMPLATE.instantiate(
        "project", project, "location", location, "session", session, "stream", stream);
  }

  /** Builder for ReadStreamName. */
  public static class Builder {

    private String project;
    private String location;
    private String session;
    private String stream;

    public String getProject() {
      return project;
    }

    public String getLocation() {
      return location;
    }

    public String getSession() {
      return session;
    }

    public String getStream() {
      return stream;
    }

    public Builder setProject(String project) {
      this.project = project;
      return this;
    }

    public Builder setLocation(String location) {
      this.location = location;
      return this;
    }

    public Builder setSession(String session) {
      this.session = session;
      return this;
    }

    public Builder setStream(String stream) {
      this.stream = stream;
      return this;
    }

    private Builder() {}

    private Builder(ReadStreamName readStreamName) {
      project = readStreamName.project;
      location = readStreamName.location;
      session = readStreamName.session;
      stream = readStreamName.stream;
    }

    public ReadStreamName build() {
      return new ReadStreamName(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ReadStreamName) {
      ReadStreamName that = (ReadStreamName) o;
      return (this.project.equals(that.project))
          && (this.location.equals(that.location))
          && (this.session.equals(that.session))
          && (this.stream.equals(that.stream));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= project.hashCode();
    h *= 1000003;
    h ^= location.hashCode();
    h *= 1000003;
    h ^= session.hashCode();
    h *= 1000003;
    h ^= stream.hashCode();
    return h;
  }
}
