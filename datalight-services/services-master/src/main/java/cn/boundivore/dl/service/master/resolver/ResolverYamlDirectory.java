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
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.yaml.YamlDirectory;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
public final class ResolverYamlDirectory {

    public static YamlDirectory DIRECTORY_YAML = new YamlDirectory();

    /**
     * Description: 解析目录配置文件
     * Created by: Boundivore
     * Creation time: 2023/4/24
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param confPath 目录配置文件路径
     */
    public static void resolver(String confPath) throws IOException {
        log.info(confPath);
        confPath = SpringContextUtilTest.CONF_ENV_DIR_LOCAL;

        //解析 Yaml
        DIRECTORY_YAML = YamlSerializer.toObject(
                FileUtil.file(
                        String.format(
                                "%s/%s",
                                confPath,
                                "directory.yaml"
                        )
                ),
                YamlDirectory.class
        );

        log.info("-------------------------directory.yaml---------------------------");
        log.info(DIRECTORY_YAML.toString());

        // 初始化 datalight-env.sh
        YamlDirectory.Directory datalight = DIRECTORY_YAML.getDatalight();
        checkConf(DIRECTORY_YAML);
        String datalightEnvStr = FileUtil.readString(
                        FileUtil.file(
                                String.format(
                                        "%s/%s",
                                        confPath,
                                        "datalight-env-templated.sh"
                                )
                        ),
                        CharsetUtil.CHARSET_UTF_8
                )
                .replace("{{DATALIGHT_DIR}}", datalight.getDatalightDir())
                .replace("{{SERVICE_DIR}}", datalight.getServiceDir())
                .replace("{{LOG_DIR}}", datalight.getLogDir())
                .replace("{{PID_DIR}}", datalight.getPidDir())
                .replace("{{DATA_DIR}}", datalight.getDataDir());

        FileUtil.writeString(
                datalightEnvStr,
                FileUtil.file(
                        String.format(
                                "%s/%s",
                                confPath,
                                "datalight-env.sh"
                        )
                ),
                CharsetUtil.CHARSET_UTF_8
        );


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
     * @param directoryYaml 当前解析后的配置文件
     */
    private static void checkConf(YamlDirectory directoryYaml) throws BException {

    }

    public static void main(String[] args) throws IOException {
        resolver("");
    }
}
