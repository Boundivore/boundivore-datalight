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
    su datalight -c "${CURRENT_SERVICE_DIR}/tez/tomcat/bin/startup.sh"
    echo $! > "${TEZUI_PID_FILE}"
    chown "${USER_NAME}":"${USER_NAME}" "${TEZUI_PID_FILE}"
    echo "TezUI started with PID $(cat "${TEZUI_PID_FILE}")."
  fi
}

stop_tezui() {
  if [ -f "${TEZUI_PID_FILE}" ]; then
    pid=$(cat "${TEZUI_PID_FILE}")
    su datalight -c "${CURRENT_SERVICE_DIR}/tez/tomcat/bin/shutdown.sh"
    if kill -0 "$pid" 2>/dev/null; then
      kill "$pid" && rm -f "${TEZUI_PID_FILE}"
      echo "TezUI stopped."
    else
      rm -f "${TEZUI_PID_FILE}"
      echo "TezUI PID file was found but the process was not running. PID file removed."
    fi
  else
    echo "TezUI PID file does not exist. Check if TezUI is running."
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
