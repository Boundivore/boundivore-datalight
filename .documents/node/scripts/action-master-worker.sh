#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges." >&2
    exit 1
fi

# 获取并验证参数
operation="$1"
component="$2"
port="$3"

if [[ -z "$operation" || -z "$component" ]]; then
    echo "Usage: $0 <operation> <component> [port]" >&2
    exit 1
fi

# 获取当前脚本所在的目录
script_dir=$(dirname "$(realpath "${BASH_SOURCE[0]}")")
if [ ! -d "$script_dir" ]; then
    echo "Error finding script directory." >&2
    exit 1
fi

datalight_dir=$(realpath "$script_dir/../../")
if [ ! -d "$datalight_dir" ]; then
    echo "Error finding datalight directory." >&2
    exit 1
fi

bin_dir=$(realpath "$datalight_dir/bin/")
if [ ! -d "$bin_dir" ]; then
    echo "Error finding bin directory." >&2
    exit 1
fi

# 执行 datalight.sh 脚本
if ! "${bin_dir}/datalight.sh" "$operation" "$component" "$port"; then
    echo "The datalight.sh script failed." >&2
    exit 1
fi

echo "$0 done."
exit 0