#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 检查参数是否为空
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <start|stop|restart> <master|worker>"
  exit 1
fi

# 获取操作和组件名称
operation="$1"
component="$2"
port="$3"

# 获取当前脚本所在目录
bin_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

# 从文件中读取主机信息
datalight_dir=$(realpath "${bin_dir}/../")
app_dir=$(realpath "${datalight_dir}/app/")
app_conf_dir=$(realpath "${datalight_dir}/app/config")

# 函数：启动 Spring Boot 程序
start_service() {
  local jar_name="$1"
  local api_type="$2"
  local port="$3"

  local command="nohup java -Dlogback.configurationFile=${app_conf_dir}/logback-${api_type}.xml -jar "${app_dir}/${jar_name}" > /dev/null 2>&1 & echo ${api_type} starting in \$!..."
  # shellcheck disable=SC2155
  local pid=$(pgrep -f "${jar_name}")

  if [ -n "${pid}" ]; then
    echo "${api_type} is already started"
  else
    cd "${datalight_dir}"
    eval "${command}"

    if [ -n "${port}" ] && [ -n "${api_type}" ]; then
      # 等待Jar包初始化完成
      while ! curl -s "http://localhost:${port}/api/v1/${api_type}/actuator/health" | grep -q '"status":"UP"'; do
        sleep 1
      done
    fi

    echo "${api_type} started."
  fi
}

# 函数：停止 Spring Boot 程序
stop_service() {
  local jar_name="$1"
  local api_type="$2"

  # shellcheck disable=SC2155
  local pid=$(pgrep -f "${jar_name}")

  if [ -n "${pid}" ]; then
    kill -9 "${pid}"
    echo "${api_type} stopped."
  else
    echo "${api_type} is not running."
  fi
}

execute_operation() {
  local jar_name
  local api_type="$1"

  case "$api_type" in
  "master")
    jar_name="services-master-1.0.0.jar"
    ;;
  "worker")
    jar_name="services-worker-1.0.0.jar"
    ;;
  *)
    echo "Invalid component name. Supported components: master, worker"
    exit 1
    ;;
  esac

  case "$operation" in
  "start")
    start_service "$jar_name" "$api_type" "$port"
    ;;
  "stop")
    stop_service "$jar_name" "$api_type"
    ;;
  "restart")
    stop_service "$jar_name" "$api_type"
    sleep 1
    start_service "$jar_name" "$api_type" "$port"
    ;;
  *)
    echo "Invalid operation. Usage: $0 <start|stop|restart> <master|worker>"
    exit 1
       ;;
  esac
}

execute_operation "$component"

echo "$0 done."
exit 0