#!/bin/bash

# 校验节点上的两套 JDK：
#   JAVA_HOME           大数据服务使用，要求 JDK 8
#   DATALIGHT_JAVA_HOME DataLight Master / Worker 使用，要求 JDK 17 及以上
# 两套并存，缺一不可。

# 检查是否以 root 身份运行脚本
if [ "$EUID" -ne 0 ]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 从 /etc/profile 中读取变量的配置值
read_from_profile() {
    local var_name="$1"
    grep -E "^export[[:space:]]+${var_name}=" /etc/profile | tail -n 1 | awk -F '=' '{print $2}' | tr -d '"'
}

# 取某个 JDK 的主版本号。JDK 8 输出形如 1.8.0_202，JDK 9 以后形如 17.0.13
java_major_version() {
    local java_bin="$1"
    local raw
    raw=$("${java_bin}" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "${raw}" == 1.* ]]; then
        echo "${raw}" | awk -F '.' '{print $2}'
    else
        echo "${raw}" | awk -F '.' '{print $1}'
    fi
}

# 校验一套 JDK：目录存在、java 可执行、主版本号匹配
# $1 变量名  $2 期望主版本  $3 用途说明
check_jdk() {
    local var_name="$1"
    local expect_major="$2"
    local purpose="$3"

    local java_home
    java_home=$(read_from_profile "${var_name}")

    if [[ -z "${java_home}" ]]; then
        echo "FAILED: /etc/profile 中未配置 ${var_name}（${purpose}）"
        return 1
    fi

    if [[ ! -d "${java_home}" ]]; then
        echo "FAILED: ${var_name} 指向的目录不存在: ${java_home}"
        return 1
    fi

    if [[ ! -x "${java_home}/bin/java" ]]; then
        echo "FAILED: ${java_home}/bin/java 不存在或不可执行"
        return 1
    fi

    local major
    major=$(java_major_version "${java_home}/bin/java")
    if [[ "${major}" != "${expect_major}" ]]; then
        echo "FAILED: ${var_name}（${purpose}）需要 JDK ${expect_major}，实际为 ${major}: ${java_home}"
        return 1
    fi

    echo "OK: ${var_name}=${java_home}，JDK ${major}，${purpose}"
    return 0
}

exit_code=0

check_jdk "JAVA_HOME" "8" "大数据服务" || exit_code=1
check_jdk "DATALIGHT_JAVA_HOME" "17" "DataLight Master/Worker" || exit_code=1

if [[ ${exit_code} -ne 0 ]]; then
    echo "JDK 环境校验未通过，请先执行 assistant/scripts/init-jdk.sh"
    exit 1
fi

echo "OK: 两套 JDK 安装与配置均正确"

echo "$0 done."
exit 0
