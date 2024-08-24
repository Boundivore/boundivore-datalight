#!/bin/bash

set -e
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

FILE_PATH=$1
USER=${2:-datalight}
GROUP=${3:-datalight}

# 函数：输出错误并退出
exit_on_error() {
  echo "Error: $1"
  exit 1
}

# 函数：设置所有者和权限
set_ownership_and_permissions() {
    if [ "$FILE_PATH" == "/etc/sssd/sssd.conf" ]; then
        chown root:root "$FILE_PATH" || exit_on_error "Failed to change ownership of $FILE_PATH"
        chmod 600 "$FILE_PATH" || exit_on_error "Failed to change permissions of $FILE_PATH"
    elif [[ "${FILE_PATH}" == *datalight* ]]; then
        PARENT_DIR=$(dirname "${FILE_PATH}")
        chown ${USER}:${GROUP} -R "${PARENT_DIR}" || exit_on_error "Failed to change ownership of ${PARENT_DIR}"
    else
        CURRENT_OWNER=$(stat -c "%U" "${FILE_PATH}")
        if [[ "${CURRENT_OWNER}" == "root" || "${CURRENT_OWNER}" == "${USER}" ]]; then
            chown ${USER}:${GROUP} "${FILE_PATH}" || exit_on_error "Failed to change ownership of ${FILE_PATH}"
            chmod 755 "${FILE_PATH}" || exit_on_error "Failed to change permissions of ${FILE_PATH}"
        fi
    fi
}

set_ownership_and_permissions "${FILE_PATH}"

echo "$0 done."
exit 0