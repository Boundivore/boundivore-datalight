#!/bin/bash
# example: sh init-start-firewall.sh

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取脚本所在路径
BASE_PATH=$(dirname "$(readlink -f "$0")")
echo "Bash Path: $BASE_PATH"

# 检查防火墙状态
FIREWALL_STATUS=$(firewall-cmd --state)
if [[ $FIREWALL_STATUS == "not running" ]]; then
    echo "Starting firewall."
    systemctl start firewalld.service
    systemctl enable firewalld.service
else
    echo "Firewall started."
fi

# 检查 SELinux 状态
SELINUX_STATUS=$(getenforce)
SELINUX_CONFIG_PATH="/etc/selinux/config"
if [[ $SELINUX_STATUS != "Enforcing" ]]; then
    echo "Starting SELINUX."
    setenforce 1
    echo "Enable SELINUX."
    sed -i "s/SELINUX=disabled/SELINUX=enforcing/g" "$SELINUX_CONFIG_PATH"
else
    echo "SELINUX started."
fi

echo "$0 done."
exit 0
