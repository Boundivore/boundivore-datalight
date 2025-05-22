#!/bin/bash

# 查找MINIO进程
NGINX_PID=$(pgrep -f "nginx server")

if [ -z "NGINX_PID" ]; then
    echo "NGINX is not running."
    exit 0
fi

echo "Stopping NGINX server (PID: $NGINX_PID)..."

# 首先尝试优雅停止
kill -15 "${NGINX_PID}"

# 等待最多30秒
WAIT_COUNT=0
while [ $WAIT_COUNT -lt 30 ]; do
    if ! ps -p "${NGINX_PID}" > /dev/null; then
        echo "NGINX server stopped successfully."
        exit 0
    fi
    sleep 1
    WAIT_COUNT=$((WAIT_COUNT + 1))
done

# 如果还在运行，强制终止
if ps -p "${NGINX_PID}" > /dev/null; then
    echo "Force stopping NGINX server..."
    kill -9 "${NGINX_PID}"
    echo "NGINX server force stopped."
fi

exit 0