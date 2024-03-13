#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

# HDFS目标路径
TEZ_HDFS_DIR="/tez"
TEZ_TAR_HDFS_PATH="${TEZ_HDFS_DIR}/tez.tar.gz"

# tez.tar.gz 的本地路径
TEZ_TAR_LOCAL_PATH="${SERVICE_DIR}/HIVE/tez/tez.tar.gz"

# 如果 HDFS (TEZ_HDFS_DIR) 的目录中存在 tez.tar.gz，则直接 exit 0 退出，不再执行后续的操作
if su -s /bin/bash datalight -c "hadoop fs -test -e ${TEZ_TAR_HDFS_PATH}"; then
    echo "tez.tar.gz already exists in HDFS at ${TEZ_HDFS_DIR}. Exiting."
    exit 0
fi

# 检查tez.tar.gz文件是否存在
if [ ! -f "${TEZ_TAR_LOCAL_PATH}" ]; then
    echo "Error: tez.tar.gz does not exist at specified path: ${TEZ_TAR_LOCAL_PATH}"
    exit 1
fi

# 使用 datalight 用户创建 HDFS 目标目录
if ! su -s /bin/bash datalight -c "hadoop fs -mkdir -p ${TEZ_HDFS_PATH}"; then
    echo "Error: Failed to create HDFS directory: ${TEZ_HDFS_DIR}"
    exit 1
fi

# 使用 datalight 用户上传 tez.tar.gz 到 HDFS
if ! su -s /bin/bash datalight -c "hadoop fs -put -f ${TEZ_TAR_LOCAL_PATH} ${TEZ_HDFS_DIR}/"; then
    echo "Error: Failed to upload tez.tar.gz to HDFS"
    exit 1
fi

su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -chmod g+w ${TEZ_HDFS_DIR}"
su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -chmod g+w ${TEZ_TAR_HDFS_PATH}"

echo "tez.tar.gz has been successfully uploaded to HDFS at ${TEZ_HDFS_DIR}"