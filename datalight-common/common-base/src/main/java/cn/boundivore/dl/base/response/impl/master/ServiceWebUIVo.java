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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 指定服务下组件 WebUI 列表响应体
 * Created by: Boundivore
 * E-mail: boundivore@formail.com
 * Creation time: 2024/4/2
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
        name = "ServiceWebUIVo",
        description = "ServiceWebUIVo 服务 WebUI 响应体"
)
public class ServiceWebUIVo implements IVo {

    private static final long serialVersionUID = -448555250270792685L;

    @Schema(name = "ClusterId", title = "服务名称", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @Schema(name = "ServiceName", title = "服务名称", required = true)
    @JsonProperty(value = "ServiceName", required = true)
    @NotNull
    private String serviceName;

    @Schema(name = "ComponentWebUIList", title = "服务名称", required = true)
    @JsonProperty(value = "ComponentWebUIList", required = true)
    @NotNull
    private List<ComponentWebUI> componentWebUIList;


    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ServiceWebUIVo.ComponentWebUI",
            description = "ServiceWebUIVo.ComponentWebUI 当前组件 UI 信息"
    )
    public static class ComponentWebUI implements IVo {

        private static final long serialVersionUID = 8142033918569301682L;

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        @NotNull
        private String componentName;

        @Schema(name = "Url", title = "WebUI 路径", required = true)
        @JsonProperty(value = "Url", required = true)
        @NotNull
        private String url;

        @Schema(name = "ShowName", title = "显示名称", required = true)
        @JsonProperty(value = "ShowName", required = true)
        @NotNull
        private String showName;
    }


}
