#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 定义FE节点的信息
FE_HOST="node01"
FE_PORT=7030
FE_USER="root"

# 列出所有要添加的BE节点，格式为 "hostname:port"
# TODO 临时测试，后续将变更为真实值
BE_NODES=("node01:7050" "node02:7050" "node03:7050")

# 遍历BE节点列表，对每个节点执行添加操作
for BE_NODE in "${BE_NODES[@]}"; do
    echo "Adding backend node: $BE_NODE"
    mysql -h$FE_HOST -P$FE_PORT -u$FE_USER -e "ALTER SYSTEM ADD BACKEND '$BE_NODE';"
done