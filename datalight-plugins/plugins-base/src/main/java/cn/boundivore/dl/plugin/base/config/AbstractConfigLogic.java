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
package cn.boundivore.dl.plugin.base.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Description: 通用逻辑处理父类，保存通用变量或通用逻辑等
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public abstract class AbstractConfigLogic {
    protected PluginConfig pluginConfig;

    protected PluginConfig.MetaService currentMetaService;

    protected PluginConfig.MetaComponent currentMetaComponent;

    public AbstractConfigLogic(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.currentMetaService = pluginConfig.getCurrentMetaService();
        this.currentMetaComponent = this.pluginConfig.getCurrentMetaComponent();
    }

    public abstract String config(File file, String replacedTemplated);

    protected void printFilename(File file) {
        log.info(
                String.format(
                        "准备处理配置文件: %s",
                        file.getName()
                )
        );
    }

}
