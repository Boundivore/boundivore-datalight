#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取脚本所在目录的绝对路径
BASE_PATH=$(dirname "$(readlink -f "$0")")
echo "Bash Path: $BASE_PATH"

# 定义路径变量
REPO_PATH="$BASE_PATH/../packages"
FOLDER_NAME="yum-yum-utils"
TAR_NAME="yum-yum-utils.tar.gz"

# 解压 yum-yum-utils 离线包
tar -zxvf "$REPO_PATH/$TAR_NAME" -C "$PACKAGES_PATH"

# 安装 yum-yum-utils 及其依赖项
yum -y localinstall "$REPO_PATH/$FOLDER_NAME"/*.rpm

# 安装完毕后，删除解压的目录
rm -rf "${REPO_PATH:?}/${FOLDER_NAME:?}/"

# 输出日志
echo "$0 done."
exit 0
