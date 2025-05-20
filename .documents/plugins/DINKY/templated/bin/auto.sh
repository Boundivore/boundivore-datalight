#!/bin/bash
# debug mode
#set -x

export RED='\033[31m'
export GREEN='\033[32m'
export YELLOW='\033[33m'
export BLUE='\033[34m'
export MAGENTA='\033[35m'
export CYAN='\033[36m'
export RESET='\033[0m'

ENV_FILE="/etc/profile.d/dinky_env"
if [ -f "${ENV_FILE}" ]; then
    source "${ENV_FILE}"
fi

DB_ENV_FILE="/etc/profile.d/dinky_db"
if [ -f "${DB_ENV_FILE}" ]; then
    source "${DB_ENV_FILE}"
fi

source /etc/profile

RETURN_HOME_PATH=""
function get_home_path() {
    SOURCE="${BASH_SOURCE[0]}"
    while [ -h "$SOURCE" ]; do
        DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
        SOURCE="$(readlink "$SOURCE")"
        [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
    done
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    RETURN_HOME_PATH=$(dirname "$DIR")
}

if [ -z "${DINKY_HOME}" ]; then
    echo -e "${RED}DINKY_HOME environment variable is not set. Attempting to determine the correct path...${RESET}"
    get_home_path
    export DINKY_HOME=${RETURN_HOME_PATH}
    echo -e "${GREEN}DINKY_HOME is set to: ${DINKY_HOME}${RESET}"
else
    get_home_path
    if [ "${DINKY_HOME}" != "${RETURN_HOME_PATH}" ]; then
        export DINKY_HOME=${RETURN_HOME_PATH}
        echo -e "${YELLOW}DINKY_HOME is not equal to the current path, reset DINKY_HOME to: ${RETURN_HOME_PATH}${RESET}"
    else
        echo -e "${GREEN}DINKY_HOME is already set to: ${DINKY_HOME}${RESET}"
    fi
fi

FLINK_VERSION=${2}

JAVA_VERSION=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}' | awk -F'.' '{print $1"."$2}')

APP_HOME="${DINKY_HOME}"

DINKY_LOG_PATH="{{DINKY_LOG_PATH}}"
if [ ! -d "${DINKY_LOG_PATH}" ]; then
  mkdir -p "${DINKY_LOG_PATH}"
fi

CUSTOMER_JAR_PATH="${APP_HOME}/customJar"
if [ ! -d "${CUSTOMER_JAR_PATH}" ]; then
  mkdir -p "${CUSTOMER_JAR_PATH}"
fi

EXTENDS_HOME="${APP_HOME}/extends"

JAR_NAME="dinky-admin"

if [ -z "${FLINK_VERSION}" ]; then
  # Obtain the Flink version under EXTENDSHOME, only perform recognition and do not perform any other operations, for prompt purposes
  FLINK_VERSION_SCAN=$(ls -n ${EXTENDS_HOME} |  grep '^d' | grep flink | awk -F 'flink' '{print $2}')
  # If FLINK_VERSION-SCAN is not empty, assign FLINK_VERSION-SCAN to FLINK_VERSION
  if [ -n "${FLINK_VERSION_SCAN}" ]; then
    FLINK_VERSION=${FLINK_VERSION_SCAN}
  fi
fi

echo -e "${GREEN}DINKY_HOME : ${APP_HOME} , JAVA_VERSION : ${JAVA_VERSION} , FLINK_VERSION : ${FLINK_VERSION} ${RESET}"

# Check whether the flink version is specified
assertIsInputVersion() {
  # If FLINK_VERSION is still empty, prompt the user to enter the Flink version
  if [ -z "${FLINK_VERSION}" ]; then
    echo -e "${RED}The flink version is not specified and the flink version cannot be found under the extends directory. Please specify the flink version${RESET}"
    exit 1
  fi
}

if [ -f "${APP_HOME}/config/application.yml" ]; then
  result=$("${APP_HOME}"/bin/parse_yml.sh "${APP_HOME}/config/application.yml" "server.port")
  APP_PORT=$result
fi

echo -e "${GREEN}From ${APP_HOME}/config/application.yml server.port: ${APP_PORT}${RESET}"

if [ -z "$APP_PORT" ]; then
    echo -e "${RED}Could not find server.port in configuration files, using default port 8888 ${RESET}"
    APP_PORT=8888
