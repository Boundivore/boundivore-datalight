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

SERVICE_NAME="SSSD"

# 检查参数是否为空
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <Sssd|Sshd> <start|stop|restart|enable|disable>"
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
start_sssd() {
  systemctl start sssd
  echo "SSSD started."
}

stop_sssd() {
  systemctl stop sssd
  echo "SSSD stopped."
}

enable_sssd() {
  systemctl enable sssd
  echo "SSSD enabled to start on boot."
}

disable_sssd() {
  systemctl disable sssd
  echo "SSSD disabled from starting on boot."
}

start_sshd() {
  systemctl start sshd
  echo "SSHD started."
}

stop_sshd() {
  systemctl stop sshd
  echo "SSHD stopped."
}

enable_sshd() {
  systemctl enable sshd
  echo "SSHD enabled to start on boot."
}

disable_sshd() {
  systemctl disable sshd
  echo "SSHD disabled from starting on boot."
}

# 执行相应的启动、停止、启用或禁用命令
case "${COMPONENT_NAME}" in
  "Sssd")
    case "${OPERATION}" in
      "start")
        start_sssd
        ;;
      "stop")
        stop_sssd
        ;;
      "restart")
        stop_sssd
        sleep 2
        start_sssd
        ;;
      "enable")
        enable_sssd
        ;;
      "disable")
        disable_sssd
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart|enable|disable]"
        exit 1
        ;;
    esac
    ;;
  "Sshd")
    case "${OPERATION}" in
      "start")
        start_sshd
        ;;
      "stop")
        stop_sshd
        ;;
      "restart")
        stop_sshd
        sleep 2
        start_sshd
        ;;
      "enable")
        enable_sshd
        ;;
      "disable")
        disable_sshd
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart|enable|disable]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported components: <Sssd|Sshd>"
    exit 1
    ;;
esac

exit 0