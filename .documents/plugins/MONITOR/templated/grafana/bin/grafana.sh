#!/bin/bash

GRAFANA_HOME="{{SERVICE_DIR}}/MONITOR/grafana"
GRAFANA_PATH="{{SERVICE_DIR}}/MONITOR/grafana/bin/grafana"
GRAFANA_CONFIG="{{SERVICE_DIR}}/MONITOR/grafana/conf/defaults.ini"

echo "GRAFANA_HOME: ${GRAFANA_HOME}"
echo "GRAFANA_PATH: ${GRAFANA_PATH}"
echo "GRAFANA_CONFIG: ${GRAFANA_CONFIG}"

start_grafana() {
  nohup "${GRAFANA_PATH}" server --config "${GRAFANA_CONFIG}" --homepath "${GRAFANA_HOME}" > /dev/null 2>&1 &
}

stop_grafana() {
  pkill -f "${GRAFANA_CONFIG}"
  exit 0
}

restart_grafana() {
  stop_grafana
  sleep 2
  start_grafana
}

case "$1" in
  "start")
    start_grafana
    ;;
  "stop")
    stop_grafana
    ;;
  "restart")
    restart_grafana
    ;;
  *)
    echo "Invalid argument. Please use 'start', 'stop', or 'restart'."
    ;;
esac