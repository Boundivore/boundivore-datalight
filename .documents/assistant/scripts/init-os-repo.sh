#!/bin/bash

# 准备操作系统软件源，必须在所有 yum 安装动作之前执行。
#
# CentOS 8 已于 2021-12-31 停止维护，mirrorlist.centos.org 已下线，
# 干净装完的机器执行 yum install 会直接报
# "Failed to download metadata for repo 'appstream'"。
# 本脚本把源指到 vault.centos.org，并启用 PowerTools 与 EPEL。
#
# PowerTools 必须启用：snappy-devel、lzo-devel、bzip2-devel 这些编译期依赖
# 在 EL8 上不在 BaseOS/AppStream 里，只在 PowerTools/CRB。
#
# Rocky Linux 8 与 AlmaLinux 8 仍在维护，源是通的，本脚本只补 CRB 与 EPEL。

if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges." >&2
  exit 1
fi

os_id=""
os_ver=""
if [ -r /etc/os-release ]; then
  # shellcheck disable=SC1091
  . /etc/os-release
  os_id="${ID}"
  os_ver="${VERSION_ID%%.*}"
fi

echo "检测到系统: ${os_id:-unknown} ${os_ver:-unknown}"

if [ "${os_ver}" != "8" ]; then
  echo "" >&2
  echo "本脚本针对 EL8（CentOS 8 / Rocky 8 / AlmaLinux 8）。" >&2
  echo "当前系统主版本为 ${os_ver:-未知}，请确认后再执行。" >&2
  echo "" >&2
  exit 1
fi

# ---------------------------------------------------------------- CentOS 8 换源

if [ "${os_id}" = "centos" ]; then
  # CentOS Stream 8 也已停止维护，同样走 vault
  if grep -rqls "mirrorlist=http" /etc/yum.repos.d/ 2>/dev/null; then
    echo "CentOS 8 官方源已下线，切换到 vault.centos.org ..."

    backup_dir="/etc/yum.repos.d/backup-$(date +%Y%m%d%H%M%S)"
    mkdir -p "${backup_dir}"
    cp -a /etc/yum.repos.d/CentOS-*.repo "${backup_dir}/" 2>/dev/null
    echo "  原 repo 文件已备份到 ${backup_dir}"

    # mirrorlist 走不通，注释掉；baseurl 从 mirror 指向 vault 并启用
    sed -i -e 's|^mirrorlist=|#mirrorlist=|g' \
           -e 's|^#\s*baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' \
           -e 's|^baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' \
           /etc/yum.repos.d/CentOS-*.repo

    yum clean all >/dev/null 2>&1
    rm -rf /var/cache/dnf /var/cache/yum 2>/dev/null
  else
    echo "未发现指向已下线镜像的配置，跳过换源"
  fi
fi

# ---------------------------------------------------------------- 启用 PowerTools / CRB

# 仓库名在各发行版和小版本之间不一致，逐个试，命中即止
enable_powertools() {
  local candidates="powertools PowerTools crb codeready-builder-for-rhel-8-x86_64-rpms"
  local name

  if ! command -v dnf >/dev/null 2>&1; then
    echo "  未找到 dnf，跳过 PowerTools 启用"
    return 0
  fi

  # 已启用就不用再动
  for name in ${candidates}; do
    if dnf repolist --enabled 2>/dev/null | grep -qi "^${name}[[:space:]]"; then
      echo "  ${name} 已启用"
      return 0
    fi
  done

  if ! command -v dnf-3 >/dev/null 2>&1 && ! rpm -q dnf-plugins-core >/dev/null 2>&1; then
    yum -y install dnf-plugins-core >/dev/null 2>&1
  fi

  for name in ${candidates}; do
    if dnf config-manager --set-enabled "${name}" >/dev/null 2>&1; then
      echo "  已启用 ${name}"
      return 0
    fi
  done

  echo "  警告: PowerTools/CRB 未能启用。" >&2
  echo "  snappy-devel、lzo-devel、bzip2-devel 可能装不上，需要手工处理。" >&2
  return 0
}

echo "启用 PowerTools / CRB ..."
enable_powertools

# ---------------------------------------------------------------- EPEL

# lzop、lrzsz 在 EL8 上只在 EPEL 里
if rpm -q epel-release >/dev/null 2>&1; then
  echo "EPEL 已安装"
else
  echo "安装 EPEL ..."
  yum -y install epel-release >/dev/null 2>&1
  if ! rpm -q epel-release >/dev/null 2>&1; then
    echo "  警告: epel-release 安装失败，lzop、lrzsz 可能装不上" >&2
  fi
fi

# EPEL 8 的 metalink 对已停服的 CentOS 8 仍然可用，这里只刷新缓存
echo "刷新软件源缓存 ..."
if yum makecache >/dev/null 2>&1; then
  echo "  缓存刷新成功"
else
  echo "" >&2
  echo "软件源不可用。请检查网络，或改用仍在维护的 Rocky Linux 8 / AlmaLinux 8。" >&2
  echo "" >&2
  exit 1
fi

echo ""
echo "已启用的仓库："
yum repolist 2>/dev/null | sed -n '2,20p'

echo ""
echo "$0 done."
exit 0
