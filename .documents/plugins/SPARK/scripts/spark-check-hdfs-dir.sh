#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

HDFS_HOME="${SERVICE_DIR}/HDFS"
SPARK_HISTORY_LOGS_DIR="$1"

"${HDFS_HOME}/bin/hadoop" fs -test -e "${SPARK_HISTORY_LOGS_DIR}"

if [ $? -eq 0 ] ;then
    echo "${SPARK_HISTORY_LOGS_DIR} already exists."
    su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -chmod  -R 777 ${SPARK_HISTORY_LOGS_DIR}"
else
    echo "${SPARK_HISTORY_LOGS_DIR} does not exist.Creating..."
    su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -mkdir -p ${SPARK_HISTORY_LOGS_DIR}"
    su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -chmod  -R 777 ${SPARK_HISTORY_LOGS_DIR}"
fi