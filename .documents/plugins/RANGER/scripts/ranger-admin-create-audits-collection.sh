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

USER_NAME="datalight"
GROUP_NAME="datalight"

SERVICE_NAME="RANGER"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

CMD="${CURRENT_SERVICE_DIR}/ranger-admin/solr/ranger_audits/scripts/create_ranger_audits_collection.sh"

su -c "${CMD}" "${USER_NAME}"

exit 0