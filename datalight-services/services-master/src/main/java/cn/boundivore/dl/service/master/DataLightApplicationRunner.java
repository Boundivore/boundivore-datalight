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
package cn.boundivore.dl.service.master;

import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.service.master.resolver.*;
import cn.boundivore.dl.service.master.service.MasterManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Description: 程序初始化器，加载部署配置文件到内存
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class DataLightApplicationRunner implements ApplicationRunner {

    private final MasterManageService masterManageService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // ./conf/datalight
        ResolverYamlDirectory.resolver(SpringContextUtil.CONF_ENV_DIR);

        // ./conf/service
        ResolverYamlServiceManifest.resolver(SpringContextUtil.CONF_SERVICE_DIR);

        // ./conf/service
        ResolverYamlServiceDetail.resolver(SpringContextUtil.CONF_SERVICE_DIR);

        // ./plugins
        ResolverYamlServicePlaceholder.resolver(SpringContextUtil.PLUGINS_DIR);

        // ./node/conf
        ResolverYamlNode.resolver(SpringContextUtil.NODE_CONF_DIR);

        this.masterManageService.updateMasterMeta();
    }

}
