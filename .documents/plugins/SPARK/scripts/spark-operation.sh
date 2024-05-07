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

SERVICE_NAME="SPARK"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

SPARK_ENV_SCRIPT="${CURRENT_SERVICE_DIR}/conf/spark-env.sh"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <SparkHistoryServer|SparkThriftServer2> <start|stop|restart>"
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

# 加载 Hive 配置
# shellcheck source=/srv/datalight/SPARK/conf/spark-env.sh
source "${SPARK_ENV_SCRIPT}"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# 定义启动和停止函数
start_sparkhistoryserver() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/sbin/start-history-server.sh"
  echo "SparkHistoryServer started."
}

stop_sparkhistoryserver() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/sbin/stop-history-server.sh"
  echo "SparkHistoryServer stopped."
}


start_sparkthriftserver2() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/sbin/start-thriftserver.sh --master yarn --deploy-mode client --driver-class-path ${CURRENT_SERVICE_DIR}/jars/mysql-connector-java-5.1.27.jar --executor-memory 3g --total-executor-cores 3 --hiveconf hive.server2.thrift.port=10001"
  echo "SparkThriftServer2 started."
}

stop_sparkthriftserver2() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/sbin/stop-thriftserver.sh"
  echo "SparkThriftServer2 stopped."
}

# 执行相应的启动或停止命令
case "${COMPONENT_NAME}" in
  "SparkHistoryServer")
    case "${OPERATION}" in
      "start")
        start_sparkhistoryserver
        ;;
      "stop")
        stop_sparkhistoryserver
        ;;
      "restart")
        stop_sparkhistoryserver
        sleep 2
        start_sparkhistoryserver
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  "SparkThriftServer2")
    case "${OPERATION}" in
      "start")
        start_sparkthriftserver2
        ;;
      "stop")
        stop_sparkthriftserver2
        ;;
      "restart")
        stop_sparkthriftserver2
        sleep 2
        start_sparkthriftserver2
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
       exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported components: <SparkHistoryServer|SparkThriftServer2>"
    exit 1
    ;;
esac

exit 0
