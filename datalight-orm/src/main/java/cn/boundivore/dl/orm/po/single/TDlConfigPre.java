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
 * 服务组件预配置信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-19 11:53:54
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_config_pre")
@ApiModel(value = "TDlConfigPre对象", description = "服务组件预配置信息表")
public class TDlConfigPre extends TBasePo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("服务名称")
    @TableField("service_name")
    private String serviceName;

    @ApiModelProperty("配置文件占位符 templated 中各类被{{}}包括的占位项")
    @TableField("placeholder")
    private String placeholder;

    @ApiModelProperty("占位符修改后的值")
    @TableField("value")
    private String value;

    @ApiModelProperty("占位符的默认值")
    @TableField("default_value")
    private String defaultValue;

    @ApiModelProperty("配置模板文件路径 绝对路径")
    @TableField("templated_config_path")
    private String templatedConfigPath;


}
