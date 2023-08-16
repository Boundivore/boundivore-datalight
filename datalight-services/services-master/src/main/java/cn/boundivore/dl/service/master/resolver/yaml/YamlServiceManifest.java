package cn.boundivore.dl.service.master.resolver.yaml;

import cn.boundivore.dl.base.enumeration.impl.ServiceTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class YamlServiceManifest {

    @JsonProperty("datalight")
    private DataLight dataLight;

    @Data
    public static class DataLight {

        @JsonProperty("dlc-version")
        private String dlcVersion;

        @JsonProperty("deploy")
        private Deploy deploy;

    }


    @Data
    public static class Deploy {
        @JsonProperty("services")
        private List<Service> services;
    }

    @Data
    public static class Service {
        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private ServiceTypeEnum type;

        @JsonProperty("priority")
        private long priority;

        @JsonProperty("desc")
        private String desc;

        @JsonProperty("dependencies")
        private List<String> dependencies;

        @JsonProperty("relatives")
        private List<String> relatives;
    }
}
