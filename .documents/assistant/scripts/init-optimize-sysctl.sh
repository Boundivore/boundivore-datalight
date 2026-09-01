#!/bin/bash

# 调整内核参数。可重复执行，同一参数不会在文件里堆出多条。

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 文件路径变量
sysctl_conf="/etc/sysctl.conf"

# 定义优化参数
optimizations=(
    "vm.max_map_count=2000000"
    "vm.dirty_ratio=10"
    "vm.dirty_background_ratio=5"
    "vm.dirty_writeback_centisecs=200"
    "vm.vfs_cache_pressure=200"
    "vm.dirty_expire_centisecs=6000"
)

if [ ! -f "${sysctl_conf}" ]; then
    touch "${sysctl_conf}"
fi

# 备份只做一次，避免重复执行时把改过的内容当成原始配置存下来
if [ ! -f "${sysctl_conf}.backup" ]; then
    cp "${sysctl_conf}" "${sysctl_conf}.backup"
fi

# 先清掉这些参数已有的配置，再统一追加。
#
# 这里按参数名做字面比较，不用正则。原因有二：
# 一是按 "key=value" 整体匹配的话，文件里已有 vm.dirty_ratio=20 时匹配不上，
# 旧值留着新值追加，反复执行会堆出一串同名参数；
# 二是参数名里的点在正则里是通配符，vm.dirty_ratio 会误伤 vmXdirty_ratio 这种
# 名字相近的无关配置，而多层引号下的转义很容易写错。
keys_file=$(mktemp)
tmp_conf=$(mktemp)
trap 'rm -f "${keys_file}" "${tmp_conf}"' EXIT

for opt in "${optimizations[@]}"; do
    echo "${opt%%=*}" >>"${keys_file}"
done

awk -v keysfile="${keys_file}" '
    BEGIN { while ((getline k < keysfile) > 0) drop[k] = 1 }
    {
        key = $0
        sub(/#.*$/, "", key)              # 去掉行尾注释
        sub(/^[[:space:]]+/, "", key)     # 去掉前导空白
        sub(/[[:space:]]*=.*$/, "", key)  # 只留等号左边
        sub(/[[:space:]]+$/, "", key)     # 去掉尾随空白
        if (key in drop) next
        print
    }
' "${sysctl_conf}" >"${tmp_conf}"

cat "${tmp_conf}" >"${sysctl_conf}"

# 增加新的优化项
for opt in "${optimizations[@]}"; do
    echo "$opt" >>"${sysctl_conf}"
done

# 使配置生效
sysctl -p

echo "$0 done."
exit 0
