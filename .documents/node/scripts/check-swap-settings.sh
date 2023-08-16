#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查是否配置了正确的交换分区设置
checkSwapSettings() {
    # 检查是否存在交换分区配置
    if grep -q "^#/dev/mapper/centos-swap" "/etc/fstab" && [[ "$(sysctl -n vm.swappiness)" -eq 0 ]]; then
        return 0
    else
        return 1
    fi
}

# 检查交换分区是否已关闭
checkSwapStatus() {
    [[ -z "$(swapon --show)" ]]
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
