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
import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.orm.po.single.TDlCluster;
import cn.boundivore.dl.orm.po.single.TDlPermission;
import cn.boundivore.dl.orm.po.single.TDlRuleInterface;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Description: IPermissionRuleConverter
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
public interface IPermissionRuleConverter {

    IPermissionRuleConverter INSTANCE = Mappers.getMapper(IPermissionRuleConverter.class);


    @Mappings({
            @Mapping(source = "id", target = "permissionId")
    })
    AbstractRolePermissionRuleVo.PermissionVo convert2PermissionVo(TDlPermission tDlPermission);

    @Mappings({
            @Mapping(source = "id", target = "ruleId")
    })
    AbstractRolePermissionRuleVo.RuleInterfaceVo convert2RuleInterfaceVo(TDlRuleInterface tDlRuleInterface);

}