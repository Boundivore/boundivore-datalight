#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 定义期望的优化参数和对应的期望值
optimizations=(
    "vm.max_map_count:655300"                        # 控制进程的最大内存映射区域数量
    "vm.dirty_ratio:10"                              # 指定内存中脏页的最大比例，当达到该比例时触发脏页回写
    "vm.dirty_background_ratio:5"                    # 指定内存中脏页的后台回写比例，当超过该比例时开始后台回写
    "vm.dirty_writeback_centisecs:200"               # 指定内存中脏页的写回间隔时间，单位为百分之一秒
    "vm.vfs_cache_pressure:200"                      # 控制用于 inode 和 dentry 缓存的内存与用于页面缓存的内存之间的权衡
    "vm.dirty_expire_centisecs:5000"                 # 指定内存中脏页过期的时间，单位为百分之一秒
)


# 检查 sysctl 配置是否满足期望值
checkSysctlSettings() {
    for opt in "${optimizations[@]}"; do
        key="${opt%%:*}"
        expected_value="${opt#*:}"
        current_value=$(sysctl -n "$key")
        if [[ "$current_value" -lt "$expected_value" ]]; then
            echo "Configuration not satisfied: $key=$current_value (expected: >= $expected_value)"
            return 1
        fi
    done
    return 0
}

# 检查 sysctl 配置是否满足期望值
if checkSysctlSettings; then
    echo "OK: All sysctl configurations are satisfied."
    exit 0
else
    exit 1
fi

echo "$0 done."
exit 0
