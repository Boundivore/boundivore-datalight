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

import cn.boundivore.dl.base.utils.YamlDeserializer;
import cn.boundivore.dl.base.utils.YamlSerializer;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.yaml.YamlPrometheusRules;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Description: 用于将各类服务的部署配置解析到对应的实体中
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public final class ResolverYamlPrometheusRules {

    /**
     * Description: 解析 Prometheus 规则配置文件
     * Created by: Boundivore
     * Creation time: 2024/4/24
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param confPath 规则配置文件路径
     */
    public static YamlPrometheusRules resolve(String confPath, String fileName) throws IOException {
        log.info("Config Path: {}", confPath);

        // 解析 YAML
        YamlPrometheusRules rulesYaml = YamlSerializer.toObject(
                new File(
                        String.format(
                                "%s/%s",
                                confPath,
                                fileName
                        )
                ),
                YamlPrometheusRules.class
        );

        log.info("-------------------------YamlPrometheusRules---------------------------");
        log.info(rulesYaml.toString());

        // 这里可以添加更多的处理逻辑，如校验规则的有效性等
        checkRules(rulesYaml);

        return rulesYaml;
    }

    /**
     * Description: 检查规则的合理性，不合理则抛出异常
     * Created by: Boundivore
     * Creation time: 2024/4/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param rulesYaml 当前解析后的规则配置
     */
    private static void checkRules(YamlPrometheusRules rulesYaml) {
        // 进行规则检查
        if (rulesYaml.getGroups().isEmpty()) {
            throw new BException("未找到规则组");
        }
    }

    /**
     * Description: 将 PrometheusRules 对象序列化为 YAML 字符串
     * Created by: Boundivore
     * Creation time: 2024/4/24
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param rules PrometheusRules 对象
     * @return 序列化后的 YAML 字符串
     * @throws IOException 如果序列化过程中发生错误
     */
    public static String serializeYamlPrometheusRules(YamlPrometheusRules rules) throws IOException {
        return YamlDeserializer.toString(rules);
    }

    public static void main(String[] args) throws IOException {
        YamlPrometheusRules deserialize = ResolverYamlPrometheusRules.resolve(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\templated\\prometheus\\rules",
                "RULE-YARN-NodeManager.yml"
        );

        System.out.println(deserialize);
        String serialize = ResolverYamlPrometheusRules.serializeYamlPrometheusRules(deserialize);
        System.out.println(serialize);

    }
}
