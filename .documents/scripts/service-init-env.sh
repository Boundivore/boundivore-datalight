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
TARGZ_NAME="$2"

TARGZ_PATH="${DATALIGHT_DIR}/plugins/${SERVICE_NAME}/dlc/${TARGZ_NAME}"

# 函数：输出错误并退出
exit_on_error() {
  echo "Error: $1"
  exit 1
}

# 函数：设置所有者和权限
set_ownership_and_permissions() {
  USER_NAME="datalight"
  GROUP_NAME="datalight"

  chown -R "${USER_NAME}:${GROUP_NAME}" "$1" || exit_on_error "Failed to set ownership for $1"
  chmod -R 755 "$1" || exit_on_error "Failed to set permissions for $1"
}


# 创建父目录：判断 SERVICE_DIR 目录是否存在，不存在则创建并授权
mkdir -p "${SERVICE_DIR}" || exit_on_error "Failed to create ${SERVICE_DIR}"
mkdir -p "${LOG_DIR}/${SERVICE_NAME}" || exit_on_error "Failed to create ${LOG_DIR}/${SERVICE_NAME}"
mkdir -p "${PID_DIR}/${SERVICE_NAME}" || exit_on_error "Failed to create ${PID_DIR}/${SERVICE_NAME}"
mkdir -p "${DATA_DIR}/${SERVICE_NAME}" || exit_on_error "Failed to create ${DATA_DIR}/${SERVICE_NAME}"

# 解压 DLC 安装包到指定目录，并为目录赋权
tar -zxf "${TARGZ_PATH}" -C "${SERVICE_DIR}" || exit_on_error "Failed to extract ${TARGZ_PATH}"

# 重新授权
set_ownership_and_permissions "${DATALIGHT_DIR}"
set_ownership_and_permissions "${SERVICE_DIR}"
set_ownership_and_permissions "${LOG_DIR}"
set_ownership_and_permissions "${PID_DIR}"
set_ownership_and_permissions "${DATA_DIR}"

exit 0
