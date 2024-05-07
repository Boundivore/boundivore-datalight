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

SERVICE_NAME="KAFKA"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <KafkaBroker> <start|stop|restart>"
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
start_kafkabroker() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/bin/kafka-server-start.sh -daemon ${CURRENT_SERVICE_DIR}/config/server.properties"
  echo "KafkaBroker started."
}

stop_kafkabroker() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/bin/kafka-server-stop.sh"
  echo "KafkaBroker stopped."
}

# 执行相应的启动或停止命令
case "${COMPONENT_NAME}" in
  "KafkaBroker")
    case "${OPERATION}" in
      "start")
        start_kafkabroker
        ;;
      "stop")
        stop_kafkabroker
        ;;
      "restart")
        stop_kafkabroker
        sleep 2
        start_kafkabroker
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported component: <KafkaBroker>"
    exit 1
    ;;
esac

exit 0