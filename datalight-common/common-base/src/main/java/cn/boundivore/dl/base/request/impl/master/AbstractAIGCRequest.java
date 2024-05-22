package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.AIGCTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Description: AIGC 相关请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAIGCRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAIGCRequest.SendMessageRequest",
            description = "AbstractAIGCRequest.SendMessageRequest: AIGC 内容生成 请求体"
    )
    public final static class SendMessageRequest implements IRequest {

        private static final long serialVersionUID = -4330420182299623230L;

        @Schema(name = "AIGCType", title = "大模型类型", required = true)
        @JsonProperty(value = "AIGCType", required = true)
        @NotNull(message = "大模型类型不能为空")
        private AIGCTypeEnum aigcType;

        @Schema(name = "Message", title = "询问消息", required = true)
        @JsonProperty(value = "Message", required = true)
        @NotNull(message = "询问消息不能为空")
        private String message;

    }

}
