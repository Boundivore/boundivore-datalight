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

import cn.boundivore.dl.base.response.impl.master.AbstractDLCVo;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Description: IDLCConverter
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@Mapper(componentModel = SPRING)
@Configuration
public interface IDLCConverter {

    IDLCConverter INSTANCE = Mappers.getMapper(IDLCConverter.class);

    /**
     * 不包含服务组件在集群中的部署状态
     */
    @Mappings(
            {
                    @Mapping(source = "name", target = "serviceName"),
                    @Mapping(source = "type", target = "serviceTypeEnum"),
                    @Mapping(source = "dependencies", target = "dependencyList"),
                    @Mapping(source = "relatives", target = "relativeList")
            }
    )
    AbstractDLCVo.ServiceSummaryVo convert2ServiceSummaryVo(YamlServiceDetail.Service service);

    @Mappings(
            {
                    @Mapping(source = "name", target = "componentName"),
                    @Mapping(source = "mutexes", target = "mutexesList")
            }
    )
    AbstractDLCVo.ComponentSummaryVo convert2ComponentSummaryVo(YamlServiceDetail.Component component);

}
