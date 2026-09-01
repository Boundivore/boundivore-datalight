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
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Description: 用于将目录配置解析到对应的实体中
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
                .replace("{{JAVA_HOME}}", datalight.getJavaHome())
                .replace("{{DATALIGHT_JAVA_HOME}}", datalight.getDatalightJavaHome())
                .replace("{{DATALIGHT_DIR}}", datalight.getDatalightDir())
                .replace("{{SERVICE_DIR}}", datalight.getServiceDir())
                .replace("{{LOG_DIR}}", datalight.getLogDir())
                .replace("{{PID_DIR}}", datalight.getPidDir())
                .replace("{{DATA_DIR}}", datalight.getDataDir())
                // ai 节点为可选配置，缺失时按未启用处理
                .replace("{{AI_ENABLED}}", aiEnabled(datalight))
                .replace("{{AI_HOME}}", aiValue(datalight == null || datalight.getAi() == null ? null : datalight.getAi().getHome()))
                .replace("{{AI_PORT}}", aiPort(datalight))
                .replace("{{AI_UV_BIN}}", aiValue(datalight == null || datalight.getAi() == null ? null : datalight.getAi().getUvBin()));

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
        YamlDirectory.Directory datalight = directoryYaml.getDatalight();
        Assert.notNull(
                datalight,
                () -> new BException("directory.yaml 缺少 datalight 配置节")
        );

        // 逐项校验，缺项直接在启动阶段拦下，不要等到部署时才暴露
        checkNotBlank(datalight.getJavaHome(), "java-home");
        checkNotBlank(datalight.getDatalightJavaHome(), "datalight-java-home");
        checkNotBlank(datalight.getDatalightDir(), "datalight-dir");
        checkNotBlank(datalight.getServiceDir(), "service-dir");
        checkNotBlank(datalight.getLogDir(), "log-dir");
        checkNotBlank(datalight.getPidDir(), "pid-dir");
        checkNotBlank(datalight.getDataDir(), "data-dir");
    }

    /**
     * Description: 取 AI 服务的启用开关。未配置视为未启用
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param datalight 目录配置
     * @return String true 或 false
     */
    private static String aiEnabled(YamlDirectory.Directory datalight) {
        if (datalight == null || datalight.getAi() == null || datalight.getAi().getEnabled() == null) {
            return "false";
        }
        return String.valueOf(datalight.getAi().getEnabled());
    }

    /**
     * Description: 取 AI 服务端口。未配置时给默认值
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param datalight 目录配置
     * @return String 端口号
     */
    private static String aiPort(YamlDirectory.Directory datalight) {
        if (datalight == null || datalight.getAi() == null || datalight.getAi().getPort() == null) {
            return "8010";
        }
        return String.valueOf(datalight.getAi().getPort());
    }

    /**
     * Description: 取可选字符串配置，null 统一转成空串，避免占位符替换出 "null"
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param value 原始值
     * @return String 非空字符串
     */
    private static String aiValue(String value) {
        return value == null ? "" : value;
    }

    /**
     * Description: 校验配置项非空，且必须是绝对路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException
     *
     * @param value 配置项的值
     * @param key   配置项名称，用于异常提示
     */
    private static void checkNotBlank(String value, String key) throws BException {
        Assert.notBlank(
                value,
                () -> new BException(String.format("directory.yaml 中 %s 不能为空", key))
        );
        Assert.isTrue(
                value.startsWith("/"),
                () -> new BException(String.format("directory.yaml 中 %s 必须是绝对路径: %s", key, value))
        );
    }

    public static void main(String[] args) throws IOException {
        ResolverYamlDirectory.resolver( SpringContextUtilTest.CONF_ENV_DIR_LOCAL);
    }
}
