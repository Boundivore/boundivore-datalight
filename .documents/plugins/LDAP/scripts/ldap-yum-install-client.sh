#!/bin/bash

# 全局路径变量参考
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

SERVICE_NAME="LDAP"

cd "$SERVICE_DIR/${SERVICE_NAME}/ldap-package" || exit

# 安装 LDAP 客户端 RPM 包
yum -y localinstall ./*.rpm --disablerepo='*' --exclude=openldap-server*

echo "LDAP client installation complete."

exit 0