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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: ConfigPreSaveRequest
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
        name = "ConfigPreSaveRequest",
        description = "ConfigPreListRequest: 预配置项列表 请求体"
)
public class ConfigPreSaveRequest implements IRequest {

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @Schema(name = "ServiceList", title = "多个服务的预配置列表", required = true)
    @JsonProperty(value = "ServiceList", required = true)
    private List<ServiceRequest> serviceList;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ConfigPreSaveRequest#ServiceRequest",
            description = "ConfigPreListVo#ServiceRequest: 当前服务"
    )
    public static class ServiceRequest implements IRequest {

        @Schema(name = "ServiceName", title = "当前服务", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull
        private String serviceName;

        @Schema(name = "PlaceholderInfoList", title = "预配置占位信息列表", required = true)
        @JsonProperty(value = "PlaceholderInfoList", required = true)
        private List<PlaceholderInfoRequest> placeholderInfoList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ConfigPreSaveRequest#PlaceholderInfoRequest",
            description = "ConfigPreSaveRequest#PlaceholderInfoRequest: 预配置信息"
    )
    public static class PlaceholderInfoRequest implements IRequest {

        @Schema(name = "TemplatedFilePath", title = "模板配置文件路径", required = true)
        @JsonProperty(value = "TemplatedFilePath", required = true)
        private String templatedFilePath;

        @Schema(name = "PropertyList", title = "属性列表", required = true)
        @JsonProperty(value = "PropertyList", required = true)
        private List<PropertyRequest> propertyList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ConfigPreSaveRequest#PropertyRequest",
            description = "ConfigPreSaveRequest#PropertyRequest: 预配置属性信息"
    )
    public static class PropertyRequest implements IRequest {

        @Schema(name = "Placeholder", title = "占位符", required = true)
        @JsonProperty(value = "Placeholder", required = true)
        @NotNull
        private String placeholder;

        @Schema(name = "Value", title = "占位符修改后的值", required = true)
        @JsonProperty(value = "Value", required = true)
        @NotNull
        private String value;

        @Schema(name = "Describe", title = "描述", required = true)
        @JsonProperty(value = "Describe", required = true)
        private String describe;

        @Schema(name = "Default", title = "占位符默认值", required = true)
        @JsonProperty(value = "Default", required = true)
        private String defaultValue;
    }


}
