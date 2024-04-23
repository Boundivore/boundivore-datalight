package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 告警与处理告警信息关联表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-17 10:56:03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_alert_handler_relation")
@ApiModel(value = "TDlAlertHandlerRelation对象", description = "告警与处理告警信息关联表")
public class TDlAlertHandlerRelation extends TBasePo<TDlAlertHandlerRelation> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("告警 ID")
    @TableField("alert_id")
    private Long alertId;

    @ApiModelProperty("处理信息 ID")
    @TableField("handler_id")
    private Long handlerId;

    @ApiModelProperty("告警处理类型")
    @TableField("handler_type")
    private AlertHandlerTypeEnum handlerType;

}
