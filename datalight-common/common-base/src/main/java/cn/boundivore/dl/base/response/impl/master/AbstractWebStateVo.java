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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Map;


public abstract class AbstractWebStateVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractWebStateVo.WebStateMapVo",
            description = "AbstractWebStateVo.WebStateMapVo 前端状态缓存信息集合"
    )
    public static class WebStateMapVo implements IVo {

        private static final long serialVersionUID = 1486215129046356914L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "UserId", title = "用户 ID", required = true)
        @JsonProperty(value = "UserId", required = true)
        @NotNull
        private Long userId;

        @Schema(name = "KVMap", title = "Web 缓存键值对信息", required = true)
        @JsonProperty(value = "KVMap", required = true)
        @NotNull
        private Map<String, String> kvMap;
    }

}
