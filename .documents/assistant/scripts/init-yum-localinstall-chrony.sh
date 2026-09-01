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
REPO_PATH="$BASE_PATH/../repo"
FOLDER_NAME="yum-chrony"
TAR_NAME="yum-chrony.tar.gz"

# 解压 yum-chrony 离线包
# 解到 repo 目录下，与后面找 rpm 的路径保持一致
# （原先写的是未定义的 $PACKAGES_PATH，解压落点和查找路径对不上）
if [ ! -f "$REPO_PATH/$TAR_NAME" ]; then
    echo "离线包不存在: $REPO_PATH/$TAR_NAME" >&2
    exit 1
fi
tar -zxf "$REPO_PATH/$TAR_NAME" -C "$REPO_PATH"

# 安装 yum-chrony及其依赖项
yum -y install "$REPO_PATH/$FOLDER_NAME"/*.rpm

# 安装完毕后，删除解压的目录
rm -rf "${REPO_PATH:?}/${FOLDER_NAME:?}/"

# 输出日志
echo "$0 done."
exit 0
