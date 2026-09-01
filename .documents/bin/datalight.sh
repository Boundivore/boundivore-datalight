#!/bin/bash

set -e

# 记录开始时间
start_time=$(date +%s)

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 检查参数是否为空
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <start|stop|restart> <master|worker|aiagent>"
  exit 1
fi

# 获取操作和组件名称
operation="$1"
component="$2"
port="$3"
master_ip="$4"

# 获取当前脚本所在目录
bin_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

# 从文件中读取主机信息
datalight_dir=$(realpath "${bin_dir}/../")
app_dir=$(realpath "${datalight_dir}/app/")
app_conf_dir=$(realpath "${datalight_dir}/app/config")
env_file="${datalight_dir}/conf/env/datalight-env.sh"

# 载入平台环境变量，其中 DATALIGHT_JAVA_HOME 指向平台自身使用的 JDK 17
if [ -f "${env_file}" ]; then
  # shellcheck source=/dev/null
  source "${env_file}"
fi

# 平台自身固定使用 DATALIGHT_JAVA_HOME 下的 JDK，不跟随 PATH。
# PATH 上的 java 是给大数据服务用的 JDK 8，两者不能混用。
if [ -n "${DATALIGHT_JAVA_HOME}" ] && [ -x "${DATALIGHT_JAVA_HOME}/bin/java" ]; then
  JAVA_BIN="${DATALIGHT_JAVA_HOME}/bin/java"
else
  echo "DATALIGHT_JAVA_HOME 未配置或不可用，请检查 conf/env/directory.yaml 中的 datalight-java-home" >&2
  exit 1
fi

# 校验版本，低于 17 直接拒绝启动，避免运行到一半才报 UnsupportedClassVersionError
java_major=$("${JAVA_BIN}" -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ -z "${java_major}" ] || [ "${java_major}" -lt 17 ]; then
  echo "DataLight Master/Worker 需要 JDK 17 及以上，当前: ${java_major}，路径: ${JAVA_BIN}" >&2
  exit 1
fi

# 启动 Spring Boot 程序
start_service() {
  local jar_name="$1"
  local api_type="$2"
  local port="$3"


  # 如果 masterIp 参数不为空，则将参数传递到 main 方法中
  if [[ -n "$master_ip" ]]; then
    if validate_ip "$master_ip"; then
      master_ip_arg="-DmasterIp=${master_ip}"
    else
      echo "masterIp is invalid"
      exit 1
    fi
  else
    echo "No masterIp provided or masterIp is empty. Skipping IP validation."
  fi

  # 根据api_type选择不同的配置文件
  local spring_config="--spring.config.location=${app_conf_dir}/application-${api_type}.yml"
  local log_config="-Dlogging.config=${app_conf_dir}/logback-${api_type}.xml"
  # JVM 配置
#  local jvm_opt="-Xmx3g -Xms1g -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=200 -XX:G1NewSizePercent=25 -XX:InitiatingHeapOccupancyPercent=65 -XX:+ParallelRefProcEnabled -XX:ConcGCThreads=4 -XX:ParallelGCThreads=16 -XX:MaxTenuringThreshold=8 -XX:G1HeapRegionSize=32m -XX:G1MixedGCCountTarget=32 -XX:G1OldCSetRegionThresholdPercent=5 -XX:MetaspaceSize=128m -XX:SurvivorRatio=4"
  local jvm_opt="-Xmx3g -Xms1g -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=200 -XX:G1NewSizePercent=25 -XX:InitiatingHeapOccupancyPercent=65 -XX:+ParallelRefProcEnabled -XX:ConcGCThreads=4 -XX:ParallelGCThreads=16 -XX:MaxTenuringThreshold=8 -XX:G1HeapRegionSize=32m -XX:G1MixedGCCountTarget=32 -XX:G1OldCSetRegionThresholdPercent=5 -XX:MetaspaceSize=256m -XX:SurvivorRatio=4"

  # JDK 17 模块系统默认封闭，Hutool、fastjson2、XStream 等库有深反射，需显式开放
  local jdk_opt="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED"

  local command="nohup ${JAVA_BIN} ${jdk_opt} ${log_config} ${jvm_opt} ${master_ip_arg} -jar ${app_dir}/${jar_name} ${spring_config} > /dev/null 2>&1 & echo ${api_type} starting in \$!..."
  # shellcheck disable=SC2155
  local pid=$(pgrep -f "${jar_name}")

  if [ -n "${pid}" ]; then
    echo "${api_type} is already started"
  else
    cd "${datalight_dir}"
    eval "${command}"

    if [ -n "${port}" ] && [ -n "${api_type}" ]; then
      # 等待Jar包初始化完成
      local attempt=0
      while ! curl -s "http://localhost:${port}/actuator/health" | grep -q '"status":"UP"'; do
        sleep 1
        attempt=$((attempt + 1))
        if [ $attempt -ge 180 ]; then
          echo "${api_type} failed to start after 120 attempts."
          exit 1
        fi
      done
    fi

    echo "${api_type} started."
  fi
}

