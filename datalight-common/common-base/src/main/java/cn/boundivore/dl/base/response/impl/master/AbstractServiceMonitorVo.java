package cn.boundivore.dl.base.response.impl.master;

import cn.boundivore.dl.base.enumeration.impl.ServiceMonitorStateEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: 李煌民
 * @date: 2026-05-20 09:44
 *
 * 服务监控状态相关 VO
 **/
public abstract class AbstractServiceMonitorVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(name = "ServiceMonitorVo.ServiceMonitorStatusVo", description = "服务监控状态响应体")
    public static class ServiceMonitorStatusVo implements IVo {

        private static final long serialVersionUID = 1L;

        @Schema(name = "ServiceName", title = "服务名称")
        @JsonProperty(value = "ServiceName")
        private String serviceName;

        @Schema(name = "ServiceState", title = "服务状态")
        @JsonProperty(value = "ServiceState")
        private ServiceMonitorStateEnum serviceState;

        @Schema(name = "ComponentStatusList", title = "组件状态列表")
        @JsonProperty(value = "ComponentStatusList")
        private List<ComponentMonitorStatusVo> componentStatusList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(name = "ServiceMonitorVo.ComponentMonitorStatusVo", description = "组件监控状态响应体")
    public static class ComponentMonitorStatusVo implements IVo {

        private static final long serialVersionUID = 1L;

        @Schema(name = "ComponentName", title = "组件名称")
        @JsonProperty(value = "ComponentName")
        private String componentName;

        @Schema(name = "State", title = "组件状态: UP/DOWN/UNKNOWN")
        @JsonProperty(value = "State")
        private String state;

        @Schema(name = "Endpoint", title = "端点地址")
        @JsonProperty(value = "Endpoint")
        private String endpoint;

        @Schema(name = "ScrapePool", title = "抓取池名称")
        @JsonProperty(value = "ScrapePool")
        private String scrapePool;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(name = "ServiceMonitorVo.ServiceMonitorStatusResponse", description = "服务监控状态响应")
    public static class ServiceMonitorStatusResponse implements IVo {

        private static final long serialVersionUID = 1L;

        @Schema(name = "ServiceMonitorStatusList", title = "服务监控状态列表")
        @JsonProperty(value = "ServiceMonitorStatusList")
        private List<ServiceMonitorStatusVo> serviceMonitorStatusList;
    }
}
