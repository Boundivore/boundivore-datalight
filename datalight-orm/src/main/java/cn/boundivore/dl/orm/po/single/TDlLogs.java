package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.result.ResultEnum;
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
 * 日志埋点信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-06-11 12:28:15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_logs")
@ApiModel(value = "TDlLogs对象", description = "日志埋点信息表")
public class TDlLogs extends TBasePo<TDlLogs> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志名称")
    @TableField("log_name")
    private String logName;

    @ApiModelProperty("用户 ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("请求发生的时间戳")
    @TableField("timestamp")
    private Long timestamp;

    @ApiModelProperty("格式化日期")
    @TableField("date_format")
    private String dateFormat;

    @ApiModelProperty("日志类型")
    @TableField("log_type")
    private LogTypeEnum logType;

    @ApiModelProperty("请求的类")
    @TableField("class_name")
    private String className;

    @ApiModelProperty("请求的方法")
    @TableField("method_name")
    private String methodName;

    @ApiModelProperty("请求的来源 IP")
    @TableField("ip")
    private String ip;

    @ApiModelProperty("请求的来源 URI")
    @TableField("uri")
    private String uri;

    @ApiModelProperty("业务响应状态码")
    @TableField("result_code")
    private String resultCode;

    @ApiModelProperty("业务响应状态枚举")
    @TableField("result_enum")
    private ResultEnum resultEnum;

    @ApiModelProperty("请求入参")
    @TableField("params")
    private String params;

    @ApiModelProperty("响应体结果")
    @TableField("result")
    private String result;


}
