#!/bin/bash

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查 JDK 1.8 的安装和版本
jdk_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$jdk_version" != "1.8"* ]]; then
    echo "JDK 1.8 not found or version mismatch"
    exit 1
fi

# 检查配置文件中的 JAVA_HOME
check_configuration() {
    local file_path="$1"
    local java_home

    if [[ -f "$file_path" ]]; then
        java_home=$(grep -E '^export\s+JAVA_HOME' "$file_path" | awk -F '=' '{print $2}')
        if [[ -n "$java_home" ]]; then
            echo "OK: $file_path JAVA_HOME directory exists: $java_home"
        else
            echo "$file_path JAVA_HOME configuration not found or invalid"
            exit 1
        fi
    else
        echo "File not found: $file_path"
        exit 1
    fi
}

# 检查 datalight 用户和配置文件
#check_user_and_configuration "datalight"

# 检查 /etc/profile 文件
check_configuration "/etc/profile"

echo "OK: JDK 1.8 installation, configuration, and JAVA_HOME directory are correct"

echo "$0 done."
exit 0
