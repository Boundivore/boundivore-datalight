package cn.boundivore.dl.service.master.resolver.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class YamlDirectory {

    @JsonProperty(value = "datalight", required = true)
    private Directory datalight;

    @Data
    public static class Directory {
        @JsonProperty("java-home")
        private String javaHome;

        @JsonProperty("datalight-dir")
        private String datalightDir;

        @JsonProperty("service-dir")
        private String serviceDir;

        @JsonProperty("log-dir")
        private String logDir;

        @JsonProperty("pid-dir")
        private String pidDir;

        @JsonProperty("data-dir")
        private String dataDir;
    }
}
