#!/bin/bash
# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 检查输入参数
if [ $# -ne 2 ]; then
    echo "Usage: $0 <enable|disable> <SERVICE_NAME>"
    exit 1
fi

ACTION="$1"
SERVICE_NAME="$2"

# 验证操作参数
if [ "$ACTION" != "enable" ] && [ "$ACTION" != "disable" ]; then
    echo "Invalid action: $ACTION. Please use 'enable' or 'disable'."
    exit 1
fi

USER_NAME="datalight"
GROUP_NAME="datalight"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# 将 SERVICE_NAME 转换为小写
SERVICE_NAME_LOWER=$(echo "$SERVICE_NAME" | tr '[:upper:]' '[:lower:]')

PLUGIN_DIR="${CURRENT_SERVICE_DIR}/ranger-${SERVICE_NAME_LOWER}-plugin"

# 检查插件目录是否存在
if [ ! -d "$PLUGIN_DIR" ]; then
    echo "Plugin directory does not exist: $PLUGIN_DIR"
    exit 1
fi

cd "$PLUGIN_DIR"

# 根据操作选择相应的脚本
if [ "$ACTION" = "enable" ]; then
    SCRIPT_NAME="enable-${SERVICE_NAME_LOWER}-plugin.sh"
else
    SCRIPT_NAME="disable-${SERVICE_NAME_LOWER}-plugin.sh"
fi

# 检查脚本是否存在
if [ ! -f "$SCRIPT_NAME" ]; then
    echo "Script not found: $SCRIPT_NAME"
    exit 1
fi

# 执行脚本
./"$SCRIPT_NAME"

echo "${ACTION^} ${SERVICE_NAME} plugin completed."

exit 0