fi

# Function: Check the status of the health check endpoint
check_health() {
    curl --silent --max-time 2 --output /dev/null --write-out "%{http_code}" "http://localhost:$APP_PORT/actuator/health"
}

format_time() {
    local seconds=$1
    local hours=$((seconds / 3600))
    local minutes=$(( (seconds % 3600) / 60 ))
    local remaining_seconds=$((seconds % 60))
    printf "%02d:%02d:%02d" $hours $minutes $remaining_seconds
}

# 修改后的等待启动函数 - 无进度条
function wait_start_process() {
    echo -e "${GREEN}Starting application, please wait...${RESET}"
    local max_attempts=100
    local attempt=0
    local delay=0.25
    local health_status=""
    local success_status_codes=("200")
    local start_time=$(date +%s)

    while [ $attempt -lt $max_attempts ]; do
        attempt=$((attempt + 1))
        health_status=$(check_health)

        for code in "${success_status_codes[@]}"; do
            if [ "$health_status" == "$code" ]; then
                local current_time=$(date +%s)
                local elapsed_time=$((current_time - start_time))
                local formatted_time=$(format_time $elapsed_time)
                echo -e "${GREEN}Application started successfully (time: ${formatted_time})${RESET}"
                return 0
            fi
        done

        sleep $delay
    done

    local current_time=$(date +%s)
    local elapsed_time=$((current_time - start_time))
    local formatted_time=$(format_time $elapsed_time)
    echo -e "${RED}Failed to start application after ${formatted_time}${RESET}"
    return 1
}

# Use FLINK_HOME:
CLASS_PATH="${APP_HOME}:${APP_HOME}/lib/*:${APP_HOME}/config:${EXTENDS_HOME}/*:${CUSTOMER_JAR_PATH}/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/dinky/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/flink/*:${EXTENDS_HOME}/flink${FLINK_VERSION}/*"

# Log configuration file path
LOG_CONFIG=${APP_HOME}/config/log4j2.xml

if [ ${JAVA_VERSION} = "1.8" ];then
  # JVM options G1GC and OOM dump ; Note: Do not set the DisableExplicitGC parameter. Because there is a call to System. gc() in the code.
   GC_OPT="-XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -XX:+PrintGCCause -Xloggc:${APP_HOME}/logs/gc-%t.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=20M"
else
   GC_OPT="-XX:+UseG1GC -XX:+PrintGCDetails -Xloggc:${APP_HOME}/logs/gc-%t.log"
fi

# OOM dump path
OOM_OPT="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_HOME}/logs/heapdump.hprof"
# JVM parameters and log path
PARAMS_OPT="-Ddinky.logs.path=${DINKY_LOG_PATH} -Ddinky.root.path=${APP_HOME} -Ddruid.mysql.usePingMethod=false -Dlog4j2.isThreadContextMapInheritable=true"
# JAR parameters
JAR_PARAMS_OPT="--logging.config=${LOG_CONFIG}"
# JMX path
JMX="-javaagent:$APP_HOME/lib/jmx_prometheus_javaagent-0.20.0.jar=10087:$APP_HOME/config/jmx/jmx_exporter_config.yaml"
#JVM OPTS
JVM_OPTS="-Xms512M -Xmx2048M -XX:PermSize=512M -XX:MaxPermSize=1024M"

tips() {
  echo ""
  echo -e "${YELLOW}WARNING!!!......Tips, please use command: sh auto.sh [start|startOnPending|startWithJmx|stop|restart|restartWithJmx|status].   For example: sh auto.sh start   ${RESET}"
  echo ""
  exit 1
}

# 使用更精确的方式查找Dinky进程
find_dinky_process() {
  ps -ef | grep "org.dinky.Dinky" | grep -v grep | awk '{print $2}' | head -1
}

