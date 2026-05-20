package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.api.third.define.IThirdPrometheusAPI;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ServiceMonitorStateEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractServiceMonitorVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.service.single.ITDlComponentService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 李煌民
 * @date: 2026-05-20 09:50
 *
 * 服务监控状态服务
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterServiceMonitorService {

    private final RemoteInvokePrometheusService remoteInvokePrometheusService;
    private final MasterComponentService masterComponentService;
    private final MasterNodeService masterNodeService;
    private final ITDlComponentService componentService;

    /**
     * 获取服务监控状态列表
     *
     * @param clusterId 集群ID
     * @return 服务监控状态响应
     */
    public AbstractServiceMonitorVo.ServiceMonitorStatusResponse getServiceMonitorStatus(Long clusterId) {
        // 获取Prometheus监控目标数据
        String prometheusResponse = invokePrometheusTargets(clusterId);

        if (prometheusResponse == null) {
            return new AbstractServiceMonitorVo.ServiceMonitorStatusResponse()
                    .setServiceMonitorStatusList(Collections.emptyList());
        }

        // 解析Prometheus响应
        List<AbstractServiceMonitorVo.ServiceMonitorStatusVo> statusList = parsePrometheusTargets(prometheusResponse);

        // 获取已部署的组件列表
        Set<String> deployedServices = getDeployedServices(clusterId);

        // 过滤：只保留已部署的服务
        List<AbstractServiceMonitorVo.ServiceMonitorStatusVo> filteredList = statusList.stream()
                .filter(status -> deployedServices.contains(status.getServiceName()))
                .collect(Collectors.toList());

        return new AbstractServiceMonitorVo.ServiceMonitorStatusResponse()
                .setServiceMonitorStatusList(filteredList);
    }

    /**
     * 获取集群中已部署的服务名称集合
     *
     * @param clusterId 集群ID
     * @return 已部署的服务名称集合
     */
    private Set<String> getDeployedServices(Long clusterId) {
        try {
            // 获取所有已部署的组件（排除 REMOVED 和 UNSELECTED 状态）
            List<TDlComponent> allComponents = componentService.lambdaQuery()
                    .select()
                    .eq(TDlComponent::getClusterId, clusterId)
                    .eq(TDlComponent::getComponentState, SCStateEnum.STARTED)
                    .list();

            if (CollUtil.isEmpty(allComponents)) {
                return Collections.emptySet();
            }

            // 提取服务名称（去重）
            return allComponents.stream()
                    .map(TDlComponent::getServiceName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取已部署服务列表失败", e);
            return Collections.emptySet();
        }
    }

    /**
     * 调用Prometheus的targets接口
     *
     * @param clusterId 集群ID
     * @return Prometheus响应JSON字符串
     */
    private String invokePrometheusTargets(Long clusterId) {
        try {
            // 获取Prometheus组件信息
            List<TDlComponent> prometheusList = masterComponentService
                    .getTDlComponentListByServiceName(clusterId, "MONITOR")
                    .stream()
                    .filter(c -> "Prometheus".equals(c.getComponentName()))
                    .collect(Collectors.toList());

            if (CollUtil.isEmpty(prometheusList)) {
                log.warn("未找到Prometheus组件实例");
                return null;
            }

            // 获取Prometheus组件和端口
            TDlComponent prometheus = prometheusList.get(0);
            String port = "9090";

            // 通过NodeId获取主机名
            List<TDlNode> nodeList = masterNodeService.getNodeListInNodeIds(clusterId,
                    Collections.singletonList(prometheus.getNodeId()));

            if (CollUtil.isEmpty(nodeList)) {
                log.warn("未找到Prometheus所在节点信息");
                return null;
            }

            String hostname = nodeList.get(0).getHostname();

            // 调用Prometheus API
            IThirdPrometheusAPI api = remoteInvokePrometheusService.iThirdPrometheusAPI(hostname, port);

            Map<String, String> params = MapUtil.of("search", "");
            Result<String> result = api.getPrometheus("api/v1/targets", params);

            if (result == null || result.getData() == null) {
                log.warn("Prometheus API返回为空");
                return null;
            }

            return result.getData();
        } catch (Exception e) {
            log.error("调用Prometheus失败", e);
            return null;
        }
    }

    /**
     * 解析Prometheus targets响应
     *
     * @param response Prometheus响应JSON字符串
     * @return 服务监控状态列表
     */
    private List<AbstractServiceMonitorVo.ServiceMonitorStatusVo> parsePrometheusTargets(String response) {
        try {
            JSONObject json = JSONUtil.parseObj(response);
            String status = json.getStr("status");

            if (!"success".equals(status)) {
                log.warn("Prometheus API返回失败: {}", status);
                return Collections.emptyList();
            }

            JSONObject data = json.getJSONObject("data");
            JSONArray activeTargets = data.getJSONArray("activeTargets");

            if (activeTargets == null || activeTargets.isEmpty()) {
                return Collections.emptyList();
            }

            // 按scrapePool分组
            Map<String, List<AbstractServiceMonitorVo.ComponentMonitorStatusVo>> groupedByService = new LinkedHashMap<>();

            for (Object targetObj : activeTargets) {
                JSONObject target = (JSONObject) targetObj;
                String scrapePool = target.getStr("scrapePool");
                String health = target.getStr("health");
                String scrapeUrl = target.getStr("scrapeUrl");

                // 从scrapePool提取服务和组件名称
                String serviceName = extractServiceName(scrapePool);
                String componentName = extractComponentName(scrapePool);
                String endpoint = extractEndpoint(scrapeUrl);

                AbstractServiceMonitorVo.ComponentMonitorStatusVo componentStatus = new AbstractServiceMonitorVo.ComponentMonitorStatusVo()
                        .setComponentName(componentName)
                        .setState(health.toUpperCase())
                        .setEndpoint(endpoint)
                        .setScrapePool(scrapePool);

                groupedByService.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(componentStatus);
            }

            // 转换为服务状态列表
            List<AbstractServiceMonitorVo.ServiceMonitorStatusVo> result = new ArrayList<>();
            for (Map.Entry<String, List<AbstractServiceMonitorVo.ComponentMonitorStatusVo>> entry : groupedByService.entrySet()) {
                AbstractServiceMonitorVo.ServiceMonitorStatusVo serviceStatus = new AbstractServiceMonitorVo.ServiceMonitorStatusVo()
                        .setServiceName(entry.getKey())
                        .setComponentStatusList(entry.getValue());

                // 计算服务状态
                serviceStatus.setServiceState(calculateServiceState(entry.getValue()));
                result.add(serviceStatus);
            }

            return result;
        } catch (Exception e) {
            log.error("解析Prometheus响应失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 计算服务状态
     *
     * @param components 组件状态列表
     * @return 服务状态
     */
    private ServiceMonitorStateEnum calculateServiceState(List<AbstractServiceMonitorVo.ComponentMonitorStatusVo> components) {
        if (CollUtil.isEmpty(components)) {
            return ServiceMonitorStateEnum.UNKNOWN;
        }

        long upCount = components.stream()
                .filter(c -> "UP".equals(c.getState()))
                .count();

        long downCount = components.stream()
                .filter(c -> "DOWN".equals(c.getState()))
                .count();

        if (upCount == components.size()) {
            return ServiceMonitorStateEnum.GREEN;
        } else if (downCount == components.size()) {
            return ServiceMonitorStateEnum.RED;
        } else {
            return ServiceMonitorStateEnum.YELLOW;
        }
    }

    /**
     * 从scrapePool提取服务名称
     * 格式: SERVICE-Component
     *
     * @param scrapePool 抓取池名称
     * @return 服务名称
     */
    private String extractServiceName(String scrapePool) {
        if (scrapePool == null || !scrapePool.contains("-")) {
            return scrapePool;
        }
        return scrapePool.split("-")[0];
    }

    /**
     * 从scrapePool提取组件名称
     * 格式: SERVICE-Component
     *
     * @param scrapePool 抓取池名称
     * @return 组件名称
     */
    private String extractComponentName(String scrapePool) {
        if (scrapePool == null || !scrapePool.contains("-")) {
            return scrapePool;
        }
        String[] parts = scrapePool.split("-");
        if (parts.length > 1) {
            return parts[1];
        }
        return scrapePool;
    }

    /**
     * 从URL提取端点地址
     *
     * @param url URL地址
     * @return 端点地址(如: host:port)
     */
    private String extractEndpoint(String url) {
        if (url == null) {
            return "";
        }
        try {
            // 移除协议部分
            String hostPart = url.replaceFirst("^https?://", "");
            // 移除路径部分
            int slashIndex = hostPart.indexOf('/');
            if (slashIndex > 0) {
                return hostPart.substring(0, slashIndex);
            }
            return hostPart;
        } catch (Exception e) {
            return url;
        }
    }

}
