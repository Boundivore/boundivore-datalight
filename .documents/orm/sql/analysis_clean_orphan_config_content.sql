-- 配置内容分析与自动批量清理脚本
-- 创建时间：2024-12-26
-- 用途：分析 t_dl_config_content 表中的重复和未引用记录
-- 执行环境：MySQL 5.7+

-- ===================== 清理遗留临时表 =====================
DROP TEMPORARY TABLE IF EXISTS cleanup_stats_before;
DROP TEMPORARY TABLE IF EXISTS cleanup_stats_after;
DROP TEMPORARY TABLE IF EXISTS duplicate_contents;
DROP TEMPORARY TABLE IF EXISTS duplicate_analysis;
DROP TEMPORARY TABLE IF EXISTS tmp_orphan_content_ids;
DROP TEMPORARY TABLE IF EXISTS tmp_batch_ids;
DROP TEMPORARY TABLE IF EXISTS cluster_stats;
DROP TEMPORARY TABLE IF EXISTS path_analysis;

-- ===================== 第一部分：数据分析 =====================

-- 创建清理日志表（如果不存在）
CREATE TABLE IF NOT EXISTS t_dl_cleanup_log (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                batch_number INT,
                                                cluster_id BIGINT,
                                                cleaned_records INT,
                                                cleaned_size_mb DECIMAL(10,2),
                                                start_time BIGINT,
                                                end_time BIGINT,
                                                execution_status VARCHAR(50),
                                                summary TEXT
);

-- ===================== 待清理数据详细分析 =====================

-- 1. 未引用配置详细信息
SELECT
    cc.id,
    cc.cluster_id,
    cc.filename,
    FROM_UNIXTIME(cc.create_time/1000) as create_time,
    FROM_UNIXTIME(cc.update_time/1000) as update_time,
    ROUND(LENGTH(cc.config_data)/1024, 2) as size_kb,
    ROUND(LENGTH(cc.config_data)/1024/1024, 2) as size_mb,
    LEFT(cc.sha256, 8) as sha256_prefix,
    CASE
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)) * 1000 THEN '1天内'
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)) * 1000 THEN '7天内'
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 30 DAY)) * 1000 THEN '30天内'
        ELSE '30天以上'
        END as age
FROM t_dl_config_content cc
         LEFT JOIN t_dl_config c ON cc.id = c.config_content_id
WHERE c.id IS NULL
ORDER BY cc.create_time DESC;

-- 2. 按集群统计待清理配置
SELECT
    cc.cluster_id,
    COUNT(*) as orphan_count,
    ROUND(SUM(LENGTH(cc.config_data)/1024/1024), 2) as total_size_mb,
    MIN(FROM_UNIXTIME(cc.create_time/1000)) as earliest_record,
    MAX(FROM_UNIXTIME(cc.create_time/1000)) as latest_record,
    COUNT(DISTINCT cc.filename) as unique_files
FROM t_dl_config_content cc
         LEFT JOIN t_dl_config c ON cc.id = c.config_content_id
WHERE c.id IS NULL
GROUP BY cc.cluster_id
ORDER BY orphan_count DESC;

-- 3. 按时间段统计待清理配置
SELECT
    CASE
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)) * 1000 THEN '1天内'
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)) * 1000 THEN '7天内'
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 30 DAY)) * 1000 THEN '30天内'
        ELSE '30天以上'
        END as time_range,
    COUNT(*) as record_count,
    ROUND(SUM(LENGTH(cc.config_data)/1024/1024), 2) as total_size_mb,
    COUNT(DISTINCT cc.cluster_id) as affected_clusters
FROM t_dl_config_content cc
         LEFT JOIN t_dl_config c ON cc.id = c.config_content_id
WHERE c.id IS NULL
GROUP BY
    CASE
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)) * 1000 THEN '1天内'
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)) * 1000 THEN '7天内'
        WHEN cc.create_time > UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 30 DAY)) * 1000 THEN '30天内'
        ELSE '30天以上'
        END
ORDER BY
    CASE time_range
        WHEN '1天内' THEN 1
        WHEN '7天内' THEN 2
        WHEN '30天内' THEN 3
        ELSE 4
        END;

-- 4. 重复配置分析
SELECT
    cc.cluster_id,
    cc.filename,
    cc.sha256,
    COUNT(*) as duplicate_count,
    ROUND(SUM(LENGTH(cc.config_data)/1024/1024), 2) as total_size_mb,
    GROUP_CONCAT(FROM_UNIXTIME(cc.create_time/1000)) as create_times,
    GROUP_CONCAT(cc.id) as content_ids
FROM t_dl_config_content cc
GROUP BY cc.cluster_id, cc.filename, cc.sha256
HAVING COUNT(*) > 1
ORDER BY COUNT(*) DESC, cc.cluster_id, cc.filename;

-- 5. 大文件配置分析（大于1MB）
SELECT
    cc.id,
    cc.cluster_id,
    cc.filename,
    FROM_UNIXTIME(cc.create_time/1000) as create_time,
    ROUND(LENGTH(cc.config_data)/1024/1024, 2) as size_mb,
    LEFT(cc.sha256, 8) as sha256_prefix,
    CASE WHEN c.id IS NULL THEN '未引用' ELSE '已引用' END as reference_status
FROM t_dl_config_content cc
         LEFT JOIN t_dl_config c ON cc.id = c.config_content_id
WHERE LENGTH(cc.config_data) > 1024 * 1024
ORDER BY LENGTH(cc.config_data) DESC
LIMIT 20;

-- 6. 总体统计
SELECT
    COUNT(*) as total_orphan_records,
    COUNT(DISTINCT cluster_id) as affected_clusters,
    ROUND(SUM(LENGTH(config_data)/1024/1024), 2) as total_size_mb,
    MIN(FROM_UNIXTIME(create_time/1000)) as earliest_record,
    MAX(FROM_UNIXTIME(create_time/1000)) as latest_record
FROM t_dl_config_content cc
         LEFT JOIN t_dl_config c ON cc.id = c.config_content_id
WHERE c.id IS NULL;

/*
查询结果说明：
1. 未引用配置详细信息：展示每条待清理记录的详细信息
2. 集群统计：按集群维度统计待清理的配置数量和大小
3. 时间分布：展示不同时间段的待清理配置分布
4. 重复配置：识别完全相同的配置文件
5. 大文件分析：找出占用空间较大的配置文件
6. 总体统计：提供待清理数据的整体视图

使用建议：
1. 优先检查大文件配置，评估清理影响
2. 关注重复配置的情况，考虑优化存储
3. 查看时间分布，确定清理策略
4. 注意集群维度的影响范围
5. 建议分批次进行清理操作
*/