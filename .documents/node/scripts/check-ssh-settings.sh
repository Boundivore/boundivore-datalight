#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查是否移除了 SSH 询问提示
if grep -q "StrictHostKeyChecking no" "/etc/ssh/ssh_config"; then
    echo "OK: SSH prompt has been removed"
    exit 0
else
    echo "SSH prompt has not been removed"
    exit 1
fi

echo "$0 done."
exit 0
