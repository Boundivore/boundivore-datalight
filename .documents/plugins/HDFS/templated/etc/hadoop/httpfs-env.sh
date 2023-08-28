#!/usr/bin/env bash
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License. See accompanying LICENSE file.
#

# Set httpfs specific environment variables here.
#
# hadoop-env.sh is read prior to this file.
#

# HTTPFS config directory
#
# export HTTPFS_CONFIG=${HADOOP_CONF_DIR}

# HTTPFS log directory
#
# export HTTPFS_LOG=${HADOOP_LOG_DIR}

# HTTPFS temporary directory
#
# export HTTPFS_TEMP=${HADOOP_HDFS_HOME}/temp

# The HTTP port used by HTTPFS
#
# export HTTPFS_HTTP_PORT=14000

# The maximum number of HTTP handler threads
#
# export HTTPFS_MAX_THREADS=1000

# The hostname HttpFS server runs on
#
# export HTTPFS_HTTP_HOSTNAME=$(hostname -f)

# The maximum size of HTTP header
#
# export HTTPFS_MAX_HTTP_HEADER_SIZE=65536

# Whether SSL is enabled
#
# export HTTPFS_SSL_ENABLED=false

# The location of the SSL keystore if using SSL
#
# export HTTPFS_SSL_KEYSTORE_FILE=${HOME}/.keystore

# The password of the SSL keystore if using SSL
#
# export HTTPFS_SSL_KEYSTORE_PASS=password

#export HDFS_HTTPFS_OPTS="$HDFS_HTTPFS_OPTS \
# -Dcom.sun.management.jmxremote.authenticate=false \
# -Dcom.sun.management.jmxremote.ssl=false \
# -Dcom.sun.management.jmxremote.local.only=false \
# -Dcom.sun.management.jmxremote.port={{jmxRemotePort_HttpFS}} \
# -javaagent:${DATALIGHT_DIR}/exporter/jar/jmx_exporter.jar={{jmxExporterPort_HttpFS}}:${SERVICE_DIR}/HDFS/exporter/conf/jmx_config_HttpFS.yaml"
