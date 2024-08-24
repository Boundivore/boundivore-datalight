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
package cn.boundivore.dl.base.request.impl.worker;

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Description: 修改配置请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ConfigFileRequest", description = "ConfigFileRequest: 修改配置文件请求体")
public class ConfigFileRequest implements IRequest {

    private static final long serialVersionUID = 8083098193378451754L;

    @Schema(name = "Path", title = "配置文件路径", required = true)
    @JsonProperty("Path")
    private String path;

    @Schema(name = "ConfigVersion", title = "当前版本号", required = true)
    @JsonProperty("ConfigVersion")
    private Long configVersion;

    @Schema(name = "Filename", title = "配置文件名称", required = true)
    @JsonProperty("Filename")
    private String filename;

    @Schema(name = "ContentBase64", title = "配置文件内容（Base64）", required = true)
    @JsonProperty("ContentBase64")
    private String contentBase64;

    @Schema(name = "Sha256", title = "配置文件信息摘要", required = true)
    @JsonProperty("Sha256")
    private String sha256;

    @Schema(name = "User", title = "配置文件及其目录所属用户", required = true)
    @JsonProperty("User")
    private String user = "datalight";

    @Schema(name = "Group", title = "配置文件及其目录所属用户组", required = true)
    @JsonProperty("Group")
    private String group = "datalight";
}
