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
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Description: 用于记录当前异步 Job 的配置信息
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
public class JobMeta extends TimeMeta {
    private static final long serialVersionUID = 2377853761262481016L;

    private String tag;

    private long id;

    private ClusterMeta clusterMeta;

    private ActionTypeEnum actionTypeEnum;

    private String name;

    private JobResult jobResult;

    private ExecStateEnum execStateEnum;

    //<StageId, StageMeta>
    private LinkedHashMap<Long, StageMeta> stageMetaMap;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public final static class JobResult implements Serializable {
        private static final long serialVersionUID = 2211348172168285854L
                ;
        private boolean isSuccess;
    }
}
