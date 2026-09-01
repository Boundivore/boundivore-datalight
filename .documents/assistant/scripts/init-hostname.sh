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

# 修改主机名。
# EL7 起 hostnamectl 才是权威来源，/etc/sysconfig/network 里的 HOSTNAME=
# 已经不起作用，EL8 上整个文件基本废弃。原先用 > 覆盖写，
# 既写了不生效的 HOSTNAME=，又会清掉文件里原有的其他内容。
modifyHostname(){
    echo "${hostname}" > /etc/hostname
    hostnamectl set-hostname "${hostname}"
}

modifyHostname

echo "$0 done."
exit 0
