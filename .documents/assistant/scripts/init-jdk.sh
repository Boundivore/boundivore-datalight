#!/bin/bash

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

# 检查 JDK 是否已经正确安装
if command -v java &>/dev/null && java -version 2>&1 | grep -q "java version"; then
  echo "JDK already installed."
  exit 0
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

# JDK 相关配置
jdk_dir_name="jdk1.8.0_202"
profile_path="/etc/profile"
jdk_tar_name="jdk-8u202-linux-x64.tar.gz"
jdk_str=""

for file in "${jdk_repo_dir}"/*; do
  if [[ -d "${file}" && "${file}" =~ ${jdk_dir_name} ]]; then
    jdk_str="${file}"
    break
  fi
done

if [ -z "${jdk_str}" ]; then
  echo "Preparing to install JDK..."
  sleep 2s

  # 解压 JDK 安装包
  tar -zxvf "${jdk_repo_dir}/${jdk_tar_name}" -C "${install_dir}"

  JAVA_HOME="${install_dir}/${jdk_dir_name}"
  JAVA_ENV="export JAVA_HOME=${JAVA_HOME}; export CLASSPATH=.:\$JAVA_HOME/jre/lib/rt.jar:\$JAVA_HOME/lib/dt.jar:\$JAVA_HOME/lib/tools.jar; export PATH=\$PATH:\$JAVA_HOME/bin"

  # 将 JDK 环境变量添加到 profile
  echo "${JAVA_ENV}" >>"${profile_path}"
  # 立即生效环境变量
  # shellcheck source=/etc/profile
  source "${profile_path}"

  # 在 root 用户以及 datalight 的家目录下的
  # /root/.bash_profile
  # /root/.bashrc
  # /home/datalight/.bash_profile
  # /home/datalight/.bashrc
  # 文件中的末尾分别添加 source /etc/profile 这句话，
  # 如果这句话存在，则不添加
  if ! grep -q "source /etc/profile" /root/.bash_profile; then
    echo "source /etc/profile" >>/root/.bash_profile
  fi

  if ! grep -q "source /etc/profile" /root/.bashrc; then
    echo "source /etc/profile" >>/root/.bashrc
  fi

  if ! grep -q "source /etc/profile" /home/datalight/.bash_profile; then
    echo "source /etc/profile" >>/home/datalight/.bash_profile
  fi

  if ! grep -q "source /etc/profile" /home/datalight/.bashrc; then
    echo "source /etc/profile" >>/home/datalight/.bashrc
  fi

  echo "JDK installed."
  java -version

  echo "Preparing to configure BCPROV..."

  JAVA_SECURITY_DIR="${JAVA_HOME}/jre/lib/security/java.security"
  JAVA_BCPROV_DIR="${JAVA_HOME}/jre/lib/ext/"
  JAVA_BCPROV_JAR="${jdk_repo_dir}/bcprov-jdk15on-1.56.jar"

  # 配置 Java 安全提供程序和添加 Bouncy Castle 提供程序
  JAVA_SECURITY_ARGS_ARR=(
    "security.provider.1=sun.security.provider.Sun"
    "security.provider.2=sun.security.rsa.SunRsaSign"
    "security.provider.3=com.sun.net.ssl.internal.ssl.Provider"
    "security.provider.4=com.sun.crypto.provider.SunJCE"
    "security.provider.5=sun.security.jgss.SunProvider"
    "security.provider.6=com.sun.security.sasl.Provider"
    "security.provider.7=org.jcp.xml.dsig.internal.dom.XMLDSigRI"
    "security.provider.8=sun.security.smartcardio.SunPCSC"
    "security.provider.9=org.bouncycastle.jce.provider.BouncyCastleProvider"
  )

  # 将安全提供程序参数拼接成一个字符串
  JAVA_SECURITY_ARGS=$(printf "%s\n" "${JAVA_SECURITY_ARGS_ARR[@]}")

  echo -e "${JAVA_SECURITY_ARGS}" >>"${JAVA_SECURITY_DIR}"
  cp -a "${JAVA_BCPROV_JAR}" "${JAVA_BCPROV_DIR}"

  echo "BCPROV installed."

  echo "If you need to apply the environment variable in the current session, please run: "
  echo -e "\t source ${profile_path}"
else
  echo "JDK already installed."
fi

echo "$0 done."
exit 0
