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

import cn.boundivore.dl.base.response.impl.master.ServiceDependenciesVo;
import cn.boundivore.dl.orm.po.single.TDlConfigPre;
import cn.boundivore.dl.orm.service.single.impl.TDlConfigPreServiceImpl;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 服务预配置通用逻辑服务
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/27
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class MasterConfigPreCommonService {

    private final TDlConfigPreServiceImpl tDlConfigPreService;

    /**
     * Description: 根据服务名称获取当前服务的配置文件目录和模板目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceName 当前服务名
     * @return 服务配置目录与模板目录信息
     */
    public List<ServiceDependenciesVo.ConfDirVo> getConfDirList(String serviceName) {
        YamlServiceDetail.Service service = ResolverYamlServiceDetail.SERVICE_MAP.get(serviceName);
        List<YamlServiceDetail.ConfDir> confDirs = service.getConfDirs();

        return confDirs.stream()
                .map(i -> new ServiceDependenciesVo.ConfDirVo(
                                i.getServiceConfDir(),
                                i.getTemplatedDir()
                        )
                )
                .collect(Collectors.toList());

    }

    /**
     * Description: 获取已经保存的预配置信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   当前集群 ID
     * @param serviceName 当前服务名
     * @return 返回已经保存的预配置信息
     */
    public List<ServiceDependenciesVo.PropertyVo> getPropertyList(Long clusterId, String serviceName) {
        List<TDlConfigPre> tDlConfigPreList = this.tDlConfigPreService.lambdaQuery()
                .select()
                .eq(TDlConfigPre::getClusterId, clusterId)
                .eq(TDlConfigPre::getServiceName, serviceName)
                .list();

        return tDlConfigPreList.stream()
                .map(i -> new ServiceDependenciesVo.PropertyVo(
                                i.getTemplatedConfigPath(),
                                i.getPlaceholder(),
                                i.getValue()
                        )
                )
                .collect(Collectors.toList());
    }
}
