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

SERVICE_NAME="HBASE"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <JournalNode|NameNode|ZKFailoverController|DataNode|HttpFS> <start|stop|restart>"
  exit 1
fi

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# new cmd: su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon start journalnode" "${USER_NAME}"

shift

case "${COMPONENT_NAME}" in
"HMaster")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon start master" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon stop master" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon stop master" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon start master" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"HRegionServer")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon start regionserver" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon stop regionserver" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon stop regionserver" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon start regionserver" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;

"HThriftServer")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon start thriftserver" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon stop thriftserver" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon stop thriftserver" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase --daemon start thriftserver" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;

*)
  echo "Invalid component name. Supported components: <HMaster|HRegionServer|HThriftServer>"
  exit 1
  ;;
esac

exit 0
