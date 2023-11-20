package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 初始化步骤缓存信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-28 03:26:07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_init_procedure")
@Schema(name = "TDlInitProcedure对象", description = "初始化步骤缓存信息表")
public class TDlInitProcedure extends TBasePo<TDlInitProcedure> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "本次进度唯一编码")
    @TableField("tag")
    private String tag;

    @Schema(name = "进度名称")
    @TableField("procedure_name")
    private String procedureName;

    @Schema(name = "进度状态 枚举，具体见代码")
    @TableField("procedure_state")
    private String procedureState;


}
