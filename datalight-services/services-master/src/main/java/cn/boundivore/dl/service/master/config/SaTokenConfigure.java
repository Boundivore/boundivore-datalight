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

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
public class SaTokenConfigure implements WebMvcConfigurer {


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
                            "/**",
                            "/api/v1/master/user/login",
                            r -> StpUtil.checkLogin()
                    );

                    SaRouter.match(
                            "/api/v1/master/**",
                            r -> StpUtil.checkRoleOr("admin", "super-admin")
                    );

                    // 权限校验
                    SaRouter.match("/api/v1/master/**", r -> StpUtil.checkPermission("user"));
                    SaRouter.match("/api/v1/master/**", r -> StpUtil.checkPermission("admin"));
                    SaRouter.match("/api/v1/master/**", r -> StpUtil.checkPermission("goods"));
                    SaRouter.match("/api/v1/master/**", r -> StpUtil.checkPermission("orders"));
                    SaRouter.match("/api/v1/master/**", r -> StpUtil.checkPermission("notice"));
                    SaRouter.match("/api/v1/master/**", r -> StpUtil.checkPermission("comment"));

                    // 甚至你可以随意的写一个打印语句
                    SaRouter.match("/**", r -> System.out.println("----啦啦啦----"));

                    // 连缀写法
                    SaRouter.match("/**").check(r -> System.out.println("----啦啦啦----"));

                })
        ).addPathPatterns("/**");
    }

//    @Bean
//    public StpLogic getStpLogicJwt() {
//        return new StpLogicJwtForStateless();
//    }
}
