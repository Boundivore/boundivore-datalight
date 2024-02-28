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

import cn.boundivore.dl.api.master.define.IMasterWebStateAPI;
import cn.boundivore.dl.base.request.impl.master.AbstractWebStateRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractWebStateVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterWebStateService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterWebStateController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/2/28
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@SaCheckLogin
public class MasterWebStateController implements IMasterWebStateAPI {

    private final MasterWebStateService masterWebStateService;


    @Override
    public Result<String> saveWebState(AbstractWebStateRequest.SaveStateRequest request) throws Exception {
        return this.masterWebStateService.saveWebState(request);
    }

    @Override
    public Result<AbstractWebStateVo.WebStateMapVo> getWebStateMap(Long clusterId,
                                                                Long userId,
                                                                String webKey) throws Exception {
        return this.masterWebStateService.getWebStateMap(clusterId, userId, webKey);
    }
}
