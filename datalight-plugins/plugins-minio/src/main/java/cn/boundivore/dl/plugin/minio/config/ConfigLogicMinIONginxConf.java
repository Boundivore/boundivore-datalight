package cn.boundivore.dl.plugin.minio.config;


import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;

/**
 * @author: 李煌民
 * @date: 2025-05-22 09:53
 **/
public class ConfigLogicMinIONginxConf extends AbstractConfigLogic {

    public ConfigLogicMinIONginxConf(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        //{{MINIO_SERVER_PORT_LIST}}
        String minioServerPortList = this.serverPortList();

        return replacedTemplated
                .replace(
                        "{{MINIO_SERVER_PORT_LIST}}",
                        minioServerPortList
                );
    }

    private String serverPortList() {
        return super.currentMetaService.getMetaComponentMap()
                .keySet()
                .stream()
                .filter(k -> k.contains("MinIOServer"))
                .map(k -> super.currentMetaService.getMetaComponentMap().get(k))
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .map(metaComponent -> String.format(
                        "server %s:9001;",
                        metaComponent.getHostname()
                ))
                .reduce((s1, s2) -> s1.concat("\n").concat(s2))
                .get();
    }
}
