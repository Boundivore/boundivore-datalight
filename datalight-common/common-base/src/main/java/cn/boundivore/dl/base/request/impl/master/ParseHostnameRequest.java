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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

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
@Schema(
        name = "ParseHostnameRequest",
        description = "ParseHostnameRequest: 解析主机名 请求体"
)
public class ParseHostnameRequest implements IRequest {

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @Schema(name = "HostnameBase64", title = "节点名称，换行符分割每一项(Base64)", required = true)
    @JsonProperty(value = "HostnameBase64", required = true)
    @NotNull
    private String hostnameBase64;

    @Schema(name = "SshPort", title = "SSH 端口号", required = true)
    @JsonProperty(value = "SshPort", required = true)
    @NotNull
    private Long sshPort;

}
