#!/bin/bash

ALERTMANAGER_PATH="{{SERVICE_DIR}}/MONITOR/alertmanager/alertmanager"
ALERTMANAGER_CONFIG="{{SERVICE_DIR}}/MONITOR/alertmanager/alertmanager.yml"
ALERTMANAGER_STORAGE_PATH="{{DATA_DIR}}/MONITOR/alertmanager"
LISTEN_ADDRESS="--web.listen-address=0.0.0.0:9093"

start_alertmanager() {
  nohup "${ALERTMANAGER_PATH}" --config.file="${ALERTMANAGER_CONFIG}" --storage.path="${ALERTMANAGER_STORAGE_PATH}" "${LISTEN_ADDRESS}" > /dev/null 2>&1 &
}

stop_alertmanager() {
  pkill -f "${ALERTMANAGER_CONFIG}"
}

restart_alertmanager() {
  stop_alertmanager
  sleep 1
  start_alertmanager
}

case "$1" in
  "start")
    start_alertmanager
    ;;
  "stop")
    stop_alertmanager
    ;;
  "restart")
    restart_alertmanager
    ;;
  *)
    echo "Invalid argument. Please use 'start', 'stop', or 'restart'."
    ;;
esac