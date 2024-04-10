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

import cn.boundivore.dl.service.master.bean.PermissionTemplated;
import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.exceptions.ExceptionUtil;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 扫描 Swagger 注解，解析权限项，返回权限详情等
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/10
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterPermissionHandlerService implements StpInterface {

    // 定义包名和类名通配符
    private final static String CLASS = "*.class";
    private final static String PACKAGE = "cn.boundivore.dl.api.master.define.";

    // <PermissionCode, PermissionTemplated>
    private final Map<String, PermissionTemplated> permissionTemplatedMap = new HashMap<>();

    @PostConstruct
    public void init() {
        scanApiAnnotation();
    }

    /**
     * Description: 扫描 Swagger ApiOperation 注解，并解析所有接口用于权限配置，
     * 其中该注解中 nickname 属性为当前接口 URI 的唯一权限标识
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void scanApiAnnotation() {
        // 创建一个资源解析器来扫描类路径
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        // 转换包名为类路径的通配符形式
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(PACKAGE) + CLASS;

        try {
            // 获取所有匹配的资源
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            // 创建一个 MetadataReaderFactory 用于读取类的元数据
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

            for (Resource resource : resources) {
                // 创建MetadataReader来读取每个资源的信息
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                // 获取类的全名
                String className = reader.getClassMetadata().getClassName();
                // 反射加载类
                Class<?> clazz = Class.forName(className);

                // 获取类上FeignClient注解
                FeignClient feignClient = clazz.getAnnotation(FeignClient.class);
                // FeignClient注解的path属性值
                String feignClientPath = feignClient != null ? normalizePath(feignClient.path()) : "";

                // 获取并遍历类中的所有方法
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(ApiOperation.class)) {
                        MappingBean mappingBean = extractHttpMethodPath(method);

                        String relativePath = mappingBean.getHttpPath() != null ? mappingBean.getHttpPath() : "";
                        String interfacePath = feignClientPath + relativePath;
                        String httpMethod = mappingBean.getHttpMethod();
                        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);

//                        log.info("Path: {}, Class: {}, Method: {}, Operation: {}, Notes: {}, Nickname: {}",
//                                fullPath,
//                                classname,
//                                method.getName(),
//                                apiOperation.value(),
//                                apiOperation.notes(),
//                                apiOperation.nickname()
//                        );
                        String permissionCode = this.getPermissionCode(clazz.getSimpleName(), method.getName());
                        String permissionName = apiOperation.value();
                        log.info("Path: {}, PermissionCode: {}, PermissionName: {}, HttpMethod: {}",
                                interfacePath,
                                permissionCode,
                                permissionName,
                                httpMethod

                        );

                        if (!method.getName().contains("login")) {
                            this.permissionTemplatedMap.put(
                                    permissionCode,
                                    new PermissionTemplated(
                                            interfacePath,
                                            permissionCode,
                                            permissionName,
                                            cn.hutool.http.Method.valueOf(httpMethod)
                                    )
                            );
                        }

                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }

    /**
     * Description: 通过简化类名（不包含报名）+ "." + 方法名作为权限唯一编码
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param classSimpleName 简化类名
     * @param methodName      方法名
     * @return String 权限编码
     */
    private String getPermissionCode(String classSimpleName, String methodName) {
        return String.format(
                "%s.%s",
                classSimpleName,
                methodName
        );
    }

    /**
     * Description: 合理化 uri 中的 path 路径格式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param path 路径格式
     * @return String 优化后的 path 路径格式
     */
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }

        String normalizedPath = path;

        // 如果路径不是以 "/" 开始的，添加一个 "/"
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }

        // 如果路径除根路径外以 "/" 结尾的，移除末尾的 "/"
        if (normalizedPath.length() > 1 && normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }

        return normalizedPath;
    }

    /**
     * Description: 获取 GetMapping 或 PostMapping 中的 value 值并包含具体 HttpMethod
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param method 当前被 ApiOperation 注解标注的方法
     * @return 返回 GetMapping 或 PostMapping 中的 value 值
     */
    private MappingBean extractHttpMethodPath(Method method) {
        // 获取方法上的 GetMapping 或 PostMapping 注解
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);

        if (getMapping != null && getMapping.value().length > 0) {

            return new MappingBean("GET", getMapping.value()[0]);
        } else if (postMapping != null && postMapping.value().length > 0) {
            return new MappingBean("POST", postMapping.value()[0]);
        }

        return null;
    }

    /**
     * Description: GET POST 信息封装静态内部类
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Data
    @AllArgsConstructor
    private static class MappingBean {
        private String httpMethod;
        private String httpPath;
    }

    /**
     * Description: 获取当前登录用户的权限内容列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param loginId   账号 ID
     * @param loginType 账号类型
     * @return List<String> 权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissionList = new ArrayList<>();
        permissionList.add("IMasterPermissionAPI.testPermissionInterface");
        log.info("调用了获取权限方法: {}", permissionList);
        return permissionList;
    }

    /**
     * Description: 获取当前登录用户的角色内容列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param loginId   账号 ID
     * @param loginType 账号类型
     * @return List<String>角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roleList = new ArrayList<>();
        roleList.add("ADMIN");
        log.info("调用了获取角色方法: {}", roleList);
        return roleList;
    }


}
