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
package cn.boundivore.dl.orm.mapper.custom;

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.orm.po.custom.ComponentNodeDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description: 以 Component 为查询目的的自定义 Mapper
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Mapper
public interface ComponentNodeMapper extends BaseMapper<ComponentNodeDto> {


    /**
     * Description: 返回服务、组件、节点的关联信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId          集群 ID
     * @param serviceName        指定服务
     * @param componentStateList IN 组件状态列表
     * @return ComponentNodeDto JOIN 后的详情
     */
    @Select("<script>\n" +
            "SELECT \n" +
            "    t1.id AS component_id, \n" +
            "    t1.version,\n" +
            "    t1.cluster_id, \n" +
            "    t1.node_id, \n" +
            "    t1.component_name, \n" +
            "    t1.component_state,\n" +
            "    t1.priority AS component_priority,\n" +
            "    t2.hostname,\n" +
            "    t2.ipv4, \n" +
            "    t2.ipv6, \n" +
            "    t2.ssh_port, \n" +
            "    t2.cpu_arch, \n" +
            "    t2.cpu_cores, \n" +
            "    t2.ram, \n" +
            "    t2.disk,\n" +
            "    t2.node_state, \n" +
            "    t2.os_version\n" +
            "FROM \n" +
            "    t_dl_component t1\n" +
            "JOIN \n" +
            "    t_dl_node t2 ON t1.node_id = t2.id\n" +
            "WHERE \n" +
            "    t1.cluster_id = #{clusterId}\n" +
            "    AND t1.service_name = #{serviceName}\n" +
            "    AND t1.component_state IN \n" +
            "       <foreach item='componentStateItem' collection='componentStateList' open='(' separator=',' close=')'>\n" +
            "           #{componentStateItem}\n" +
            "       </foreach>\n" +
            "</script>")
    List<ComponentNodeDto> selectComponentNodeInStatesDto(
            @Param("clusterId")
            Long clusterId,
            @Param("serviceName")
            String serviceName,
            @Param("componentStateList")
            List<SCStateEnum> componentStateList
    );

    /**
     * Description: 返回服务、组件、节点的关联信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId          集群 ID
     * @param serviceName        指定服务
     * @param componentStateList NOT IN 组件状态列表
     * @return ComponentNodeDto JOIN 后的详情
     */
    @Select("<script>\n" +
            "SELECT \n" +
            "    t1.id AS component_id, \n" +
            "    t1.version,\n" +
            "    t1.cluster_id, \n" +
            "    t1.node_id, \n" +
            "    t1.component_name, \n" +
            "    t1.component_state,\n" +
            "    t1.priority AS component_priority,\n" +
            "    t2.hostname,\n" +
            "    t2.ipv4, \n" +
            "    t2.ipv6, \n" +
            "    t2.ssh_port, \n" +
            "    t2.cpu_arch, \n" +
            "    t2.cpu_cores, \n" +
            "    t2.ram, \n" +
            "    t2.disk,\n" +
            "    t2.node_state, \n" +
            "    t2.os_version\n" +
            "FROM \n" +
            "    t_dl_component t1\n" +
            "JOIN \n" +
            "    t_dl_node t2 ON t1.node_id = t2.id\n" +
            "WHERE \n" +
            "    t1.cluster_id = #{clusterId}\n" +
            "    AND t1.service_name = #{serviceName}\n" +
            "    AND t1.component_state NOT IN \n" +
            "       <foreach item='componentStateItem' collection='componentStateList' open='(' separator=',' close=')'>\n" +
            "           #{componentStateItem}\n" +
            "       </foreach>\n" +
            "</script>")
    List<ComponentNodeDto> selectComponentNodeNotInStatesDto(
            @Param("clusterId")
            Long clusterId,
            @Param("serviceName")
            String serviceName,
            @Param("componentStateList")
            List<SCStateEnum> componentStateList
    );

}
