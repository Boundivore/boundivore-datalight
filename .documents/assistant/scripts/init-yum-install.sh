#!/bin/bash

# 安装节点依赖的系统软件包。
#
# 执行前提：先跑过 init-os-repo.sh。EL8 上 snappy-devel、lzo-devel、bzip2-devel
# 在 PowerTools/CRB 里，lzop、lrzsz 在 EPEL 里，源没准备好这里必然失败。
#
# 原先每个包一条 yum install 且不看返回值，装失败也一路往下走，
# 要到后面部署服务时才暴露。改成分组安装并逐组检查，缺什么当场说清楚。

if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges." >&2
  exit 1
fi

failed_groups=()

# 装一组包。$1 是分组说明，其余是包名
install_group() {
  local desc="$1"
  shift
  echo "安装 ${desc}: $*"
  if yum -y install "$@"; then
    return 0
  fi
  echo "  失败: ${desc}" >&2
  failed_groups+=("${desc}")
  return 1
}

# 基础工具。expect 用于自动交互，psmisc 提供 fuser，HDFS 高可用切换要用
install_group "基础工具" psmisc expect curl unzip zip net-tools bc lsof patch

# 时间同步。集群节点间时钟不一致会导致各种诡异问题
install_group "时间同步" chrony

# yum 工具集。离线包制作时要用 yumdownloader
install_group "yum 工具" yum-utils dnf-plugins-core

# 编译工具链。部分服务组件需要本地编译
install_group "编译工具链" gcc gcc-c++ make autoconf automake libtool

# 压缩与加密库。Hadoop 系的原生库依赖这些
install_group "压缩与加密库" \
  zlib zlib-devel \
  openssl openssl-devel \
  snappy snappy-devel \
  bzip2 bzip2-devel \
  lzo lzo-devel \
  ncurses-devel

# 系统 Python。给节点上的运维脚本用，AIAgent 自带独立运行时不依赖这个
install_group "系统 Python" python3

# 下面这些在 EL8 上属于 EPEL，单独一组，装不上不影响平台核心功能
install_group "EPEL 附加工具" lrzsz lzop libXtst ruby

if [ ${#failed_groups[@]} -ne 0 ]; then
  echo "" >&2
  echo "以下分组安装失败：" >&2
  for g in "${failed_groups[@]}"; do
    echo "  - ${g}" >&2
  done
  echo "" >&2
  echo "常见原因：" >&2
  echo "  1. 没先执行 assistant/scripts/init-os-repo.sh，PowerTools/CRB 或 EPEL 未启用" >&2
  echo "  2. CentOS 8 已停止维护，官方源下线，需要切到 vault（init-os-repo.sh 会处理）" >&2
  echo "  3. 节点不通外网，需要改用离线包方式安装" >&2
  echo "" >&2
  exit 1
fi

echo ""
echo "全部软件包安装完成。"
echo "$0 done."
exit 0
