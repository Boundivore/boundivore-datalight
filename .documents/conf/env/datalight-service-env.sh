#!/bin/bash

# 获取当前脚本所在目录的绝对路径
env_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

# 获取 datalight-env.sh 的绝对路径
source_file_path=$(realpath "${env_dir}/datalight-env.sh")

# shellcheck source=./datalight-env.sh
source "${source_file_path}"

# BASE
# shellcheck disable=SC2153
export ZOOKEEPER_HOME=${SERVICE_DIR}/ZOOKEEPER
export ZOOKEEPER_BIN=${ZOOKEEPER_HOME}/bin

# COMPUTE
export YARN_HOME=${SERVICE_DIR}/YARN
export YARN_BIN=${YARN_HOME}/bin

export HIVE_HOME=${SERVICE_DIR}/HIVE
export HIVE_BIN=${HIVE_HOME}/bin

export SPARK_HOME=${SERVICE_DIR}/SPARK
export SPARK_BIN=${SPARK_HOME}/bin

export FLINK_HOME=${SERVICE_DIR}/FLINK
export FLINK_BIN=${FLINK_HOME}/bin

export KYUUBI_HOME=${SERVICE_DIR}/KYUUBI
export KYUUBI_BIN=${KYUUBI_HOME}/bin

# STORAGE
export HDFS_HOME=${SERVICE_DIR}/HDFS
export HDFS_BIN=${HDFS_HOME}/bin

export HBASE_HOME=${SERVICE_DIR}/HBASE
export HBASE_BIN=${HBASE_HOME}/bin

export KAFKA_HOME=${SERVICE_DIR}/KAFKA
export KAFKA_BIN=${KAFKA_HOME}/bin

export SERVICE_BIN_STR="${ZOOKEEPER_BIN}:${YARN_BIN}:${HIVE_BIN}:${SPARK_BIN}:${FLINK_BIN}:${KYUUBI_BIN}:${HDFS_BIN}:${HBASE_BIN}:${KAFKA_BIN}"
export PATH="${PATH}:${SERVICE_BIN_STR}"