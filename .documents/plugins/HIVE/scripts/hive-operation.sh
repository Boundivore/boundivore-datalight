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

SERVICE_NAME="HIVE"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <MetaStore|HiveServer2> <start|stop|restart>"
  exit 1
fi

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

shift

case "${COMPONENT_NAME}" in
"MetaStore")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon start journalnode" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon stop journalnode" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon stop journalnode" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon start journalnode" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"HiveServer2")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon start namenode" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon stop namenode" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon stop namenode" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon start namenode" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
*)
  echo "Invalid component name. Supported components: <MetaStore|HiveServer2>"
  exit 1
  ;;
esac

exit 0


# su -s /bin/bash datalight -c "/srv/datalight/HIVE/tez/tomcat/bin/startup.sh"
# su -s /bin/bash datalight -c "/srv/datalight/HIVE/tez/tomcat/bin/shutdown.sh"