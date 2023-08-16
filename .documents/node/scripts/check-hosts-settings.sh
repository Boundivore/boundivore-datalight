#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取当前脚本所在目录
script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

# 从文件中读取主机信息
file_path=$(realpath "$script_dir/../conf/auto-hosts.conf")

# 检查文件是否存在
if [ -f "$file_path" ]; then
    echo "OK: Reading conf file: $file_path"
    # 读取配置文件
    hosts=()
    while IFS= read -r line; do
        hosts+=("$line")
    done < "$file_path"
else
    echo "Config file does not exist: $file_path"
    exit 1
fi

# 检查每个主机信息是否存在
missing_hosts=()
for host in "${hosts[@]}"; do
    if ! grep -q "$host" /etc/hosts; then
        missing_hosts+=("$host")
    fi
done

# 判断是否存在缺失的主机信息
if [[ ${#missing_hosts[@]} -eq 0 ]]; then
    echo "OK: All specified hosts are present in /etc/hosts"
    exit 0
else
    echo "The following hosts are missing in /etc/hosts:"
    for missing_host in "${missing_hosts[@]}"; do
        echo "$missing_host"
    done
    exit 1
fi

echo "$0 done."
exit 0