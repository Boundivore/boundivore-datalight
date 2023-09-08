#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查是否安装了 jq
if command -v jq >/dev/null 2>&1; then
    echo "OK: jq has already installed"
    exit 0
else
    echo "jq not installed"
    exit 1
fi

echo "$0 done."
exit 0
