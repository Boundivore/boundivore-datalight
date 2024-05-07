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

# shellcheck disable=SC2034
USER_NAME="datalight"
# shellcheck disable=SC2034
GROUP_NAME="datalight"

SERVICE_NAME="FLINK"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

FLINK_LOG_DIR="${LOG_DIR}/${SERVICE_NAME}"
FLINK_PID_DIR="${PID_DIR}/${SERVICE_NAME}"

FLINK_ENV_SCRIPT="${CURRENT_SERVICE_DIR}/conf/flink-env.sh"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <FlinkHistoryServer> <start|stop|restart>"
  exit 1
fi


# 确保日志和PID目录存在
mkdir -p "${FLINK_LOG_DIR}"
mkdir -p "${FLINK_PID_DIR}"

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 加载 Hive 配置
# shellcheck source=/srv/datalight/FLINK/conf/flink-env.sh
source "${FLINK_ENV_SCRIPT}"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# 定义启动和停止函数
start_flinkhistoryserver() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/sbin/start-history-server.sh"
  echo "FlinkHistoryServer started."
}

stop_flinkhistoryserver() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/sbin/stop-history-server.sh"
  echo "FlinkHistoryServer stopped."
}


# 执行相应的启动或停止命令
case "${COMPONENT_NAME}" in
  "FlinkHistoryServer")
    case "${OPERATION}" in
      "start")
        start_flinkhistoryserver
        ;;
      "stop")
        stop_flinkhistoryserver
        ;;
      "restart")
        stop_flinkhistoryserver
        sleep 2
        start_flinkhistoryserver
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported components: <FlinkHistoryServer>"
    exit 1
    ;;
esac

exit 0
