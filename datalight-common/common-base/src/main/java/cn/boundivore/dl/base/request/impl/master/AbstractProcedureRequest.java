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
package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 步骤信息相关请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractProcedureRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "PersistProcedureRequest",
            description = "PersistProcedureRequest: 持久化初始化进度状态请求体"
    )
    public static class PersistProcedureRequest implements IRequest {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "ProcedureStateEnum", title = "当前已成功完成的状态", required = true)
        @JsonProperty(value = "ProcedureStateEnum", required = true)
        @NotNull
        private ProcedureStateEnum procedureStateEnum;

        @Schema(name = "NodeJobId", title = "NodeJob Id", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        private Long nodeJobId;

        @Schema(name = "NodeInfoList", title = "节点信息列表", required = true)
        @JsonProperty(value = "NodeInfoList", required = true)
        @NotNull
        private List<NodeInfoListRequest> nodeInfoList;


    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "NodeInfoListRequest",
            description = "NodeInfoListRequest: 操作的节点信息列表请求体"
    )
    public static class NodeInfoListRequest implements IRequest {
        @Schema(name = "NodeId", title = "节点 Id", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;
    }
}
