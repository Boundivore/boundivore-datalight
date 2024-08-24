#!/bin/bash

# 全局路径变量参考
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

SERVICE_NAME=KERBEROS

cd "$SERVICE_DIR/${SERVICE_NAME}/kerberos-package" || exit

# 安装所有 RPM 包
# ls ./*.rpm | grep -v 'krb5-server' | xargs yum -y localinstall
yum -y localinstall ./*.rpm --disablerepo='*' --exclude=krb5-server*

echo "Kerberos installation complete."

exit 0