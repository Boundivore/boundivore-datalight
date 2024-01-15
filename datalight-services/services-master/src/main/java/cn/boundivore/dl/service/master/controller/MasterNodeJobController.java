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

import cn.boundivore.dl.api.master.define.IMasterNodeJobAPI;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterNodeJobService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterNodeJobController
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
@SaCheckLogin
public class MasterNodeJobController implements IMasterNodeJobAPI {

    private final MasterNodeJobService masterNodeJobService;

    @Override
    public Result<AbstractNodeJobVo.NodeJobProgressVo> getNodeJobProgress(Long nodeJobId) throws Exception {
        return this.masterNodeJobService.getNodeJobProgress(nodeJobId);
    }

    @Override
    public Result<AbstractNodeJobVo.AllNodeJobTransferProgressVo> getNodeJobDispatchProgress(Long nodeJobId) throws Exception {
        return this.masterNodeJobService.getNodeJobDispatchProgress(nodeJobId);
    }

    @Override
    public Result<AbstractNodeJobVo.NodeJobTransferProgressDetailVo> getNodeJobDispatchProgressDetail(Long nodeJobId,
                                                                                                      Long nodeTaskId,
                                                                                                      Long nodeStepId) throws Exception {
        return this.masterNodeJobService.getNodeJobDispatchProgressDetail(nodeJobId, nodeTaskId, nodeStepId);
    }

    @Override
    public Result<AbstractNodeJobVo.NodeJobLogListVo> getNodeJobLogList(Long clusterId,
                                                                        Long nodeJobId,
                                                                        Long nodeTaskId,
                                                                        Long nodeStepId) throws Exception {
        return this.masterNodeJobService.getNodeJobLogList(
                clusterId,
                nodeJobId,
                nodeTaskId,
                nodeStepId
        );
    }
}
