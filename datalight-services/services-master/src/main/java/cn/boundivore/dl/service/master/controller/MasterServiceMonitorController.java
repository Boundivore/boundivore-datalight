package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.api.master.define.IMasterServiceMonitorAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractServiceMonitorVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterServiceMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 李煌民
 * @date: 2026-05-20 09:47
 * 服务监控状态控制器
 **/
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterServiceMonitorController implements IMasterServiceMonitorAPI {

    private final MasterServiceMonitorService masterServiceMonitorService;

    @Override
    public Result<AbstractServiceMonitorVo.ServiceMonitorStatusResponse> getServiceMonitorStatus(Long clusterId) throws Exception {
        AbstractServiceMonitorVo.ServiceMonitorStatusResponse response = masterServiceMonitorService.getServiceMonitorStatus(clusterId);
        return Result.success(response);
    }
}
