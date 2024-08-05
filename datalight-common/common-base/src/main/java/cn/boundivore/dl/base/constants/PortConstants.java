/**
 * Copyright (C) <2023> <Boundivore> <boundivore@foxmail.com>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Apache License, Version 2.0
 * as published by the Apache Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License, Version 2.0 for more details.
 * <p>
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program; if not, you can obtain a copy at
 * http://www.apache.org/licenses/LICENSE-2.0.
 */
package cn.boundivore.dl.base.constants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: 端口号常量类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class PortConstants {

    public static final String TEZ_UI_PORT = "9999";

    // <ServiceName-ComponentName, ExporterPort>
    public final static Map<String, String> EXPORTER_PORT_MAP = new LinkedHashMap<String, String>() {
        private static final long serialVersionUID = -8990084112729979456L;


        {
            put("MONITOR-AlertManager", "9093");
            put("MONITOR-Prometheus", "9090");
            put("MONITOR-Grafana", "3000");
            put("MONITOR-MySQLExporter", "9104");
            put("MONITOR-NodeExporter", "9100");

            put("ZOOKEEPER-QuarumPeermain", "19001");

            put("HDFS-JournalNode", "19002");
            put("HDFS-NameNode", "19003");
            put("HDFS-ZKFailoverController", "19004");
            put("HDFS-DataNode", "19005");
            put("HDFS-HttpFS", "19006");

            put("YARN-ResourceManager", "19007");
            put("YARN-NodeManager", "19008");
            put("YARN-TimelineServer", "19009");
            put("YARN-HistoryServer", "19010");

            put("HIVE-MetaStore", "19011");
            put("HIVE-HiveServer2", "19012");
            put("HIVE-TezUI", "19013");

            put("HBASE-HMaster", "19014");
            put("HBASE-HRegionServer", "19015");
            put("HBASE-HThriftServer2", "19016");

            put("KAFKA-KafkaBroker", "19017");

            put("SPARK-SparkHistoryServer", "19018");

            put("FLINK-FlinkHistoryServer", "19019");

            put("ZKUI-ZKUIServer", "19020");

            put("KYUUBI-KyuubiServer", "19021");
        }
    };

    // <ServiceName-ComponentName, RemotePort>
    public final static Map<String, String> REMOTE_PORT_MAP = new LinkedHashMap<String, String>() {
        private static final long serialVersionUID = -6792272135600528315L;

        {
            put("ZOOKEEPER-QuarumPeermain", "18001");

            put("HDFS-JournalNode", "18002");
            put("HDFS-NameNode", "18003");
            put("HDFS-ZKFailoverController", "18004");
            put("HDFS-DataNode", "18005");
            put("HDFS-HttpFS", "18006");

            put("YARN-ResourceManager", "18007");
            put("YARN-NodeManager", "18008");
            put("YARN-TimelineServer", "18009");
            put("YARN-HistoryServer", "18010");

            put("HIVE-MetaStore", "18011");
            put("HIVE-HiveServer2", "18012");
            put("HIVE-TezUI", "18013");

            put("HBASE-HMaster", "18014");
            put("HBASE-HRegionServer", "18015");
            put("HBASE-HThriftServer2", "18016");

            put("KAFKA-KafkaBroker", "18017");

            put("SPARK-SparkHistoryServer", "18018");

            put("FLINK-FlinkHistoryServer", "18019");

            put("ZKUI-ZKUIServer", "18020");

            put("KYUUBI-KyuubiServer", "18021");
        }
    };

    /**
     * Description: 根据服务名称、组件名称获取 Monitor Exporter 端口号
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceName   服务名称
     * @param componentName 组件名称
     * @return Exporter 端口号
     */
    public static String getExporterPort(String serviceName, String componentName) {
        return EXPORTER_PORT_MAP.get(
                String.format(
                        "%s-%s",
                        serviceName,
                        componentName
                )
        );
    }

    /**
     * Description: 根据服务名称、组件名称获取 Monitor Remote 端口号
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceName   服务名称
     * @param componentName 组件名称
     * @return Remote 端口号
     */
    public static String getRemotePort(String serviceName, String componentName) {
        return REMOTE_PORT_MAP.get(
                String.format(
                        "%s-%s",
                        serviceName,
                        componentName
                )
        );
    }
}
