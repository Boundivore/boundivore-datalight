package cn.boundivore.dl.orm.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: NewCodeGeneratorConstants
 * 代码生成器的数据库连接信息。本项目开源，连接串与账号密码一律从环境变量或 JVM 参数读取，
 * 不在源码中保留任何真实凭据。
 * 支持的环境变量（JVM 参数同名，用 -D 传入，优先级高于环境变量）：
 * DL_GENERATOR_DB_URL、DL_GENERATOR_DB_USER、DL_GENERATOR_DB_PASSWORD
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/20
 * Modification description: 移除硬编码数据库凭据，改为从环境变量读取
 * Modified by: Boundivore
 * Modification time: 2026/9/1
 * Version: V2.0
 */
public class INewCodeGeneratorConstants {

    public static final String DEFAULT_DB_NAME = "db_datalight";

    private static final String ENV_DB_URL = "DL_GENERATOR_DB_URL";
    private static final String ENV_DB_USER = "DL_GENERATOR_DB_USER";
    private static final String ENV_DB_PASSWORD = "DL_GENERATOR_DB_PASSWORD";

    private static final String DEFAULT_DB_URL =
            "jdbc:mysql://localhost:3306/db_datalight?useUnicode=true&useSSL=false&characterEncoding=utf8";

    public static final Map<String, DatabaseInfo> databaseList = new HashMap<String, DatabaseInfo>() {
        {
            put(
                    DEFAULT_DB_NAME,
                    new DatabaseInfo(
                            resolve(ENV_DB_URL, DEFAULT_DB_URL),
                            resolve(ENV_DB_USER, "root"),
                            resolve(ENV_DB_PASSWORD, "")
                    )
            );
        }
    };

    /**
     * Description: 优先取 JVM 参数，其次取环境变量，都没有则回落到默认值
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param key          配置项名称
     * @param defaultValue 默认值
     * @return String 最终取到的配置值
     */
    private static String resolve(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(key);
        }
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    /**
     * Description: 返回指定数据库的连接信息
     * Created by: Boundivore
     * Creation time: 2023/5/20
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param dbName 数据库名称
     * @return DatabaseInfo 数据库连接信息
     */
    public static DatabaseInfo database(String dbName) {
        return databaseList.get(dbName);
    }

}
