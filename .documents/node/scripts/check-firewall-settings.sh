#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查防火墙状态
firewall_status=$(systemctl status firewalld.service | grep 'Active:' | awk '{print $2}')

if [[ "$firewall_status" == "inactive" ]]; then
    echo "OK: Firewall is disabled."
    exit 0
elif [[ "$firewall_status" == "active" ]]; then
    echo "Firewall is enabled."
    exit 1
else
    echo "Unable to determine firewall status."
    exit 1
fi

echo "$0 done."
exit 0