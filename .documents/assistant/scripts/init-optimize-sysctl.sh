#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 文件路径变量
sysctl_conf="/etc/sysctl.conf"

# 定义优化参数
optimizations=(
    "vm.max_map_count=2000000"
    "vm.dirty_ratio=10"
    "vm.dirty_background_ratio=5"
    "vm.dirty_writeback_centisecs=200"
    "vm.vfs_cache_pressure=200"
    "vm.dirty_expire_centisecs=6000"
)

# 移除已经存在的优化项
for opt in "${optimizations[@]}"; do
    if grep -q "^$opt" "$sysctl_conf"; then
        sed -i "/^$opt/d" "$sysctl_conf"
    fi
done

# 增加新的优化项
for opt in "${optimizations[@]}"; do
    echo "$opt" >> "$sysctl_conf"
done

# 使配置生效
sysctl -p

echo "$0 done."
exit 0
