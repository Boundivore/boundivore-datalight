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
package cn.boundivore.dl.service.master.manage.service.bean;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Description: 用于记录当前异步 Stage 任务的配置信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString(
        exclude = {
                "jobMeta"
        }
)
public class StageMeta extends TimeMeta {
    private static final long serialVersionUID = -3531007069256178385L;

    private transient JobMeta jobMeta;

    private long id;

    private String name;

    private String serviceName;

    // 执行时服务当前状态
    private SCStateEnum currentServiceState;

    private Long priority;

    private StageResult stageResult;

    private ExecStateEnum stageStateEnum;

    //<TaskId, TaskMeta>
    private LinkedHashMap<Long, TaskMeta> taskMetaMap;

    /**
     * Description: 异步 Stage 执行结果信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Version: V1.0
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public final static class StageResult implements Serializable {
        private static final long serialVersionUID = -7532545325216862478L;

        private boolean isSuccess;
    }

}
