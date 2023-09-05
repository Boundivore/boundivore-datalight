#!/bin/bash

set -e
# example: sh init-ssh-copy-key.sh
# 获取当前用户
CURRENT_USER="$USER"
echo "Current User: ${CURRENT_USER}"

# 获取当前脚本所在目录的绝对路径
script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")
echo "script_dir: ${script_dir}"

# 获取 assistant 目录
assistant_dir=$(realpath "${script_dir}/..")
echo "assistant_dir: ${assistant_dir}"

conf_dir=$(realpath "${assistant_dir}/conf")
echo "conf_dir: ${conf_dir}"

# 指定 properties 文件路径
properties_file="${conf_dir}/init-main-cluster.properties"

# 读取 properties 文件内容并过滤注释行和空行
properties_content=$(grep -v '^\s*#' "$properties_file" | grep -v '^\s*$')

# 以换行符分隔为数组
mapfile -t properties_array <<<"${properties_content}"

# 定义关联数组
declare -A data_array

# 遍历数组，提取每组数据
for item in "${properties_array[@]}"; do
  # 提取键和值
  key=$(echo "$item" | cut -d '=' -f 1)
  value=$(echo "$item" | cut -d '=' -f 2)

  # 提取前缀数字和属性名
  if [[ $key =~ ^([0-9]+)\.(.*) ]]; then
    prefix="${BASH_REMATCH[1]}"
    prop="${BASH_REMATCH[2]}"
    data_array["$prefix.$prop"]=$value
  fi
done

# 对关联数组的键进行排序
sorted_keys=()
while IFS= read -r line; do
  sorted_keys+=("$line")
done <<<"$(printf '%s\n' "${!data_array[@]}" | sort -t. -k1,1n -k2)"

# 检查 IP 合法性
checkIp() {
  IP_REGEX="^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$"
  if ! [[ $1 =~ $IP_REGEX ]]; then
    echo "The string $1 is not a correct IP."
    exit 1
  else
    echo "$1"
  fi
}

# 复制 SSH 配置
copyKeyConfig() {
  local sshDir="${HOME}/.ssh"
  local ip=$1
  local pwd=$2

  echo "sshDir: ${sshDir}"
  echo "ip: ${ip}"
  echo "pwd: ${pwd}"

  /usr/bin/expect <<-EOF
    set timeout -1
    spawn scp -o "StrictHostKeyChecking no" -r ${sshDir}/ "${CURRENT_USER}@${ip}:${HOME}"
    expect {
        "password:" { send "$pwd\r"; exp_continue }
        "Are you sure you want to continue connecting (yes/no)?" { send "yes\r"; exp_continue }
        eof
    }
EOF
}

# 验证 IP 和密码的格式是否正确
validateIPsAndPasswords() {
  # 循环遍历一个存储节点配置信息的数组
  for prefix_prop in "${sorted_keys[@]}"; do
    # 从数组中获取当前节点的配置值
    value="${data_array[${prefix_prop}]}"

    # 使用正则表达式匹配节点配置名，并提取节点编号
    if [[ ${prefix_prop} =~ ^([0-9]+)\.node\.hostname ]]; then
      # 提取节点编号
      prefix="${BASH_REMATCH[1]}"

      # 获取节点配置信息
      serial=${prefix}
      ip=${data_array["${prefix}.node.ip"]}
      hostname=${data_array["${prefix}.node.hostname"]}

      user_root=${data_array["${prefix}.node.user.root"]}
      pwd_root=${data_array["${prefix}.node.pwd.root"]}
      user_datalight=${data_array["${prefix}.node.user.datalight"]}
      pwd_datalight=${data_array["${prefix}.node.pwd.datalight"]}

      # 输出节点信息
      echo "serial: ${serial}"
      echo "ip: ${ip}"
      echo "hostname: ${hostname}"
      echo "user_root: ${user_root}"
      echo "pwd_root: ${pwd_root}"
      echo "user_datalight: ${user_datalight}"
      echo "pwd_datalight: ${pwd_datalight}"

      checkIp "${ip}"

      local pwd
      if [ "$CURRENT_USER" = "root" ]; then
        pwd="${pwd_root}"
      elif [ "$CURRENT_USER" = "datalight" ]; then
        pwd="${pwd_datalight}"
      else
        echo "UnknownUser：$CURRENT_USER"
        exit 1
      fi

      # 分发秘钥
      copyKeyConfig "${ip}" "${pwd}"
    fi

  done

}

# 移除 SSH 询问提示
removeSSHAsk() {
  if [ "$CURRENT_USER" = "root" ]; then
    sed -i '/^#.*StrictHostKeyChecking ask/s/^#//g' /etc/ssh/ssh_config
    sed -i '/StrictHostKeyChecking ask/s/ask/no/g' /etc/ssh/ssh_config
  fi
}

validateIPsAndPasswords
echo ""
removeSSHAsk
echo "$0 done."
exit 0
