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

import cn.boundivore.dl.orm.po.single.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Description: IRolePermissionRuleConverter
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Mapper(componentModel = SPRING)
@Component
public interface IRolePermissionRuleConverter {
    /**
     * 模板权限转权限
     */
//    @Mappings({
//            @Mapping(source = "", target = "")
//    })
    TDlPermission convert2TDlPermission(TDlPermissionTemplated tDlPermissionTemplated);

    TDlRuleInterface convert2TDlRuleInterface(TDlRuleInterfaceTemplated tDlRuleInterfaceTemplated);

    TDlPermissionRuleRelation convert2TDlPermissionRuleRelation(TDlPermissionRuleRelationTemplated tDlPermissionRuleRelationTemplated);
}
