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
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceWebUI;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 加载并解析服务下各组件 Web UI 配置信息到实体类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public final class ResolverYamlComponentWebUI {

    public static YamlServiceWebUI SERVICE_WEB_UI_YAML = new YamlServiceWebUI();

    // <ServiceName, List<ComponentUI>>
    public static HashMap<String, List<YamlServiceWebUI.Component>> WEB_UI_MAP = new HashMap<>();

    /**
     * Description: 解析 YAML 配置
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
        SERVICE_WEB_UI_YAML = YamlSerializer.toObject(
                FileUtil.file(
                        String.format(
                                "%s/%s",
                                confPath,
                                "SERVICE-WEB-UI.yaml"
                        )
                ),
                YamlServiceWebUI.class
        );

        log.info("-------------------------COMPONENT-WEB-UI.yaml---------------------------");
        log.info(SERVICE_WEB_UI_YAML.toString());

        // 初始化 datalight-env.sh
        YamlServiceWebUI.DataLight datalight = SERVICE_WEB_UI_YAML.getDatalight();

        datalight.getServices().forEach(component -> {
                    List<YamlServiceWebUI.Component> componentList = WEB_UI_MAP.getOrDefault(
                            component.getService(),
                            new ArrayList<>()
                    );

                    componentList.add(component);
                    WEB_UI_MAP.put(component.getService(), componentList);
                }
        );

    }


    public static void main(String[] args) throws IOException {
        ResolverYamlComponentWebUI.resolver("D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\conf\\web\\");
        System.out.println(WEB_UI_MAP.get("HDFS"));
    }
}
