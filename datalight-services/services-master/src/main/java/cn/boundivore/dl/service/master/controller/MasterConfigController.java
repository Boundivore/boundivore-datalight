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
package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.api.master.define.IMasterConfigAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractReverseSyncRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveByGroupRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveRequest;
import cn.boundivore.dl.base.response.impl.common.ConfigHistoryVersionVo;
import cn.boundivore.dl.base.response.impl.master.ConfigListByGroupVo;
import cn.boundivore.dl.base.response.impl.master.ConfigSummaryListVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterConfigReverseSyncService;
import cn.boundivore.dl.service.master.service.MasterConfigService;
import cn.boundivore.dl.service.master.service.MasterConfigSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterConfigController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterConfigController implements IMasterConfigAPI {

    private final MasterConfigService masterConfigService;

    private final MasterConfigSyncService masterConfigSyncService;

    private final MasterConfigReverseSyncService masterConfigReverseSyncService;



    @Override
    public Result<String> saveConfig(ConfigSaveRequest request) throws Exception {
        return this.masterConfigSyncService.saveConfigOrUpdateBatch(request) ?
                Result.success() :
                Result.fail();
    }

    @Override
    public Result<String> saveConfigByGroup(ConfigSaveByGroupRequest request) throws Exception {
        return this.masterConfigSyncService.saveConfigByGroup(request);
    }

    @Override
    public Result<ConfigSummaryListVo> getConfigSummaryList(Long clusterId, String serviceName) throws Exception {
        return this.masterConfigService.getConfigSummaryList(
                clusterId,
                serviceName
        );
    }

    @Override
    public Result<ConfigListByGroupVo> getConfigListByGroup(Long clusterId,
                                                            String serviceName,
                                                            String filename,
                                                            String configPath) throws Exception {
        return this.masterConfigService.getConfigListByGroup(
                clusterId,
                serviceName,
                configPath
        );
    }

    @Override
    public Result<ConfigHistoryVersionVo> getConfigVersionInfo(Long clusterId,
                                                               Long nodeId,
                                                               String serviceName,
                                                               String filename,
                                                               String configPath) throws Exception {
        return this.masterConfigService.getConfigVersionInfo(
                clusterId,
                nodeId,
                serviceName,
                filename,
                configPath
        );
    }

    @Override
    public Result<ConfigHistoryVersionVo.ConfigVersionDetailVo> getConfigVersionDetail(Long clusterId,
                                                                 Long nodeId,
                                                                 String serviceName,
                                                                 String filename,
                                                                 String configPath,
                                                                 Long historyConfigVersion) throws Exception {
        return this.masterConfigService.getConfigVersionDetail(
                clusterId,
                nodeId,
                serviceName,
                filename,
                configPath,
                historyConfigVersion
        );
    }

    @Override
    public Result<String> reverseConfig(AbstractReverseSyncRequest.ReverseSyncRequest request) throws Exception {
        return this.masterConfigReverseSyncService.reverseSyncConfig(request);
    }

}
