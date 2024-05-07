#!/bin/bash

# 定义源目录和目标目录
SOURCE_DIRS="/srv/datalight/YARN/etc/hadoop:/srv/datalight/YARN/share/hadoop/common/lib/*:/srv/datalight/YARN/share/hadoop/common/*:/srv/datalight/YARN/share/hadoop/hdfs:/srv/datalight/YARN/share/hadoop/hdfs/lib/*:/srv/datalight/YARN/share/hadoop/hdfs/*:/srv/datalight/YARN/share/hadoop/mapreduce/lib/*:/srv/datalight/YARN/share/hadoop/mapreduce/*:/srv/datalight/YARN/share/hadoop/yarn/lib/*:/srv/datalight/YARN/share/hadoop/yarn/*"
TARGET_DIR="/data/install/FLINK/lib-aux"

# 检查目标目录是否存在
if [ ! -d "$TARGET_DIR" ]; then
  echo "目标目录 $TARGET_DIR 不存在。"
  exit 1
fi

# 使用冒号分割源目录字符串并循环每个路径
IFS=':' read -ra ADDR <<< "$SOURCE_DIRS"
for dir in "${ADDR[@]}"; do
  # 检查路径中是否包含通配符
  if [[ "$dir" == *\* ]]; then
    # 使用 globbing 拷贝文件，仅包括以 .jar 结尾的文件
    find $dir -type f -name "*.jar" -exec cp -v {} "$TARGET_DIR" \;
  else
    # 拷贝整个目录中的 .jar 文件到目标目录
    find $dir -type f -name "*.jar" -exec cp -v {} "$TARGET_DIR" \;
  fi
done

echo "文件拷贝完成。"