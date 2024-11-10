#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 获取当前的 vm.max_map_count 值
current_value=$(sysctl vm.max_map_count | awk '{print $3}')

# 检查当前值是否小于 2000000
if [ "$current_value" -lt 2000000 ]; then
    # 修改 /etc/sysctl.conf 文件
    if grep -q '^vm.max_map_count' /etc/sysctl.conf; then
        # 替换现有行
        sed -i '/^vm.max_map_count/c\vm.max_map_count = 2000000' /etc/sysctl.conf
    else
        # 添加新行
        echo 'vm.max_map_count = 2000000' >> /etc/sysctl.conf
    fi

    # 重新加载 sysctl 配置
    sysctl -p

    echo "vm.max_map_count has been updated to 2000000."
else
    echo "vm.max_map_count is already set to or greater than 2000000, no changes needed."
fi

# 输出当前设置以确认
sysctl vm.max_map_count