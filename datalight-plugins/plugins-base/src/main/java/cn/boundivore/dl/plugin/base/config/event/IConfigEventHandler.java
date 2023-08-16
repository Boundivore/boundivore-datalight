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
package cn.boundivore.dl.plugin.base.config.event;

import cn.boundivore.dl.plugin.base.bean.PluginConfigEvent;
import cn.boundivore.dl.plugin.base.bean.PluginConfigSelf;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;

import java.util.List;

/**
 * Description: 处理服务配置变动更新的事件信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023-04-25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public interface IConfigEventHandler {
    /**
     * Description: 初始化发生的配置事件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigEvent 被依赖服务的配置文件发生变动，包括变动的内容
     */
    void init(PluginConfigEvent pluginConfigEvent);


    /**
     * Description: 根据发生的配置事件，检查是否需要修改自身的配置文件，
     * 如果需要，则根据 configPathList 返回自己本次需要修改的配置文件列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param configPathList 当前服务自己的配置文件列表（绝对路径）
     * @return 受影响的配置文件的绝对路径
     */
    List<String> getRelativeConfigPathList(List<String> configPathList);

    /**
     * Description: 根据发生的配置事件，检查是否需要修改自身的配置文件，如果需要，则修改自身的配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigSelf 当前服务自己的配置文件信息（只包含 getRelativeConfigPathList 中指定的配置文件）
     * @return ConfigResult 自身配置文件修改结果，如果未修改，则 configMap 必须为元素为 0 的集合，否则会导致互相依赖的服务陷入修改循环
     */
    PluginConfigResult configByEvent(PluginConfigSelf pluginConfigSelf);

}
