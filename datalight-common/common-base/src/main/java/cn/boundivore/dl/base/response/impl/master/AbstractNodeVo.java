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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 节点相关响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractNodeVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeVo.NodeVo",
            description = "AbstractNodeVo.NodeVo 节点信息"
    )
    public static class NodeVo implements IVo {

        private static final long serialVersionUID = 6644089617550259009L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "NodeDetailList", title = "节点信息详情列表", required = true)
        @JsonProperty(value = "NodeDetailList", required = true)
        private List<NodeDetailVo> nodeDetailList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeVo.NodeDetailVo",
            description = "AbstractNodeVo.NodeDetailVo NodeDetailVo 节点信息详情"
    )
    public static class NodeDetailVo implements IVo {

        private static final long serialVersionUID = -6327460533692159906L;

        @Schema(name = "NodeId", title = "节点主键 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @Schema(name = "NodeIp", title = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;

        @Schema(name = "SshPort", title = "SSH 端口号", required = true)
        @JsonProperty(value = "SshPort", required = true)
        private Long sshPort;

        @Schema(name = "CpuArch", title = "CPU 架构", required = true)
        @JsonProperty(value = "CpuArch", required = true)
        private String cpuArch;

        @Schema(name = "CpuCores", title = "CPU 核数", required = true)
        @JsonProperty(value = "CpuCores", required = true)
        private Long cpuCores;

        @Schema(name = "Ram", title = "内存总大小（MB）", required = true)
        @JsonProperty(value = "Ram", required = true)
        private Long ram;

        @Schema(name = "DiskTotal", title = "磁盘总大小（MB）", required = true)
        @JsonProperty(value = "DiskTotal", required = true)
        private Long diskTotal;

        @Schema(name = "NodeState", title = "节点状态", required = true)
        @JsonProperty(value = "NodeState", required = true)
        private NodeStateEnum nodeState;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeVo.NodeWithComponentListVo",
            description = "AbstractNodeVo.NodeWithComponentListVo 节点信息详情与组件分布信息列表"
    )
    public static class NodeWithComponentListVo implements IVo {

        private static final long serialVersionUID = 3235981754623810962L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "NodeWithComponentList", title = "节点信息详情与组件分布信息列表", required = true)
        @JsonProperty(value = "NodeWithComponentList", required = true)
        private List<NodeWithComponentVo> nodeWithComponentList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeVo.NodeWithComponentVo",
            description = "AbstractNodeVo.NodeWithComponentVo 节点信息详情与组件分布信息"
    )
    public static class NodeWithComponentVo implements IVo {

        private static final long serialVersionUID = 3480044359965267635L;

        @Schema(name = "NodeDetail", title = "节点信息详情", required = true)
        @JsonProperty(value = "NodeDetail", required = true)
        private NodeDetailVo nodeDetail;

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        private List<String> componentName;

    }
}
