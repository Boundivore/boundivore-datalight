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
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.boundivore.dl.service.master.resolver.*;
import cn.boundivore.dl.service.master.service.MasterManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DataLightApplicationRunner implements ApplicationRunner {

    private final MasterManageService masterManageService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 下方存在变量前后依赖关系，需留意执行顺序，不可随意调整
        // ./conf/datalight
        log.info("CONF_ENV_DIR: {}" , DataLightEnv.CONF_ENV_DIR);
        ResolverYamlDirectory.resolver(DataLightEnv.CONF_ENV_DIR);

        // ./conf/service
        log.info("CONF_SERVICE_DIR: {}" , DataLightEnv.CONF_SERVICE_DIR);
        ResolverYamlServiceManifest.resolver(DataLightEnv.CONF_SERVICE_DIR);

        // ./conf/service
        log.info("CONF_SERVICE_DIR: {}" , DataLightEnv.CONF_SERVICE_DIR);
        ResolverYamlServiceDetail.resolver(DataLightEnv.CONF_SERVICE_DIR);

        // ./plugins
        log.info("PLUGINS_DIR_LOCAL: {}" , DataLightEnv.PLUGINS_DIR_LOCAL);
        ResolverYamlServicePlaceholder.resolver(DataLightEnv.PLUGINS_DIR_LOCAL);

        // ./node/conf
        log.info("NODE_CONF_DIR: {}" , DataLightEnv.NODE_CONF_DIR);
        ResolverYamlNode.resolver(DataLightEnv.NODE_CONF_DIR);

        this.masterManageService.updateMasterMeta();
    }

}
