#!/bin/bash

# 把某个软件包及其全部依赖下载下来打成 tar.gz，供离线节点安装。
# example: sh tool-download-compress-yum.sh yum-utils
#
# 需要在一台能联网、且与目标节点同版本同架构的机器上执行。
# 产出放到 assistant/repo/ 下，由 init-yum-localinstall-*.sh 解压安装。

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges." >&2
    exit 1
fi

if [ $# -eq 0 ]; then
  echo "Usage: $0 <package_name>" >&2
  exit 1
fi

# 获取当前脚本所在目录的绝对路径
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 获取 /opt/datalight/init 路径
parent_dir="$(dirname "$script_dir")"
echo "parent_dir: $parent_dir"

# 获取 /opt/datalight/init/packages 路径
packages_dir="$parent_dir/packages"
echo "packages_dir: $packages_dir"

if [ ! -d "$packages_dir" ]; then
  mkdir -p "$packages_dir"
fi

package_name=$1
folder_name="yum-$package_name"
tar_file="$folder_name.tar.gz"

work_dir=$(mktemp -d)
trap 'rm -rf "${work_dir}"' EXIT
download_dir="${work_dir}/${folder_name}"
mkdir -p "${download_dir}"

# 下载包及其依赖。
#
# dnf 与 yum 在这里行为不一样，必须区分：
# yumdownloader --resolve 在 dnf 上只下载「当前机器还没装」的依赖，
# 而制作离线包的机器往往已经装好了这些依赖，结果就是依赖被静默漏掉，
# 拿到离线节点上安装时才报缺包。dnf download --resolve --alldeps 才是
# 无论本机装没装都一并下载。
if command -v dnf >/dev/null 2>&1; then
  echo "使用 dnf download --resolve --alldeps 下载 ${package_name} ..."
  if ! dnf download --resolve --alldeps --destdir="${download_dir}" "${package_name}"; then
    echo "下载失败: ${package_name}" >&2
    exit 1
  fi
else
  echo "使用 yumdownloader 下载 ${package_name} ..."
  if ! yumdownloader --resolve --destdir="${download_dir}" "${package_name}"; then
    echo "下载失败: ${package_name}" >&2
    exit 1
  fi
fi

rpm_count=$(find "${download_dir}" -maxdepth 1 -name '*.rpm' | wc -l)
if [ "${rpm_count}" -eq 0 ]; then
  echo "没有下载到任何 rpm，检查包名是否正确: ${package_name}" >&2
  exit 1
fi
echo "共下载 ${rpm_count} 个 rpm"

# 压缩。解压时会还原出 $folder_name 目录，
# 与 init-yum-localinstall-*.sh 里查找 rpm 的路径对应
tar -C "${work_dir}" -zcf "${work_dir}/${tar_file}" "${folder_name}"
mv "${work_dir}/${tar_file}" "${packages_dir}/"

echo "已生成: ${packages_dir}/${tar_file}"
echo "$0 done."
exit 0
