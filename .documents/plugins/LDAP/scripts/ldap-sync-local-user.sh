#!/bin/bash

# 全局路径变量参考：
# DATALIGHT_DIR="/opt/datalight"
# SERVICE_DIR="/srv/datalight"
# LOG_DIR="/data/datalight/logs"
# PID_DIR="/data/datalight/pids"
# DATA_DIR="/data/datalight/data"

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 检查参数是否提供
if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <ldap_admin_password> <user_password>"
  exit 1
fi

# LDAP 管理员的 DN 和密码
LDAP_ADMIN_DN="${LDAP_ADMIN_DN:-cn=admin,dc=datalight,dc=com}"
LDAP_ADMIN_PASS="$1"

# 普通用户密码
USER_PASSWORD="$2"

# LDAP 服务器地址
LDAP_SERVER="${LDAP_SERVER:-ldap://localhost}"

# 日志文件位置
LOG_FILE="${LOG_DIR}/LDAP/ldap_sync.log"

# 检查依赖项
function check_dependencies() {
    if ! command -v slappasswd &> /dev/null; then
        echo "Error: slappasswd is not installed." >&2
        exit 1
    fi
}

# 写入日志
function log() {
    local message="$1"
    echo "$(date '+%Y-%m-%d %H:%M:%S') $message" >> "$LOG_FILE"
}

# 获取本地用户列表并排除系统用户
function get_local_users() {
    awk -F: '$3 >= 1000 { print $1 }' /etc/passwd
}

# 生成用户的密码哈希
function generate_password_hash() {
    local password="$1"
    slappasswd -s "$password"
}

# 创建 LDIF 条目
function create_ldif_entry() {
    local user="$1"
    local password_hash="$2"
    cat <<EOF
dn: uid=$user,dc=datalight,dc=com
objectClass: inetOrgPerson
objectClass: posixAccount
objectClass: top
cn: $user
sn: $user
uid: $user
uidNumber: $(id -u $user)
gidNumber: $(id -g $user)
homeDirectory: /home/$user
loginShell: /bin/bash
userPassword: $password_hash
EOF
}

# 主函数
function main() {
    check_dependencies

    log "Starting LDAP synchronization."

    # 获取本地用户列表
    LOCAL_USERS=$(get_local_users)

    # 遍历每个本地用户
    for USER in $LOCAL_USERS; do
        # 生成用户的密码哈希
        PASSWORD_HASH=$(generate_password_hash "$USER_PASSWORD")

        # 创建 LDIF 条目
        LDIF_ENTRY=$(create_ldif_entry "$USER" "$PASSWORD_HASH")

        # 将用户添加到 LDAP
        echo "$LDIF_ENTRY" | ldapadd -x -D "$LDAP_ADMIN_DN" -w "$LDAP_ADMIN_PASS" -H "$LDAP_SERVER" || {
            log "Failed to add user $USER to LDAP."
            continue
        }

        log "User $USER added to LDAP successfully."
    done

    log "LDAP synchronization completed."
}

main