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

import cn.boundivore.dl.base.enumeration.impl.MasterWorkerEnum;
import cn.boundivore.dl.base.utils.YamlSerializer;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.bean.config.YamlAlertManagerConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.util.stream.Collectors;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * Description: 配置 alertmanager.yml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicAlertManagerYml extends AbstractConfigLogic {


    public ConfigLogicAlertManagerYml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );
        try {
            YamlAlertManagerConfig yamlAlertManagerConfig = YamlSerializer.toObject(
                    replacedTemplated,
                    YamlAlertManagerConfig.class
            );

            yamlAlertManagerConfig.getReceivers()
                    .get(0)
                    .getWebhookConfigs()
                    .get(0)
                    .setUrl(this.getWebReceiver());


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return replacedTemplated;
    }

    /**
     * Description: Master 告警钩子接口
     * /api/v1/master/alert/alertHook
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 告警接口
     */
    private String getWebReceiver() {
        return super.pluginConfig
                .getMasterWorkerMetaList()
                .stream()
                .filter(meta -> meta.getMasterWorkerEnum() == MasterWorkerEnum.MASTER)
                .map(meta -> String.format(
                                "http://%s:%s%s/alert/alertHook",
                                meta.getHostname(),
                                meta.getPort(),
                                MASTER_URL_PREFIX
                        )
                )
                .collect(Collectors.toList())
                .get(0);
    }

    public static void main(String[] args) {
        try {
            String replacedTemplated = FileUtil.readString(
                    "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\templated\\alertmanager\\conf\\alertmanager.yml",
                    CharsetUtil.CHARSET_UTF_8
            );

            YamlAlertManagerConfig yamlAlertManagerConfig = YamlSerializer.toObject(
                    replacedTemplated,
                    YamlAlertManagerConfig.class
            );

            yamlAlertManagerConfig.getReceivers()
                    .get(0)
                    .getWebhookConfigs()
                    .get(0)
                    .setUrl("TestUrl");

            System.out.println(yamlAlertManagerConfig);


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
