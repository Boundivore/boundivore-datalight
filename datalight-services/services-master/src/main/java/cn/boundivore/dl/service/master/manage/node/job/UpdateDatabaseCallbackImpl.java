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
package cn.boundivore.dl.service.master.manage.node.job;

import cn.boundivore.dl.service.master.manage.node.bean.NodeStepMeta;
import cn.boundivore.dl.ssh.bean.TransferProgress;
import cn.boundivore.dl.ssh.listener.UpdateDatabaseCallback;

/**
 * Description: 更新文件传输进度实现
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/2/27
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class UpdateDatabaseCallbackImpl implements UpdateDatabaseCallback {

    private final NodeJobService nodeJobService;

    private final NodeStepMeta nodeStepMeta;

    public UpdateDatabaseCallbackImpl(NodeJobService nodeJobService,
                                      NodeStepMeta nodeStepMeta) {

        this.nodeJobService = nodeJobService;
        this.nodeStepMeta = nodeStepMeta;
    }

    @Override
    public void update(TransferProgress transferProgress) {
        if (transferProgress.getTotalTransferBytes().get() > 0
                && transferProgress.getTotalProgress() > 0
                // 控制进度每增加 2% 更新一次数据库，降低数据库压力
                && transferProgress.getTotalProgress() % 2 == 0) {
            this.nodeJobService.updateNodeStepDatabase(this.nodeStepMeta);
        }
    }
}
