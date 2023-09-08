#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 暴露 apiserver 服务
kubectl -n kubesphere-system patch service ks-apiserver -p '{"spec":{"type":"NodePort"}}'
