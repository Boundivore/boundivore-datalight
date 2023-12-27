#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查是否配置了正确的交换分区设置
checkSwapSettings() {
    # 检查/etc/fstab文件中是否有未被注释掉的swap分区
    if ! grep -qE '^[^#].*\sswap\s' /etc/fstab; then
        # 检查swappiness值是否为0
        if [[ "$(sysctl -n vm.swappiness)" -eq 0 ]]; then
            return 0
        fi
    fi
    return 1
}

# 检查交换分区状态是否为关闭
checkSwapStatus() {
    # 检查系统上是否没有激活的swap分区
    if [[ -z "$(swapon --show)" ]]; then
        return 0
    else
        return 1
    fi
}

# 检查交换分区配置是否正确
if ! checkSwapSettings; then
    echo "Swap settings are not configured correctly."
    exit 1
fi

# 检查交换分区是否已关闭
if ! checkSwapStatus; then
    echo "Swap is still enabled."
    exit 1
fi

echo "OK: Swap settings are configured correctly and swap is disabled."

echo "$0 done."
exit 0