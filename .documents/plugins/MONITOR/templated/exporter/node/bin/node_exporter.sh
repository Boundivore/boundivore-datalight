#!/bin/bash

EXPORTER_PATH="{{SERVICE_DIR}}/MONITOR/exporter/node/node_exporter"

EXPORTER_OPTIONS=(
  "--web.listen-address=0.0.0.0:9100"
)


start_exporter() {
  nohup "${EXPORTER_PATH}" "${EXPORTER_OPTIONS[@]}" > /dev/null 2>&1 &
}

stop_exporter() {
  pkill -f "node_exporter ${LISTEN_ADDRESS}"
  exit 0
}

restart_exporter() {
  stop_exporter
  sleep 1
  start_exporter
}

if [[ "$1" == "start" ]]; then
  start_exporter
elif [[ "$1" == "stop" ]]; then
  stop_exporter
elif [[ "$1" == "restart" ]]; then
  restart_exporter
else
  echo "Invalid argument. Please use 'start', 'stop', or 'restart'."
fi