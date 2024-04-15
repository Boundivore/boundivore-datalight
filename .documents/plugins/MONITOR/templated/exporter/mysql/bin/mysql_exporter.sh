#!/bin/bash

EXPORTER_PATH="{{SERVICE_DIR}}/MONITOR/exporter/mysql/mysqld_exporter"
CONFIG_FILE="{{SERVICE_DIR}}/MONITOR/exporter/mysql/mysql-datalight.cnf"
EXPORTER_OPTIONS=(
  "--config.my-cnf=${CONFIG_FILE}"
  "--web.listen-address=0.0.0.0:9104"
  "--collect.slave_status"
  "--collect.binlog_size"
  "--collect.info_schema.processlist"
  "--collect.info_schema.innodb_metrics"
  "--collect.engine_innodb_status"
  "--collect.perf_schema.file_events"
  "--collect.perf_schema.replication_group_member_stats"
)

start_exporter() {
  nohup "${EXPORTER_PATH}" "${EXPORTER_OPTIONS[@]}" > /dev/null 2>&1 &
}

stop_exporter() {
    local pids
    pids=$(ps aux | grep "${CONFIG_FILE}" | grep -v grep | awk '{print $2}')
    if [ -n "$pids" ]; then
        kill -9 "${pids}"
        echo "Exporter stopped successfully."
    else
        echo "No Exporter process found."
    fi
}

restart_exporter() {
  stop_exporter
  sleep 3
  start_exporter
}

case "$1" in
  "start")
    start_exporter
    ;;
  "stop")
    stop_exporter
    ;;
  "restart")
    restart_exporter
    ;;
  *)
    echo "Invalid argument. Please use 'start', 'stop', or 'restart'."
    ;;
esac

exit 0