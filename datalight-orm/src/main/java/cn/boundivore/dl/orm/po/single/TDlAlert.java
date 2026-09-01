package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "TDlAlert对象", description = "告警配置信息表")
public class TDlAlert extends TBasePo<TDlAlert> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(description = "告警配置名称")
    @TableField("alert_name")
    private String alertName;

    @Schema(description = "规则配置文件路径")
    @TableField("alert_file_path")
    private String alertFilePath;

    @Schema(description = "规则配置文件名称")
    @TableField("alert_file_name")
    private String alertFileName;

    @Schema(description = "规则配置文件内容")
    @TableField("alert_rule_content")
    private String alertRuleContent;

    @Schema(description = "是否启用")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "告警规则文件版本")
    @TableField("alert_version")
    private Long alertVersion;
}
