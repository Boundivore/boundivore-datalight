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

# 下面两个名字是 directory.yaml 里 java-home / datalight-java-home 的落点，
# 保持稳定。各发行版解压出来的真实目录名不一样（Oracle 是 jdk-17.0.13，
# Temurin 带 +11 后缀，Zulu、Corretto 又是另一套），对不上时由脚本建软链抹平，
# 配置不用跟着发行版改。
service_jdk_dir_name="jdk1.8.0_202"
service_jdk_tar_glob="*8u*.tar.gz"

datalight_jdk_dir_name="jdk-17.0.13"
datalight_jdk_tar_glob="*17*.tar.gz"

# 取某个 JDK 的主版本号。JDK 8 输出形如 1.8.0_202，JDK 9 以后形如 17.0.13
# 与 node/scripts/check-jdk-settings.sh 保持同一套解析
java_major_version() {
  local java_bin="$1"
  local raw
  raw=$("${java_bin}" -version 2>&1 | awk -F '"' '/version/ {print $2}')
  if [[ "${raw}" == 1.* ]]; then
    echo "${raw}" | awk -F '.' '{print $2}'
  else
    echo "${raw}" | awk -F '.' '{print $1}'
  fi
}

# 解压安装一套 JDK。已存在则跳过，保证脚本可重复执行。
# 文件名各发行版差别太大，光靠文件名判断不可靠，所以解压后用 java -version 核对。
# $1 期望的稳定目录名  $2 安装包匹配模式  $3 期望主版本  $4 人看的用途说明
install_jdk() {
  local dir_name="$1"
  local tar_glob="$2"
  local expect_major="$3"
  local purpose="$4"
  local target_dir="${install_dir}/${dir_name}"

  # 软链也算装过，所以用 -e 不用 -d
  if [ -e "${target_dir}" ]; then
    echo "JDK already installed: ${target_dir}"
    return 0
  fi

  local tar_path
  tar_path=$(find "${jdk_repo_dir}" -maxdepth 1 -type f -name "${tar_glob}" 2>/dev/null | sort | head -n 1)

  if [ -z "${tar_path}" ]; then
    echo "" >&2
    echo "找不到${purpose}的安装包。" >&2
    echo "  期望位置: ${jdk_repo_dir}" >&2
    echo "  匹配模式: ${tar_glob}" >&2
    echo "请把 JDK ${expect_major} 的 linux-x64 安装包放进该目录后重新执行本脚本。" >&2
    echo "" >&2
    return 1
  fi

  # 先看包里的顶层目录名，解压后才知道东西落在哪
  local top_dir
  top_dir=$(tar -tzf "${tar_path}" 2>/dev/null | head -n 1 | cut -d/ -f1)
  if [ -z "${top_dir}" ]; then
    echo "安装包无法读取或不是 tar.gz: ${tar_path}" >&2
    return 1
  fi

  echo "Installing ${purpose} from $(basename "${tar_path}") ..."
  tar -zxf "${tar_path}" -C "${install_dir}" || return 1

  local real_dir="${install_dir}/${top_dir}"
  if [ ! -d "${real_dir}" ]; then
    echo "解压结果与预期不符，包内顶层目录为 ${top_dir}，未在 ${install_dir} 下找到" >&2
    return 1
  fi

  if [ ! -x "${real_dir}/bin/java" ]; then
    echo "${real_dir}/bin/java 不存在或不可执行，这个包可能不是 JDK" >&2
    return 1
  fi

  # 文件名可能骗人（jdk8u172 这种名字里也带 17），以 java -version 为准
  local major
  major=$(java_major_version "${real_dir}/bin/java")
  if [ "${major}" != "${expect_major}" ]; then
    echo "" >&2
    echo "版本不符：${purpose}需要 JDK ${expect_major}，但 $(basename "${tar_path}") 实际是 JDK ${major}。" >&2
    echo "请换成正确的安装包。已解压的目录 ${real_dir} 未做清理，确认后自行处理。" >&2
    echo "" >&2
    return 1
  fi

  # 发行版目录名与约定名不一致时建软链，让 directory.yaml 保持稳定
  if [ "${top_dir}" != "${dir_name}" ]; then
    ln -sfn "${real_dir}" "${target_dir}" || return 1
    echo "已建立软链 ${target_dir} -> ${real_dir}"
  fi

  echo "${purpose} installed: ${target_dir} (JDK ${major})"
  return 0
}

install_jdk "${service_jdk_dir_name}" "${service_jdk_tar_glob}" "8" "大数据服务用的 JDK 8" || exit 1
install_jdk "${datalight_jdk_dir_name}" "${datalight_jdk_tar_glob}" "17" "平台自身用的 JDK 17" || exit 1

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
