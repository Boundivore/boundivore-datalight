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

SERVICE_NAME="DINKY"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <DinkyServer> <start|stop|restart|status>"
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

# 查找Dinky进程的更精确方法
find_dinky_process() {
  ps -ef | grep "org.dinky.Dinky" | grep -v grep | awk '{print $2}' | head -1
}

# 定义启动函数
start_dinkyserver() {
  # 检查是否已经在运行
  PID=$(find_dinky_process)
  if [ -n "$PID" ]; then
    echo "DinkyServer is already running with PID: $PID"
    return
  fi

  # 以datalight用户启动服务
  echo "Starting DinkyServer as user ${USER_NAME}..."
  cd ${CURRENT_SERVICE_DIR}
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/bin/auto.sh start"

  # 验证是否成功启动
  sleep 3
  PID=$(find_dinky_process)
  if [ -n "$PID" ]; then
    echo "DinkyServer started successfully with PID: $PID"
  else
    echo "Warning: DinkyServer may have failed to start. Check logs at ${LOG_DIR}/${SERVICE_NAME}/logs/dinky-start.log"
  fi
}

# 定义停止函数
stop_dinkyserver() {
  # 查找正在运行的进程
  PID=$(find_dinky_process)
  if [ -n "$PID" ]; then
    echo "Stopping DinkyServer with PID: $PID"
    kill -9 $PID
    sleep 2

    # 检查是否成功停止
    if ps -p $PID > /dev/null 2>&1; then
      echo "Warning: Failed to kill process $PID"
    else
      echo "DinkyServer stopped successfully"
    fi
  else
    echo "DinkyServer is not running."
  fi
}

# 查看状态函数
status_dinkyserver() {
  PID=$(find_dinky_process)
  if [ -n "$PID" ]; then
    echo "DinkyServer is running with PID: $PID"
  else
    echo "DinkyServer is not running."
  fi
}

# 执行相应的启动或停止命令
case "${COMPONENT_NAME}" in
  "DinkyServer")
    case "${OPERATION}" in
      "start")
        start_dinkyserver
        ;;
      "stop")
        stop_dinkyserver
        ;;
      "restart")
        stop_dinkyserver
        sleep 3
        start_dinkyserver
        ;;
      "status")
        status_dinkyserver
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart|status]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported component: <DinkyServer>"
    exit 1
    ;;
esac

exit 0