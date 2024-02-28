#!/bin/bash

# 获取当前用户
CURRENT_USER="$USER"
echo "Current User: ${CURRENT_USER}"

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges." >>"${log_file}"
  exit 1
fi



serial=$1
ip=$2
ssh_port=$3
hostname=$4
masterIp=$5

# 进度条显示宽度
display_width=50
# 当前进度
progress=0

# 获取当前脚本所在目录的绝对路径
main_script_dir=$(realpath "$(dirname "${BASH_SOURCE[0]}")")
echo "main_script_dir: ${main_script_dir}"

# 清空日志文件
log_file="${main_script_dir}/init-main-single-node.log"
touch "${log_file}"
echo "" >"${log_file}"

# 获取 assistant 目录
assistant_dir=$(realpath "${main_script_dir}/..")
echo "assistant_dir: ${assistant_dir}" >>"${log_file}"

# 获取 conf 目录
conf_dir=$(realpath "${assistant_dir}/conf")
echo "conf_dir: ${conf_dir}" >>"${log_file}"

# 获取 scripts 目录
script_dir=$(realpath "${assistant_dir}/scripts")
echo "script_dir: ${script_dir}" >>"${log_file}"

settings_file="${conf_dir}/init-main-single-settings.txt"



# 清空一行并将光标移动到行首
echo -ne "\r"

# 统计 init-main-single-settings.txt 文件中的脚本数量
script_count=$(grep -vc '^$' "${settings_file}")
echo "Prepare to exec scripts count: ${script_count}"

echo -e "Serial: ${serial} Hostname: ${hostname} IP: ${ip}" >>"${log_file}"

# 函数：执行远程脚本
# 参数：
#   $1: 脚本路径
#   $2: 参数1
#   $3: 参数2
#   ...
execute_remote_script() {
  local script_path="$1"
  shift  # 移除 script_path，剩下的所有参数都是要传递给脚本的
  ssh -p "${ssh_port}" "${CURRENT_USER}@${ip}" "cd ${script_dir};" "bash -s" -- "$@" < "${script_path}" >> "${log_file}"
}

# 输出进度条字符串
print_progress() {
  local completed=$1
  local percentage=$2
  local script_name=$3

  bar="["
  for ((j = 0; j < completed; j++)); do
    bar+="#"
  done

  for ((j = completed; j < display_width; j++)); do
    bar+=" "
  done

  bar+="] (${progress} / ${script_count}) ${percentage}% Job: ${script_name} done."

  # 使用 CSI 和光标控制命令实现固定长度的进度条
  # 清空当前行
  echo -ne "\033[2K"
  # 将光标移动到行首
  echo -ne "\033[1G"
  # 输出进度条
  echo -ne "${bar}"
}

grep -v '^$' "$settings_file" | while IFS= read -r script_name; do
  # 设置脚本路径
  script_path="${script_dir}/${script_name}"

  # 输出脚本路径到文件
  echo -e "ScriptPath: ${script_path}" >>"${log_file}"


  # 远程执行脚本，将脚本内容传递给远程执行，并将远程输出重定向到本地日志文件
  case "${script_name}" in
  init-hostname.sh)
    execute_remote_script "${script_path}" "${hostname}"
    ;;
  init-chrony-server-config.sh)
    if [ "${masterIp}" == "${ip}" ]; then
      execute_remote_script "${script_path}"
    fi
    ;;
  init-chrony-client-config.sh)
    if [ "${masterIp}" != "${ip}" ]; then
      execute_remote_script "${script_path}" "${masterIp}"
    fi
    ;;
  *)
    execute_remote_script "${script_path}"
    ;;
  esac

#  sleep 1

  # 更新进度
  set +e
  ((progress++))
  set -e

  # 计算进度百分比
  percentage=$((progress * 100 / script_count))
  # 计算已完成部分的长度
  completed=$((percentage * display_width / 100))

  # 限制进度百分比不超过100%
  if ((percentage > 100)); then
    percentage=100
  fi

  # 输出进度条
  print_progress "${completed}" "${percentage}" "${script_name}"
done

echo ""
echo "Job done"
