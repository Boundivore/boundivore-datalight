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

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;

/**
 * Description: 用于记录当前异步 Task 任务的配置信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString(
        exclude = {
                "stageMeta"
        }
)
public class TaskMeta extends TimeMeta {

    private StageMeta stageMeta;

    //Task 执行时，组件状态
    private SCStateEnum startState;

    //Task 失败后，组件状态
    private SCStateEnum failState;

    //Task 成功后，组件状态
    private SCStateEnum successState;

    //组件当前状态
    private SCStateEnum currentState;

    // 标记：用于判断开始执行当前 Task 之前，是否需要阻塞等待之前 Task 执行完毕，true（需要），false（不需要）
    // 例如利用与滚动重启，或低功耗部署等（Task runs One by one）
    protected boolean wait;

    private long id;

    private String name;

    private long priority;

    private String hostname;

    private String nodeIp;

    private long nodeId;

    private long ram;

    private String serviceName;

    private String componentName;

    private ExecStateEnum taskStateEnum;

    private ActionTypeEnum actionTypeEnum;

    private TaskResult taskResult;

    //<StepId, StepMeta>
    private LinkedHashMap<Long, StepMeta> stepMetaMap;


    /**
     * Description: 异步 Task 执行结果信息
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
    public final static class TaskResult {
        private boolean isSuccess;
    }

}
