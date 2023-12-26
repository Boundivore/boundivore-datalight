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
import cn.boundivore.dl.service.master.resolver.yaml.YamlDirectory;
import cn.boundivore.dl.service.master.resolver.yaml.YamlNodeAction;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Description: 解析节点初始化的任务配置文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public final class ResolverYamlNode {

    public static YamlNodeAction NODE_INIT_YAML = new YamlNodeAction();


    /**
     * Description: 解析 init-check-node.yaml 配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param confPath 配置文件路径
     */
    public static void resolver(String confPath) throws IOException {
        log.info(confPath);

        //目录配置
        YamlDirectory.Directory directory = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();

        NODE_INIT_YAML = YamlSerializer.toObject(
                FileUtil.file(
                        String.format(
                                "%s/%s",
                                confPath,
                                "node-action.yaml"
                        )
                ),
                YamlNodeAction.class
        );

        log.info("-------------------------node-action.yaml---------------------------");
        log.info(NODE_INIT_YAML.toString());
    }

    public static void main(String[] args) throws IOException {
        ResolverYamlDirectory.resolver( SpringContextUtilTest.CONF_ENV_DIR_LOCAL);

        ResolverYamlServiceManifest.resolver(SpringContextUtilTest.CONF_SERVICE_DIR);

        ResolverYamlServiceDetail.resolver(SpringContextUtilTest.CONF_SERVICE_DIR);

        ResolverYamlNode.resolver(SpringContextUtilTest.NODE_CONF_DIR_LOCAL);
    }

}
