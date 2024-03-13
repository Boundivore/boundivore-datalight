#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

# 日志文件路径
HIVE_LOG_DIR="${LOG_DIR}/HIVE"
# 确保日志目录存在
mkdir -p "${HIVE_LOG_DIR}"

LOG_FILE="${HIVE_LOG_DIR}/metastore-init.log"

# 初始化 MetaStore 的函数
initMetaStore() {
  # 确保 schematool 脚本存在并且可执行
  SCHEMATOOL_SCRIPT="${SERVICE_DIR}/HIVE/bin/schematool"
  if [ ! -x "${SCHEMATOOL_SCRIPT}" ]; then
    echo "schematool script does not exist or is not executable." | tee -a "${LOG_FILE}"
    exit 1
  fi

  # 执行初始化操作，并将输出重定向到日志文件
  if su -s /bin/bash datalight -c "${SCHEMATOOL_SCRIPT} -dbType mysql -initSchema" &>> "${LOG_FILE}"; then
    echo "MetaStore initialized successfully." | tee -a "${LOG_FILE}"
    exit 0
  else
    echo "Failed to initialize MetaStore." | tee -a "${LOG_FILE}"
    exit 0
  fi
}

# 调用初始化函数
initMetaStore