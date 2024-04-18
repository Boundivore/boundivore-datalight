package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 告警配置信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-16 04:51:39
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_alert")
@ApiModel(value = "TDlAlert对象", description = "告警配置信息表")
public class TDlAlert extends TBasePo<TDlAlert> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("告警配置名称")
    @TableField("alert_name")
    private String alertName;

    @ApiModelProperty("规则配置文件路径")
    @TableField("alert_file_path")
    private String alertFilePath;

    @ApiModelProperty("规则配置文件名称")
    @TableField("alert_file_name")
    private String alertFileName;

    @ApiModelProperty("规则配置文件内容")
    @TableField("alert_rule_content")
    private String alertRuleContent;

    @ApiModelProperty("是否启用")
    @TableField("enabled")
    private Boolean enabled;

    @ApiModelProperty("告警规则文件版本")
    @TableField("alert_version")
    private Long alertVersion;

    @ApiModelProperty("告警处理类型")
    @TableField("handler_type")
    private AlertHandlerTypeEnum handlerType;


}
