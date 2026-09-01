#!/bin/bash

# 安装 DataLight 需要的两套 JDK：
#   JDK 8  给大数据服务用，各服务对版本有兼容性要求，不跟随平台升级
#   JDK 17 给 DataLight Master / Worker 自身用
# 两套并存，互不覆盖。PATH 上放 JDK 8，平台启动脚本显式走 DATALIGHT_JAVA_HOME。

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

jdk_repo_dir=$(realpath "${assistant_dir}/repo/jdk")
echo "jdk_repo_dir: ${jdk_repo_dir}"

install_dir="/opt"
profile_path="/etc/profile"

# 服务侧 JDK 8
service_jdk_dir_name="jdk1.8.0_202"
service_jdk_tar_name="jdk-8u202-linux-x64.tar.gz"

# 平台侧 JDK 17
datalight_jdk_dir_name="jdk-17.0.13"
datalight_jdk_tar_name="jdk-17.0.13_linux-x64_bin.tar.gz"

# 解压安装一套 JDK。已存在则跳过，保证脚本可重复执行。
# $1 目录名  $2 安装包名
install_jdk() {
  local dir_name="$1"
  local tar_name="$2"
  local target_dir="${install_dir}/${dir_name}"

  if [ -d "${target_dir}" ]; then
    echo "JDK already installed: ${target_dir}"
    return 0
  fi

  if [ ! -f "${jdk_repo_dir}/${tar_name}" ]; then
    echo "JDK package not found: ${jdk_repo_dir}/${tar_name}" >&2
    return 1
  fi

  echo "Installing ${dir_name} ..."
  tar -zxf "${jdk_repo_dir}/${tar_name}" -C "${install_dir}" || return 1

  if [ ! -d "${target_dir}" ]; then
    echo "Unexpected directory layout in ${tar_name}, expected ${target_dir}" >&2
    return 1
  fi

  echo "${dir_name} installed."
  return 0
}

install_jdk "${service_jdk_dir_name}" "${service_jdk_tar_name}" || exit 1
install_jdk "${datalight_jdk_dir_name}" "${datalight_jdk_tar_name}" || exit 1

SERVICE_JAVA_HOME="${install_dir}/${service_jdk_dir_name}"
DATALIGHT_JAVA_HOME="${install_dir}/${datalight_jdk_dir_name}"

# 写入 /etc/profile。已写过则不重复追加，避免多次执行把 profile 撑大。
if ! grep -q "^export JAVA_HOME=${SERVICE_JAVA_HOME}$" "${profile_path}"; then
  {
    echo ""
    echo "# DataLight: 大数据服务使用的 JDK"
    echo "export JAVA_HOME=${SERVICE_JAVA_HOME}"
    echo "export CLASSPATH=.:\$JAVA_HOME/jre/lib/rt.jar:\$JAVA_HOME/lib/dt.jar:\$JAVA_HOME/lib/tools.jar"
    echo "export PATH=\$JAVA_HOME/bin:\$PATH"
  } >>"${profile_path}"
  echo "JAVA_HOME written to ${profile_path}"
fi

if ! grep -q "^export DATALIGHT_JAVA_HOME=${DATALIGHT_JAVA_HOME}$" "${profile_path}"; then
  {
    echo ""
    echo "# DataLight: Master / Worker 自身使用的 JDK"
    echo "export DATALIGHT_JAVA_HOME=${DATALIGHT_JAVA_HOME}"
  } >>"${profile_path}"
  echo "DATALIGHT_JAVA_HOME written to ${profile_path}"
fi

# shellcheck source=/etc/profile
source "${profile_path}"

# 在 root 与 datalight 用户的家目录下补上 source /etc/profile
for rc_file in /root/.bash_profile /root/.bashrc /home/datalight/.bash_profile /home/datalight/.bashrc; do
  if [ ! -f "${rc_file}" ]; then
    touch "${rc_file}"
  fi
  if ! grep -q "source /etc/profile" "${rc_file}"; then
    echo "source /etc/profile" >>"${rc_file}"
  fi
done

echo "服务侧 JDK: ${SERVICE_JAVA_HOME}"
"${SERVICE_JAVA_HOME}/bin/java" -version
echo "平台侧 JDK: ${DATALIGHT_JAVA_HOME}"
"${DATALIGHT_JAVA_HOME}/bin/java" -version

echo "If you need to apply the environment variable in the current session, please run: "
echo -e "\t source ${profile_path}"

echo "$0 done."
exit 0
