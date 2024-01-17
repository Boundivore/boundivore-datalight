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
package cn.boundivore.dl.service.master.converter;

import cn.boundivore.dl.base.request.impl.master.AbstractClusterRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
import cn.boundivore.dl.orm.po.single.TDlCluster;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Description: IClusterConverter
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/27
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@Mapper(componentModel = SPRING)
@Configuration
public interface IClusterConverter {

    IClusterConverter INSTANCE = Mappers.getMapper(IClusterConverter.class);

    @Mappings({
            @Mapping(source = "clusterTypeEnum", target = "clusterType")
    })
    TDlCluster convert2TDlCluster(AbstractClusterRequest.NewClusterRequest request);

    @Mappings({
            @Mapping(source = "id", target = "clusterId"),
            @Mapping(source = "clusterType", target = "clusterTypeEnum"),
            @Mapping(source = "clusterState", target = "clusterStateEnum")
    })
    AbstractClusterVo.ClusterVo convert2ClusterVo(TDlCluster tDlCluster);

}