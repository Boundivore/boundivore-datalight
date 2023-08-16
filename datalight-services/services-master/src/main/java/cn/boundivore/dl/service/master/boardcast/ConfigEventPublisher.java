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
package cn.boundivore.dl.service.master.boardcast;

import cn.boundivore.dl.plugin.base.bean.PluginConfigEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Description: 配置文件事件消息发布者
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(ConfigEvent configEvent) {
        PluginConfigEvent pluginConfigEvent = configEvent.getPluginConfigEvent();

        log.info("发布配置文件变动: ClusterId: {} ServiceName: {}",
                pluginConfigEvent.getClusterId(),
                pluginConfigEvent.getServiceName()
        );

        eventPublisher.publishEvent(
                new ConfigEvent(
                        pluginConfigEvent
                )
        );
    }
}
