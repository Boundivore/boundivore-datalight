#!/bin/bash

# 全局路径变量参考
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

cd "$SERVICE_DIR/SSSD/sssd-package" || exit

# 安装所有 RPM 包
yum -y localinstall ./*.rpm --disablerepo='*'

echo "SSSD installation complete."

exit 0