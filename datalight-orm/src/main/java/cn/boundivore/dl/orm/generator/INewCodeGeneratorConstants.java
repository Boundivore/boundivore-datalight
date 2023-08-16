package cn.boundivore.dl.orm.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: NewCodeGeneratorConstants
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class INewCodeGeneratorConstants {

    public static final Map<String, DatabaseInfo> databaseList = new HashMap<String, DatabaseInfo>() {
        {
            put(
                    "db_datalight",
                    new DatabaseInfo(
                            "jdbc:mysql://node01:3306/db_datalight?useUnicode=true&useSSL=false&characterEncoding=utf8",
                            "root",
                            "1qaz!QAZ"
                    )
            );
        }
    };


    /**
     * Description: Return database info.
     * Created by: Boundivore
     * Creation time: 2023/5/20
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param dbName db_data_light
     * @return DatabaseInfo
     */
    public static DatabaseInfo database(String dbName) {
        return databaseList.get(dbName);
    }

}
