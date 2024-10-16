#!/bin/bash
# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# HDFS 高可用切换所需的 fuster
yum -y install psmisc
# 交互自动交互工具
yum -y install expect
yum -y install epel-release
#yum -y install jq
yum -y install curl
yum -y install chrony
yum -y install openssl openssl-devel patch
yum -y install lrzsz
yum -y install unzip zip
yum -y install yum-utils
yum -y install net-tools
yum -y install bc

yum -y install gcc gcc-c++
yum -y install make
yum -y install autoconf automake libtool curl
yum -y install zlib lzo-devel zlib-devel openssl openssl-devel ncurses-devel ruby
yum -y install snappy snappy-devel bzip2 bzip2-devel lzo lzo-devel lzop libXtst
yum -y install lsof
yum -y install python3

echo "$0 done."
exit 0