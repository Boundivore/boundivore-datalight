#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 禁用所有激活的 swap 分区
if ! swapoff -a; then
    echo "Disabling the swap partition failed."
    exit 1
fi

echo "Swap partition(s) disabled."

# 注释掉 /etc/fstab 中所有的 swap 相关行
if ! sed -i '/\sswap\s/s/^/#/' /etc/fstab; then
    echo "Failed to comment out swap entries in /etc/fstab."
    exit 1
fi

echo "Swap entries in /etc/fstab commented out."

# 设置 vm.swappiness 为 0
# 首先检查是否有 vm.swappiness 的设置存在
if grep -q '^vm.swappiness' /etc/sysctl.conf; then
    # 如果存在，则替换为新值
    if ! sed -i 's/^vm.swappiness.*/vm.swappiness=0/' /etc/sysctl.conf; then
        echo "Failed to set vm.swappiness to 0 in /etc/sysctl.conf."
        exit 1
    fi
else
    # 如果不存在，则添加设置
    if ! echo "vm.swappiness=0" >> /etc/sysctl.conf; then
        echo "Failed to add vm.swappiness setting to /etc/sysctl.conf."
        exit 1
    fi
fi

# 应用 sysctl 设置
if ! sysctl -p; then
    echo "Failed to reload sysctl settings."
    exit 1
fi

echo "vm.swappiness set to 0 and sysctl settings reloaded."

echo "$0 completed successfully."
exit 0