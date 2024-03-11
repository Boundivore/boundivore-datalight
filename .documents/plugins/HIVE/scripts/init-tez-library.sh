#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

# HDFS目标路径
TEZ_HDFS_DIR="/user/datalight/tez"

# tez.tar.gz 的本地路径
TEZ_TAR_LOCAL_PATH="${SERVICE_DIR}/HIVE/tez/path/tez.tar.gz"

# 检查tez.tar.gz文件是否存在
if [ ! -f "${TEZ_TAR_PATH}" ]; then
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

echo "tez.tar.gz has been successfully uploaded to HDFS at ${TEZ_HDFS_DIR}"