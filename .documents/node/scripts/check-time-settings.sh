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
ip_address=$(checkIp "${ip_address}")

# 定义时间误差的阈值，单位为秒
threshold=0.1

# 获取节点的时间偏移
offset=$(ssh -o ConnectTimeout=5 -n "${ip_address}" "chronyc tracking 2>/dev/null" | grep 'System time' | awk '{print $4}')

if [[ $? -ne 0 ]]; then
    echo "Failed to execute chronyc tracking command on ${ip_address}. Please ensure that chrony is installed and running."
    exit 1
fi

if [[ -z "$offset" ]]; then
    echo "Failed to get the time offset. Please ensure that the chrony service is running on ${ip_address}."
    exit 1
fi

# 绝对值函数
abs(){
    echo "$(echo "$1" | awk '{if($1>=0) print $1; else print -$1}')"
}

# 计算时间偏移是否超出阈值
offset_abs=$(abs "${offset}")

if (( $(echo "${offset_abs} <= ${threshold}" | bc -l) )); then
    echo "OK: Time offset between local and ${ip_address} is within the threshold: ${offset_abs} seconds."
    exit 0
else
    echo "Time offset between local and ${ip_address} exceeds the threshold: ${offset_abs} seconds."
    exit 1
fi

echo "$0 done."
exit 0