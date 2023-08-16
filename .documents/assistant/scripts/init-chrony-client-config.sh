#!/bin/bash
# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查传入的参数数量
if [ "$#" -ne 1 ]; then
  echo "Error: please supply chrony server ip"
  echo "Example: ./init-chrony-client.sh 192.168.137.10"
  exit 1
fi

# 传入的主服务器的 IP 地址
MASTER_SERVER_IP="$1"

# 配置文件路径
CONFIG_FILE="/etc/chrony.conf"

# 覆盖配置文件
cat <<EOT > "$CONFIG_FILE"
# Use public servers from the pool.ntp.org project.
# Please consider joining the pool (http://www.pool.ntp.org/join.html).
server $MASTER_SERVER_IP iburst

# Record the rate at which the system clock gains/losses time.
driftfile /var/lib/chrony/drift

# Allow the system clock to be stepped in the first three updates
# if its offset is larger than 1 second.
makestep 1.0 3

# Enable kernel synchronization of the real-time clock (RTC).
rtcsync

# Enable hardware timestamping on all interfaces that support it.
#hwtimestamp *

# Increase the minimum number of selectable sources required to adjust
# the system clock.
#minsources 2

# Allow NTP client access from local network.
allow 192.168.0.0/16

# Serve time even if not synchronized to a time source.
#local stratum 10

# Specify file containing keys for NTP authentication.
#keyfile /etc/chrony.keys

# Specify directory for log files.
logdir /var/log/chrony

# Select which information is logged.
#log measurements statistics tracking
EOT


# 启动 Chrony 服务并设置开机自启动
systemctl restart chronyd
systemctl enable chronyd

# 输出配置信息
echo "ServerIp: $MASTER_SERVER_IP"
echo "Config file: $CONFIG_FILE"

echo "$0 done."
exit 0