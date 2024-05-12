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
# shellcheck disable=SC2034
GROUP_NAME="datalight"

SERVICE_NAME="ZKUI"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 确保日志和 PID 目录存在
mkdir -p "${LOG_DIR}/${SERVICE_NAME}"
mkdir -p "${PID_DIR}/${SERVICE_NAME}"

chown ${USER_NAME}:${GROUP_NAME} -R "${LOG_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${PID_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${DATA_DIR}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 ZKUIServer <start|stop|restart>"
  exit 1
fi

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# 移除第一个参数
shift

case "${COMPONENT_NAME}" in
"ZKUIServer")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/zkui.sh start" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/zkui.sh stop" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/zkui.sh restart" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
*)
  echo "Invalid component name. Supported component: ZKUIServer"
  exit 1
  ;;
esac

exit 0