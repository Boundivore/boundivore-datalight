/**
 * Copyright (C) <2023> <Boundivore> <boundivore@foxmail.com>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Apache License, Version 2.0
 * as published by the Apache Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License, Version 2.0 for more details.
 * <p>
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program; if not, you can obtain a copy at
 * http://www.apache.org/licenses/LICENSE-2.0.
 */
package cn.boundivore.dl.base.enumeration.impl;


import cn.boundivore.dl.base.enumeration.IBaseEnum;

/**
 * Description: NodeStateEnum
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public enum NodeStateEnum implements IBaseEnum {

    RESOLVED("0", "主机名已解析"),

    DETECTING("1", "探测中"),
    ACTIVE("2", "活跃的节点"),
    INACTIVE("3", "不活跃的节点"),

    UNSELECTED("4", "未选择"),
    SELECTED("5", "已选择"),

    CHECKING("6", "节点正在检查"),
    CHECK_OK("7", "节点检查成功"),
    CHECK_ERROR("8", "节点检查失败"),

    PUSHING("9", "节点正在推送"),
    PUSH_OK("10", "节点推送成功"),
    PUSH_ERROR("11", "节点推送失败"),

    MAINTENANCE("12", "维护中"),
    MAINTENANCE_ADD("13", "维护-新增"),
    MAINTENANCE_ALTER("14", "维护-变更"),

    STOPPING("15", "正在关机"),
    STARTED("16", "运行中"),
    STARTING("17", "正在开机"),
    STOPPED("18", "已关机"),
    RESTARTING("19", "正在重启"),
    REMOVED("20", "已移除");

    private final String code;
    private final String message;

    NodeStateEnum(String code, String message) {
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
