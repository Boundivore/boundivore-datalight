#!/bin/bash
export MINIO_ROOT_USER=admin
export MINIO_ROOT_PASSWORD=adminadmin
export MINIO_BROWSER_ROOTDRIVE_ENABLED=on

LOG_DIR="{{LOG_DIR}}"

nohup /srv/datalight/MINIO/minio server \
--config-dir /srv/datalight/MINIO/conf \
--address "0.0.0.0:9539" \
--console-address ":9600" \
{{STORAGE_PATH}} \
> "${LOG_DIR}/minio-server.log" 2>&1 &

echo "MINIO server starting in background, check logs at ${LOG_DIR}/minio.log"

exit 0
