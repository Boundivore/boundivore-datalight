#!/bin/bash

# 检查是否以 root 身份运行脚本
if [[ $EUID -ne 0 ]]; then
    echo "Please run the script with root privileges."
    exit 1
fi

# 检查 /etc/systemd/system.conf 配置
checkSystemdSystemConf() {
    defaultLimitNOFILE=$(grep '^DefaultLimitNOFILE=' /etc/systemd/system.conf | awk -F= '{print $2}')
    defaultLimitNPROC=$(grep '^DefaultLimitNPROC=' /etc/systemd/system.conf | awk -F= '{print $2}')

    if [[ $defaultLimitNOFILE -gt 65535 ]] && [[ $defaultLimitNPROC -gt 65535 ]]; then
        return 0
    else
        echo "Configuration not satisfied: /etc/systemd/system.conf"
        echo "Expected values: DefaultLimitNOFILE > 65535, DefaultLimitNPROC > 65535"
        return 1
    fi
}

# 检查 /etc/security/limits.conf 配置
checkLimitsConf() {
    if grep -qE '^\*\s+(soft|hard)\s+(fsize|cpu|as|nofile|nproc)\s+(unlimited|[0-9]+)$' /etc/security/limits.conf \
        && grep -qE '^\*\s+(soft|hard)\s+(nofile|nproc)\s+(unlimited|[0-9]+)$' /etc/security/limits.conf \
        && grep -qE '^root\s+soft\s+nproc\s+(unlimited|[0-9]+)$' /etc/security/limits.conf; then
        return 0
    else
        echo "Configuration not satisfied: /etc/security/limits.conf"
        echo "Expected configuration format: * (soft|hard) (fsize|cpu|as|nofile|nproc) (unlimited|[0-9]+)"
        return 1
    fi
}

# 检查 /etc/security/limits.d/20-nproc.conf 配置
checkLimitsNprocConf() {
    if grep -qE '^\*\s+soft\s+nproc\s+(unlimited|[0-9]+)$' /etc/security/limits.d/20-nproc.conf \
        && grep -qE '^root\s+soft\s+nproc\s+(unlimited|[0-9]+)$' /etc/security/limits.d/20-nproc.conf; then
        return 0
    else
        echo "Configuration not satisfied: /etc/security/limits.d/20-nproc.conf"
        echo "Expected configuration format: * soft nproc (unlimited|[0-9]+)"
        return 1
    fi
}

if checkSystemdSystemConf && checkLimitsConf && checkLimitsNprocConf; then
    echo "OK: All configurations are correct."
    exit 0
else
    echo "Some configurations are incorrect."
    exit 1
fi

echo "$0 done."
exit 0