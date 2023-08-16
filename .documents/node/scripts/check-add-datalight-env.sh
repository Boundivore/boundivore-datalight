#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取当前脚本所在目录的绝对路径
script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

# 获取 datalight-service-env.sh 的绝对路径
service_source_file_path=$(realpath "${script_dir}/../../conf/env/datalight-service-env.sh")

# 定义要检查的文件
file_path="/etc/profile"

# 检查文件是否包含 "datalight-service-env.sh"，如果包含，则删除整行并重新添加新的 source 行
if grep -q "datalight-service-env.sh" "$file_path"; then
    echo "The file '$file_path' contains 'datalight-service-env.sh'. Removing the line and adding again..."
    if ! sed -i "/datalight-service-env.sh/d" "$file_path"; then
        echo "Failed to modify the file '$file_path'. Exiting..."
        exit 1
    fi
    if ! echo "source ${service_source_file_path}" >>"$file_path"; then
        echo "Failed to modify the file '$file_path'. Exiting..."
        exit 1
    fi
    echo "Added the source line to '$file_path'."
fi

# 定义要检查的文件列表
file_list=(
  "/root/.bashrc"
  "/root/.bash_profile"
  "/home/datalight/.bashrc"
  "/home/datalight/.bash_profile"
)

# 检查每个文件是否包含 "source /etc/profile"，如果包含，则删除整行并重新添加新的 source 行
for file_path in "${file_list[@]}"; do
    if grep -q "source /etc/profile" "$file_path"; then
        echo "The file '$file_path' contains 'source /etc/profile'. Removing the line and adding again..."
        if ! sed -i "/source \/etc\/profile/d" "$file_path"; then
            echo "Failed to modify the file '$file_path'. Exiting..."
            exit 1
        fi
    fi
    if ! echo "source /etc/profile" >> "$file_path"; then
        echo "Failed to modify the file '$file_path'. Exiting..."
        exit 1
    fi
    echo "Added the source line to '$file_path'."
done

echo "$0 done."
exit 0