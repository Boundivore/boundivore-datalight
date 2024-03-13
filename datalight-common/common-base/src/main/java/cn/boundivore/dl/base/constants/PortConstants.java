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

            put("ZOOKEEPER-QuarumPeermain", "17001");

            put("HDFS-JournalNode", "17002");
            put("HDFS-NameNode", "17003");
            put("HDFS-ZKFailoverController", "17004");
            put("HDFS-DataNode", "17005");
            put("HDFS-HttpFS", "17006");

            put("YARN-ResourceManager", "17007");
            put("YARN-NodeManager", "17008");
            put("YARN-TimelineServer", "17009");
            put("YARN-HistoryServer", "17010");

            put("HIVE-MetaStore", "17011");
            put("HIVE-HiveServer2", "17012");
            put("HIVE-TezUI", "17013");

        }
    };

    // <ServiceName-ComponentName, RemotePort>
    public final static Map<String, String> REMOTE_PORT_MAP = new LinkedHashMap<String, String>() {
        private static final long serialVersionUID = -6792272135600528315L;

        {
            put("ZOOKEEPER-QuarumPeermain", "16001");

            put("HDFS-JournalNode", "16002");
            put("HDFS-NameNode", "16003");
            put("HDFS-ZKFailoverController", "16004");
            put("HDFS-DataNode", "16005");
            put("HDFS-HttpFS", "16006");

            put("YARN-ResourceManager", "16007");
            put("YARN-NodeManager", "16008");
            put("YARN-TimelineServer", "16009");
            put("YARN-HistoryServer", "16010");

            put("HIVE-MetaStore", "16011");
            put("HIVE-HiveServer2", "16012");
            put("HIVE-TezUI", "16013");
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
