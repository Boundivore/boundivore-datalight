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
HIVE_CONFIG_SCRIPT="${CURRENT_SERVICE_DIR}/bin/hive-config.sh"

HIVE_LOG_DIR="${LOG_DIR}/${SERVICE_NAME}"
METASTORE_LOG_FILE="${HIVE_LOG_DIR}/hive-metastore-$(date +%Y-%m-%d).log"
HIVESERVER2_LOG_FILE="${HIVE_LOG_DIR}/hiveserver2-$(date +%Y-%m-%d).log"

HIVE_PID_DIR="${PID_DIR}/${SERVICE_NAME}"
METASTORE_PID_FILE="${HIVE_PID_DIR}/hive-metastore.pid"
HIVESERVER2_PID_FILE="${HIVE_PID_DIR}/hiveserver2.pid"
TEZUI_PID_FILE="${HIVE_PID_DIR}/tez-ui.pid"

# 确保日志和 PID 目录存在
mkdir -p "${LOG_DIR}/${SERVICE_NAME}"
mkdir -p "${PID_DIR}/${SERVICE_NAME}"

chown ${USER_NAME}:${GROUP_NAME} -R "${LOG_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${PID_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${DATA_DIR}"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <MetaStore|HiveServer2|TezUI> <start|stop|restart>"
  exit 1
fi

# 检查 Hive 配置脚本是否存在并且可执行
if [ ! -x "$HIVE_CONFIG_SCRIPT" ]; then
  echo "The Hive configuration script does not exist or is not executable. Please check the path and permissions."
  exit 1
fi

# 确保日志和PID目录存在
mkdir -p "${HIVE_LOG_DIR}"
mkdir -p "${HIVE_PID_DIR}"

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 加载 Hive 配置
# shellcheck source=/srv/datalight/HIVE/bin/hive-config.sh
source "${HIVE_CONFIG_SCRIPT}"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# 定义启动和停止函数
start_metastore() {
  su -s /bin/bash "${USER_NAME}" -c "nohup hive --service metastore > \"${METASTORE_LOG_FILE}\" 2>&1 & echo \$! > \"${METASTORE_PID_FILE}\""
  echo "MetaStore started with PID $(cat "${METASTORE_PID_FILE}")."
}

stop_metastore() {
  if [ -f "${METASTORE_PID_FILE}" ]; then
    kill "$(cat "${METASTORE_PID_FILE}")" && rm -f "${METASTORE_PID_FILE}"
    echo "MetaStore stopped."
  else
    echo "MetaStore PID file does not exist."
  fi
}

start_hiveserver2() {
  su -s /bin/bash "${USER_NAME}" -c "nohup hiveserver2 > \"${HIVESERVER2_LOG_FILE}\" 2>&1 & echo \$! > \"${HIVESERVER2_PID_FILE}\""
  echo "HiveServer2 started with PID $(cat "${HIVESERVER2_PID_FILE}")."
}

stop_hiveserver2() {
  if [ -f "${HIVESERVER2_PID_FILE}" ]; then
    kill "$(cat "${HIVESERVER2_PID_FILE}")" && rm -f "${HIVESERVER2_PID_FILE}"
    echo "HiveServer2 stopped."
  else
    echo "HiveServer2 PID file does not exist."
  fi
}


start_tezui() {
  if [ -f "${TEZUI_PID_FILE}" ]; then
    echo "TezUI is already running."
  else
    su "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/tez/tomcat/bin/startup.sh"
    echo $! > "${TEZUI_PID_FILE}"
    chown "${USER_NAME}":"${USER_NAME}" "${TEZUI_PID_FILE}"
    echo "TezUI started with PID $(cat "${TEZUI_PID_FILE}")."
  fi
}

stop_tezui() {
    local timeout=30
    local force_kill=false

    # 检查是否有tezui相关进程正在运行（不依赖pid文件）
    local running_pids=$(ps -ef | grep "/tez/tomcat" | grep -v grep | awk '{print $2}')

    if [ -z "$running_pids" ]; then
        echo "No TezUI process found running."
        # 清理可能存在的过期pid文件
        [ -f "${TEZUI_PID_FILE}" ] && rm -f "${TEZUI_PID_FILE}"
        return 0
    fi

    echo "Found running TezUI process(es): $running_pids"

    # 尝试正常关闭
    if [ -f "${CURRENT_SERVICE_DIR}/tez/tomcat/bin/shutdown.sh" ]; then
        echo "Attempting graceful shutdown using shutdown.sh..."
        su "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/tez/tomcat/bin/shutdown.sh" || true
        sleep 5  # 给一些时间让进程正常退出
    fi

    # 检查并终止所有相关进程
    for pid in $running_pids; do
        if ps -p "$pid" > /dev/null 2>&1; then
            echo "Stopping TezUI process $pid..."

            # 首先尝试正常终止
            kill "$pid" 2>/dev/null || true

            # 等待进程退出
            local counter=0
            while ps -p "$pid" > /dev/null 2>&1; do
                counter=$((counter + 1))
                if [ $counter -ge $timeout ]; then
                    echo "Process $pid did not stop gracefully after $timeout seconds, forcing kill..."
                    force_kill=true
                    break
                fi
                sleep 1
            done

            # 如果进程仍然存在，强制终止
            if [ "$force_kill" = true ]; then
                echo "Forcing kill of process $pid..."
                kill -9 "$pid" 2>/dev/null || true
                sleep 1
            fi

            # 最终确认进程已经终止
            if ps -p "$pid" > /dev/null 2>&1; then
                echo "WARNING: Failed to stop process $pid"
            else
                echo "Successfully stopped process $pid"
            fi
        fi
    done

    # 清理PID文件
    if [ -f "${TEZUI_PID_FILE}" ]; then
        rm -f "${TEZUI_PID_FILE}"
        echo "Removed PID file"
    fi

    # 最终确认没有遗留进程
    running_pids=$(ps -ef | grep "/tez/tomcat" | grep -v grep | awk '{print $2}')
    if [ -z "$running_pids" ]; then
        echo "TezUI stopped successfully."
        return 0
    else
        echo "WARNING: Some TezUI processes may still be running: $running_pids"
        return 1
    fi
}

# 执行相应的启动或停止命令
case "${COMPONENT_NAME}" in
  "MetaStore")
    case "${OPERATION}" in
      "start")
        start_metastore
        ;;
      "stop")
        stop_metastore
        ;;
      "restart")
        stop_metastore
        sleep 2
        start_metastore
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  "HiveServer2")
    case "${OPERATION}" in
      "start")
        start_hiveserver2
        ;;
      "stop")
        stop_hiveserver2
        ;;
      "restart")
        stop_hiveserver2
        sleep 2
        start_hiveserver2
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
       exit 1
        ;;
    esac
    ;;
  "TezUI")
      case "${OPERATION}" in
        "start")
          start_tezui
          ;;
        "stop")
          stop_tezui
          ;;
        "restart")
          stop_tezui
          sleep 2
          start_tezui
          ;;
        *)
          echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
         exit 1
          ;;
      esac
      ;;
  *)
    echo "Invalid component name. Supported components: <MetaStore|HiveServer2|TezUI>"
    exit 1
    ;;
esac

exit 0
