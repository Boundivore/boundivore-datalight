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
package cn.boundivore.dl.service.worker.cache;

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Description: 节点、服务等元数据信息缓存类
 * Created by: Boundivore
 * E-mail: boundivore@formail.com
 * Creation time: 2023/8/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RequiredArgsConstructor
@Component
public class MetaCache {

    private final static String KEY_MASTER_META = "MASTER_META";
    private final static String KEY_SERVICE_META = "SERVICE_META";

    private Cache<String, MasterMeta> masterMetaCache;
    private Cache<String, ServiceMeta> serviceMetaCache;

    @PostConstruct
    public void init() {
        this.masterMetaCache = CacheUtil.newFIFOCache(1);
        this.serviceMetaCache = CacheUtil.newFIFOCache(1);
    }

    /**
     * Description: 更新 MasterMeta 缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param masterMeta Master 元数据信息
     */
    public void updateMasterMeta(MasterMeta masterMeta) {
        this.masterMetaCache.put(
                MetaCache.KEY_MASTER_META,
                masterMeta
        );
    }

    /**
     * Description: 更新 ServiceMeta 缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceMeta 已服役的 Service 元数据信息
     */
    public void updateMasterMeta(ServiceMeta serviceMeta) {
        this.serviceMetaCache.put(
                MetaCache.KEY_SERVICE_META,
                serviceMeta
        );
    }

    /**
     * Description: 获取 MasterMeta
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return MasterMeta
     */
    public MasterMeta getMasterMeta() {
        return this.masterMetaCache.get(MetaCache.KEY_MASTER_META);
    }

    /**
     * Description: 获取 ServiceMeta
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return ServiceMeta
     */
    public ServiceMeta getServiceMeta() {
        return this.serviceMetaCache.get(MetaCache.KEY_SERVICE_META);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class MasterMeta {
        private String ip;

        private String port;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class ServiceMeta {
        private Long nodeId;

        private String hostname;

        private String ip;

        private List<Service> serviceList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Service {

        private Long clusterId;

        private String serviceName;

        private SCStateEnum scStateEnum;

        private List<Component> componentList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Component {

        private String componentName;

        private SCStateEnum scStateEnum;

        private String startShell;

        private String stopShell;

    }
}

