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
package cn.boundivore.dl.base.response.impl.master;


import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;


public abstract class AbstractNodeVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "AbstractNodeVo.NodeVo",
            description = "AbstractNodeVo.NodeVo: NodeVo 节点信息"
    )
    public static class NodeVo implements IVo {

        @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @ApiModelProperty(name = "NodeDetailList", value = "节点信息详情列表", required = true)
        @JsonProperty(value = "NodeDetailList", required = true)
        @NotNull
        private List<NodeDetailVo> nodeDetailList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "AbstractNodeVo.NodeDetailVo",
            description = "AbstractNodeVo.NodeDetailVo: NodeDetailVo 节点信息详情"
    )
    public static class NodeDetailVo implements IVo {

        @ApiModelProperty(name = "NodeId", value = "节点主键 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @ApiModelProperty(name = "Hostname", value = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;

        @ApiModelProperty(name = "NodeIp", value = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        @NotNull
        private String nodeIp;

        @ApiModelProperty(name = "SshPort", value = "SSH 端口号", required = true)
        @JsonProperty(value = "SshPort", required = true)
        @NotNull
        private Long sshPort;

        @ApiModelProperty(name = "CpuArch", value = "CPU 架构", required = true)
        @JsonProperty(value = "CpuArch", required = true)
        @NotNull
        private String cpuArch;

        @ApiModelProperty(name = "CpuCores", value = "CPU 核数", required = true)
        @JsonProperty(value = "CpuCores", required = true)
        @NotNull
        private Long cpuCores;

        @ApiModelProperty(name = "Ram", value = "内存总大小（MB）", required = true)
        @JsonProperty(value = "Ram", required = true)
        @NotNull
        private Long ram;

        @ApiModelProperty(name = "DiskTotal", value = "磁盘总大小（MB）", required = true)
        @JsonProperty(value = "DiskTotal", required = true)
        @NotNull
        private Long diskTotal;

        @ApiModelProperty(name = "NodeState", value = "节点状态", required = true)
        @JsonProperty(value = "NodeState", required = true)
        @NotNull
        private NodeStateEnum nodeState;


    }
}
