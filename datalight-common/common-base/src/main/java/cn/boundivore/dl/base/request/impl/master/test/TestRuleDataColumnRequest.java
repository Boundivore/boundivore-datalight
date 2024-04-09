package cn.boundivore.dl.base.request.impl.master.test;

import cn.boundivore.dl.base.request.impl.master.AbstractRuleDataColumnRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "TestRuleDataColumnRequest",
        description = "TestRuleDataColumnRequest 测试数据列权限 请求体"
)
public class TestRuleDataColumnRequest extends AbstractRuleDataColumnRequest {

    private static final long serialVersionUID = -6735595334993932951L;

    @ApiModelProperty(name = "TestComment", value = "测试说明", required = true)
    @JsonProperty(value = "TestComment", required = true)
    private String testComment;
}
