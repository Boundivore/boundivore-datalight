#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

# 检查是否提供了密码参数
if [ -z "$1" ]; then
  echo "Usage: $0 <admin_password>"
  exit 1
fi

ADMIN_PASSWORD="$1"

# 创建 Kerberos 超级管理员
kadmin.local -q "addprinc -pw $ADMIN_PASSWORD admin@DATALIGHT"

echo "Kerberos administrator 'admin@DATALIGHT' created."

exit 0