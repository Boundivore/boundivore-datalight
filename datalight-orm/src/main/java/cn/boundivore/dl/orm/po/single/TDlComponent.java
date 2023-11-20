package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 组件信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-09 11:37:19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_component")
@Schema(name = "TDlComponent对象", description = "组件信息表")
public class TDlComponent extends TBasePo<TDlComponent> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "节点 ID")
    @TableField("node_id")
    private Long nodeId;

    @Schema(name = "服务名称")
    @TableField("service_name")
    private String serviceName;

    @Schema(name = "组件名称")
    @TableField("component_name")
    private String componentName;

    @Schema(name = "组件状态")
    @TableField("component_state")
    private SCStateEnum componentState;

    @Schema(name = "优先级 数字越小，优先级越高")
    @TableField("priority")
    private Long priority;

    @Schema(name = "是否需要重启，如修改配置文件等")
    @TableField("need_restart")
    private Boolean need_restart;

}
