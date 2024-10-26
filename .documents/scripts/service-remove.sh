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

SERVICE_NAME="$1"

# 检查 SERVICE_NAME 是否为空
if [ -z "${SERVICE_NAME}" ]; then
  echo "Error: SERVICE_NAME is not provided. Please provide a valid SERVICE_NAME."
  exit 1
fi

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"
SERVICE_DATA_DIR="${DATA_DIR}/${SERVICE_NAME}"
SERVICE_LOG_DIR="${LOG_DIR}/${SERVICE_NAME}"

# 函数：输出错误并退出
exit_on_error() {
  echo "Error: $1"
  exit 1
}

# 清除服务目录
clear_service_dir() {
  if [ -d "${CURRENT_SERVICE_DIR}" ]; then
    echo "Removing ${CURRENT_SERVICE_DIR}"
    echo "Removing ${SERVICE_DATA_DIR}"
    echo "Removing ${SERVICE_LOG_DIR}"
    rm -rf "${CURRENT_SERVICE_DIR}" "${SERVICE_DATA_DIR}" "${SERVICE_LOG_DIR}" || exit_on_error "Failed to remove ${CURRENT_SERVICE_DIR} ${SERVICE_DATA_DIR} ${SERVICE_LOG_DIR}"
  fi
}

# 清除原目录
clear_service_dir

exit 0
