package cn.boundivore.dl.api.master.define;

import cn.boundivore.dl.base.response.impl.master.AbstractServiceMonitorVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * @author: 李煌民
 * @date: 2026-05-14 13:48
 * 服务监控状态 API
 **/
@Api(value = "IMasterServiceMonitorAPI", tags = "Master 接口：服务监控状态相关")
@FeignClient(name = "IMasterServiceMonitorAPI", contextId = "IMasterServiceMonitorAPI", path = MASTER_URL_PREFIX)
public interface IMasterServiceMonitorAPI {

    @GetMapping(value = "/service/monitor/status")
    @Operation(summary = "获取服务监控状态列表")
    Result<AbstractServiceMonitorVo.ServiceMonitorStatusResponse> getServiceMonitorStatus(
            @Schema(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;
}
