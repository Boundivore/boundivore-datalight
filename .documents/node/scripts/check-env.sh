#!/bin/bash

# 开启错误终止模式
set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 获取当前脚本所在的目录
script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")

# 获取 master_ip 参数
master_ip="$1"
# 获取 desired_hostname 参数
desired_hostname="$2"

# 调用 check-firewall-settings.sh
"$script_dir/check-firewall-settings.sh" || {
    echo "Failed to check firewall settings."
    exit 1
}

# 调用 check-iptables-settings.sh
"$script_dir/check-iptables-disabled-settings.sh" || {
    echo "Failed to check iptables settings."
    exit 1
}

# 调用 check-hostname-settings.sh，并传入 desired_hostname 参数
"$script_dir/check-hostname-settings.sh" "${desired_hostname}" || {
    echo "Failed to check hostname settings."
    exit 1
}

# 调用 check-time-settings.sh，并传入主机 IP 地址参数
"$script_dir/check-time-settings.sh" "${master_ip}" || {
    echo "Failed to check time settings."
    exit 1
}

# 调用 check-ssh-settings.sh
"$script_dir/check-ssh-settings.sh" || {
    echo "Failed to check SSH settings."
    exit 1
}

# 调用 check-jdk-settings.sh
"$script_dir/check-jdk-settings.sh" || {
    echo "Failed to check JDK settings."
    exit 1
}

# 调用 check-swap-settings.sh
"$script_dir/check-swap-settings.sh" || {
    echo "Failed to check swap settings."
    exit 1
}

# 调用 check-sysctl-settings.sh
"$script_dir/check-sysctl-settings.sh" || {
    echo "Failed to check sysctl settings."
    exit 1
}

# 调用 check-systemd-settings.sh
"$script_dir/check-systemd-settings.sh" || {
    echo "Failed to check systemd settings."
    exit 1
}

# 调用 check-add-hosts-settings.sh
"$script_dir/check-add-hosts-settings.sh" || {
    echo "Failed to check hosts settings."
    exit 1
}

# 调用 check-add-group-and-user.sh
"$script_dir/check-add-group-and-user.sh" || {
    echo "Failed to check group-user settings."
    exit 1
}

# 调用 check-add-datalight-env.sh
"$script_dir/check-add-datalight-env.sh" || {
    echo "Failed to check datalight-env settings."
    exit 1
}

# 调用 check-dependencies-settings.sh
"$script_dir/check-dependencies-settings.sh" || {
    echo "Failed to check-dependencies-settings."
    exit 1
}

# 调用 check-and-add-user-profile.sh
"$script_dir/check-and-add-user-profile.sh" || {
    echo "Failed to check-and-add-user-profile."
    exit 1
}

# 所有检查成功通过
echo "$0 done."
exit 0
