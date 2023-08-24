#!/bin/bash
# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"


set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# shellcheck disable=SC2034
USER_NAME="datalight"
# shellcheck disable=SC2034
GROUP_NAME="datalight"

SERVICE_NAME="HDFS"
CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 格式化 ZKFC
formatZFKC() {
  /usr/bin/expect <<-EOF
        set timeout -1
        spawn su -c "${CURRENT_SERVICE_DIR}/bin/hdfs zkfc -formatZK" "${USER_NAME}"
        expect {
            "Proceed formatting*" { send "N\r"; exp_continue }
            eof
        }
EOF

  wait
  echo "Finish NameNode formatted"
}

formatZFKC

exit 0
