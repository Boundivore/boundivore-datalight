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
 * 处理告警邮箱配置信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-17 10:56:03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_alert_handler_mail")
@ApiModel(value = "TDlAlertHandlerMail对象", description = "处理告警邮箱配置信息表")
public class TDlAlertHandlerMail extends TBasePo<TDlAlertHandlerMail> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("收件人地址")
    @TableField("mail_account")
    private String mailAccount;


}
