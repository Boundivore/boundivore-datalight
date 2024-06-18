#!/bin/bash
# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# shellcheck disable=SC2034
USER_NAME="datalight"
# shellcheck disable=SC2034
GROUP_NAME="datalight"

SERVICE_NAME="HDFS"
CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

check_all_nodes_decommission_status() {
  report=$(su -c "${CURRENT_SERVICE_DIR}/bin/hdfs dfsadmin -report" "${USER_NAME}")

  echo "$report" | awk '
    /Name:/ {
      node_name=$2
    }
    /Hostname:/ {
      node_hostname=$2
    }
    /Decommission Status/ {
      status=$4
      if (status == "Decommissioned") {
        print node_hostname ":Decommissioned:(DataNodeStateMark)"
      } else if (status == "Decommissioning") {
        print node_hostname ":Decommissioning:(DataNodeStateMark)"
      } else {
        print node_hostname ":Normal:(DataNodeStateMark)"
      }
    }
  '
}

check_all_nodes_decommission_status

exit 0
