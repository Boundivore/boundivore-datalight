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
package cn.boundivore.dl.service.worker.converter;

import cn.boundivore.dl.base.request.impl.worker.ServiceMetaRequest;
import cn.boundivore.dl.service.worker.cache.MetaCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Description: IServiceMetaConverter
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@Mapper(componentModel = SPRING)
@Component
public interface IServiceMetaConverter {

    IServiceMetaConverter INSTANCE = Mappers.getMapper(IServiceMetaConverter.class);

    MetaCache.ServiceMeta convert2ServiceMeta(ServiceMetaRequest request);

    List<MetaCache.Service> convert2Service(List<ServiceMetaRequest.MetaServiceRequest> requestList);

    List<MetaCache.Component> convert2Component(List<ServiceMetaRequest.MetaComponentRequest> request);

}
