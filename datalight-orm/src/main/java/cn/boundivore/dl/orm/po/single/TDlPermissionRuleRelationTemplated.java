package cn.boundivore.dl.orm.po.single;

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
 * 接口权限与规则映射常量模板表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_permission_rule_relation_templated")
@ApiModel(value = "TDlPermissionRuleRelationTemplated对象", description = "接口权限与规则映射常量模板表")
public class TDlPermissionRuleRelationTemplated extends TBasePo<TDlPermissionRuleRelationTemplated> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("权限 ID")
    @TableField("permission_id")
    private Long permissionId;

    @ApiModelProperty("接口规则 ID")
    @TableField("rule_interface_id")
    private Long ruleInterfaceId;

    @ApiModelProperty("页面规则 ID")
    @TableField("rule_page_id")
    private Long rulePageId;


}
