#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

#( sleep 3 && reboot -h now ) &
cmd="sleep 3; reboot -h now"
nohup bash -c "${cmd}" > /dev/null 2>&1 &

echo "The node will restart in 3 seconds"
echo "$0 done."
exit 0