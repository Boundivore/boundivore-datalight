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

# 用户和组名
USER_NAME="datalight"
GROUP_NAME="datalight"

SERVICE_NAME="HBASE"
CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 确保日志和 PID 目录存在
mkdir -p "${LOG_DIR}/${SERVICE_NAME}"
mkdir -p "${PID_DIR}/${SERVICE_NAME}"

chown ${USER_NAME}:${GROUP_NAME} -R "${LOG_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${PID_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${DATA_DIR}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <HMaster|HRegionServer|HThriftServer2> <start|stop|restart>"
  exit 1
fi

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

case "${COMPONENT_NAME}" in
"HMaster")
  case "${OPERATION}" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh start master" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh stop master" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh stop master" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh start master" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation for HMaster. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"HRegionServer")
  case "${OPERATION}" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh start regionserver" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh stop regionserver" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh stop regionserver" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh start regionserver" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation for HRegionServer. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"HThriftServer2")
  case "${OPERATION}" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh start thrift2 --port 29090" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh stop thrift2" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh stop thrift2" "${USER_NAME}"
    sleep 3
    su -c "${CURRENT_SERVICE_DIR}/bin/hbase-daemon.sh start thrift2 --port 29090" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation for HThriftServer2. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
*)
  echo "Invalid component name. Supported components: <HMaster|HRegionServer|HThriftServer2>"
  exit 1
  ;;
esac

exit 0