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

import cn.boundivore.dl.api.master.define.IMasterNodeAPI;
import cn.boundivore.dl.base.request.impl.master.AbstractNodeRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterNodeService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterNodeController
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
public class MasterNodeController implements IMasterNodeAPI {

    private final MasterNodeService masterNodeService;


    @Override
    public Result<AbstractNodeJobVo.NodeJobIdVo> operateNode(NodeJobRequest request) throws Exception {
        return this.masterNodeService.operateNode(request);
    }

    @Override
    public Result<AbstractNodeVo.NodeVo> getNodeList(Long clusterId) throws Exception {
        return this.masterNodeService.getNodeList(clusterId);
    }

    @Override
    public Result<String> removeBatchByIds(AbstractNodeRequest.NodeIdListRequest request) throws Exception {
        return this.masterNodeService.removeBatchByIds(request);
    }

    @Override
    public Result<AbstractNodeVo.NodeWithComponentListVo> getNodeListWithComponent(Long clusterId) throws Exception {
        return this.masterNodeService.getNodeListWithComponent(clusterId);
    }
}
