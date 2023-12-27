#!/bin/bash
# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 探测节点 IP 地址
ip addr show | grep inet | grep 'ens33' | grep -v 127.0.0.1 | grep -v 'inet6' | awk '{print $2}' | cut -f1 -d'/'

# 获取 CPU 架构
lscpu | grep 'Architecture' | awk '{print $2}'

# 获取 CPU 核数
lscpu | sed -n '4p' | awk '{print $2}'

# 获取内存大小（以单位 M 表示）
free -m | awk 'NR==2{print int($2)}'

# 获取磁盘大小（以单位 M 表示）
df -m --total | tail -n 1 | awk '{print $2 "\n" $4}'

cat /etc/centos-release

echo "$0 done."
exit 0