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
package cn.boundivore.dl.service.master.config;

import cn.boundivore.dl.base.enumeration.impl.StaticRoleTypeEnum;
import cn.boundivore.dl.service.master.bean.PermissionTemplated;
import cn.boundivore.dl.service.master.service.MasterPermissionHandlerService;
import cn.boundivore.dl.service.master.utils.SaTokenCheckUtil;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * Description: SaTokenConfigure
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfigure implements WebMvcConfigurer {

    private final MasterPermissionHandlerService masterPermissionHandlerService;

    /**
     * Description: 注册 Sa-Token 拦截器，打开注解式鉴权功能
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param registry 映射拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义认证规则
        registry.addInterceptor(
                new SaInterceptor(handler -> {

                    // 登录校验，拦截所有路由，并排除登录 URI
                    SaRouter.match(
                            MASTER_URL_PREFIX + "/**",
                            r -> StpUtil.checkLogin()
                    );

                    // <PermissionCode, PermissionTemplated> 动态加载当前所有接口权限
                    Map<String, PermissionTemplated> permissionTemplatedMap = this.masterPermissionHandlerService.getPermissionTemplatedMap();

                    // 遍历添加权限校检
                    permissionTemplatedMap.forEach((permissionCode, permissionTemplated) -> {
                                SaRouter.match(
                                        permissionTemplated.getPath(),
                                        r -> SaTokenCheckUtil.checkRoleOrPermission(
                                                CollUtil.newArrayList(StaticRoleTypeEnum.ADMIN.name()),
                                                CollUtil.newArrayList(permissionTemplated.getCode())
                                        )
                                );
                            }
                    );
                })
        ).addPathPatterns("/**");
    }

//    @Bean
//    public StpLogic getStpLogicJwt() {
//        return new StpLogicJwtForStateless();
//    }
}
