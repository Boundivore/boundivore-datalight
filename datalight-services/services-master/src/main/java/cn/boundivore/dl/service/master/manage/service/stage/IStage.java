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
package cn.boundivore.dl.service.master.manage.service.stage;

import cn.boundivore.dl.service.master.manage.service.bean.StageMeta;
import cn.boundivore.dl.service.master.manage.service.task.ITask;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description: 包含多个 Task，所有 Stage 必须先实现 IStage 接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public interface IStage extends Callable<StageMeta>{

    /**
     * Description: 向当前 Stage 中添加 Task
     * Created by: Boundivore
     * Creation time: 2023/4/23 13:50
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param task 异步 Task
     * @return LinkedBlockingQueue<ITask> 返回当前 Task 缓存队列
     */
    LinkedBlockingQueue<ITask> offerTask(ITask task);

    /**
     * Description: 从当前 Stage 中取出 Task
     * Created by: Boundivore
     * Creation time: 2023/4/23 13:50
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return ITask 异步 Task 接口
     */
    ITask pollTask();

    /**
     * Description: 执行当前 Stage 中的所有 Task
     * Created by: Boundivore
     * Creation time: 2023/4/23 13:52
     * Modification description:
     * Modified by:
     * Modification time:
     */
    void runTaskBatch() throws Exception;

    /**
     * Description: Stage 执行成功后处理逻辑
     * Created by: Boundivore
     * Creation time: 2023/4/23 14:20
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return StageResult Stage 结果集
     */
    StageMeta.StageResult success();


    /**
     * Description: Stage 执行失败后处理逻辑
     * Created by: Boundivore
     * Creation time: 2023/4/23 14:20
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return StageResult Stage 结果集
     */
    StageMeta.StageResult fail();

    /**
     * Description: 获取当前阶段的 StageMeta
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7 9:43
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return StageMeta 返回当前任务的 StageMeta
     */
    StageMeta getStageMeta();
}
