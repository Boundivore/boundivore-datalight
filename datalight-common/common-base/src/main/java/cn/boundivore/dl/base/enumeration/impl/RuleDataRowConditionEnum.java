package cn.boundivore.dl.base.enumeration.impl;


import cn.boundivore.dl.base.enumeration.IBaseEnum;

/**
 * Description: RuleDataRowConditionEnum
 * Created by: Boundivore
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public enum RuleDataRowConditionEnum implements IBaseEnum {

    EQ("=", "等于"),
    NE("!=", "不等于"),
    GT(">", "大于"),
    GE(">=", "大于等于"),
    LT("<", "小于"),
    LE("<=", "小于等于");

    private final String code;
    private final String message;

    RuleDataRowConditionEnum(String code, String message) {
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