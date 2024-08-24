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

SERVICE_NAME="LDAPVIEWER"

# 检查参数是否为空
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 LDAPViewerServer <start|stop|restart|enable|disable>"
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
start_ldapviewerserver() {
  systemctl start httpd
  echo "LDAPViewerServer (httpd) started."
}

stop_ldapviewerserver() {
  systemctl stop httpd
  echo "LDAPViewerServer (httpd) stopped."
}

enable_ldapviewerserver() {
  systemctl enable httpd
  echo "LDAPViewerServer (httpd) enabled to start on boot."
}

disable_ldapviewerserver() {
  systemctl disable httpd
  echo "LDAPViewerServer (httpd) disabled from starting on boot."
}

# 执行相应的启动、停止、启用或禁用命令
case "${COMPONENT_NAME}" in
  "LDAPViewerServer")
    case "${OPERATION}" in
      "start")
        start_ldapviewerserver
        ;;
      "stop")
        stop_ldapviewerserver
        ;;
      "restart")
        stop_ldapviewerserver
        sleep 2
        start_ldapviewerserver
        ;;
      "enable")
        enable_ldapviewerserver
        ;;
      "disable")
        disable_ldapviewerserver
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart|enable|disable]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported component: LDAPViewerServer"
    exit 1
    ;;
esac

exit 0