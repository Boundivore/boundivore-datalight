package cn.boundivore.dl.orm.po.single;

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
 * 配置信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-07-31 10:51:47
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_config")
@ApiModel(value = "TDlConfig对象", description = "配置信息表")
public class TDlConfig extends TBasePo<TDlConfig> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("节点 ID")
    @TableField("node_id")
    private Long nodeId;

    @ApiModelProperty("服务名称")
    @TableField("service_name")
    private String serviceName;

    @ApiModelProperty("配置文件 ID")
    @TableField("config_content_id")
    private Long configContentId;

    @ApiModelProperty("配置文件名称")
    @TableField("filename")
    private String filename;

    @ApiModelProperty("配置文件路径")
    @TableField("config_path")
    private String configPath;

    @ApiModelProperty("当前版本号")
    @TableField("config_version")
    private Long configVersion;


}
