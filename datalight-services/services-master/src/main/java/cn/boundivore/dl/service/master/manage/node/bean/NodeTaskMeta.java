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
package cn.boundivore.dl.service.master.manage.node.bean;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;

/**
 * Description: 用于记录当前异步 NodeTask 任务的配置信息
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
                "nodeJobMeta"
        }
)
public class NodeTaskMeta extends NodeTimeMeta {

    private NodeJobMeta nodeJobMeta;

    //NodeTask 执行时，节点状态
    private NodeStateEnum startState;

    //NodeTask 失败后，节点状态
    private NodeStateEnum failState;

    //NodeTask 成功后，节点状态
    private NodeStateEnum successState;

    //当前节点状态
    private NodeStateEnum currentState;

    // 标记：用于判断开始执行当前 NodeTask 之前，是否需要阻塞等待之前 NodeTask 执行完毕，true（需要），false（不需要）
    // 例如利用与滚动重启，或低功耗部署等（Task runs One by one）
    protected boolean wait;

    private long id;

    private String name;

    private String hostname;

    private Integer sshPort;

    private String privateKeyPath;

    private String nodeIp;

    private long nodeId;

    private ExecStateEnum nodeTaskStateEnum;

    private NodeActionTypeEnum nodeActionTypeEnum;

    private NodeTaskResult nodeTaskResult;

    //<NodeStepId, NodeStepMeta>
    private LinkedHashMap<Long, NodeStepMeta> nodeStepMetaMap;


    /**
     * Description: 异步 NodeTask 执行结果信息
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
    public final static class NodeTaskResult {
        private boolean isSuccess;
    }

}
