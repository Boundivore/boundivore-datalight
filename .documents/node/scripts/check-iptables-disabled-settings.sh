#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查 iptables 是否已禁用
iptables_status=$(systemctl is-active iptables)
if [[ "$iptables_status" == "inactive" ]]; then
    echo "OK: iptables is disabled. All traffic is allowed."
    exit 0
else
    echo "iptables is enabled. Please disable iptables before running this script."
    exit 1
fi

echo "$0 done."
exit 0