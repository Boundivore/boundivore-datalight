#!/bin/bash

# 初始化每个节点时需在 /etc/profile 中配置 source /opt/datalight/conf/env/datalight-env.sh
# 并在 .bashrc 和 .bash_profile 中添加 source /etc/profile
# 下面的占位符将在 Master 启动时被 directory.yaml 中配置的值替换
# 替换后举例:
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

export DATALIGHT_DIR="/opt/datalight"
export SERVICE_DIR="/srv/datalight"
export LOG_DIR="/data/datalight/logs"
export PID_DIR="/data/datalight/pids"
export DATA_DIR="/data/datalight/data"

# 函数：设置所有者和权限
set_ownership_and_permissions() {
  USER_NAME="datalight"
  GROUP_NAME="datalight"

  chown -R "$USER_NAME:$GROUP_NAME" "$1" || {
    echo "Failed to set ownership for $1"
    exit 1
  }
  chmod -R 755 "$1" || {
    echo "Failed to set permissions for $1"
    exit 1
  }
}

# 创建目录并设置权限
if [[ -n "${DATALIGHT_DIR}" ]]; then
  mkdir -p "${DATALIGHT_DIR}"
fi

if [[ -n "${SERVICE_DIR}" ]]; then
  mkdir -p "${SERVICE_DIR}"
fi

if [[ -n "${LOG_DIR}" ]]; then
  mkdir -p "${LOG_DIR}"
fi

if [[ -n "${PID_DIR}" ]]; then
  mkdir -p "${PID_DIR}"
fi

if [[ -n "${DATA_DIR}" ]]; then
  mkdir -p "${DATA_DIR}"
fi


set_ownership_and_permissions "${DATALIGHT_DIR}"
set_ownership_and_permissions "${SERVICE_DIR}"
set_ownership_and_permissions "${LOG_DIR}"
set_ownership_and_permissions "${PID_DIR}"
set_ownership_and_permissions "${DATA_DIR}"
