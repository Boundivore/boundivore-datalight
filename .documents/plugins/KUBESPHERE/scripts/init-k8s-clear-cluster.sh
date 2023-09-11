#!/bin/bash

set -e

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# shellcheck disable=SC2034
USER_NAME="datalight"
# shellcheck disable=SC2034
GROUP_NAME="datalight"

SERVICE_NAME="KUBESPHERE"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"
BIN_DIR="${CURRENT_SERVICE_DIR}/bin"
CONFIG_DIR="${CURRENT_SERVICE_DIR}/conf"

# 授权可执行权限
chmod +x -R "${BIN_DIR}"

# 准备清理 K8S 集群
/usr/bin/expect <<-EOF
    set timeout -1
    spawn "${BIN_DIR}/kk" delete cluster -f "${CONFIG_DIR}/datalight-config-auth-harbor.yaml"
    expect {
        "Are you sure to delete this cluster? [yes/no]:*" { send "yes\r"; exp_continue }
        eof
    }
EOF

kubectl delete --all pods --all-namespaces
kubectl delete --all deployments --all-namespaces
kubectl delete --all services --all-namespaces
kubectl delete ns kubesphere-system kubesphere-monitoring-system openpitrix-system
rm -rf /etc/kubernetes /etc/cni /opt/cni /var/lib/etcd /var/lib/kubelet /var/run/kubernetes ~/.kube/
docker stop $(docker ps -aq)
docker rm -f $(docker ps -aq)
docker rmi -f $(docker images -q)
docker network prune -f
docker volume prune -f

