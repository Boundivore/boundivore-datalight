package cn.boundivore.dl.base.enumeration.impl;

/**
 * @author: 李煌民
 * @date: 2026-05-20 09:45
 *
 * 服务监控状态枚举
 **/
public enum ServiceMonitorStateEnum {
    /**
     * 全部组件正常
     */
    GREEN("GREEN", "全部正常"),

    /**
     * 部分组件异常
     */
    YELLOW("YELLOW", "部分异常"),

    /**
     * 全部组件失败
     */
    RED("RED", "全部失败"),

    /**
     * 状态未知
     */
    UNKNOWN("UNKNOWN", "未知");

    private final String code;
    private final String message;

    ServiceMonitorStateEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
