#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

new_password=$1
new_password=${new_password:-'2wsx@WSX'}

cluster_ip=$(kubectl -n kubesphere-system get svc | grep 'ks-apiserver' | awk '{print $3}')
current_password=$(kubectl logs -n kubesphere-system \
"$(kubectl get pod -n kubesphere-system -l 'app in (ks-install, ks-installer)' -o jsonpath='{.items[0].metadata.name}')" | grep 'Password:' | awk '{print $2}')

# 获取 Oauth Token
token=$(curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' \
 "http://${cluster_ip}:80/oauth/token" \
  --data-urlencode 'grant_type=password' \
  --data-urlencode 'username=admin' \
  --data-urlencode "password=${current_password}" \
  --data-urlencode 'client_id=kubesphere' \
  --data-urlencode 'client_secret=kubesphere' | jq -r '.access_token')

# 重置密码
curl -X PUT \
 -H 'Content-Type: application/json' \
 -H "Authorization: Bearer ${token}" \
 "http://${cluster_ip}:80/kapis/iam.kubesphere.io/v1alpha2/users/admin/password" \
  --data "{\"currentPassword\": \"${current_password}\",\"password\": \"${new_password}\" }"

exit 0