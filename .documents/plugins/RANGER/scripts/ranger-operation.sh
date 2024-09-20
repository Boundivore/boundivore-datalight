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

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <RangerAdmin|RangerUserSync> <start|stop|restart>"
  exit 1
fi

# 确保日志和 PID 目录存在
mkdir -p "${LOG_DIR}/${SERVICE_NAME}"
mkdir -p "${PID_DIR}/${SERVICE_NAME}"

chown ${USER_NAME}:${GROUP_NAME} -R "${LOG_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${PID_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${DATA_DIR}"

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# 定义启动和停止函数
start_rangeradmin() {
  su -c "${CURRENT_SERVICE_DIR}/ranger-admin/ews/ranger-admin-services.sh start" "${USER_NAME}"
  echo "RangerAdmin started."
}

stop_rangeradmin() {
  su -c "${CURRENT_SERVICE_DIR}/ranger-admin/ews/ranger-admin-services.sh stop" "${USER_NAME}"
  echo "RangerAdmin stopped."
}

start_rangerusersync() {
  su -c "${CURRENT_SERVICE_DIR}/ranger-usersync/ranger-usersync-services.sh start" "${USER_NAME}"
  echo "RangerUserSync started."
}

stop_rangerusersync() {
  su -c "${CURRENT_SERVICE_DIR}/ranger-usersync/ranger-usersync-services.sh stop" "${USER_NAME}"
  echo "RangerUserSync stopped."
}

# 执行相应的启动或停止命令
case "${COMPONENT_NAME}" in
  "RangerAdmin")
    case "${OPERATION}" in
      "start")
        start_rangeradmin
        ;;
      "stop")
        stop_rangeradmin
        ;;
      "restart")
        stop_rangeradmin
        sleep 2
        start_rangeradmin
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  "RangerUserSync")
    case "${OPERATION}" in
      "start")
        start_rangerusersync
        ;;
      "stop")
        stop_rangerusersync
        ;;
      "restart")
        stop_rangerusersync
        sleep 2
        start_rangerusersync
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported components: <RangerAdmin|RangerUserSync>"
    exit 1
    ;;
esac

exit 0