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
package cn.boundivore.dl.plugin.base.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 抽象公共方法
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public abstract class AbstractConfig implements IConfig {

    protected PluginConfig pluginConfig;

    protected PluginConfig.MetaService currentMetaService;

    protected PluginConfig.MetaComponent currentMetaComponent;

    @Override
    public void init(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;

        this.currentMetaService = pluginConfig.getCurrentMetaService();
        this.currentMetaComponent = pluginConfig.getCurrentMetaComponent();
    }

    @Override
    public PluginConfigResult configSelf() {
        log.info("{} 初始化自身配置", this.currentMetaService.getServiceName());

        PluginConfigResult pluginConfigResult = new PluginConfigResult(
                this.currentMetaService.getPluginClusterMeta().getClusterId(),
                this.currentMetaService.getServiceName(),
                new LinkedHashMap<>()
        );

        this.currentMetaService.getConfDirList().forEach(i -> {

            //服务配置文件路径
            final String serviceConfDirStr = this.trimDir(
                    i.getServiceConfDir()
            );

            //模板配置文件路径
            final String templatedDirStr = this.trimDir(
                    i.getTemplatedDir()
            );

            this.templatedFileList(templatedDirStr)
                    .forEach(templatedFile -> {

                        //结合模板，补充用户提前配置信息，并返回修改后的模板
                        String replacedTemplate = this.preConfig(templatedFile);

                        //得到最终配置文件数据(未 Base64)
                        String configData = this.configLogic(
                                templatedFile,
                                replacedTemplate
                        );

                        //组装 ConfigKey
                        PluginConfigResult.ConfigKey configKey = this.assembleConfigKey(
                                serviceConfDirStr,
                                templatedFile,
                                this.currentMetaComponent
                        );

                        //组装 ConfigValue
                        PluginConfigResult.ConfigValue configValue = this.assembleConfigValue(
                                templatedFile,
                                configData
                        );

                        //存放到 Map 集合
                        this.putConfig(
                                pluginConfigResult,
                                configKey,
                                configValue
                        );

                    });

        });

        return pluginConfigResult;
    }


    /**
     * Description: 获取模板目录下的模板文件列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param templatedDirStr 模板文件目录
     * @return 模板文件列表
     */
    protected List<File> templatedFileList(String templatedDirStr) {
        log.info("展示 {} 服务配置模板路径: {}", this.currentMetaService.getServiceName(), templatedDirStr);
        List<File> templatedFileList = Arrays.stream(
                        Objects.requireNonNull(
                                FileUtil.file(templatedDirStr).listFiles()
                        )
                ).filter(File::isFile)
                .collect(Collectors.toList());

        Assert.notEmpty(
                templatedFileList,
                () -> new RuntimeException(
                        String.format(
                                "配置模板目录下文件列表为空: %s",
                                templatedDirStr
                        )
                )
        );

        return templatedFileList;
    }

    /**
     * Description: 容错处理，去除路径中可能存在的最后一个目录分隔符
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param dir 当前目录 String
     * @return 容错后的目录
     */
    protected String trimDir(String dir) {
        String regex = "[/\\\\]+$";
        return dir.replaceAll(regex, "")
                .replace('\\', '/')
                .trim();
    }

    /**
     * Description: 将指定内容转换为 SHA256 字符串
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param content 原字符串内容
     * @return SHA256 摘要字符串
     */
    protected String sha256(String content) {
        return SecureUtil.sha256(content);
    }

    /**
     * Description: 将指定内容转换为 Base64 字符串
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param content 原字符串内容
     * @return Base64 字符串
     */
    protected String base64(String content) {
        return Base64.encode(content);
    }

    /**
     * Description: 解析 Base64 字符串
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param content Base64 原字符串内容
     * @return 原字符串
     */
    protected String deBase64(String content) {
        return Base64.decodeStr(content);
    }

    /**
     * Description: 替换当前模板中的占位符为用户在页面、部署前 提前配置的值
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param templatedFile 模板文件的 File 对象
     * @return String 处理后的模板文件字符串
     */
    protected String preConfig(File templatedFile) {

        // <模板文件路径(templated-file-path), <{{占位符字串}}(placeholder), 执行部署前用户在页面提前设置的预配置内容>>
        final Map<String, Map<String, String>> configPreMap = this.currentMetaService.getConfigPreMap();

        //读取模板内容
        String replacedTemplate = FileUtil.readUtf8String(templatedFile);

        //根据绝对路径获取当前占位符以及用户预配置内容 Map 集合
        final Map<String, String> placeholderValueMap = configPreMap.get(templatedFile.getAbsolutePath());

        //当前配置文件没有预配置选项，直接返回
        if (placeholderValueMap == null) {
            return replacedTemplate;
        }

        //使用 .replace() 方法替换 replacedTemplate 字符串中的相应占位符
        return placeholderValueMap.entrySet()
                .stream()
                .reduce(replacedTemplate,
                        (content, entry) -> content.replace(
                                entry.getKey(),
                                entry.getValue()
                        ),
                        (a, b) -> b);
    }

    /**
     * Description: 组装当前 ConfigKey
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceConfDirStr 服务配置文件目录路径，String 对象
     *                          (由于可能在 Windows 中调试，因此此处如果传递为 File，则会造成额外处理文件系统标识的麻烦）
     * @param templatedFile     模板文件路径，File 对象
     * @param metaComponent     操作的配置文件的元数据信息
     * @return 组装好的 ConfigKey
     */
    protected PluginConfigResult.ConfigKey assembleConfigKey(String serviceConfDirStr,
                                                             File templatedFile,
                                                             PluginConfig.MetaComponent metaComponent) {
        String fileName = templatedFile.getName();

        return new PluginConfigResult.ConfigKey(
                metaComponent.getNodeId(),
                String.format(
                        "%s/%s",
                        serviceConfDirStr,
                        fileName
                )
        );

    }

    /**
     * Description: 组装当前 ConfigValue
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param templatedFile      模板文件路径，File 对象
     * @param configDataNoBase64 修改后的配置文件（尚未转 Base64）
     * @return 组装好的 ConfigValue
     */
    protected PluginConfigResult.ConfigValue assembleConfigValue(File templatedFile,
                                                                 String configDataNoBase64) {

        String configDataSha256 = this.sha256(configDataNoBase64);
        String configData = this.base64(configDataNoBase64);

        String fileName = templatedFile.getName();

        return new PluginConfigResult.ConfigValue(
                fileName,
                configData,
                configDataSha256
        );
    }

    /**
     * Description: 将当前 ConfigKey ConfigValue 存入 Map 集合
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigResult 模板文件路径，File 对象
     * @param configKey          组装好的 ConfigKey
     * @param configValue        组装好的 ConfigValue
     * @return PluginConfigResult 原 PluginConfigResult 引用
     */
    protected PluginConfigResult putConfig(PluginConfigResult pluginConfigResult,
                                           PluginConfigResult.ConfigKey configKey,
                                           PluginConfigResult.ConfigValue configValue) {

        pluginConfigResult.getConfigMap().put(configKey, configValue);

        return pluginConfigResult;
    }

}
