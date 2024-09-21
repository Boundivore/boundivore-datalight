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

cd "${CURRENT_SERVICE_DIR}/ranger-usersync"

./setup.sh

sed -i '/<name>ranger.usersync.enabled<\/name>/{n;s/<value>false<\/value>/<value>true<\/value>/}' ${SERVICE_DIR}/RANGER/ranger-usersync/conf/ranger-ugsync-site.xml


exit 0