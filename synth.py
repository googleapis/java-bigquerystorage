# Copyright 2019 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""This script is used to synthesize generated parts of this library."""

import synthtool as s
import synthtool.gcp as gcp
import synthtool.languages.java as java

gapic = gcp.GAPICGenerator()
service = 'bigquerystorage'
versions = ['v1beta1']

for version in versions:
  java.gapic_library(
      service=service,
      version=version,
      config_pattern='/google/cloud/bigquery/storage/artman_{service}_{version}.yaml',
      package_pattern='com.google.cloud.bigquery.storage.{version}',
      gapic=gapic,
  )

for version in ['v1beta2']:
  java.bazel_library(
      service='bigquery-storage',
      version=version,
      package_pattern='com.google.cloud.bigquery.storage.{version}',
      proto_path=f'google/cloud/bigquery/storage/{version}',
      bazel_target=f'//google/cloud/bigquery/storage/{version}:google-cloud-bigquery-storage-{version}-java',
      destination_name='bigquerystorage',
  )

common_templates = gcp.CommonTemplates()
templates = common_templates.java_library()
s.copy(templates, excludes=[
    'README.md',
])
