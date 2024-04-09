package cn.boundivore.dl.base.request.impl.master.test;

import cn.boundivore.dl.base.request.impl.master.AbstractRuleDataRowRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
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
        name = "TestRuleDataRowRequest",
        description = "TestRuleDataRowRequest 测试数据行权限 请求体"
)
public class TestRuleDataRowRequest extends AbstractRuleDataRowRequest {

    private static final long serialVersionUID = -4482913261936795286L;

    @ApiModelProperty(name = "TestComment", value = "测试说明", required = true)
    @JsonProperty(value = "TestComment", required = true)
    private String testComment;
}
