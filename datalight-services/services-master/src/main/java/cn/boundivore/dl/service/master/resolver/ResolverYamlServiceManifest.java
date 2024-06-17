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
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceManifest;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
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
public final class ResolverYamlServiceManifest {

    public static YamlServiceManifest SERVICE_MANIFEST_YAML = new YamlServiceManifest();

    /**
     * 映射方式：<ServiceName, Service 概览实体>
     */
    public final static Map<String, YamlServiceManifest.Service> MANIFEST_SERVICE_MAP = new LinkedHashMap<>();


    /**
     * Description: 解析服务配置文件
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

        //解析 Yaml
        SERVICE_MANIFEST_YAML = YamlSerializer.toObject(
                FileUtil.file(
                        String.format(
                                "%s/%s",
                                confPath,
                                "0-SERVICE-MANIFEST.yaml"
                        )
                ),
                YamlServiceManifest.class
        );

        log.info("-------------------------0-SERVICE-MANIFEST.yaml---------------------------");
        log.info(SERVICE_MANIFEST_YAML.toString());

        //存放于 Map 集合，方便读取
        SERVICE_MANIFEST_YAML.getDataLight()
                .getDeploy()
                .getServices()
                .forEach(service -> MANIFEST_SERVICE_MAP.put(service.getName(), service));


        //检查配置文件合理性
        checkConf(SERVICE_MANIFEST_YAML);

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
     *
     * @param yamlServiceManifestOld 当前解析后的配置文件
     */
    private static void checkConf(YamlServiceManifest yamlServiceManifestOld) throws BException {

    }

    public static void main(String[] args) throws IOException {
        resolver("");
    }
}
