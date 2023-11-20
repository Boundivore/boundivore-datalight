package cn.boundivore.dl.orm.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * FUNCTIONAL_DESCRIPTION: 基础 Po，包含每张表的通用属性
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TBasePo<T extends Model<?>> extends Model<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(name = "主键 ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    protected Long id;

    @Schema(name = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected Long createTime;

    @Schema(name = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected Long updateTime;

    @Schema(name = "乐观锁版本")
    @TableField(value = "version", fill = FieldFill.INSERT_UPDATE)
    @Version
    protected Long version;

}
