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

#set -eo pipefail

## Helper functions
function now() { date +"%Y-%m-%d %H:%M:%S" | tr -d '\n'; }
function msg() { println "$*" >&2; }
function println() { printf '%s\n' "$(now) $*"; }

## Run dependency list completeness check
function completenessCheck() {
  # Output dep list with compile scope generated using the original pom
  msg "Generating dependency list using original pom..."
  mvn dependency:list -f pom.xml -Dsort=true | grep '\[INFO]    .*:.*:.*:.*:.*' | grep -v ':test$' >.org-list.txt || true # continue without error if list is empty

  # Output dep list generated using the flattened pom (test scope deps are ommitted)
  msg "Generating dependency list using flattened pom..."
  mvn dependency:list -f .flattened-pom.xml -Dsort=true | grep '\[INFO]    .*:.*:.*:.*:.*' >.new-list.txt || true # continue without error if list is empty

  # Compare two dependency lists
  msg "Comparing dependency lists..."
  diff .org-list.txt .new-list.txt >.diff.txt
  if [[ $? == 0 ]]
    then
      msg "Success. No diff!"
  else
    msg "Diff found. Check .diff.txt file located in $1."
    return 1
  fi
}
