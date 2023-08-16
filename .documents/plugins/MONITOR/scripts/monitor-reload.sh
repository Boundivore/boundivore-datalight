#!/bin/bash

set -e

prometheus_host=$1
prometheus_port=$2

# 检查参数是否为空
if [[ -z "$prometheus_host" || -z "$prometheus_port" ]]; then
  echo "错误：缺少 prometheus_host 或 prometheus_port 参数！"
  exit 1
fi

reload_url="http://${prometheus_host}:${prometheus_port}/-/reload"

# 使用 curl 命令调用 reload_url
curl_command="curl -X POST $reload_url"
eval $curl_command