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

import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.boundivore.dl.service.master.resolver.*;
import cn.boundivore.dl.service.master.service.*;
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
public class DataLightMasterApplicationRunner implements ApplicationRunner {

    private final MasterManageService masterManageService;

    private final MasterUserService masterUserService;

    private final MasterNodeJobService masterNodeJobService;

    private final MasterJobService masterJobService;

    private final MasterServiceService masterServiceService;

    private final MasterComponentService masterComponentService;

    private final MasterLoadExcelPermissionService masterLoadExcelPermissionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 下方存在变量前后依赖关系，需留意执行顺序，不可随意调整
        // ./conf/datalight
        log.info("CONF_ENV_DIR: {}", DataLightEnv.CONF_ENV_DIR);
        ResolverYamlDirectory.resolver(DataLightEnv.CONF_ENV_DIR);

        // MANIFEST: ./conf/service
        log.info("CONF_SERVICE_DIR: {}", DataLightEnv.CONF_SERVICE_DIR);
        ResolverYamlServiceManifest.resolver(DataLightEnv.CONF_SERVICE_DIR);

        // DETAIL: ./conf/service
        log.info("CONF_SERVICE_DIR: {}", DataLightEnv.CONF_SERVICE_DIR);
        ResolverYamlServiceDetail.resolver(DataLightEnv.CONF_SERVICE_DIR);

        // ./conf/web
        log.info("CONF_WEB_DIR: {}", DataLightEnv.CONF_WEB_DIR);
        ResolverYamlComponentWebUI.resolver(DataLightEnv.CONF_WEB_DIR);

        // ./plugins
        log.info("PLUGINS_DIR_LOCAL: {}", DataLightEnv.PLUGINS_DIR_LOCAL);
        ResolverYamlServicePlaceholder.resolver(DataLightEnv.PLUGINS_DIR_LOCAL);

        // ./node/conf
        log.info("NODE_CONF_DIR: {}", DataLightEnv.NODE_CONF_DIR);
        ResolverYamlNode.resolver(DataLightEnv.NODE_CONF_DIR);

        // ./conf/permission
        log.info("CONF_PERMISSION_DIR: {}", DataLightEnv.CONF_PERMISSION_DIR);
        this.masterLoadExcelPermissionService.initPermissionTable(DataLightEnv.CONF_PERMISSION_DIR);


        // 更新 Master 所在节点的元数据信息
        // TODO 如果后续开启 Master 高可用功能，则此处需要根据持有活跃锁的状态来执行下面的函数
//        this.masterManageService.updateMasterMeta();

        // 检查超级用户是否注入到数据库
        this.masterUserService.checkInitSuperUser();

        // 检查是否存在 Master 异常终止导致数据库状态异常，若存在，则使其恢复到正常状态
        // TODO 如果后续开启 Master 高可用功能，此处功能需要移动到 “切换为活跃锁时，检查状态异常”
        this.masterNodeJobService.checkNodeJobState();
        this.masterJobService.checkJobState();
        this.masterServiceService.checkServiceState();
        this.masterComponentService.checkComponentState();

    }

}
