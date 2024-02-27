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

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 节点信息请求体集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class AbstractNodeRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeRequest.NodeIdListRequest",
            description = "AbstractNodeRequest.NodeIdListRequest: 节点 ID 列表请求体"
    )
    public final static class NodeIdListRequest implements IRequest{

        private static final long serialVersionUID = -6076022897180781653L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "NodeIdList", title = "节点 ID 列表", required = true)
        @JsonProperty(value = "NodeIdList", required = true)
        @NotEmpty
        private List<NodeIdRequest> nodeIdList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeRequest.NodeIdRequest",
            description = "AbstractNodeRequest.NodeIdRequest: 节点 ID 请求体"
    )
    public final static class NodeIdRequest implements IRequest {

        private static final long serialVersionUID = -2614333915667046320L;

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;
    }

    /**
     * Description: 节点信息请求体
     * Created by: Boundivore
     * E-mail: boundivore@formail.com
     * Creation time: 2023/7/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Version: V1.0
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeRequest.NodeInfoRequest",
            description = "AbstractNodeRequest.NodeInfoRequest: 节点信息 请求体"
    )
    public final static class NodeInfoRequest implements IRequest {

        private static final long serialVersionUID = 1728819516337583002L;

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;
    }
}
