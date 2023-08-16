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
package cn.boundivore.dl.service.master.resolver;

import cn.boundivore.dl.base.utils.YamlSerializer;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.yaml.YamlDirectory;
import cn.boundivore.dl.service.master.resolver.yaml.YamlNodeAction;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceManifest;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 用于将各类服务的部署配置解析到对应的实体中
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public final class ResolverYamlServiceDetail {

    /**
     * 映射方式：<ServiceName, Service 详细实体>
     */
    public final static Map<String, YamlServiceDetail.Service> SERVICE_MAP = new LinkedHashMap<>();

    /**
     * 映射方式：<ServiceName, List<Component> 实体列表>
     */
    public final static Map<String, List<YamlServiceDetail.Component>> COMPONENT_LIST_MAP = new LinkedHashMap<>();

    /**
     * 映射方式：<ComponentName, Component 实体>
     */
    public final static Map<String, YamlServiceDetail.Component> COMPONENT_MAP = new LinkedHashMap<>();


    /**
     * Description: 将各个服务组件的配置解析到对应实体
     * Created by: Boundivore
     * Creation time: 2023/4/24
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param confPath DataLight 安装目录下的相关配置文件目录
     */
    public static void resolver(String confPath) throws IOException {
        log.info(confPath);

        //TEST
        confPath = SpringContextUtilTest.SERVICE_CONF_LOCAL;

        // 总配置
        YamlServiceManifest.DataLight dataLight = ResolverYamlServiceManifest.SERVICE_MANIFEST_YAML.getDataLight();
        //获取服务组件资源版本
        String dlcVersion = dataLight.getDlcVersion();
        //获取 Manifest Deploy 对象
        YamlServiceManifest.Deploy deploy = dataLight.getDeploy();
        //获取已支持的服务列表
        List<YamlServiceManifest.Service> services = deploy.getServices();

        //准备解析 Yaml
        //根据已支持的服务列表，找到对应的服务配置
        for (YamlServiceManifest.Service mainService : services) {
            File deployConfFile = FileUtil.file(
                    String.format(
                            "%s/%s.yaml",
                            confPath,
                            mainService.getName()
                    )
            );

            //如果服务配置文件不存在，则抛出异常
            Assert.isTrue(
                    deployConfFile.exists(),
                    () -> new FileNotFoundException(
                            String.format(
                                    "服务 %s 配置文件不存在: %s",
                                    mainService,
                                    deployConfFile.getAbsolutePath()
                            )
                    )
            );

            //解析服务配置文件
            YamlServiceDetail yamlServiceDetail = YamlSerializer.toObject(
                    deployConfFile,
                    YamlServiceDetail.class
            );

            //拆分 Yaml 数据
            YamlServiceDetail.Service service = yamlServiceDetail.getDataLight().getService();
            //将配置汇总中的相关信息加入到当前服务详情中
            service.setDlcVersion(dlcVersion);
            service.setType(mainService.getType());
            service.setDesc(mainService.getDesc());
            service.setDependencies(mainService.getDependencies());
            service.setRelatives(mainService.getRelatives());

            //目录配置
            YamlDirectory.Directory directory = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();

            //将各个服务配置文件中的 {{datalight-dir}}、{{service-dir}} 替换为 0-SERVICE-MANIFEST.yaml 中配置的内容
            service.getConfDirs().forEach(i -> {
                String serviceConfDir = i.getServiceConfDir()
                        .replace(
                                "{{service-dir}}",
                                directory.getServiceDir()
                        );

                String templatedDir = i.getTemplatedDir().
                        replace(
                                "{{datalight-dir}}",
                                directory.getDatalightDir()
                        );

                i.setServiceConfDir(serviceConfDir);
                i.setTemplatedDir(templatedDir);
            });

            //将部署概览中的服务优先级添加到部署服务详情的配置中去
            service.setPriority(mainService.getPriority());

            SERVICE_MAP.put(service.getName(), service);
            COMPONENT_LIST_MAP.put(service.getName(), service.getComponents());
            service.getComponents().forEach(i -> COMPONENT_MAP.put(i.getName(), i));
        }

        log.info("-----------------------${SERVICENAME}.yaml---------------------");
        log.info(SERVICE_MAP.toString());
        log.info("---------------------------------------------------------------");
        log.info(COMPONENT_LIST_MAP.toString());
        log.info("---------------------------------------------------------------");
        log.info(COMPONENT_MAP.toString());

        //TODO 需要严谨检查整个配置文件是否合理
        checkConf();

    }

    /**
     * Description: 检查文件合理性，不合理则抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/29
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException
     */
    private static void checkConf() throws BException {
        //当前服务部署优先级 与 服务依赖项中的服务优先级检查，即：不允许出现被依赖服务的优先级低于当前服务
//        SERVICE_GROSS_MAP.forEach((serviceName, mainService) -> {
//            mainService.getDependencies().forEach(dependencyServiceName -> {
//                Assert.isTrue(SERVICE_GROSS_MAP.get(dependencyServiceName).getPriority() < mainService.getPriority(),
//                        () -> new BException(
//                                String.format("被依赖服务的部署优先级应当高于当前服务，当前优先级: %s < %s，应当优先级：%s > %s",
//                                        dependencyServiceName,
//                                        serviceName,
//                                        dependencyServiceName,
//                                        serviceName)
//                        )
//                );
//            });
//        });

//        Assert.equals(
//                mainService.getName(),
//                service.getName(),
//                () -> new IllegalArgumentException("部署配置有误, 服务名不一致")
//        );
    }

    public static void main(String[] args) throws IOException {
        ResolverYamlDirectory.resolver("");

        ResolverYamlServiceManifest.resolver("");

        resolver("");
    }
}
