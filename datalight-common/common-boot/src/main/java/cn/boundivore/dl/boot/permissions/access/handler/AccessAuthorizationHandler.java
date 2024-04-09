package cn.boundivore.dl.boot.permissions.access.handler;

import cn.boundivore.dl.base.response.impl.master.AbstractPermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.result.ResultEnum;
import cn.boundivore.dl.boot.permissions.access.IAccessAuthorizationHandler;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Description: 根据 PermissionTable 验证当前用户的请求是否具备应有的权限
 * Created by: Boundivore
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Component
public class AccessAuthorizationHandler implements IAccessAuthorizationHandler {
    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        // TODO 预加载权限数据或配置缓存，增加权限检查效率，如若此做，需考虑缓存与数据库实际值的同步更新契机，防止使用过期的缓存
    }

    @Override
    public boolean isPermitted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ParseException {
        try {
            String requestURI = httpServletRequest.getRequestURI();

            log.info(String.format(
                            "URI: %s",
                            requestURI
                    )
            );

            List<AbstractPermissionRuleVo.PermissionRuleInterfaceVo> permissionRuleInterfaceList = this.getPermissionRuleInterfaceVo(
                    requestURI
            );

            if (CollUtil.isNotEmpty(permissionRuleInterfaceList)) {
                return true;
            }

            httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(
                    mapper.writeValueAsString(
                            Result.fail(
                                    ResultEnum.FAIL_INTERFACE_UNAUTHORIZED
                            )
                    )
            );

            return false;
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            return false;
        }
    }

    /**
     * Description:通过数据库验证权限
     * TODO 如果想进一步提升此处的性能，可考虑二级缓存
     * Created by: Boundivore
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param ruleInterfaceUri 接口权限规则 URI
     * @return List<AbstractPermissionRuleVo.PermissionRuleInterfaceVo> 权限与规则列表
     */
    private List<AbstractPermissionRuleVo.PermissionRuleInterfaceVo> getPermissionRuleInterfaceVo(String ruleInterfaceUri) throws Exception {

        return null;
    }
}
