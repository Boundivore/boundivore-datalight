#!/bin/bash

set -e
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

DIR=$1

# 函数：输出错误并退出
exit_on_error() {
  echo "Error: $1"
  exit 1
}

# 函数：设置所有者和权限
set_ownership_and_permissions() {
  USER_NAME="datalight"
  GROUP_NAME="datalight"

  chown -R "$USER_NAME:$GROUP_NAME" "$1" || exit_on_error "Failed to set ownership for $1"
  chmod -R 755 "$1" || exit_on_error "Failed to set permissions for $1"
}

set_ownership_and_permissions "${DIR}"

echo "$0 done."
exit 0

