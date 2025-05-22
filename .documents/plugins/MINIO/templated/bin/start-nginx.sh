#!/bin/bash

# 启用严格模式
set -euo pipefail

# 配置变量
LOG_DIR="{{LOG_DIR}}"

# 启动Nginx服务器
nohup /srv/datalight/MINIO/nginx/sbin/nginx -c /srv/datalight/MINIO/nginx/conf/nginx.conf > "${LOG_DIR}/nginx-server.log" 2>&1 &

echo "Nginx server starting in background, check logs at ${LOG_DIR}/nginx-server.log"

exit 0