#!/bin/bash
set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查 expect 是否已安装
check_expect_installed() {
  # 使用命令 command -v 检查 expect 是否已安装，并且禁止输出结果到终端
  if command -v expect > /dev/null; then
    echo "OK: Expect is installed."
  else
    echo "Expect is not installed. Please install expect before running this script."
    exit 1
  fi
}

# 调用函数检查 expect 是否已安装
check_expect_installed

echo "$0 done."
exit 0
