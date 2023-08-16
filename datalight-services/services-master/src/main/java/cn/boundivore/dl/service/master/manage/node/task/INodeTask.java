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
package cn.boundivore.dl.service.master.manage.node.task;


import cn.boundivore.dl.service.master.manage.node.bean.NodeTaskMeta;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Callable;

/**
 * Description: NodeTask 异步任务接口，用于规范异步任务执行逻辑，所有 NodeTask 必须先实现 INodeTask 接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public interface INodeTask extends Callable<NodeTaskMeta> {

    /**
     * Description: NodeTask 执行任务细节
     * Created by: Boundivore
     * Creation time: 2023/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: 执行成功时无返回，执行异常时抛出 BException 异常。
     */
    void run() throws Exception;


    /**
     * Description: NodeTask 执行成功后处理逻辑
     * Created by: Boundivore
     * Creation time: 2023/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return NodeTaskResult
     */
    NodeTaskMeta.NodeTaskResult success();


    /**
     * Description: NodeTask 执行失败后处理逻辑
     * Created by: Boundivore
     * Creation time: 2023/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return NodeTaskResult
     */
    NodeTaskMeta.NodeTaskResult fail();

    /**
     * Description: 获取当前任务的 NodeTaskMeta
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return TaskMeta 返回当前任务的 NodeTaskMeta
     */
    @NotNull
    NodeTaskMeta getNodeTaskMeta();
}
