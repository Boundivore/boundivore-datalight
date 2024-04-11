package cn.boundivore.dl.base.enumeration.impl;


import cn.boundivore.dl.base.enumeration.IBaseEnum;

/**
 * Description: 角色类型枚举：静态角色任何用户不可编辑，动态角色通过 edit_enable 字段来区分是否可编辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/8
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
public enum RoleTypeEnum implements IBaseEnum {

    ROLE_STATIC("0", "静态角色"),
    ROLE_DYNAMIC("1", "动态角色");

    private final String code;
    private final String message;

    RoleTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}