#!/bin/bash

HDFS_HOME="$1"
SPARK_HISTORY_LOGS_DIR="$2"
VERSION="$3"
chmod -R 777 /data/udp/$VERSION/spark/
"${HDFS_HOME}/bin/hadoop" fs -test -e "${SPARK_HISTORY_LOGS_DIR}"
if [ $? -eq 0 ] ;then
    echo "${SPARK_HISTORY_LOGS_DIR} already exists."
    su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -chmod  -R 777 ${SPARK_HISTORY_LOGS_DIR}"
else
    echo "${SPARK_HISTORY_LOGS_DIR} does not exist."
    su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -mkdir -p ${SPARK_HISTORY_LOGS_DIR}"
    su -s /bin/bash hadoop -c "${HDFS_HOME}/bin/hadoop fs -chmod  -R 777 ${SPARK_HISTORY_LOGS_DIR}"
fi