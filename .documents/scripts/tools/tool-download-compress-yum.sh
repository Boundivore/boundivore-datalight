#!/bin/bash

# example: sh tool-download-compress-yum.sh yum-utils
# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

if [ $# -eq 0 ]; then
  echo "Usage: $0 <package_name>"
  exit 1
fi

# 获取当前脚本所在目录的绝对路径
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 获取 /opt/datalight/init 路径
parent_dir="$(dirname "$script_dir")"
echo "parent_dir: $parent_dir"

# 获取 /opt/datalight/init/packages 路径
packages_dir="$parent_dir/packages"
echo "packages_dir: $packages_dir"

package_name=$1
folder_name="yum-$package_name"
tar_file="$folder_name.tar.gz"

# 创建以 yum 前缀的文件夹
mkdir "$folder_name"

# 下载资源到指定文件夹
yumdownloader --resolve --destdir="$folder_name" "$package_name"

# 压缩指定资源
tar -zcf "$tar_file" "$folder_name"

mv "$tar_file" "$packages_dir"

# 删除之前的文件夹
rm -rf "$folder_name"

echo "Package $package_name downloaded and compressed to $tar_file"

echo "$0 done."
exit 0
