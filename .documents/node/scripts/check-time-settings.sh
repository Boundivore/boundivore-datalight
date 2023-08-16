#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查IP格式是否正确
checkIp(){
    IP_REGEX="^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$"
    if ! [[ $1 =~ $IP_REGEX ]]; then
        echo "The string $1 is not a correct IP."
        exit 1
    else
        echo "$1"
    fi
}

# 获取传入的主机 IP 地址
ip_address="$1"

# 检查主机 IP 地址的合法性
ip_address=$(checkIp "$ip_address")

# 定义时间误差的阈值，单位为毫秒
threshold=2000

# 获取节点的时间
node_time=$(ssh -o ConnectTimeout=5 -n "$ip_address" "date +%s%3N" 2>/dev/null)

# 计算本地时间和节点时间的误差
local_time=$(date +%s%3N)
time_difference=$((local_time - node_time))
time_difference=${time_difference#-}

if ((time_difference <= threshold)); then
    echo "OK: Time difference between local and $ip_address is within the threshold: $time_difference milliseconds."
    exit 0
else
    echo "Time difference between local and $ip_address exceeds the threshold: $time_difference milliseconds."
    exit 1
fi

echo "$0 done."
exit 0