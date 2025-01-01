#!/bin/bash

# 检查是否以root权限运行
if [ "$(id -u)" != "0" ]; then
   echo "此脚本需要root权限"
   exit 1
fi

DISK="/dev/sdb"
MOUNT_POINT="/data1"

# 检查磁盘是否存在
if [ ! -b "$DISK" ]; then
    echo "错误: 找不到磁盘 $DISK"
    exit 1
fi

# 检查是否已经被分区
if fdisk -l "$DISK" | grep "${DISK}[0-9]" >/dev/null; then
    echo "警告: 磁盘已经包含分区"
    exit 1
fi

echo "开始创建分区..."
# 使用here document自动化fdisk分区
fdisk "$DISK" <<EOF
n
p
1


w
EOF

# 等待系统识别新分区
sleep 2

# 检查分区是否创建成功
if [ ! -b "${DISK}1" ]; then
    echo "错误: 分区创建失败"
    exit 1
fi

echo "正在格式化分区..."
# 格式化分区
mkfs.ext4 "${DISK}1"

echo "创建挂载点..."
# 创建挂载点目录
mkdir -p "$MOUNT_POINT"

echo "挂载分区..."
# 挂载分区
mount "${DISK}1" "$MOUNT_POINT"

# 检查是否挂载成功
if ! mount | grep "${DISK}1" > /dev/null; then
    echo "错误: 挂载失败"
    exit 1
fi

echo "配置开机自动挂载..."
# 检查fstab中是否已经存在该配置
if ! grep -q "${DISK}1" /etc/fstab; then
    echo "${DISK}1    $MOUNT_POINT    ext4    defaults    0    0" >> /etc/fstab
fi

echo "验证挂载结果..."
df -h | grep "$MOUNT_POINT"

echo "完成！新磁盘已成功挂载到 $MOUNT_POINT"