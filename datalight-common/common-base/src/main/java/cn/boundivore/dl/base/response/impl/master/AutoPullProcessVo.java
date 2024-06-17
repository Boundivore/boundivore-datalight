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

import javax.validation.constraints.NotNull;

/**
 * Description: 自动进程拉起相关响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "AutoPullProcessVo",
        description = "AutoPullProcessVo 自动拉起开关状态 响应体"
)
public class AutoPullProcessVo implements IVo {

    private static final long serialVersionUID = 1897100658946944743L;

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;



    @Schema(name = "AutoPullWorker", title = "自动拉起 Worker 开关状态", required = true)
    @JsonProperty(value = "AutoPullWorker", required = true)
    @NotNull
    private Boolean autoPullWorker;

    @Schema(name = "AutoCloseBeginTimeWorker", title = "Worker 自动拉起开关关闭时的起始时间", required = true)
    @JsonProperty(value = "AutoCloseBeginTimeWorker", required = true)
    @NotNull
    private Long autoCloseBeginTimeWorker = 0L;

    @Schema(name = "AutoCloseEndTimeWorker", title = "Worker 自动拉起开关关闭时的截止时间", required = true)
    @JsonProperty(value = "AutoCloseEndTimeWorker", required = true)
    @NotNull
    private Long autoCloseEndTimeWorker = 0L;

}
