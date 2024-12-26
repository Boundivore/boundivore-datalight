-- 配置内容清理脚本
-- 创建时间：2024-12-26
-- 用途：批量清理 t_dl_config_content 表中未被引用的记录（可定时，一般情况下，不需要清理，数据量较小，且可能被复用）
-- 执行环境：MySQL 5.7+
-- 优化说明：针对MySQL 5.7特性优化，提升性能和可靠性

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ===================== 清理遗留临时表 =====================
DROP TEMPORARY TABLE IF EXISTS tmp_orphan_content_ids;
DROP TEMPORARY TABLE IF EXISTS tmp_batch_ids;
DROP TEMPORARY TABLE IF EXISTS tmp_cleanup_stats;
DROP TEMPORARY TABLE IF EXISTS cleanup_total_stats;

-- ===================== 删除已存在的存储过程和日志表 =====================
DROP PROCEDURE IF EXISTS sp_clean_orphan_config;
DROP TABLE IF EXISTS t_dl_cleanup_log;

-- ===================== 创建执行日志表 =====================
CREATE TABLE t_dl_cleanup_log (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                                  batch_number INT COMMENT '批次号',
                                  cleaned_records INT COMMENT '清理记录数',
                                  cleaned_size_mb DECIMAL(10,2) COMMENT '清理数据大小(MB)',
                                  start_time BIGINT COMMENT '开始时间',
                                  end_time BIGINT COMMENT '结束时间',
                                  execution_status VARCHAR(50) COMMENT '执行状态',
                                  summary TEXT COMMENT '执行摘要',
                                  KEY idx_batch_number (batch_number),
                                  KEY idx_status_time (execution_status, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '配置清理日志表';

-- ===================== 创建清理存储过程 =====================
DELIMITER //

CREATE PROCEDURE sp_clean_orphan_config(
    IN p_batch_size INT,              -- 每批处理记录数
    IN p_sleep_seconds DECIMAL(10,2), -- 批次间隔休眠时间
    IN p_max_batches INT              -- 最大批次数
)
BEGIN
    -- 声明变量
    DECLARE v_batch_number INT DEFAULT 0;
    DECLARE v_batch_count INT DEFAULT 0;
    DECLARE v_batch_size_mb DECIMAL(10,2);
    DECLARE v_continue BOOLEAN DEFAULT TRUE;
    DECLARE v_cleanup_log_id BIGINT;
    DECLARE v_batch_log_id BIGINT;
    DECLARE v_error_occurred BOOLEAN DEFAULT FALSE;
    DECLARE v_error_message TEXT;
    DECLARE v_start_time BIGINT;

    -- 声明异常处理
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        BEGIN
            SET v_error_occurred = TRUE;
            GET DIAGNOSTICS CONDITION 1
                v_error_message = MESSAGE_TEXT;
            -- 记录错误日志
            IF v_batch_log_id IS NOT NULL THEN
                UPDATE t_dl_cleanup_log
                SET execution_status = 'ERROR',
                    summary = CONCAT('Error occurred: ', v_error_message),
                    end_time = UNIX_TIMESTAMP(NOW()) * 1000
                WHERE id = v_batch_log_id;
            END IF;
        END;

    -- 开始事务
    START TRANSACTION;

    -- 创建统计临时表（提升性能）
    CREATE TEMPORARY TABLE tmp_cleanup_stats (
                                                 total_records BIGINT,
                                                 cleanable_records BIGINT,
                                                 cleanable_size_mb DECIMAL(10,2),
                                                 KEY (total_records)
    ) ENGINE=MEMORY;

    -- 填充统计数据（使用JOIN代替子查询）
    INSERT INTO tmp_cleanup_stats
    SELECT
        (SELECT COUNT(*) FROM t_dl_config_content),
        COUNT(cc.id),
        COALESCE(SUM(LENGTH(cc.config_data)/1024/1024), 0)
    FROM t_dl_config_content cc
             LEFT JOIN t_dl_config c ON cc.id = c.config_content_id
    WHERE c.id IS NULL;

    -- 初始化清理记录
    SET v_start_time = UNIX_TIMESTAMP(NOW()) * 1000;
    INSERT INTO t_dl_cleanup_log(
        batch_number, start_time, execution_status, summary
    )
    SELECT
        0,
        v_start_time,
        'STARTED',
        CONCAT('Initial state: ',
               total_records, ' total records, ',
               cleanable_records, ' cleanable records, ',
               ROUND(cleanable_size_mb, 2), ' MB cleanable')
    FROM tmp_cleanup_stats;

    SET v_cleanup_log_id = LAST_INSERT_ID();

    -- 创建待清理记录临时表（使用索引优化）
    CREATE TEMPORARY TABLE tmp_orphan_content_ids (
                                                      id BIGINT,
                                                      content_size_mb DECIMAL(10,2),
                                                      KEY idx_id (id)
    ) ENGINE=MEMORY;

    -- 填充待清理数据
    INSERT INTO tmp_orphan_content_ids
    SELECT
        cc.id,
        LENGTH(cc.config_data)/1024/1024
    FROM t_dl_config_content cc
             LEFT JOIN t_dl_config c ON cc.id = c.config_content_id
    WHERE c.id IS NULL;

    COMMIT;

    -- 批量处理循环
    cleanup_loop: WHILE v_continue AND v_batch_number < p_max_batches DO
            -- 开始新的批次事务
            START TRANSACTION;

            SET v_batch_number = v_batch_number + 1;

            -- 创建本批次处理记录
            INSERT INTO t_dl_cleanup_log(
                batch_number, start_time, execution_status
            )
            VALUES (
                       v_batch_number,
                       UNIX_TIMESTAMP(NOW()) * 1000,
                       'PROCESSING'
                   );

            SET v_batch_log_id = LAST_INSERT_ID();

            -- 获取本批次要处理的记录
            CREATE TEMPORARY TABLE IF NOT EXISTS tmp_batch_ids (
                                                                   id BIGINT,
                                                                   content_size_mb DECIMAL(10,2),
                                                                   KEY idx_id (id)
            ) ENGINE=MEMORY;

            INSERT INTO tmp_batch_ids
            SELECT id, content_size_mb
            FROM tmp_orphan_content_ids
            LIMIT p_batch_size;

            -- 统计本批次数据
            SELECT
                COUNT(*),
                COALESCE(SUM(content_size_mb), 0)
            INTO
                v_batch_count,
                v_batch_size_mb
            FROM tmp_batch_ids;

            -- 检查是否还有数据需要处理
            IF v_batch_count = 0 OR v_error_occurred = TRUE THEN
                SET v_continue = FALSE;
                ROLLBACK;
            ELSE
                -- 执行删除操作
                DELETE FROM t_dl_config_content
                WHERE id IN (
                    SELECT id FROM tmp_batch_ids
                );

                -- 更新处理日志
                UPDATE t_dl_cleanup_log
                SET cleaned_records = v_batch_count,
                    cleaned_size_mb = v_batch_size_mb,
                    end_time = UNIX_TIMESTAMP(NOW()) * 1000,
                    execution_status = 'COMPLETED',
                    summary = CONCAT(
                            'Cleaned ',
                            v_batch_count,
                            ' records, ',
                            ROUND(v_batch_size_mb, 2),
                            ' MB'
                              )
                WHERE id = v_batch_log_id;

                -- 从待处理表中删除已处理的记录
                DELETE FROM tmp_orphan_content_ids
                WHERE id IN (
                    SELECT id FROM tmp_batch_ids
                );

                -- 提交事务
                COMMIT;

                -- 删除本批次临时表
                DROP TEMPORARY TABLE IF EXISTS tmp_batch_ids;

                -- 休眠指定时间
                DO SLEEP(p_sleep_seconds);
            END IF;
        END WHILE;

    -- 创建汇总统计临时表
    CREATE TEMPORARY TABLE cleanup_total_stats AS
    SELECT
        SUM(cleaned_records) as total_cleaned_records,
        SUM(cleaned_size_mb) as total_cleaned_size_mb
    FROM t_dl_cleanup_log
    WHERE id != v_cleanup_log_id
      AND execution_status = 'COMPLETED';

    -- 更新总执行记录
    UPDATE t_dl_cleanup_log
    SET end_time = UNIX_TIMESTAMP(NOW()) * 1000,
        cleaned_records = (SELECT total_cleaned_records FROM cleanup_total_stats),
        cleaned_size_mb = (SELECT total_cleaned_size_mb FROM cleanup_total_stats),
        execution_status = IF(v_error_occurred, 'ERROR', 'COMPLETED'),
        summary = CONCAT(
                'Total cleaned: ',
                (SELECT total_cleaned_records FROM cleanup_total_stats),
                ' records, ',
                ROUND((SELECT total_cleaned_size_mb FROM cleanup_total_stats), 2),
                ' MB',
                IF(v_error_occurred, CONCAT(' (Error: ', v_error_message, ')'), '')
                  )
    WHERE id = v_cleanup_log_id;

    -- 输出清理报告
    SELECT
        'Cleanup Summary' as description,
        cts.total_records as before_total_records,
        cts.total_records - COALESCE(cls.total_cleaned_records, 0) as after_total_records,
        COALESCE(cls.total_cleaned_records, 0) as cleaned_records,
        ROUND(COALESCE(cls.total_cleaned_size_mb, 0), 2) as cleaned_size_mb,
        IF(v_error_occurred, 'ERROR', 'COMPLETED') as final_status,
        IF(v_error_occurred, v_error_message, 'Success') as execution_message,
        TIMESTAMPDIFF(
                SECOND,
                FROM_UNIXTIME(MIN(cl.start_time/1000)),
                FROM_UNIXTIME(MAX(cl.end_time/1000))
        ) as execution_seconds
    FROM tmp_cleanup_stats cts
             LEFT JOIN cleanup_total_stats cls ON 1=1
             LEFT JOIN t_dl_cleanup_log cl ON cl.execution_status IN ('COMPLETED', 'ERROR');

    -- 清理所有临时表
    DROP TEMPORARY TABLE IF EXISTS tmp_cleanup_stats;
    DROP TEMPORARY TABLE IF EXISTS cleanup_total_stats;
    DROP TEMPORARY TABLE IF EXISTS tmp_orphan_content_ids;
    DROP TEMPORARY TABLE IF EXISTS tmp_batch_ids;

END //

DELIMITER ;

-- ===================== 执行清理存储过程 =====================
CALL sp_clean_orphan_config(500, 0.5, 1000);

-- ===================== 输出批次执行详情 =====================
SELECT
    batch_number,
    cleaned_records,
    ROUND(cleaned_size_mb, 2) as cleaned_size_mb,
    FROM_UNIXTIME(start_time/1000) as start_time,
    FROM_UNIXTIME(end_time/1000) as end_time,
    FLOOR((end_time - start_time)/1000) as seconds_taken,
    execution_status,
    summary
FROM t_dl_cleanup_log
WHERE batch_number > 0
ORDER BY batch_number;

SET FOREIGN_KEY_CHECKS = 1;

/*
重要说明：
1. 此脚本针对MySQL 5.7优化，通过存储过程实现批量清理未引用的配置内容记录
2. 性能优化：
   - 使用临时表缓存中间结果
   - 优化子查询结构
   - 合理使用索引
   - 批量处理提高效率
3. 可靠性提升：
   - 添加错误处理机制
   - 使用事务确保数据一致性
   - 详细的执行日志
4. 默认参数：
   - 批次大小：500条记录
   - 休眠间隔：0.5秒
   - 最大批次：1000
5. 监控和统计：
   - 详细的执行日志
   - 性能统计
   - 错误追踪

使用建议：
1. 执行前先分析数据量和资源情况
2. 在业务低峰期执行清理操作
3. 执行前务必做好数据备份
4. 可根据系统负载调整批次参数
5. 及时检查执行日志和统计信息

注意事项：
1. 确保数据库有足够的连接数和临时表空间
2. 监控系统资源使用情况
3. 建议先在测试环境验证
4. 保持数据库事务日志有足够空间
5. 建议配置合理的锁等待超时时间
*/