#!/usr/bin/env bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

set -eo pipefail

curdir="$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"

BROKER_HOME=$(
    cd "${curdir}/.."
    pwd
)
export BROKER_HOME

#PID_DIR="$(
#    cd "${curdir}"
#    pwd
#)"
PID_DIR="{{PID_DIR}}"
export PID_DIR

pidfile="${PID_DIR}/apache_hdfs_broker.pid"

if [[ -f "${pidfile}" ]]; then
    pid="$(cat "${pidfile}")"
    pidcomm="$(basename "$(ps -p "${pid}" -o comm=)")"

    if [[ "java" != "${pidcomm}" ]]; then
        echo "ERROR: pid process may not broker. "
    fi

    if kill -9 "${pid}" >/dev/null 2>&1; then
        echo "stop ${pidcomm}, and remove pid file. "
        rm "${pidfile}"
    fi
fi
