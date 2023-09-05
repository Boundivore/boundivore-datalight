#!/bin/bash

# example: sh init-ssh-gen-key.sh

# 获取当前用户的主目录
USER_HOME="${HOME}"

# 移除 SSH 询问提示
removeSSHAsk() {
  if [ $EUID -eq 0 ]; then
    sed -i '/^#.*StrictHostKeyChecking ask/s/^#//g' /etc/ssh/ssh_config
    sed -i '/StrictHostKeyChecking ask/s/ask/no/g' /etc/ssh/ssh_config
  fi
}

# 生成 SSH 密钥配置
keygenConfig() {
/usr/bin/expect <<-EOF
        set timeout -1
        spawn ssh-keygen -t rsa
        expect {
            "Enter file in which to save the key*" { send "\r"; exp_continue }
            "Overwrite (y/n)?*" { send "n\r"; exp_continue }
            "Enter passphrase (empty for no passphrase):*" { send "\r"; exp_continue }
            "Enter same passphrase again: " { send "\r"; exp_continue }
            eof
        }
EOF

  wait
  echo "Finish ssh-keygen -t rsa."
}

# 配置 authorized_keys 文件
configAuthorizedKeys() {
  cat "$USER_HOME/.ssh/id_rsa.pub" >>"$USER_HOME/.ssh/authorized_keys"
}

removeSSHAsk
keygenConfig
configAuthorizedKeys

echo "$0 done."
exit 0
