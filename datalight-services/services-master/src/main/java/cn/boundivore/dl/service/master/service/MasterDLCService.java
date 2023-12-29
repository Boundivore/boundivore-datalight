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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.response.impl.master.AbstractDLCVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.converter.IDLCConverter;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceManifest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Description: DLC 操作相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterDLCService {

    private final IDLCConverter iDlcConverter;

    /**
     * Description: 返回 DLC 包中服务的相关信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AbstractDLCVo.DLCSummaryVo> 包中服务的相关信息
     */
    public Result<AbstractDLCVo.DLCServiceVo> dlcServiceList() {
        return Result.success(
                new AbstractDLCVo.DLCServiceVo(
                        ResolverYamlServiceManifest.SERVICE_MANIFEST_YAML
                                .getDataLight()
                                .getDlcVersion(),
                        ResolverYamlServiceDetail.SERVICE_MAP
                                .values()
                                .stream()
                                .map(this.iDlcConverter::convert2ServiceSummaryVo)
                                .collect(Collectors.toList())
                )
        );
    }

    /**
     * Description: 返回服务组件列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AbstractDLCVo.DLCComponentVo> 服务组件列表信息
     */
    public Result<AbstractDLCVo.DLCComponentVo> dlcComponentList() {

        return Result.success(
                new AbstractDLCVo.DLCComponentVo(
                        ResolverYamlServiceManifest.SERVICE_MANIFEST_YAML
                                .getDataLight()
                                .getDlcVersion(),
                        ResolverYamlServiceDetail.SERVICE_MAP
                                .values()
                                .stream()
                                .map(i -> new AbstractDLCVo.ServiceComponentSummaryVo(
                                                this.iDlcConverter.convert2ServiceSummaryVo(i),
                                                ResolverYamlServiceDetail.COMPONENT_LIST_MAP
                                                        .get(i.getName())
                                                        .stream()
                                                        .map(this.iDlcConverter::convert2ComponentSummaryVo)
                                                        .collect(Collectors.toList())
                                        )
                                )
                                .collect(Collectors.toList())
                )
        );
    }
}
