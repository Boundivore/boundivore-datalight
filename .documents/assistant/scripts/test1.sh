#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

test_array=("${@:1}")

echo "params: "
for item in "${test_array[@]}"; do
  echo "${item}"
done

echo "-------------------------test1.sh done.-------------------------"
exit 0
