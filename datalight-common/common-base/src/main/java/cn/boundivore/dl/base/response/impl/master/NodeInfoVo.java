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

import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Description: 节点信息响应体
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
@ApiModel(
        value = "NodeInfoVo",
        description = "NodeInfoVo: 节点信息 响应体"
)
public class NodeInfoVo implements IVo {

    @ApiModelProperty(name = "NodeId", value = "节点 ID", required = true)
    @JsonProperty(value = "NodeId", required = true)
    @NotNull
    private Long nodeId;

    @ApiModelProperty(name = "Hostname", value = "主机名", required = true)
    @JsonProperty(value = "Hostname", required = true)
    @NotNull
    private String hostname;
}
