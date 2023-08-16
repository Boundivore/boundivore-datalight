#!/bin/bash

set -e
# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 获取当前脚本所在目录
script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

# 从文件中读取主机信息
datalight_dir=$(realpath "$script_dir/../../")

# 定义要创建的用户名和用户组名
USER_NAME="datalight"
GROUP_NAME="datalight"
NEW_PASSWORD="12345678"

# 检查用户组是否已存在
if grep -q "^${GROUP_NAME}:" /etc/group; then
  echo "Group '${GROUP_NAME}' already exists."
else
  echo "Creating group '${GROUP_NAME}'..."
  if groupadd "${GROUP_NAME}"; then
    echo "Group '${GROUP_NAME}' created successfully."
  else
    echo "Failed to create group '${GROUP_NAME}'."
    exit 1
  fi
fi

# 检查用户是否已存在
if id "${USER_NAME}" &>/dev/null; then
  echo "User '${USER_NAME}' already exists."
else
  echo "Creating user '${USER_NAME}'..."
  if useradd -m -g "${GROUP_NAME}" "${USER_NAME}"; then
    echo "User '${USER_NAME}' created successfully."
  else
    echo "Failed to create user '${USER_NAME}'."
    exit 1
  fi
fi

# 设置用户密码（可选）
echo "Setting password for user '${USER_NAME}'..."
echo "${USER_NAME}:${NEW_PASSWORD}" | chpasswd
# 可选：将用户加入sudo组（如果需要）
usermod -aG wheel "${USER_NAME}"

# 将 datalight_dir 目录以及其下的所有用户和组变更为 datalight
chown -R "${USER_NAME}:${GROUP_NAME}" "${datalight_dir}"

# 将 datalight_dir 目录以及其下的所有子目录和文件权限变更为 755
#find "$datalight_dir" -type d -exec chmod 755 {} +
#find "$datalight_dir" -type f -exec chmod 755 {} +
chmod -R 755 "$datalight_dir"

# 所有检查成功通过
echo "$0 done."
exit 0
