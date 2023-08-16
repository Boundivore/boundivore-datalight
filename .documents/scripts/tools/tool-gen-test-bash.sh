#!/bin/bash

# 脚本数量
script_count=100

# 创建 scripts/init 目录（如果不存在）
mkdir -p ./init

# 生成测试脚本
for ((i = 1; i <= script_count; i++))
do
    # 生成随机字符串
    random_string=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 8 | head -n 1)

    # 创建测试脚本文件
    script_file="scripts/init/test_script$i.sh"
    touch "$script_file"

    # 将脚本内容写入文件
    echo -e "#!/bin/bash\n\n# 输出脚本名称和随机字符串\necho \"Script Name: $script_file\"\necho \"Random String: $random_string\"" > "$script_file"

    # 添加可执行权限
    chmod +x "$script_file"
done

echo "$0 done."
exit 0