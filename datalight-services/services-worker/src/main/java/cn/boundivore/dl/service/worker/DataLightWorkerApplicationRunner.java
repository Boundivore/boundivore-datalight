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
package cn.boundivore.dl.service.worker;

import cn.boundivore.dl.base.request.impl.worker.MasterMetaRequest;
import cn.boundivore.dl.service.worker.service.WorkerManageService;
import cn.hutool.core.util.StrUtil;
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
 * Creation time: 2024/1/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataLightWorkerApplicationRunner implements ApplicationRunner {

    private final WorkerManageService workerManageService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (StrUtil.isNotBlank(DataLightWorkerApplication.MASTER_IP_FROM_SHELL)) {
            this.workerManageService.updateMasterMeta(
                    new MasterMetaRequest(
                            DataLightWorkerApplication.MASTER_IP_FROM_SHELL
                    )
            );
        }
    }

}
