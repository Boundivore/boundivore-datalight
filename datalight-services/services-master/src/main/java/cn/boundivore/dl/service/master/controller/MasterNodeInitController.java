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

import cn.boundivore.dl.api.master.define.IMasterNodeInitAPI;
import cn.boundivore.dl.base.request.impl.master.AbstractNodeInitRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.request.impl.master.ParseHostnameRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeInitVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.response.impl.master.ParseHostnameVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterNodeInitService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterNodeInitController
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
public class MasterNodeInitController implements IMasterNodeInitAPI {

    private final MasterNodeInitService masterNodeInitService;

    @Override
    public Result<ParseHostnameVo> parseHostname(ParseHostnameRequest request) throws Exception {
        return masterNodeInitService.parseHostname(request);
    }

    @Override
    public Result<AbstractNodeJobVo.NodeJobIdVo> detectNode(NodeJobRequest request) throws Exception {
        return masterNodeInitService.detectNode(request);
    }

    @Override
    public Result<AbstractNodeJobVo.NodeJobIdVo> checkNode(NodeJobRequest request) throws Exception {
        return masterNodeInitService.checkNode(request);
    }

    @Override
    public Result<AbstractNodeJobVo.NodeJobIdVo> dispatchNode(NodeJobRequest request) throws Exception {
        return masterNodeInitService.dispatchNode(request);
    }

    @Override
    public Result<AbstractNodeInitVo.NodeInitVo> initParseList(Long clusterId) throws Exception {
        return masterNodeInitService.initParseList(clusterId);
    }

    @Override
    public Result<AbstractNodeInitVo.NodeInitVo> initDetectList(AbstractNodeInitRequest.NodeInitInfoListRequest request) throws Exception {
        return masterNodeInitService.initDetectList(request);
    }

    @Override
    public Result<AbstractNodeInitVo.NodeInitVo> initCheckList(AbstractNodeInitRequest.NodeInitInfoListRequest request) throws Exception {
        return masterNodeInitService.initCheckList(request);
    }

    @Override
    public Result<AbstractNodeInitVo.NodeInitVo> initDispatchList(AbstractNodeInitRequest.NodeInitInfoListRequest request) throws Exception {
        return masterNodeInitService.initDispatchList(request);
    }

    @Override
    public Result<String> addNode(AbstractNodeInitRequest.NodeInitInfoListRequest request) throws Exception {
        return masterNodeInitService.addNode(request);
    }

}
