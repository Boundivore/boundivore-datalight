#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 禁用交换分区
if ! swapoff -a; then
    echo "Disabling the swap partition failed."
    exit 1
fi

echo "Disabling the swap partition"

# 注释 /etc/fstab 中的交换分区配置
sed -i 's/^\/dev\/mapper\/centos-swap swap/#&/' /etc/fstab

# 设置 vm.swappiness 为 0
grep -q '^vm.swappiness' /etc/sysctl.conf
if [[ $? -eq 0 ]]; then
    sed -i 's/^vm.swappiness.*/vm.swappiness=0/' /etc/sysctl.conf
else
    echo "vm.swappiness=0" >> /etc/sysctl.conf
fi

sysctl vm.swappiness=0

echo "$0 done."
exit 0
