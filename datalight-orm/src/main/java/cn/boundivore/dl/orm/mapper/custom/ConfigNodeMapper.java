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

import cn.boundivore.dl.orm.po.custom.ConfigNodeDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description: 以 Config 为查询目的的自定义 Mapper
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Mapper
public interface ConfigNodeMapper extends BaseMapper<ConfigNodeDto> {


    /**
     * Description: 返回配置、节点的关联信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 指定服务
     * @return ComponentNodeDto JOIN 后的详情
     */
    @Select("SELECT\n" +
            "  t3.hostname,\n" +
            "  t3.ipv4,\n" +
            "  t3.ipv6,\n" +
            "  t3.ssh_port,\n" +
            "  t3.cpu_arch,\n" +
            "  t3.cpu_cores,\n" +
            "  t3.ram,\n" +
            "  t3.disk,\n" +
            "  t3.node_state,\n" +
            "  t3.os_version,\n" +
            "  t3.config_id,\n" +
            "  t3.cluster_id,\n" +
            "  t3.node_id,\n" +
            "  t3.service_name,\n" +
            "  t3.component_name,\n" +
            "  t3.filename,\n" +
            "  t3.config_content_id,\n" +
            "  t3.config_path,\n" +
            "  t3.config_version,\n" +
            "  t4.sha256,\n" +
            "  t4.config_data \n" +
            "FROM\n" +
            "  (\n" +
            "  SELECT\n" +
            "    t1.hostname,\n" +
            "    t1.ipv4,\n" +
            "    t1.ipv6,\n" +
            "    t1.ssh_port,\n" +
            "    t1.cpu_arch,\n" +
            "    t1.cpu_cores,\n" +
            "    t1.ram,\n" +
            "    t1.disk,\n" +
            "    t1.node_state,\n" +
            "    t1.os_version,\n" +
            "    t2.id AS config_id,\n" +
            "    t2.cluster_id AS cluster_id,\n" +
            "    t2.node_id AS node_id,\n" +
            "    t2.service_name,\n" +
            "    t2.component_name,\n" +
            "    t2.filename,\n" +
            "    t2.config_content_id,\n" +
            "    t2.config_path,\n" +
            "    t2.config_version \n" +
            "  FROM\n" +
            "    t_dl_node t1\n" +
            "    JOIN t_dl_config t2 ON t1.id = t2.node_id \n" +
            "  WHERE\n" +
            "    t2.cluster_id = #{clusterId} \n" +
            "    AND t2.service_name = #{serviceName} \n" +
            "  ) t3\n" +
            "  JOIN t_dl_config_content t4 ON t3.config_id = t4.id;")
    List<ConfigNodeDto> selectConfigNodeDto(
            @Param("clusterId")
            Long clusterId,

            @Param("serviceName")
            String serviceName
    );

    /**
     * Description: 返回配置、节点的关联信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 指定服务
     * @param configPath  配置文件路径
     * @return ComponentNodeDto JOIN 后的详情
     */
    @Select("SELECT\n" +
            "  t3.hostname,\n" +
            "  t3.ipv4,\n" +
            "  t3.ipv6,\n" +
            "  t3.ssh_port,\n" +
            "  t3.cpu_arch,\n" +
            "  t3.cpu_cores,\n" +
            "  t3.ram,\n" +
            "  t3.disk,\n" +
            "  t3.node_state,\n" +
            "  t3.os_version,\n" +
            "  t3.config_id,\n" +
            "  t3.cluster_id,\n" +
            "  t3.node_id,\n" +
            "  t3.service_name,\n" +
            "  t3.component_name,\n" +
            "  t3.filename,\n" +
            "  t3.config_content_id,\n" +
            "  t3.config_path,\n" +
            "  t3.config_version,\n" +
            "  t4.sha256,\n" +
            "  t4.config_data \n" +
            "FROM\n" +
            "  (\n" +
            "  SELECT\n" +
            "    t1.hostname,\n" +
            "    t1.ipv4,\n" +
            "    t1.ipv6,\n" +
            "    t1.ssh_port,\n" +
            "    t1.cpu_arch,\n" +
            "    t1.cpu_cores,\n" +
            "    t1.ram,\n" +
            "    t1.disk,\n" +
            "    t1.node_state,\n" +
            "    t1.os_version,\n" +
            "    t2.id AS config_id,\n" +
            "    t2.cluster_id AS cluster_id,\n" +
            "    t2.node_id AS node_id,\n" +
            "    t2.service_name,\n" +
            "    t2.component_name,\n" +
            "    t2.filename,\n" +
            "    t2.config_content_id,\n" +
            "    t2.config_path,\n" +
            "    t2.config_version \n" +
            "  FROM\n" +
            "    t_dl_node t1\n" +
            "    JOIN t_dl_config t2 ON t1.id = t2.node_id \n" +
            "  WHERE\n" +
            "    t2.cluster_id = #{clusterId} \n" +
            "    AND t2.service_name = #{serviceName} \n" +
            "    AND t2.config_path = #{configPath}\n" +
            "  ) t3\n" +
            "  JOIN t_dl_config_content t4 ON t3.config_id = t4.id;")
    List<ConfigNodeDto> selectConfigNodeDtoByConfigPath(
            @Param("clusterId")
            Long clusterId,

            @Param("serviceName")
            String serviceName,

            @Param("configPath")
            String configPath
    );
}
