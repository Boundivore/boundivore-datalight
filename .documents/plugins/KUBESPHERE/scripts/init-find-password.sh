#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 查看 KubeSphere 初始密码
kubectl logs -n kubesphere-system \
"$(kubectl get pod -n kubesphere-system -l 'app in (ks-install, ks-installer)' -o jsonpath='{.items[0].metadata.name}')" | grep 'Password:' | awk '{print $2}'