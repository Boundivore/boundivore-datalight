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
 * 服务信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-09 11:37:19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_service")
@Schema(name = "TDlService对象", description = "服务信息表")
public class TDlService extends TBasePo<TDlService> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "服务名称")
    @TableField("service_name")
    private String serviceName;

    @Schema(name = "服务状态")
    @TableField("service_state")
    private SCStateEnum serviceState;

    @Schema(name = "优先级 数字越小，优先级越高")
    @TableField("priority")
    private Long priority;


}
