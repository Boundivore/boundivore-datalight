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
package cn.boundivore.dl.service.master.service;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: AIAgent 实例注册表。
 * <p>
 * 平台不引入 Nacos 这类注册中心，所以 Master 不去「发现」AIAgent，
 * 而是由 AIAgent 主动上报。方向是反的，原因有两个：
 * <p>
 * 一是 Master 是平台里最先起来、地址最固定的角色，AIAgent 的配置里本来就有
 * Master 地址（DATALIGHT_MASTER_BASE_URL），反过来 Master 无从知道 AIAgent 去了哪。
 * <p>
 * 二是 AIAgent 的核心场景恰恰是集群还不存在的时候做问答式部署，
 * 此时 t_dl_node 里一个节点都没有，靠查库定位是行不通的。
 * <p>
 * 换节点部署不需要改 Master 配置：新节点起来后自行注册，
 * 旧实例停止心跳，超过 TTL 自动被淘汰，路由自然切过去。
 * <p>
 * 注册信息只放内存，不落库。理由是这份数据本身就是易失的——
 * 它描述的是「此刻谁活着」，重启后由心跳在一个周期内自愈，
 * 落库反而会留下过期的假信息。代价是 Master 重启后有一个心跳周期的空窗，
 * 期间对话接口会明确报未注册，而不是连到一个错误的地址上。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Service
public class MasterAiAgentRegistry {

    /**
     * 心跳超时。AIAgent 每 15 秒上报一次，连续错过 4 次即认为失联。
     * 放宽到 60 秒是为了容忍网络抖动与 GC 停顿，避免正常实例被误判下线。
     */
    private static final long HEARTBEAT_TIMEOUT_MILLIS = 60_000L;

    /**
     * key 为实例地址，同一地址重复注册即刷新
     */
    private final Map<String, AgentInstance> instances = new ConcurrentHashMap<>();

    /**
     * Description: 注册或刷新一个实例。
     * <p>
     * 注册与心跳走同一个方法。AIAgent 侧不需要区分「首次注册」和「后续心跳」，
     * 少一个状态就少一类不一致：实例重启后直接重新上报即可，
     * 不必先反注册，也不怕 Master 那边还留着旧记录。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param baseUrl  实例地址，形如 http://192.168.1.10:8010
     * @param hostname 实例所在主机名，仅用于展示与排查
     * @param version  实例版本
     * @return 当前存活实例数
     */
    public int register(String baseUrl, String hostname, String version) {
        final String normalized = normalize(baseUrl);
        if (StrUtil.isBlank(normalized)) {
            throw new IllegalArgumentException("AIAgent 上报的地址为空");
        }

        final AgentInstance previous = this.instances.put(
                normalized,
                new AgentInstance(normalized, hostname, version, System.currentTimeMillis())
        );

        if (previous == null) {
            log.info("AIAgent 上线: {} (hostname={}, version={})", normalized, hostname, version);
            // 有新实例进来时顺手清一次过期的，省得单开一个定时任务
            this.evictExpired();
        }

        return this.aliveInstances().size();
    }

    /**
     * Description: 主动下线。AIAgent 正常停止时调用，让路由立刻切走，不用等 TTL。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param baseUrl 实例地址
     */
    public void unregister(String baseUrl) {
        final String normalized = normalize(baseUrl);
        if (this.instances.remove(normalized) != null) {
            log.info("AIAgent 主动下线: {}", normalized);
        }
    }

    /**
     * Description: 解析出当前该把请求发给谁。
     * <p>
     * 多个实例同时存活时取心跳最新的那个。这种情况多见于换节点部署的过渡期，
     * 取最新的能让流量尽快切到新实例上。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 实例地址，没有存活实例时返回 null
     */
    public String resolveBaseUrl() {
        return this.aliveInstances()
                .stream()
                .max(Comparator.comparingLong(AgentInstance::getLastHeartbeatTime))
                .map(AgentInstance::getBaseUrl)
                .orElse(null);
    }

    /**
     * Description: 列出当前存活的实例，供页面与排查使用。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 存活实例列表
     */
    public List<AgentInstance> aliveInstances() {
        final long deadline = System.currentTimeMillis() - HEARTBEAT_TIMEOUT_MILLIS;
        final List<AgentInstance> alive = new ArrayList<>();
        for (AgentInstance instance : this.instances.values()) {
            if (instance.getLastHeartbeatTime() >= deadline) {
                alive.add(instance);
            }
        }
        return alive;
    }

    /**
     * 清理心跳超时的实例
     */
    private void evictExpired() {
        final long deadline = System.currentTimeMillis() - HEARTBEAT_TIMEOUT_MILLIS;
        this.instances.entrySet().removeIf(entry -> {
            if (entry.getValue().getLastHeartbeatTime() < deadline) {
                log.info("AIAgent 心跳超时，移出注册表: {}", entry.getKey());
                return true;
            }
            return false;
        });
    }

    /**
     * 去掉末尾斜杠，避免同一实例因写法不同被登记成两条
     */
    private static String normalize(String baseUrl) {
        if (StrUtil.isBlank(baseUrl)) {
            return "";
        }
        String trimmed = baseUrl.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    /**
     * Description: 一个 AIAgent 实例的登记信息。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Version: V1.0
     */
    @Getter
    public static class AgentInstance {

        private final String baseUrl;

        private final String hostname;

        private final String version;

        private final long lastHeartbeatTime;

        public AgentInstance(String baseUrl, String hostname, String version, long lastHeartbeatTime) {
            this.baseUrl = baseUrl;
            this.hostname = hostname;
            this.version = version;
            this.lastHeartbeatTime = lastHeartbeatTime;
        }
    }
}
