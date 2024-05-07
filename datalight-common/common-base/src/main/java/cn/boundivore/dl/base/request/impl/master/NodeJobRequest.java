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

import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
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
 * Description: NodeJobRequest
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
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
        name = "NodeJobRequest",
        description = "NodeJobRequest: NodeJob 请求体"
)
public class NodeJobRequest implements IRequest {

    private static final long serialVersionUID = 2100478736900830314L;

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull(message = "集群 ID不能为空")
    private Long clusterId;

    @Schema(name = "SshPort", title = "SSH 端口号", required = true)
    @JsonProperty(value = "SshPort", required = true)
    @NotNull(message = "SSH 端口号不能为空")
    private Long sshPort;

    @Schema(name = "NodeActionTypeEnum", title = "节点执行操作的类型", required = true)
    @JsonProperty(value = "NodeActionTypeEnum", required = true)
    @NotNull(message = "节点执行操作的类型不能为空")
    private NodeActionTypeEnum nodeActionTypeEnum;

    @Schema(name = "NodeInfoList", title = "选择的节点列表", required = true)
    @JsonProperty(value = "NodeInfoList", required = true)
    @NotEmpty(message = "选择的节点列表不能为空")
    private List<AbstractNodeRequest.NodeInfoRequest> nodeInfoList;

}
