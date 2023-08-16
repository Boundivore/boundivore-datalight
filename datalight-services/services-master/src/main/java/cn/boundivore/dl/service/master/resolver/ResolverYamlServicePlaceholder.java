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
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceManifest;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServicePlaceholder;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 解析服务预配置占位信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public final class ResolverYamlServicePlaceholder {

    /**
     * 映射方式：<ServiceName, Placeholder 实体>
     */
    public final static Map<String, YamlServicePlaceholder.Service> PLACEHOLDER_MAP = new LinkedHashMap<>();

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
        confPath = SpringContextUtilTest.PLUGINS_PATH_DIR_LOCAL;

        // 总配置
        YamlServiceManifest.DataLight dataLight = ResolverYamlServiceManifest.SERVICE_MANIFEST_YAML.getDataLight();
        //获取服务组件资源版本
        String dlcVersion = dataLight.getDlcVersion();
        //获取 Manifest Deploy 对象
        YamlServiceManifest.Deploy deploy = dataLight.getDeploy();
        //获取已支持的服务列表
        List<YamlServiceManifest.Service> services = deploy.getServices();

        //目录配置
        YamlDirectory.Directory directory = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();

        //准备解析 Yaml
        //根据已支持的服务列表，找到对应的服务配置
        for (YamlServiceManifest.Service mainService : services) {
            //解析服务组件的预配置文件内容
            File placeHolderConfFile = FileUtil.file(
                    String.format(
                            "%s/%s/placeholder/%s-PLACEHOLDER.yaml",
                            confPath,
                            mainService.getName(),
                            mainService.getName()
                    )
            );

            if (placeHolderConfFile.exists()) {
                YamlServicePlaceholder servicePlaceholderYaml =  YamlSerializer.toObject(
                        placeHolderConfFile,
                        YamlServicePlaceholder.class
                );

                YamlServicePlaceholder.Service placeholderService = servicePlaceholderYaml.getDataLight().getService();

                //替换各个预配置文件中的 {{datalight-dir}}
                List<YamlServicePlaceholder.PlaceholderInfo> placeholderInfos = placeholderService.getPlaceholderInfos();
                placeholderInfos.forEach(i -> {
                    String templatedFilePath = i.getTemplatedFilePath()
                            .replace(
                                    "{{datalight-dir}}",
                                    directory.getDatalightDir()
                            );
                    i.setTemplatedFilePath(templatedFilePath);
                });


                PLACEHOLDER_MAP.put(mainService.getName(), placeholderService);
            } else {
                log.info("该服务不存在预配置: {}", mainService.getName());
            }
        }

        log.info("------------------${SERVICENAME-PLACEHOLDER}.yaml--------------");
        log.info(PLACEHOLDER_MAP.toString());
        log.info("---------------------------------------------------------------");

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

    }

    public static void main(String[] args) throws IOException {
        ResolverYamlDirectory.resolver("");

        ResolverYamlServiceManifest.resolver("");

        ResolverYamlServiceDetail.resolver("");

        resolver("");
    }
}
