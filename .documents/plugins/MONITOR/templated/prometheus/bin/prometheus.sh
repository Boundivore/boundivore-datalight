#!/bin/bash

PROMETHEUS_PATH="{{SERVICE_DIR}}/MONITOR/prometheus/prometheus"
PROMETHEUS_CONFIG="{{SERVICE_DIR}}/MONITOR/prometheus/prometheus.yml"
PROMETHEUS_LOG_PATH="{{LOG_DIR}}/MONITOR/prometheus.log"

echo "PROMETHEUS_PATH: ${PROMETHEUS_PATH}"
echo "PROMETHEUS_CONFIG: ${PROMETHEUS_CONFIG}"
echo "PROMETHEUS_LOG_PATH: ${PROMETHEUS_LOG_PATH}"

EXPORTER_OPTIONS=(
  "--web.listen-address=0.0.0.0:9090"
  "--storage.tsdb.path={{DATA_DIR}}/MONITOR/prometheus"
  "--storage.tsdb.retention.time=8d"
  "--web.enable-lifecycle"
  "--web.max-connections=1000000"
  "--web.read-timeout=1m"
  "--query.max-concurrency=10000"
  "--query.timeout=1m"
)

start_prometheus() {
  nohup "${PROMETHEUS_PATH}" --config.file="${PROMETHEUS_CONFIG}" "${EXPORTER_OPTIONS[@]}" > "${PROMETHEUS_LOG_PATH}" 2>&1 &
}

stop_prometheus() {
  local pids
      pids=$(ps aux | grep "${PROMETHEUS_CONFIG}" | grep -v grep | awk '{print $2}')
      if [ -n "$pids" ]; then
          kill -9 "${pids}"
      else
          echo "No Prometheus process found."
      fi
  exit 0
}

restart_prometheus() {
  stop_prometheus
  sleep 3
  start_prometheus
}

case "$1" in
  "start")
    start_prometheus
    ;;
  "stop")
    stop_prometheus
    ;;
  "restart")
    restart_prometheus
    ;;
  *)
    echo "Invalid argument. Please use 'start', 'stop', or 'restart'."
    ;;
esac
