#!/bin/bash

# 初始化每个节点时需在 /etc/profile 中配置 source /opt/datalight/conf/env/datalight-env.sh
# 并在 .bashrc 和 .bash_profile 中添加 source /etc/profile
# 下面的占位符将在 Master 启动时被 directory.yaml 中配置的值替换
# 替换后举例:
# JAVA_HOME="/opt/jdk1.8.0_202"
# DATALIGHT_JAVA_HOME="/opt/jdk-17.0.13"
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

# 大数据服务使用的 JDK。各服务对 JDK 版本有兼容性要求，统一停留在 JDK 8。
# HDFS、YARN、HIVE 等服务的启动脚本都读这个变量。
export JAVA_HOME="{{JAVA_HOME}}"

# DataLight Master / Worker 自身使用的 JDK，固定 JDK 17。
# 与 JAVA_HOME 相互独立，不要互相覆盖。
export DATALIGHT_JAVA_HOME="{{DATALIGHT_JAVA_HOME}}"

export DATALIGHT_DIR="{{DATALIGHT_DIR}}"
export SERVICE_DIR="{{SERVICE_DIR}}"
export LOG_DIR="{{LOG_DIR}}"
export PID_DIR="{{PID_DIR}}"
export DATA_DIR="{{DATA_DIR}}"

# AI 智能体服务（datalight-services-ai）。与 Master、Worker 平级，默认不启用。
# 不装 Python 也不影响 DataLight 正常运行。
export DATALIGHT_AI_ENABLED="{{AI_ENABLED}}"
export DATALIGHT_AI_HOME="{{AI_HOME}}"
export DATALIGHT_AI_PORT="{{AI_PORT}}"
export DATALIGHT_AI_UV_BIN="{{AI_UV_BIN}}"

# PATH 里放服务侧 JDK，保证在节点上直接敲 java 时拿到的是大数据服务用的版本。
# 平台自身启动一律显式使用 ${DATALIGHT_JAVA_HOME}/bin/java，不依赖 PATH。
if [[ -n "${JAVA_HOME}" && -d "${JAVA_HOME}" ]]; then
  export CLASSPATH=".:${JAVA_HOME}/lib/dt.jar:${JAVA_HOME}/lib/tools.jar"
  export PATH="${JAVA_HOME}/bin:${PATH}"
fi

# 函数：设置所有者和权限
set_ownership_and_permissions() {
  USER_NAME="datalight"
  GROUP_NAME="datalight"

  if [[ $(id -u) -eq 0 ]]; then
    chown -R "$USER_NAME:$GROUP_NAME" "$1" || exit 1
    chmod -R 755 "$1" || exit 1
  fi
}

# 创建目录并设置权限
if [[ -n "${DATALIGHT_DIR}" ]]; then
  mkdir -p "${DATALIGHT_DIR}"
fi

if [[ -n "${SERVICE_DIR}" ]]; then
  mkdir -p "${SERVICE_DIR}"
fi

if [[ -n "${LOG_DIR}" ]]; then
  mkdir -p "${LOG_DIR}"
fi

if [[ -n "${PID_DIR}" ]]; then
  mkdir -p "${PID_DIR}"
fi

if [[ -n "${DATA_DIR}" ]]; then
  mkdir -p "${DATA_DIR}"
fi


set_ownership_and_permissions "${DATALIGHT_DIR}"
set_ownership_and_permissions "${SERVICE_DIR}"
set_ownership_and_permissions "${LOG_DIR}"
set_ownership_and_permissions "${PID_DIR}"
set_ownership_and_permissions "${DATA_DIR}"
