#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查 iptables 是否已禁用
iptables_status=$(systemctl is-active iptables)
if [[ "$iptables_status" == "inactive" ]]; then
    echo "OK: iptables is disabled. All traffic is allowed."
    exit 0
fi

# 定义需要检查的IP和端口文件地址
file_path="$1"

# 检查输出规则和输入规则
while IFS= read -r line; do
    ip_address=$(echo "$line" | awk '{print $1}')
    port_number=$(echo "$line" | awk '{print $2}')

    # 检查输出规则
    output_rule=$(iptables -S OUTPUT | grep "dport ${port_number}.*-d ${ip_address}")
    if [[ -z "$output_rule" ]]; then
        echo "Output traffic for specified IP and port is denied: ${ip_address}:${port_number}"
        exit 1
    fi

    # 检查输入规则
    input_rule=$(iptables -S INPUT | grep "sport ${port_number}.*-s ${ip_address}")
    if [[ -z "$input_rule" ]]; then
        echo "Input traffic for specified IP and port is denied: ${ip_address}:${port_number}"
        exit 1
    fi

done < "$file_path"

echo "OK: iptables all traffic is allowed."

echo "$0 done."
exit 0
