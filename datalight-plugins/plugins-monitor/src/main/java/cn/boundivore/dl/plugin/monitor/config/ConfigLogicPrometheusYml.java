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
package cn.boundivore.dl.plugin.monitor.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.base.enumeration.impl.MasterWorkerEnum;
import cn.boundivore.dl.base.utils.YamlDeserializer;
import cn.boundivore.dl.base.utils.YamlSerializer;
import cn.boundivore.dl.plugin.base.bean.MasterWorkerMeta;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.bean.config.YamlPrometheusConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 配置 prometheus.yml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicPrometheusYml extends AbstractConfigLogic {

    private final static String JOB_NAME_Prometheus = "MONITOR-Prometheus";
    private final static String JOB_NAME_AlertManager = "MONITOR-AlertManager";
    private final static String JOB_NAME_Grafana = "MONITOR-Grafana";
    private final static String JOB_NAME_MySQLExporter = "MONITOR-MySQLExporter";
    private final static String JOB_NAME_NodeExporter = "MONITOR-NodeExporter";
    private final static String JOB_NAME_Master = "DATALIGHT-Master";
    private final static String JOB_NAME_Worker = "DATALIGHT-Worker";


    public ConfigLogicPrometheusYml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );
        // 获取 prometheus.yml 最终值
        return this.prometheusYml(replacedTemplated);
    }

    /**
     * Description: 获取 prometheus.yml 修改后的最终值
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param replacedTemplated 当前 prometheus 的值
     * @return 修改变更后的内容
     */
    private String prometheusYml(String replacedTemplated) {
        try {
            YamlPrometheusConfig yamlPrometheusConfig = YamlSerializer.toObject(
                    replacedTemplated,
                    YamlPrometheusConfig.class
            );

            yamlPrometheusConfig.getScrapeConfigs()
                    .forEach(i -> {
                        switch (i.getJobName()) {
                            case JOB_NAME_Prometheus:
                                i.getStaticConfigs().get(0)
                                        .setTargets(
                                                this.getTargetList("Prometheus")
                                        );
                                break;
                            case JOB_NAME_AlertManager:
                                i.getStaticConfigs().get(0)
                                        .setTargets(
                                                this.getTargetList("AlertManager")
                                        );
                                break;
                            case JOB_NAME_Grafana:
                                i.getStaticConfigs().get(0)
                                        .setTargets(
                                                this.getTargetList("Grafana")
                                        );
                                break;
                            case JOB_NAME_MySQLExporter:
                                i.getStaticConfigs().get(0)
                                        .setTargets(
                                                this.getTargetList("MySQLExporter")
                                        );
                                break;
                            case JOB_NAME_NodeExporter:
                                i.getStaticConfigs().get(0)
                                        .setTargets(
                                                this.getTargetList("NodeExporter")
                                        );
                                break;
                            case JOB_NAME_Master:
                                i.getStaticConfigs().get(0)
                                        .setTargets(
                                                this.getMasterWorkerMetaList(MasterWorkerEnum.MASTER)
                                        );
                                break;
                            case JOB_NAME_Worker:
                                i.getStaticConfigs().get(0)
                                        .setTargets(
                                                this.getMasterWorkerMetaList(MasterWorkerEnum.WORKER)
                                        );
                                break;
                            default:
                                break;
                        }
                    });

            yamlPrometheusConfig.getAlerting()
                    .getAlertmanagers()
                    .get(0)
                    .getStaticConfigs()
                    .get(0)
                    .setTargets(this.getTargetList("AlertManager"));


            replacedTemplated = YamlDeserializer.toString(yamlPrometheusConfig);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return replacedTemplated;
    }

    /**
     * Description: 获取 Master Worker Meta List
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param masterWorkerEnum Master Worker 枚举
     * @return 主机名:端口号 形式为 List<String>，例如：["hostname:port"]
     */
    private List<String> getMasterWorkerMetaList(MasterWorkerEnum masterWorkerEnum) {
        return super.pluginConfig
                .getMasterWorkerMetaList()
                .stream()
                .filter(meta -> meta.getMasterWorkerEnum() == masterWorkerEnum)
                .sorted(Comparator.comparing(MasterWorkerMeta::getHostname))
                .map(meta -> String.format(
                                "%s:%s",
                                meta.getHostname(),
                                meta.getPort()
                        )
                )
                .collect(Collectors.toList());
    }

    /**
     * Description: 拼接某个组件在 Prometheus 中的注册信息（targets 集合）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 主机名:端口号 形式为 List<String>，例如：["hostname:port"]
     */
    private List<String> getTargetList(String componentName) {
        return super.pluginConfig
                .getCurrentMetaService()
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals(componentName))
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .map(i -> String.format(
                        "%s:%s",
                        i.getHostname(),
                        PortConstants.getMonitorExporterPort(
                                super.pluginConfig
                                        .getCurrentMetaService()
                                        .getServiceName(),
                                i.getComponentName()
                        )
                ))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        YamlPrometheusConfig yamlPrometheusConfig = YamlSerializer.toObject(
                FileUtil.file("D:\\workspace\\datalight_workspace\\datalight\\.documents\\plugins\\MONITOR\\templated\\prometheus\\conf\\prometheus.yml"),
                YamlPrometheusConfig.class
        );
        System.out.println(yamlPrometheusConfig);


        yamlPrometheusConfig.getScrapeConfigs()
                .forEach(i -> i.getStaticConfigs().get(0)
                        .setTargets(
                                CollUtil.newArrayList("demo01:8888")
                        )
                );

        String string = YamlDeserializer.toString(yamlPrometheusConfig);
        System.out.println(string);
    }

}
