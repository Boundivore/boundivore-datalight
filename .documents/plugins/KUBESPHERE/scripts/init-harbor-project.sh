#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# shellcheck disable=SC2034
USER_NAME="datalight"
# shellcheck disable=SC2034
GROUP_NAME="datalight"

SERVICE_NAME="KUBESPHERE"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"
BIN_DIR="${CURRENT_SERVICE_DIR}/bin"

# 授权可执行权限
chmod +x -R "${BIN_DIR}"

EXEC="${BIN_DIR}/create_project_harbor.sh"

sh "${EXEC}"
chown "${USER_NAME}:${GROUP_NAME}" -R /opt/harbor