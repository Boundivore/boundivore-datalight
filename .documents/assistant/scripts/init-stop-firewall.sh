#!/bin/bash
# example: sh init-stop-firewall.sh

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取脚本所在路径
BASE_PATH=$(dirname "$0")
echo "Bash Path: $BASE_PATH"

# 检查防火墙状态
systemctl stop firewalld.service
systemctl disable firewalld.service

# 检查 SELinux 状态
SELINUX_STATUS=$(getenforce)
SELINUX_CONFIG_PATH="/etc/selinux/config"
if [[ $SELINUX_STATUS == "Enforcing" ]]; then
    echo "Closing SELINUX."
    setenforce 0
    echo "Disabling SELINUX."
    sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" "$SELINUX_CONFIG_PATH"
else
    echo "SELINUX closed."
fi

echo "$0 done."
exit 0
