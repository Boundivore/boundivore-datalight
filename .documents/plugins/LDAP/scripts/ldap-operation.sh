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

SERVICE_NAME="LDAP"
CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <Slapd|Rsyslog|LDAPExporter> <start|stop|restart|enable|disable>"
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

# 定义启动、停止和启用/禁用函数
start_slapd() {
  systemctl start slapd
  echo "Slapd started."
}

stop_slapd() {
  systemctl stop slapd
  echo "Slapd stopped."
}

enable_slapd() {
  systemctl enable slapd
  echo "Slapd enabled to start on boot."
}

disable_slapd() {
  systemctl disable slapd
  echo "Slapd disabled from starting on boot."
}

start_rsyslog() {
  systemctl start rsyslog
  echo "Rsyslog started."
}

stop_rsyslog() {
  systemctl stop rsyslog
  echo "Rsyslog stopped."
}

enable_rsyslog() {
  systemctl enable rsyslog
  echo "Rsyslog enabled to start on boot."
}

disable_rsyslog() {
  systemctl disable rsyslog
  echo "Rsyslog disabled from starting on boot."
}

start_LDAPExporter() {
  "${CURRENT_SERVICE_DIR}/exporter/ldap/bin/ldap_exporter.sh" start
  echo "LDAPExporter started."
}

stop_LDAPExporter() {
  "${CURRENT_SERVICE_DIR}/exporter/ldap/bin/ldap_exporter.sh" stop
  echo "LDAPExporter stopped."
}

restart_LDAPExporter() {
  stop_LDAPExporter
  sleep 2
  start_LDAPExporter
}

# 执行相应的启动、停止、启用或禁用命令
case "${COMPONENT_NAME}" in
  "Slapd")
    case "${OPERATION}" in
      "start")
        start_slapd
        ;;
      "stop")
        stop_slapd
        ;;
      "restart")
        stop_slapd
        sleep 2
        start_slapd
        ;;
      "enable")
        enable_slapd
        ;;
      "disable")
        disable_slapd
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart|enable|disable]"
        exit 1
        ;;
    esac
    ;;
  "Rsyslog")
    case "${OPERATION}" in
      "start")
        start_rsyslog
        ;;
      "stop")
        stop_rsyslog
        ;;
      "restart")
        stop_rsyslog
        sleep 2
        start_rsyslog
        ;;
      "enable")
        enable_rsyslog
        ;;
      "disable")
        disable_rsyslog
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart|enable|disable]"
        exit 1
        ;;
    esac
    ;;
  "LDAPExporter")
    case "${OPERATION}" in
      "start")
        start_LDAPExporter
        ;;
      "stop")
        stop_LDAPExporter
        ;;
      "restart")
        restart_LDAPExporter
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported components: <Slapd|Rsyslog|LDAPExporter>"
    exit 1
    ;;
esac

exit 0