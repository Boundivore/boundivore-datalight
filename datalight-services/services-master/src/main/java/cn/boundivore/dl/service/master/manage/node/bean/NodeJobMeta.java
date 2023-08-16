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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;

/**
 * Description: 用于记录当前异步 NodeJob 的配置信息
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
@Accessors(chain = true)
public class NodeJobMeta extends NodeTimeMeta {

    private String tag;

    private long id;

    private long clusterId;

    private NodeActionTypeEnum nodeActionTypeEnum;

    private String name;

    private ExecStateEnum execStateEnum;

    //<NodeTaskId, NodeTaskMeta>
    private LinkedHashMap<Long, NodeTaskMeta>  nodeTaskMetaMap;

    private NodeJobResult nodeJobResult;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public final static class NodeJobResult {
        private boolean isSuccess;
    }
}
