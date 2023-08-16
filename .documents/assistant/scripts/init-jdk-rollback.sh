#!/bin/bash

# example: sh init-jdk-rollback.sh

# shellcheck source=/root/.bash_profile
source "/root/.bash_profile"

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取脚本所在路径
SCRIPT_DIR=$(dirname "$(readlink -f "$0")")

# 设置相关路径
BASE_PATH=$(dirname "$SCRIPT_DIR")
PROFILE_PATH="/root/.bash_profile"

# JDK 相关配置
JDK_PATH_NAME="jdk1.8.0_202"
MODULES_PATH="$BASE_PATH/modules"
JDK_STR=$(find "$MODULES_PATH" -maxdepth 1 -type d -name "$JDK_PATH_NAME")

if [ -z "$JDK_STR" ]; then
    echo "JDK not installed."
else
    echo "JDK installed."
    echo "Preparing to uninstall JDK..."
    sleep 2s

    JAVA_HOME="$MODULES_PATH/$JDK_PATH_NAME"
    rm -rf "$JAVA_HOME"

    sed -i "/$JDK_PATH_NAME/d" "$PROFILE_PATH"

    source "$PROFILE_PATH"

    echo "JDK uninstalled."
fi

echo "$0 done."
exit 0
