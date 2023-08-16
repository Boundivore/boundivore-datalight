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
import java.util.List;

/**
 * Description: AddNodeRequest
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
@ApiModel(
        value = "ParseHostnameVo",
        description = "ParseHostnameVo: 解析节点主机名 响应体"
)
public class ParseHostnameVo implements IVo {

    @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @ApiModelProperty(name = "SshPort", value = "SSH 端口号", required = true)
    @JsonProperty(value = "SshPort", required = true)
    @NotNull
    private Long sshPort;

    @ApiModelProperty(name = "ValidHostnameList", value = "合法主机名列表", required = true)
    @JsonProperty(value = "ValidHostnameList", required = true)
    @NotNull
    private List<String> validHostnameList;

    @ApiModelProperty(name = "InvalidHostnameList", value = "非法主机名列表", required = true)
    @JsonProperty(value = "InvalidHostnameList", required = true)
    @NotNull
    private List<String> invalidHostnameList;

}
