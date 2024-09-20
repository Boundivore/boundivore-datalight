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

#This scripts add the ranger_audit configuration with schema to zookeeper

function usage {
    echo "Error: The following properties need to be set in the script SOLR_ZK, SOLR_INSTALL_DIR and SOLR_RANGER_HOME"
    exit 1
}

SOLR_USER=datalight
SOLR_ZK={{SOLR_ZK}}
SOLR_INSTALL_DIR="${SERVICE_DIR}/SOLR"
SOLR_RANGER_HOME="${SERVICE_DIR}/RANGER/ranger-admin/solr/ranger_audits"

if [ "`whoami`" != "$SOLR_USER" ]; then
    if [ -w /etc/passwd ]; then
	echo "Running this script as $SOLR_USER..."
	su $SOLR_USER $0
    else
	echo "ERROR: You need to run this script $0 as user $SOLR_USER. You are currently running it as `whoami`"
    fi
    
    exit 1
fi

if [ "$SOLR_ZK" = "" ]; then
    usage
fi

if [ "$SOLR_INSTALL_DIR" = "" ]; then
    usage
fi

if [ "$SOLR_RANGER_HOME" = "" ]; then
    usage
fi

SOLR_RANGER_CONFIG_NAME=ranger_audits
SOLR_RANGER_CONFIG_LOCAL_PATH=${SOLR_RANGER_HOME}/conf
ZK_CLI=$SOLR_INSTALL_DIR/server/scripts/cloud-scripts/zkcli.sh


if [ ! -x $ZK_CLI ]; then
    echo "Error: $ZK_CLI is not found or you don't have permission to execute it."
    exit 1
fi
set -x
$ZK_CLI -cmd upconfig -zkhost $SOLR_ZK -confname $SOLR_RANGER_CONFIG_NAME -confdir $SOLR_RANGER_CONFIG_LOCAL_PATH
