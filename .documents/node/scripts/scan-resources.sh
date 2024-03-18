#!/bin/bash
# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 探测节点 IP 地址
ip addr show | grep inet | grep -E 'ens|eth' | grep -v 127.0.0.1 | grep -v 'inet6' | awk '{print $2}' | cut -f1 -d'/'

# 获取 CPU 架构
lscpu | grep 'Architecture' | awk '{print $2}'

# 获取 CPU 核数
lscpu | sed -n '4p' | awk '{print $2}'

# 获取内存大小（以单位 M 表示）
free -m | awk 'NR==2{print int($2)}'

# 获取磁盘大小（以单位 M 表示）
df -m | grep -vE 'overlay|/var/lib/docker' | awk 'NR>1 {print $2}' | paste -sd+ - | bc

# 获取磁盘可用大小
df -m | grep -vE 'overlay|/var/lib/docker' | awk 'NR>1 {print $4}' | paste -sd+ - | bc

cat /etc/centos-release

echo "$0 done."
exit 0