#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 获取当前脚本所在目录的绝对路径
main_script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")
echo "main_script_dir: ${main_script_dir}"

# 获取 assistant 目录
assistant_dir=$(realpath "${main_script_dir}/..")
echo "assistant_dir: ${assistant_dir}"

# 获取 conf 目录
conf_dir=$(realpath "${assistant_dir}/conf")
echo "conf_dir: ${conf_dir}"

# 获取 scripts 目录
script_dir=$(realpath "${assistant_dir}/scripts")
echo "script_dir: ${script_dir}"

# 获取 /xx/datalight 目录
datalight_dir=$(realpath "${assistant_dir}/..")
echo "datalight_dir: ${datalight_dir}"

# 获取 datalight 所在目录
datalight_parent_dir=$(realpath "${datalight_dir}/..")
echo "datalight_parent_dir: ${datalight_parent_dir}"

# 设置 root ssh 无秘钥访问
sh "${script_dir}/init-ssh-gen-key.sh"
sh "${script_dir}/init-ssh-copy-key.sh"

# 指定 properties 文件路径
properties_file="${conf_dir}/init-main-cluster.properties"

# 读取 properties 文件内容并过滤注释行和空行
properties_content=$(grep -v '^\s*#' "$properties_file" | grep -v '^\s*$')

# 以换行符分隔为数组
mapfile -t properties_array <<<"${properties_content}"

# 清空日志文件
log_file="${main_script_dir}/init-main.log"
touch "${log_file}"
echo "" >"${log_file}"

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
done <<< "$(printf '%s\n' "${!data_array[@]}" | sort -t. -k1,1n -k2)"

master_ip="127.0.0.1"
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
    ssh_port=${data_array["${prefix}.node.ssh.port"]}
    hostname=${data_array["${prefix}.node.hostname"]}

    user_root=${data_array["${prefix}.node.user.root"]}
    pwd_root=${data_array["${prefix}.node.pwd.root"]}
    user_datalight=${data_array["${prefix}.node.user.datalight"]}
    pwd_datalight=${data_array["${prefix}.node.pwd.datalight"]}

    if [ "${serial}" == "1" ]
    then
        master_ip="${ip}"
    fi

    # 输出节点信息
    echo "serial: ${serial}"
    echo "ip: ${ip}"
    echo "hostname: ${hostname}"
    echo "user_root: ${user_root}"
    echo "pwd_root: ${pwd_root}"
    echo "user_datalight: ${user_datalight}"
    echo "pwd_datalight: ${pwd_datalight}"
    echo "master_ip: ${master_ip}"

    # 去除节点的IP地址中的空格
    trimmed_target_ip=$(echo "$ip" | tr -d '[:space:]')
    trimmed_current_ip=$(hostname -I | tr -d '[:space:]')

    # 检查目标节点是否是当前节点自己，如果是则跳过当前循环
    if [[ "${trimmed_target_ip}" != "${trimmed_current_ip}" ]]; then
      # 远程创建文件夹
      ssh -p "${ssh_port}" "${user_root}@${ip}" "bash -s" <<<"mkdir -p ${datalight_dir}"
      # 使用SCP将文件夹推送到远程节点
      scp -P "${ssh_port}" -rq "${assistant_dir}"/ "${user_root}@${ip}:${datalight_dir}"/
    fi

    # 执行脚本init-main-single-node.sh，并传递节点信息作为参数
    echo "-------------------------------------${hostname}-------------------------------------"
    sh "${main_script_dir}/init-main-single-node.sh" "${serial}" "${ip}" "${ssh_port}" "${hostname} ${master_ip}"

    echo ""
  fi
done

# 设置 datalight ssh 无秘钥
su - datalight <<EOF
sh "${script_dir}/init-ssh-gen-key.sh"
chmod 700 /home/datalight/.ssh
chmod 600 /home/datalight/.ssh/authorized_keys
sh "${script_dir}/init-ssh-copy-key.sh"
EOF

exit 0
echo "Job done"
