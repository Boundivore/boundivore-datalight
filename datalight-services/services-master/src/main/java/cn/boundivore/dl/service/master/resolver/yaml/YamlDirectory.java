package cn.boundivore.dl.service.master.resolver.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class YamlDirectory {

    @JsonProperty(value = "datalight", required = true)
    private Directory datalight;

    @Data
    public static class Directory {
        /**
         * 大数据服务使用的 JDK，各服务对版本有兼容性要求，与平台自身的 JDK 相互独立
         */
        @JsonProperty("java-home")
        private String javaHome;

        /**
         * DataLight Master / Worker 自身使用的 JDK
         */
        @JsonProperty("datalight-java-home")
        private String datalightJavaHome;

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

        /**
         * AIAgent 的接入配置。它与 Master、Worker 平级，由开关控制随平台启停，
         * 不是被平台部署的大数据服务，因此不进 DLC 包。
         */
        @JsonProperty("ai")
        private Ai ai;
    }

    @Data
    public static class Ai {
        /**
         * 是否随平台启动。默认关闭，不装 Python 也不影响 DataLight 正常运行
         */
        @JsonProperty("enabled")
        private Boolean enabled = false;

        /**
         * AIAgent 仓库（datalight-services-ai）在节点上的路径，需提前执行过 uv sync
         */
        @JsonProperty("home")
        private String home;

        @JsonProperty("port")
        private Integer port = 8010;

        /**
         * uv 可执行文件路径。留空则从 PATH 里找
         */
        @JsonProperty("uv-bin")
        private String uvBin;
    }
}
