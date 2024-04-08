package cn.boundivore.dl.base.enumeration.impl;


import cn.boundivore.dl.base.enumeration.IBaseEnum;

/**
 * Description: 权限类型枚举
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/8
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
public enum PermissionTypeEnum implements IBaseEnum {

    PERMISSION_INTERFACE("0", "接口操作权限"),
    PERMISSION_DATA_ROW("1", "数据行读权限"),
    PERMISSION_DATA_COLUMN("2", "数据列读权限"),
    PERMISSION_PAGE("3", "页面操作权限");

    private final String code;
    private final String message;

    PermissionTypeEnum(String code, String message) {
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