#!/bin/bash
# 全局路径变量参考：
# DORIS_DIR="/opt/doris"
# SERVICE_DIR="/srv/doris"
# LOG_DIR="/data/doris/logs"
# PID_DIR="/data/doris/pids"
# DATA_DIR="/data/doris/data"

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

USER_NAME="datalight"
GROUP_NAME="datalight"

SERVICE_NAME="DORIS"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <ComponentName> <start|stop|restart>"
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
start_component() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/${1}/bin/start_${1}.sh --daemon"
  echo "$1 started."
}

stop_component() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/${1}/bin/stop_${1}.sh"
  echo "$1 stopped."
}

# 执行相应的启动或停止命令
case "${COMPONENT_NAME}" in
  "FEServer")
    case "${OPERATION}" in
      "start")
        start_component "fe"
        ;;
      "stop")
        stop_component "fe"
        ;;
      "restart")
        stop_component "fe"
        sleep 2
        start_component "fe"
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  "BEServer")
    case "${OPERATION}" in
      "start")
        start_component "be"
        ;;
      "stop")
        stop_component "be"
        ;;
      "restart")
        stop_component "be"
        sleep 2
        start_component "be"
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported components: <FEServer|BEServer>"
    exit 1
    ;;
esac

exit 0