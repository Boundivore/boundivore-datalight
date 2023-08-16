#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 检查是否创建了 datalight 用户
if ! id -u datalight >/dev/null 2>&1; then
  echo "datalight user does not exist"
  exit 1
fi

# 获取当前脚本所在目录的绝对路径
script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")
echo "script_dir: ${script_dir}"

# 获取 assistant 目录
assistant_dir=$(realpath "${script_dir}/..")
echo "assistant_dir: ${assistant_dir}"

profile_path="/etc/profile"

# shellcheck disable=SC1090
source "${profile_path}"

# 在 root 用户以及 datalight 的家目录下的
# /root/.bash_profile
# /root/.bashrc
# /home/datalight/.bash_profile
# /home/datalight/.bashrc
# 文件中的末尾分别添加 source /etc/profile 这句话，
# 如果这句话存在，则不添加
if ! grep -q "source /etc/profile" /root/.bash_profile; then
  echo "source ${profile_path}" >>/root/.bash_profile
fi

if ! grep -q "source /etc/profile" /root/.bashrc; then
  echo "source ${profile_path}" >>/root/.bashrc
fi

if ! grep -q "source /etc/profile" /home/datalight/.bash_profile; then
  echo "source ${profile_path}" >>/home/datalight/.bash_profile
fi

if ! grep -q "source /etc/profile" /home/datalight/.bashrc; then
  echo "source ${profile_path}" >>/home/datalight/.bashrc
fi

echo "$0 done."
exit 0
