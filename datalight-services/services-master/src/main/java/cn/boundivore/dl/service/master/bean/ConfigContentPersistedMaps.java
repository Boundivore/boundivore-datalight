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
package cn.boundivore.dl.service.master.bean;

import cn.boundivore.dl.base.request.impl.master.ConfigSaveRequest;
import cn.boundivore.dl.orm.po.single.TDlConfigContent;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * Description: 配置文件存储内容封装
 * Created by: Boundivore
 * E-mail: boundivore@formail.com
 * Creation time: 2023/7/31
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Accessors(chain = true)
public class ConfigContentPersistedMaps {

    // <clusterId + filename + sha256, ConfigRequest> 此处会丢弃部分不会被使用的配置
    private Map<String, ConfigSaveRequest.ConfigRequest> groupConfigRequestMap;

    // <clusterId + filename + sha256, TDlConfigContent>
    private Map<String, TDlConfigContent> groupTDlConfigContentMap;

    /**
     * Description: 判断当前应用的配置文件内容是否已持久化到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/31
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return true：已全部持久化，false：未全部持久化
     */
    public boolean isAllPersisted() {
        return this.groupConfigRequestMap != null &&
                this.groupTDlConfigContentMap != null &&
                this.groupConfigRequestMap.size() == this.groupTDlConfigContentMap.size();
    }

}
