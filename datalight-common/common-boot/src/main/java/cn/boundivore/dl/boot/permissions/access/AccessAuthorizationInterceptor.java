package cn.boundivore.dl.boot.permissions.access;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 在 Interceptor 中执行接口权限拦截验证
 * Created by: Boundivore
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@Slf4j
public class AccessAuthorizationInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {

        try {
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }

            PermissionInterface permissionInterfaceAnnotation = ((HandlerMethod) handler).getMethodAnnotation(PermissionInterface.class);

            if (permissionInterfaceAnnotation == null || !permissionInterfaceAnnotation.enabled()) {
                return true;
            } else {
                Class<? extends IAccessAuthorizationHandler> authHandlerClass = permissionInterfaceAnnotation.authHandler();
                return SpringUtil.getBean(authHandlerClass).isPermitted(request, response);
            }

        } catch (Exception e) {
            val error = ExceptionUtil.stacktraceToString(e);
            log.error(error);
            return false;
        }
    }
}
