#!/bin/bash

# 定义源目录
SOURCE_DIRS="/srv/datalight/YARN/etc/hadoop:/srv/datalight/YARN/share/hadoop/common/lib/*:/srv/datalight/YARN/share/hadoop/common/*:/srv/datalight/YARN/share/hadoop/hdfs:/srv/datalight/YARN/share/hadoop/hdfs/lib/*:/srv/datalight/YARN/share/hadoop/hdfs/*:/srv/datalight/YARN/share/hadoop/mapreduce/lib/*:/srv/datalight/YARN/share/hadoop/mapreduce/*:/srv/datalight/YARN/share/hadoop/yarn/lib/*:/srv/datalight/YARN/share/hadoop/yarn/*"

# 初始化总大小变量
total_size=0

# 使用冒号分割源目录字符串并循环每个路径
IFS=':' read -ra ADDR <<< "$SOURCE_DIRS"
for dir in "${ADDR[@]}"; do
  # 检查路径中是否包含通配符，并计算大小
  if [[ "$dir" == *\* ]]; then
    # 计算匹配通配符的文件大小
    for file in $dir; do
      if [ -f "$file" ]; then
        size=$(du -k "$file" | cut -f1)
        total_size=$((total_size + size))
      fi
    done
  else
    # 计算目录下所有文件的大小
    size=$(du -sk "$dir" | cut -f1)
    total_size=$((total_size + size))
  fi
done

# 将总大小从 KB 转换为 MB
total_size_mb=$((total_size / 1024))

echo "总大小为: $total_size_mb MB"