start() {
  assertIsInputVersion
  # 查找是否有正在运行的Dinky进程
  local running_pid=$(find_dinky_process)

  if [ -n "$running_pid" ]; then
    echo -e "$YELLOW Dinky process is already running with PID: $running_pid. Please stop first !!!$RESET"
  else
    nohup java ${PARAMS_OPT} ${JVM_OPTS} ${OOM_OPT} ${GC_OPT} -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky ${JAR_PARAMS_OPT} > ${DINKY_LOG_PATH}/dinky-start.log 2>&1 &
    PID=$!
    echo -e "${GREEN}Started Dinky process with PID: $PID ${RESET}"

    if ! wait_start_process; then
        echo -e "${RED}Application start failed. Please check the log for details. You can execute tail -fn1000 ${DINKY_LOG_PATH}/dinky-start.log to watch the log ${RESET}"
        exit 1
    fi
    echo -e "${GREEN}........................................Start Dinky Successfully........................................${RESET}"
    echo -e "${GREEN}Current log path: ${DINKY_LOG_PATH}/dinky-start.log, you can execute tail -fn1000 ${DINKY_LOG_PATH}/dinky-start.log to watch the log${RESET}"
  fi
}

startOnPending() {
  assertIsInputVersion
  # 查找是否有正在运行的Dinky进程
  local running_pid=$(find_dinky_process)

  if [ -n "$running_pid" ]; then
    echo -e "$YELLOW Dinky process is already running with PID: $running_pid. Please stop first !!!$RESET"
  else
    java ${PARAMS_OPT} ${JVM_OPTS} ${OOM_OPT} ${GC_OPT} -Xverify:none -cp "${CLASS_PATH}" org.dinky.Dinky ${JAR_PARAMS_OPT}
    echo -e "$GREEN........................................Start Dinky Successfully........................................$RESET"
  fi
}

startWithJmx() {
  assertIsInputVersion
  # 查找是否有正在运行的Dinky进程
  local running_pid=$(find_dinky_process)

  if [ -n "$running_pid" ]; then
    echo -e "$YELLOW Dinky process is already running with PID: $running_pid. Please stop first !!!$RESET"
  else
    nohup java ${PARAMS_OPT} ${JVM_OPTS} ${OOM_OPT} ${GC_OPT} -Xverify:none "${JMX}" -cp "${CLASS_PATH}" org.dinky.Dinky ${JAR_PARAMS_OPT} > ${DINKY_LOG_PATH}/dinky-start.log 2>&1 &
    PID=$!
    echo -e "${GREEN}Started Dinky process with JMX enabled. PID: $PID ${RESET}"

    if ! wait_start_process; then
        echo -e "${RED}Application start failed. Please check the log for details. You can execute tail -fn1000 ${DINKY_LOG_PATH}/dinky-start.log to watch the log ${RESET}"
        exit 1
    fi
    echo -e "$GREEN........................................Start Dinky with Jmx Successfully........................................$RESET"
  fi
}

stop() {
  # 查找Dinky进程
  local running_pid=$(find_dinky_process)

  if [ -z "$running_pid" ]; then
    echo "No Dinky process found. Nothing to stop."
  else
    echo "Stopping Dinky process with PID: $running_pid"
    kill -9 $running_pid
    sleep 1

    # 验证进程是否已停止
    if ps -p $running_pid > /dev/null 2>&1; then
      echo -e "${RED}Failed to stop Dinky process with PID: $running_pid${RESET}"
    else
      echo -e "$GREEN........................................Stop Dinky Successfully.....................................$RESET"
    fi
  fi
}

status() {
  # 查找Dinky进程
  local running_pid=$(find_dinky_process)

  if [ -z "$running_pid" ]; then
    echo ""
    echo -e "${RED}Service ${JAR_NAME} is not running!${RESET}"
    echo ""
    exit 1
  else
    echo ""
    echo -e "${GREEN}Service ${JAR_NAME} is running. It's PID=${running_pid}${RESET}"
    echo ""
  fi
}

restart() {
  echo ""
  assertIsInputVersion
  stop
  sleep 2
  start
  echo -e "${GREEN}........................................Restart Successfully........................................$RESET"
}

restartWithJmx() {
  echo ""
  assertIsInputVersion
  stop
  sleep 2
  startWithJmx
  echo -e "${GREEN}........................................Restart with Jmx Successfully........................................$RESET"
}

case "$1" in
"start")
  start
  ;;
"startOnPending")
  startOnPending
  ;;
"startWithJmx")
  startWithJmx
  ;;
"stop")
  stop
  ;;
"status")
  status
  ;;
"restart")
  restart
  ;;
"restartWithJmx")
  restartWithJmx
  ;;
*)
  tips
  ;;
esac