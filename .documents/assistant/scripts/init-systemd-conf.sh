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

# 修改文件 $limits_conf
cp "$limits_conf" "$limits_conf.backup"

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

# 修改文件 $nproc_conf
cp "$nproc_conf" "$nproc_conf.backup"

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
