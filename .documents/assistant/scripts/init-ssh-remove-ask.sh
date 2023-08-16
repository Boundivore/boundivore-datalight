#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

sed -i '/^#.*StrictHostKeyChecking ask/s/^#//g' /etc/ssh/ssh_config
sed -i '/StrictHostKeyChecking ask/s/ask/no/g' /etc/ssh/ssh_config

echo "$0 done."
exit 0
