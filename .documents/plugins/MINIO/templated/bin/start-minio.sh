#!/bin/bash

# 启用严格模式
set -euo pipefail

# 环境变量设置
export MINIO_ROOT_USER=datalight
export MINIO_ROOT_PASSWORD=datalight123

# 配置变量
LOG_DIR="{{LOG_DIR}}"
STORAGE_PATH="{{STORAGE_PATH}}"

# 创建MinIO存储目录的函数
create_minio_storage_dirs() {
    local storage_paths
    local actual_path
    local success

    storage_paths="$1"
    success=true

    echo "Creating MinIO storage directories..."
    for path in ${storage_paths}; do
        # 移除http://和主机名部分，保留路径
        actual_path=""
        actual_path=$(echo "$path" | sed 's|http://[^/]*||')

        if [ $? -ne 0 ]; then
            echo "Error: Failed to process path: ${path}"
            success=false
            break
        fi

        # 创建目录
        if ! sudo mkdir -p "${actual_path}"; then
            echo "Error: Failed to create directory: ${actual_path}"
            success=false
            break
        fi
        echo "Created directory: ${actual_path}"

        # 设置权限
        if ! sudo chown datalight:datalight "${actual_path}"; then
            echo "Error: Failed to set permissions for: ${actual_path}"
            success=false
            break
        fi
        echo "Set permissions for: ${actual_path}"
    done

    # 返回执行结果
    if [ "${success}" = true ]; then
        return 0
    else
        return 1
    fi
}

# 检查必要变量
if [ -z "${LOG_DIR}" ] || [ -z "${STORAGE_PATH}" ]; then
    echo "Error: Required variables LOG_DIR or STORAGE_PATH not set"
    exit 1
fi

# 创建并设置日志目录
if ! mkdir -p "${LOG_DIR}"; then
    echo "Error: Failed to create log directory: ${LOG_DIR}"
    exit 1
fi

if ! chown datalight:datalight -R "${LOG_DIR}"; then
    echo "Error: Failed to set permissions for log directory: ${LOG_DIR}"
    exit 1
fi

# 创建存储目录
if ! create_minio_storage_dirs "${STORAGE_PATH}"; then
    echo "Error: Failed to create storage directories"
    exit 1
fi

# 启动MinIO服务器
nohup /srv/datalight/MINIO/minio server \
    --config-dir /srv/datalight/MINIO/conf \
    --address "0.0.0.0:9539" \
    --console-address ":9600" \
    {{STORAGE_PATH}} \
    > "${LOG_DIR}/minio-server.log" 2>&1 &

echo "MinIO server starting in background, check logs at ${LOG_DIR}/minio-server.log"

exit 0