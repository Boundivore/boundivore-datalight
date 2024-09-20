#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

SOLR_HOST_URL=http://{{SOLR_HOST}}:8983
SOLR_ZK={{SOLR_ZK}}
SOLR_INSTALL_DIR="${SERVICE_DIR}/SOLR"
SHARDS=3
REPLICATION=2

CONF_NAME=ranger_audits
COLLECTION_NAME=ranger_audits

which curl 2>&1 > /dev/null
if [ $? -ne 0 ]; then
    echo "curl is not found. Please install it for creating the collection"
    exit 1
fi

set -x
curl --negotiate -u : "${SOLR_HOST_URL}/solr/admin/collections?action=CREATE&name=${COLLECTION_NAME}&numShards=${SHARDS}&replicationFactor=${REPLICATION}&collection.configName=$CONF_NAME&maxShardsPerNode=100"
