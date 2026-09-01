#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 文件路径变量
system_conf="/etc/systemd/system.conf"
limits_conf="/etc/security/limits.conf"
nproc_conf="/etc/security/limits.d/20-nproc.conf"

# 修改文件 $system_conf
sed -i '/^DefaultLimitNOFILE=/d' "$system_conf"
sed -i '/^DefaultLimitNPROC=/d' "$system_conf"
echo "DefaultLimitNOFILE=131072" >> "$system_conf"
echo "DefaultLimitNPROC=131072" >> "$system_conf"

# 备份只做一次。原先每次执行都覆盖 .backup，
# 第二次跑就会把已经改过的内容当成原始备份存下来，真正的原始配置丢失。
if [ -f "$limits_conf" ] && [ ! -f "$limits_conf.backup" ]; then
    cp "$limits_conf" "$limits_conf.backup"
fi

# 使用覆盖模式修改文件
cat > "/etc/security/limits.conf" << EOF
root        soft    nproc   131072
root        hard    nproc   131072
root        soft    nofile  131072
root        hard    nofile  131072
*           soft    nproc   131072
*           hard    nproc   131072
*           soft    nofile  131072
*           hard    nofile  131072
*           hard    fsize   unlimited
*           soft    fsize   unlimited
*           soft    cpu     unlimited
*           hard    cpu     unlimited
*           soft    as      unlimited
*           hard    as      unlimited
EOF

# 同上，只备份一次。该文件在部分系统上可能不存在，先判断
if [ -f "$nproc_conf" ] && [ ! -f "$nproc_conf.backup" ]; then
    cp "$nproc_conf" "$nproc_conf.backup"
fi
mkdir -p "$(dirname "$nproc_conf")"

# 使用覆盖模式修改文件
cat > "$nproc_conf" << EOF
# Default limit for number of user's processes to prevent
# accidental fork bombs.
# See rhbz #432903 for reasoning.

*          soft    nproc     131072
root       soft    nproc     unlimited
EOF

echo "$0 done."
exit 0
