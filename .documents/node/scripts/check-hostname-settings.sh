#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 传入的主机名
desired_hostname="$1"

# 读取 /etc/hostname 中的主机名
current_hostname=$(cat /etc/hostname)

# 检查主机名是否一致
if [[ "$current_hostname" == "$desired_hostname" ]]; then
    echo "OK: Hostname is correctly set to: $desired_hostname"
    exit 0
else
    echo "Hostname is not configured as: $desired_hostname"
    exit 1
fi

echo "$0 done."
exit 0