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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Description: 待部署的服务组件预配置信息响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/19
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ConfigPreVo",
        description = "ConfigPreVo 待部署的服务组件预配置信息"
)
public class ConfigPreVo implements IVo {

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    private Long clusterId;

    @Schema(name = "ServiceList", title = "多个服务的预配置列表", required = true)
    @JsonProperty(value = "ServiceList", required = true)
    private List<ServiceVo> serviceList;


    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ConfigPreVo.ServiceVo",
            description = "ConfigPreVo.ServiceVo 当前服务"
    )
    public static class ServiceVo implements IVo {

        @Schema(name = "ServiceName", title = "当前服务", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        private String serviceName;

        @Schema(name = "PlaceholderInfoList", title = "预配置占位信息列表", required = true)
        @JsonProperty(value = "PlaceholderInfoList", required = true)
        private List<PlaceholderInfoVo> placeholderInfoList;
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ConfigPreVo.PlaceholderInfoVo",
            description = "ConfigPreVo.PlaceholderInfoVo 预配置信息"
    )
    public static class PlaceholderInfoVo implements IVo {

        @Schema(name = "TemplatedFilePath", title = "模板配置文件路径", required = true)
        @JsonProperty(value = "TemplatedFilePath", required = true)
        private String templatedFilePath;

        @Schema(name = "PropertyList", title = "属性列表", required = true)
        @JsonProperty(value = "PropertyList", required = true)
        private List<PropertyVo> propertyList;
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ConfigPreVo.PropertyVo",
            description = "ConfigPreVo.PropertyVo 预配置属性信息"
    )
    public static class PropertyVo implements IVo {

        @Schema(name = "Placeholder", title = "占位符", required = true)
        @JsonProperty(value = "Placeholder", required = true)
        private String placeholder;

        @Schema(name = "Describe", title = "描述", required = true)
        @JsonProperty(value = "Describe", required = true)
        private String describe;

        @Schema(name = "Default", title = "占位符默认值", required = true)
        @JsonProperty(value = "Default", required = true)
        private String defaultValue;
    }

}
