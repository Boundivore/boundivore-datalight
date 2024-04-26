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
package cn.boundivore.dl.plugin.base.bean;

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 服务和组件配置文件的元数据信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PluginConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private UnixEnv unixEnv;

    private MySQLEnv mysqlEnv;

    // 当前服务，当前组件，当前节点的配置元信息（存在于下方的 metaServiceList 集合中，这里单独拿出来引用，方便调取）
    private MetaService currentMetaService;
    private MetaComponent currentMetaComponent;

    // 根据 0-SERVICE-MANIFEST.yaml 中 dependencies 服务列表，
    // 传入当前服务，以及所依赖的服务的配置信息（包括当前服务自己），由开发者自行决定如何配置接下来的服务和组件
    // 注意：其中的服务可能存在于不同的集群，
    // 例如：COMPUTE 服务依赖的 STORAGE 服务来自于 MIXED 集群（ 计算集群的计算服务依赖于存储集群的存储服务）
    // <ServiceName, MetaComponent>
    private Map<String, MetaService> metaServiceMap;

    private List<MasterWorkerMeta> masterWorkerMetaList;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnixEnv implements Serializable {
        private static final long serialVersionUID = 5729312354089392214L;

        String JAVA_HOME;
        String DATALIGHT_DIR;
        String SERVICE_DIR;
        String LOG_DIR;
        String PID_DIR;
        String DATA_DIR;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MySQLEnv implements Serializable {
        private static final long serialVersionUID = -4899970505611114558L;

        String dbHost;
        String dbPort;
        String dbName;
        String dbUser;
        String dbPassword;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetaService implements Serializable {
        private static final long serialVersionUID = 1L;

        private PluginClusterMeta pluginClusterMeta;

        private String serviceName;

        private SCStateEnum serviceState;

        private List<ConfDir> confDirList;

        // <模板文件路径(templated-file-path), <{{占位符字串}}(placeholder), 执行部署前用户在页面提前设置的预配置内容>>
        private Map<String, Map<String, String>> configPreMap;

        // <ComponentName+NodeId, MetaComponent>
        private Map<String, MetaComponent> metaComponentMap;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetaComponent implements Serializable {
        private static final long serialVersionUID = 1L;

        private String componentName;

        private SCStateEnum componentState;

        private Long nodeId;

        private String nodeIp;

        private String hostname;

        // 内存字节数 单位 MB
        private Long ram;

        // CPU 核数
        private Long cpuCores;
    }


    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfDir implements Serializable {
        private static final long serialVersionUID = 1L;

        private String serviceConfDir;

        private String templatedDir;
    }

}
