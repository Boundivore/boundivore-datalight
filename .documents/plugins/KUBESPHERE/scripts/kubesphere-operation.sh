#!/bin/bash
# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"


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

HARBOR_DIR="/opt/harbor"

# 检查参数是否为空
if [ -z "$1" ]; then
  echo "Usage: $0 <Harbor|K8S> <start|stop|restart>"
  exit 1
fi

# 获取第一个参数（组件名称）
COMPONENT_NAME="$1"
# 获取第二个参数（操作类型）
OPERATION="$2"

# 输出操作提醒
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# new cmd: su -c "cmd" "${USER_NAME}"

shift

case "${COMPONENT_NAME}" in
"Harbor")
  case "$1" in
  "start")
    ${HARBOR_DIR}/docker-compose up -d
    ;;
  "stop")
    ${HARBOR_DIR}/docker-compose down
    ;;
  "restart")
    ${HARBOR_DIR}/docker-compose down
    sleep 2
    ${HARBOR_DIR}/docker-compose up -d
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
"K8S")
  case "$1" in
  "start")
    kubectl apply -f before_stop_scaling.yaml
    ;;
  "stop")
    kubectl get deployments,statefulsets --all-namespaces -o yaml > before_stop_scaling.yaml
    kubectl get deployments,statefulsets --all-namespaces -o jsonpath='{range .items[*]}{.metadata.namespace} {.metadata.name}{"\n"}{end}' | while read -r ns kind name; do kubectl scale --replicas=0 "$kind/$name" -n "$ns"; done
    ;;
  "restart")
    for kind in deployment statefulset daemonset; do kubectl get "$kind" --all-namespaces -o jsonpath='{range .items[*]}{.metadata.namespace} {.metadata.name}{"\n"}{end}' | while read -r ns name; do kubectl rollout restart "$kind/$name" -n "$ns"; done; done
#    kubectl get deployment --all-namespaces -o jsonpath='{range .items[*]}{.metadata.namespace} {.metadata.name}{"\n"}{end}' | while read -r ns name; do kubectl rollout restart deployment "$name" -n "$ns"; done
#    kubectl get statefulset --all-namespaces -o jsonpath='{range .items[*]}{.metadata.namespace} {.metadata.name}{"\n"}{end}' | while read -r ns name; do kubectl rollout restart statefulset "$name" -n "$ns"; done
#    kubectl get daemonset --all-namespaces -o jsonpath='{range .items[*]}{.metadata.namespace} {.metadata.name}{"\n"}{end}' | while read -r ns name; do kubectl rollout restart daemonset "$name" -n "$ns"; done
    ;;
  *)
    echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
    exit 1
    ;;
  esac
  ;;
*)
  echo "Invalid component name. Supported components: <Harbor|K8S> <start|stop|restart>"
  exit 1
  ;;
esac

exit 0
