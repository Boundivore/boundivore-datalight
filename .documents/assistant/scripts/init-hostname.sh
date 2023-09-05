#!/bin/bash

# example: sh init-hostname.sh linux01

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取脚本所在路径
BASE_PATH=$(cd "$(dirname "$0")" || exit; pwd)
echo "Bash Path: ${BASE_PATH}"
# 参数顺序参考 init-main-single-node.sh
hostname=$1

# 修改主机名
modifyHostname(){
    echo "${hostname}" > /etc/hostname
    echo "HOSTNAME=${hostname}" > /etc/sysconfig/network
    echo "NOZEROCONF=yes" >> /etc/sysconfig/network
    hostnamectl set-hostname "${hostname}"
}

modifyHostname

echo "$0 done."
exit 0
