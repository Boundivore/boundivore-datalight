package cn.boundivore.dl.plugin.minio.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

/**
 * @author: 李煌民
 * @date: 2025-05-22 10:15
 **/
public class ConfigLogicNginxStartSh extends AbstractConfigLogic {
    public ConfigLogicNginxStartSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        return replacedTemplated
                .replace(
                        "{{LOG_DIR}}",
                        String.format(
                                "%s/%s",
                                super.logDir(),
                                ConfigMINIO.SERVICE_NAME
                        )
                );
    }
}
