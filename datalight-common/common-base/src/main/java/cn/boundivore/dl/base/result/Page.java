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
package cn.boundivore.dl.base.result;

import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: PageVo
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/27
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "Page", 
        description = "master: 统一分页字段"
)
public class Page implements IVo {
    @Schema(name = "CurrentPage", title = "当前页码", required = true)
    @JsonProperty(value = "CurrentPage", required = true)
    private Long currentPage;

    @Schema(name = "TotalPage", title = "总页码", required = true)
    @JsonProperty(value = "TotalPage", required = true)
    private Long totalPage;

    @Schema(name = "PageSize", title = "单页条数", required = true)
    @JsonProperty(value = "PageSize", required = true)
    private Long pageSize;

    @Schema(name = "TotalSize", title = "总条数", required = true)
    @JsonProperty(value = "TotalSize", required = true)
    private Long totalSize;
}
