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
package cn.boundivore.dl.plugin.zookeeper.config.event;

import cn.boundivore.dl.plugin.base.bean.PluginConfigSelf;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.plugin.base.config.event.AbstractConfigEventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Description: Zookeeper 依赖的服务发生变动时，检查是否需要联动修改自身的配置文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigEventHandler extends AbstractConfigEventHandler {

    @Override
    public List<String> getRelativeConfigPathList(List<String> configPathList) {
        return super.getRelativeConfigPathList(configPathList);
    }

    @Override
    public PluginConfigResult configByEvent(PluginConfigSelf pluginConfigSelf) {
        return super.configByEvent(pluginConfigSelf);
    }
}
