package cn.boundivore.dl.service.master.resolver.yaml;

import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStepTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class YamlNodeAction {

    @JsonProperty("datalight")
    private DataLight dataLight;

    @Data
    public static class DataLight {
        @JsonProperty("actions")
        private List<Action> actions;
    }

    @Data
    public static class Action {

        @JsonProperty("type")
        private NodeActionTypeEnum type;

        @JsonProperty("start-state")
        private NodeStateEnum startState;

        @JsonProperty("success-state")
        private NodeStateEnum successState;

        @JsonProperty("fail-state")
        private NodeStateEnum failState;

        @JsonProperty("steps")
        private List<Step> steps;
    }

    @Data
    public static class Step {

        @JsonProperty("type")
        private NodeStepTypeEnum type;

        @JsonProperty("name")
        private String name;

        @JsonProperty("shell")
        private String shell;

        @JsonProperty("args")
        private List<String> args;

        @JsonProperty("interactions")
        private List<String> interactions;

        @JsonProperty("exits")
        private int exits;

        @JsonProperty("timeout")
        private long timeout;

        @JsonProperty("sleep")
        private long sleep;
    }
}
