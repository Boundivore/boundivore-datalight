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

SERVICE_NAME="HDFS"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <component_name> <start|stop|restart>"
  exit 1
fi

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"

# new cmd: su -c "${CURRENT_SERVICE_DIR}/bin/hdfs --daemon start journalnode" "${USER_NAME}"

shift

case "${COMPONENT_NAME}" in
"JournalNode")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start journalnode" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop journalnode" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop journalnode" "${USER_NAME}"
    sleep 2
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start journalnode" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"NameNode")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start namenode" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop namenode" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop namenode" "${USER_NAME}"
    sleep 2
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start namenode" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;

"ZKFailoverController")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start zkfc" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop zkfc" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop zkfc" "${USER_NAME}"
    sleep 2
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start zkfc" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;

"DataNode")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start datanode" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop datanode" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh stop datanode" "${USER_NAME}"
    sleep 2
    su -c "${CURRENT_SERVICE_DIR}/sbin/hadoop-daemon.sh start datanode" "${USER_NAME}"
    ;;
  "refresh")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs dfsadmin -refreshNodes" "${USER_NAME}"
    ;;
  "report")
    su -c "${CURRENT_SERVICE_DIR}/bin/hdfs dfsadmin -report" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart|refresh|report]"
    exit 1
    ;;
  esac
  ;;

"HttpFS")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/sbin/httpfs.sh start" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/sbin/httpfs.sh stop" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/sbin/httpfs.sh stop" "${USER_NAME}"
    sleep 2
    su -c "${CURRENT_SERVICE_DIR}/sbin/httpfs.sh start" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
*)
  echo "Invalid component name. Supported components: NameNode, DataNode"
  exit 1
  ;;
esac

exit 0
