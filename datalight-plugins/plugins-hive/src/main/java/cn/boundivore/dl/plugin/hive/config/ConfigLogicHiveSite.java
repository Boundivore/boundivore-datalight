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
package cn.boundivore.dl.plugin.hive.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 配置 hive-site.xml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicHiveSite extends AbstractConfigLogic {


    public ConfigLogicHiveSite(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // 获取 {{property.hive.log.dir}}
        String propertyHiveLogDir = this.propertyHiveLogDir();

        // 获取 {{hive.server2.thrift.bind.host}}
//        String hiveServer2ThriftBindHost = this.hiveServer2ThriftBindHost();

        // 获取 {{hive.metastore.warehouse.dir}}
        String hiveMetaStoreWarehouseDir = this.hiveMetaStoreWarehouseDir();

        // 获取 {{hive.metastore.uris}}
        String hiveMetastoreUris = this.hiveMetastoreUris();

        // 获取 {{javax.jdo.option.ConnectionURL.host}}
        String javaxJdoOptionConnectionHost = this.javaxJdoOptionConnectionHost();

        // 获取 {{javax.jdo.option.ConnectionURL.port}}
        String javaxJdoOptionConnectionPort = this.javaxJdoOptionConnectionPort();

        // 获取 {{javax.jdo.option.ConnectionUserName}}
        String javaxJdoOptionConnectionUserName = this.javaxJdoOptionConnectionUserName();

        // 获取 {{javax.jdo.option.ConnectionPassword}}
        String javaxJdoOptionConnectionPassword = this.javaxJdoOptionConnectionPassword();

        // 获取 {{hive.execution.engine}}
        String hiveExecutionEngine = this.hiveExecutionEngine();

        // 获取 {{hive.exec.scratchdir}}
        String hiveExecScratchdir = this.hiveExecScratchdir();

        // 获取 {{hadoop.zk.address}}、{{hbase.zookeeper.quorum}}、{{hive.zookeeper.quorum}}
        String zookeeperQuorum = this.zookeeperQuorum();

        // 获取 {{hive.zookeeper.namespace}}
        String hiveZookeeperNamespace = this.hiveZookeeperNamespace();

        // 获取 {{tez.lib.uris}}
        String tezLibUris = this.tezLibUris();

        // 获取 {{tez.tez-ui.history-url.base}}
        String tezUIHistoryUrlBase = this.tezUIHistoryUrlBase();


        return replacedTemplated
                .replace(
                        "{{property.hive.log.dir}}",
                        propertyHiveLogDir
                )
//                .replace(
//                        "{{hive.server2.thrift.bind.host}}",
//                        hiveServer2ThriftBindHost
//                )
                .replace(
                        "{{hive.metastore.warehouse.dir}}",
                        hiveMetaStoreWarehouseDir
                )
                .replace(
                        "{{hive.metastore.uris}}",
                        hiveMetastoreUris
                )
                .replace(
                        "{{javax.jdo.option.ConnectionURL.host}}",
                        javaxJdoOptionConnectionHost
                )
                .replace(
                        "{{javax.jdo.option.ConnectionURL.port}}",
                        javaxJdoOptionConnectionPort
                )
                .replace(
                        "{{javax.jdo.option.ConnectionUserName}}",
                        javaxJdoOptionConnectionUserName
                )
                .replace(
                        "{{javax.jdo.option.ConnectionPassword}}",
                        javaxJdoOptionConnectionPassword
                )

                .replace(
                        "{{hive.execution.engine}}",
                        hiveExecutionEngine
                )
                .replace(
                        "{{hive.exec.scratchdir}}",
                        hiveExecScratchdir
                )
                .replace(
                        "{{hbase.zookeeper.quorum}}",
                        zookeeperQuorum
                )
                .replace(
                        "{{hadoop.zk.address}}",
                        zookeeperQuorum
                )
                .replace(
                        "{{hive.zookeeper.quorum}}",
                        zookeeperQuorum
                )
                .replace(
                        "{{hive.zookeeper.namespace}}",
                        hiveZookeeperNamespace
                )
                .replace(
                        "{{tez.lib.uris}}",
                        tezLibUris
                )
                .replace(
                        "{{tez.tez-ui.history-url.base}}",
                        tezUIHistoryUrlBase
                )
                ;
    }


    /**
     * Description: 获取 Hive 日志存放目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Hive 日志存放目录
     */
    private String propertyHiveLogDir() {
        String logDir = super.logDir();
        // EXAMPLE: /data/datalight/logs/HIVE
        return String.format(
                "%s/HIVE",
                logDir
        );
    }

    /**
     * Description: 设置 HiveServer2 主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hiveServer2ThriftBindHost() {
        return "";
    }


    /**
     * Description: 获取 HIVE 在 HDFS 中的数据仓库地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hiveMetaStoreWarehouseDir() {
        return String.format(
                "/%s/user/hive/warehouse",
                super.pluginConfig.getCurrentMetaService()
                        .getPluginClusterMeta()
                        .getClusterName()
        );
    }


    /**
     * Description: 获取 Metastore 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hiveMetastoreUris() {
        List<PluginConfig.MetaComponent> metaStoreList = super.pluginConfig
                .getCurrentMetaService()
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("MetaStore"))
                .collect(Collectors.toList());

        return metaStoreList.stream()
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .map(metaComponent -> String.format("thrift://%s:9083", metaComponent.getHostname()))
                .collect(Collectors.joining(","));
    }


    /**
     * Description: 获取 MetaStore 元数据存储库的主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String javaxJdoOptionConnectionHost() {
        return super.pluginConfig.getMysqlEnv().getDbHost();
    }


    /**
     * Description: 获取 MetaStore 元数据存储库的端口号
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String javaxJdoOptionConnectionPort() {
        return super.pluginConfig.getMysqlEnv().getDbPort();
    }


    /**
     * Description: 获取 MetaStore 元数据存储库的用户名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String javaxJdoOptionConnectionUserName() {
        return super.pluginConfig.getMysqlEnv().getDbUser();
    }


    /**
     * Description: 获取 MetaStore 元数据存储库的密码
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String javaxJdoOptionConnectionPassword() {
        return super.pluginConfig.getMysqlEnv().getDbPassword();
    }


    /**
     * Description: 返回 Hive 默认执行引擎
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hiveExecutionEngine() {
        return "tez";
    }


    /**
     * Description: 获取执行 MapReduce 任务时的临时存储空间
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hiveExecScratchdir() {
        return String.format(
                "/%s/tmp/hive",
                super.pluginConfig.getCurrentMetaService()
                        .getPluginClusterMeta()
                        .getClusterName()
        );
    }


    /**
     * Description: 获取 Zookeeper 集群地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String zookeeperQuorum() {
        return super.pluginConfig
                .getMetaServiceMap()
                .get("ZOOKEEPER")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("QuarumPeermain"))
                .map(i -> String.format("%s:2181", i.getHostname()))
                .collect(Collectors.joining(","));
    }

    /**
     * Description: 获取 Hive 在 Zookeeper 中的命名空间挂载点
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hiveZookeeperNamespace() {
        return "hive";
    }

    /**
     * Description: 获取 tez 依赖包在 HDFS 上的存储位置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String tezLibUris() {
        String hdfsClusterName = super.pluginConfig
                .getMetaServiceMap()
                .get("HDFS")
                .getPluginClusterMeta()
                .getClusterName();

        return String.format(
                "hdfs://%s/tez/tez.tar.gz",
                hdfsClusterName
        );
    }

    /**
     * Description: 获取 tez UI 的 url 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String tezUIHistoryUrlBase() {
        List<PluginConfig.MetaComponent> tezUIList = super.pluginConfig
                .getCurrentMetaService()
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("TezUI"))
                .collect(Collectors.toList());

        return String.format(
                "http://%s:%s/tez-ui/",
                tezUIList.get(0).getHostname(),
                PortConstants.TEZ_UI_PORT
        );
    }

}
