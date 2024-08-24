#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 检查参数是否提供
if [ "$#" -ne 4 ]; then
  echo "Usage: $0 <cn=admin,dc=example,dc=com> <password> <ldif_file> <ldap_server>"
  exit 1
fi

ADMIN_DN="$1"
ADMIN_PASS="$2"
LDIF_FILE="$3"
LDAP_SERVER="$4"

# ./script.sh "cn=admin,dc=example,dc=com" "password" "/path/to/ldif_file" "ldap://server-node"
ldapmodify -x -D "$ADMIN_DN" -w "$ADMIN_PASS" -H "$LDAP_SERVER" -f "$LDIF_FILE"

exit 0