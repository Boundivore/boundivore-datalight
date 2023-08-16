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

file_list=(
    "/root/.bashrc"
    "/root/.bash_profile"
    "/home/datalight/.bashrc"
    "/home/datalight/.bash_profile"
)

for file in "${file_list[@]}"; do
    if [ -f "${file}" ] && ! grep -q "source ${profile_path}" "${file}"; then
        echo "Adding 'source ${profile_path}' to ${file}"
        echo "source ${profile_path}" >> "${file}"
    fi
done

echo "$0 done."
exit 0