# 启动 AIAgent。它是与 Master、Worker 平级的平台第三角色，进程本体是
# datalight-services-ai 仓库里的 Python 服务，由 conf/env/datalight-env.sh
# 里的 DATALIGHT_AI_* 变量控制，默认不启用。
start_aiagent() {
  if [ "${DATALIGHT_AI_ENABLED}" != "true" ]; then
    echo "AIAgent 未启用，跳过。如需启用，请在 conf/env/directory.yaml 中把 datalight.ai.enabled 设为 true"
    return 0
  fi

  if [ -z "${DATALIGHT_AI_HOME}" ] || [ ! -d "${DATALIGHT_AI_HOME}" ]; then
    echo "DATALIGHT_AI_HOME 未配置或目录不存在: ${DATALIGHT_AI_HOME}" >&2
    return 1
  fi

  local uv_bin="${DATALIGHT_AI_UV_BIN}"
  if [ -z "${uv_bin}" ]; then
    uv_bin=$(command -v uv || true)
  fi
  if [ -z "${uv_bin}" ] || [ ! -x "${uv_bin}" ]; then
    echo "未找到 uv，可执行 curl -LsSf https://astral.sh/uv/install.sh | sh 安装，或在 directory.yaml 中指定 uv-bin" >&2
    return 1
  fi

  # shellcheck disable=SC2155
  local pid=$(pgrep -f "uvicorn app.main:app")
  if [ -n "${pid}" ]; then
    echo "aiagent is already started"
    return 0
  fi

  local port="${DATALIGHT_AI_PORT:-8010}"
  cd "${DATALIGHT_AI_HOME}"
  nohup "${uv_bin}" run --directory services-ai uvicorn app.main:app     --host 0.0.0.0 --port "${port}" > /dev/null 2>&1 &
  echo "aiagent starting in $!..."

  local attempt=0
  while ! curl -s "http://localhost:${port}/api/v1/ai/py/health" | grep -q '"Code":"00000"'; do
    sleep 1
    attempt=$((attempt + 1))
    if [ ${attempt} -ge 60 ]; then
      echo "aiagent failed to start after 60 attempts." >&2
      return 1
    fi
  done

  echo "aiagent started."
  return 0
}

stop_aiagent() {
  # shellcheck disable=SC2155
  local pid=$(pgrep -f "uvicorn app.main:app")
  if [ -n "${pid}" ]; then
    kill "${pid}"
    echo "aiagent stopped."
  else
    echo "aiagent is not running."
  fi
}

# 停止 Spring Boot 程序
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

  # AIAgent 是 Python 进程，不走 jar 那一套。ai 是 aiagent 的简写，两个都收
  if [ "$api_type" = "aiagent" ] || [ "$api_type" = "ai" ]; then
    case "$operation" in
    "start")
      start_aiagent
      ;;
    "stop")
      stop_aiagent
      ;;
    "restart")
      stop_aiagent
      sleep 1
      start_aiagent
      ;;
    *)
      echo "Invalid operation. Usage: $0 <start|stop|restart> <master|worker|aiagent>"
      exit 1
      ;;
    esac
    return $?
  fi

  case "$api_type" in
  "master")
    jar_name=$(find "${app_dir}" -name "services-master-*.jar" | sort -V | tail -n 1 | xargs basename)
    ;;
  "worker")
    jar_name=$(find "${app_dir}" -name "services-worker-*.jar" | sort -V | tail -n 1 | xargs basename)
    ;;
  *)
    echo "Invalid component name. Supported components: master, worker, aiagent"
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
    echo "Invalid operation. Usage: $0 <start|stop|restart> <master|worker|aiagent>"
    exit 1
    ;;
  esac
}

# 验证 IP 是否符合正则规则
validate_ip() {
  local ip=$1
  local valid_ip_regex="^(([0-9]{1,3}\.){3}[0-9]{1,3})$"

  if [[ $ip =~ $valid_ip_regex ]]; then
    # The IP address is valid, now check each octet
    IFS='.' read -r -a octets <<< "$ip"
    for octet in "${octets[@]}"; do
      if ((octet < 0 || octet > 255)); then
        echo "Invalid IP address: $ip - octet $octet is out of range (0-255)"
        return 1
      fi
    done
  else
    echo "Invalid IP address: $ip - does not match IPv4 format (x.x.x.x where x is 0-255)"
    return 1
  fi

  return 0 # IP is valid
}

execute_operation "$component"

# 记录结束时间并计算总耗时
end_time=$(date +%s)
elapsed_time=$(( end_time - start_time ))

echo "$0 done."
echo "Total execution time: ${elapsed_time} seconds"
exit 0