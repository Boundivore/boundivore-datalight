#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 检查并添加 datalight 用户权限配置
grep -qxF 'datalight ALL=(ALL) NOPASSWD: ALL' /etc/sudoers || echo 'datalight ALL=(ALL) NOPASSWD: ALL' | sudo tee -a /etc/sudoers
sed -i 's/Defaults    requiretty/#Defaults    requiretty/g' /etc/sudoers