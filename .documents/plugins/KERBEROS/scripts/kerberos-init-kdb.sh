#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

DB_PASSWORD="$1"

# 创建 Kerberos 数据库
kdb5_util create -s -r DATALIGHT -P "$DB_PASSWORD"

exit 0