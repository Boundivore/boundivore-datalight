package cn.boundivore.dl.base.request.impl.master;


import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: AbstractRuleDataColumnRequest
 * Created by: Boundivore
 * Creation time: 2024/9/4
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(
        name = "AbstractRuleDataColumnRequest",
        description = "AbstractRuleDataColumnRequest 数据列权限 请求体"
)
public abstract class AbstractRuleDataColumnRequest implements IRequest {
    private static final long serialVersionUID = 5607481998093989650L;

    //Key: <clazz extends TBasePo.class>
    //Value: ColumnNameList
    @JsonIgnore
    protected Map<Class<?>, List<String>> tableColumnNameListMap = new HashMap<>();

}
