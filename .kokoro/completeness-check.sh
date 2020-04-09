#!/bin/bash
# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -eo pipefail

## Get the directory of the build script
scriptDir=$(realpath $(dirname "${BASH_SOURCE[0]}"))
function now() { date +"%Y-%m-%d %H:%M:%S" | tr -d '\n'; }
function msg() { println "$*" >&2; }
function println() { printf '%s\n' "$(now) $*"; }

function cleanup() {
  rm .org-list.txt .new-list.txt .diff.txt
}

## cd to the parent directory, i.e. the root of the git repo
cd ${scriptDir}/..

## Locally install artifacts
#mvn verify -DskipTests

## Find client directory that continas flattened pom and cd into it
clientDir=$(find -iname ".flattened-pom.xml" | cut -d"/" -f1-2)
cd "$clientDir"

## Run dependency list completeness check

# Output dep list with compile scope generated using the original pom
msg "Generating dependency list using original pom..."
mvn dependency:list -f pom.xml -Dsort=true | grep '\[INFO]    .*:.*:.*:.*:compile' >.org-list.txt

# Output dep list with compile scope generated using the flattened pom
msg "Generating dependency list using flattened pom..."
mvn dependency:list -f .flattened-pom.xml -Dsort=true | grep '\[INFO]    .*:.*:.*:.*:compile' >.new-list.txt

# Compare two dependency lists
msg "Comparing dependency lists..."
diff .org-list.txt .new-list.txt >.diff.txt && (msg "Success. No diff!" && cleanup) || (msg "Diff found: " && cat .diff.txt)
