#!/bin/bash
# Global path variables reference:
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

set -e

# Check if the script is run as root
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

USER_NAME="datalight"
GROUP_NAME="datalight"

SERVICE_NAME="SOLR"

CURRENT_SERVICE_DIR="${SERVICE_DIR}/${SERVICE_NAME}"

# Check if the parameter is empty
if [ -z "$1" ]; then
  echo "Usage: $0 <SolrServer> <start|stop|restart>"
  exit 1
fi

# Ensure log and PID directories exist
mkdir -p "${LOG_DIR}/${SERVICE_NAME}"
mkdir -p "${PID_DIR}/${SERVICE_NAME}"

chown ${USER_NAME}:${GROUP_NAME} -R "${LOG_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${PID_DIR}"
chown ${USER_NAME}:${GROUP_NAME} -R "${DATA_DIR}"

# Get the first parameter (component name)
COMPONENT_NAME="$1"
# Get the second parameter (operation type)
OPERATION="$2"

# Output operation reminder
echo "To ${OPERATION} ${COMPONENT_NAME} ..."

# Define start, stop, and restart functions
start_solrserver() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/bin/solr start -c"
  echo "SolrServer started."
}

stop_solrserver() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/bin/solr stop"
  echo "SolrServer stopped."
}

restart_solrserver() {
  su -s /bin/bash "${USER_NAME}" -c "${CURRENT_SERVICE_DIR}/bin/solr restart -c"
  echo "SolrServer restarted."
}

# Execute the corresponding start, stop, or restart command
case "${COMPONENT_NAME}" in
  "SolrServer")
    case "${OPERATION}" in
      "start")
        start_solrserver
        ;;
      "stop")
        stop_solrserver
        ;;
      "restart")
        restart_solrserver
        ;;
      *)
        echo "Invalid operation. Usage: $0 ${COMPONENT_NAME} [start|stop|restart]"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "Invalid component name. Supported component: <SolrServer>"
    exit 1
    ;;
esac

exit 0