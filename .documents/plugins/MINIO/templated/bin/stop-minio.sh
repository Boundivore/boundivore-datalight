#!/bin/bash

# 查找MINIO进程
MINIO_PID=$(pgrep -f "minio server")

if [ -z "$MINIO_PID" ]; then
    echo "MINIO is not running."
    exit 0
fi

echo "Stopping MINIO server (PID: $MINIO_PID)..."

# 首先尝试优雅停止
kill -15 "${MINIO_PID}"

# 等待最多30秒
WAIT_COUNT=0
while [ $WAIT_COUNT -lt 30 ]; do
    if ! ps -p "${MINIO_PID}" > /dev/null; then
        echo "MINIO server stopped successfully."
        exit 0
    fi
    sleep 1
    WAIT_COUNT=$((WAIT_COUNT + 1))
done

# 如果还在运行，强制终止
if ps -p "${MINIO_PID}" > /dev/null; then
    echo "Force stopping MINIO server..."
    kill -9 "${MINIO_PID}"
    echo "MINIO server force stopped."
fi

exit 0