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

SERVICE_NAME="MONITOR"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <Prometheus|AlertManager|MySQLExporter|NodeExporter|Grafana> <start|stop|restart>"
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
"Prometheus")
  case "$1" in
  "start")
    #    su -c "${CURRENT_SERVICE_DIR}/prometheus/bin/prometheus.sh start" "${USER_NAME}"
    su -c "${CURRENT_SERVICE_DIR}/prometheus/bin/prometheus.sh start" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/prometheus/bin/prometheus.sh stop" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/prometheus/bin/prometheus.sh restart" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"AlertManager")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/alertmanager/bin/alert_manager.sh start" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/alertmanager/bin/alert_manager.sh stop" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/alertmanager/bin/alert_manager.sh restart" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"MySQLExporter")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/exporter/mysql/bin/mysql_exporter.sh start" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/exporter/mysql/bin/mysql_exporter.sh stop" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/exporter/mysql/bin/mysql_exporter.sh restart" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"NodeExporter")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/exporter/node/bin/node_exporter.sh start" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/exporter/node/bin/node_exporter.sh stop" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/exporter/node/bin/node_exporter.sh restart" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"Grafana")
  case "$1" in
  "start")
    su -c "${CURRENT_SERVICE_DIR}/grafana/bin/grafana.sh start" "${USER_NAME}"
    ;;
  "stop")
    su -c "${CURRENT_SERVICE_DIR}/grafana/bin/grafana.sh stop" "${USER_NAME}"
    ;;
  "restart")
    su -c "${CURRENT_SERVICE_DIR}/grafana/bin/grafana.sh restart" "${USER_NAME}"
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
*)
  echo "Invalid component name. Supported component: [PrometheusServer|AlertManager|MySQLExporter|NodeExporter|Grafana]"
  exit 1
  ;;
esac

exit 0
