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

    UNKNOWN("-1", "未知状态"),
    RESOLVED("0", "主机名已解析"),

    DETECTING("1", "探测中"),
    INACTIVE("2", "不活跃的节点"),
    ACTIVE("3", "活跃的节点"),

    CHECKING("4", "节点正在检查"),
    CHECK_ERROR("5", "节点检查失败"),
    CHECK_OK("6", "节点检查成功"),

    PUSHING("7", "节点正在推送"),
    PUSH_ERROR("8", "节点推送失败"),
    PUSH_OK("9", "节点推送成功"),

    STARTING_WORKER("10", "正在启动 Worker"),
    START_WORKER_ERROR("11", "启动 Worker 失败"),
    START_WORKER_OK("12", "启动 Worker 成功"),


    MAINTENANCE("13", "维护中"),
    MAINTENANCE_ADD("14", "维护-新增"),
    MAINTENANCE_ALTER("15", "维护-变更"),

    STOPPING("16", "正在关机"),
    STARTED("17", "运行中"),
    STARTING("18", "正在开机"),
    STOPPED("19", "已关机"),
    RESTARTING("20", "正在重启"),
    REMOVED("21", "已移除");

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

    public NodeStateEnum getByCode(String code) {
        for (NodeStateEnum state : NodeStateEnum.values()) {
            if (state.getCode().equals(code)) {
                return state;
            }
        }
        return null;
    }

    public NodeStateEnum pre() {
        NodeStateEnum[] states = NodeStateEnum.values();
        int ordinal = this.ordinal();
        return ordinal > 0 ? states[ordinal - 1] : null;
    }

    public NodeStateEnum next() {
        NodeStateEnum[] states = NodeStateEnum.values();
        int ordinal = this.ordinal();
        return ordinal < states.length - 1 ? states[ordinal + 1] : null;
    }

    public boolean isLessThanOrEqualTo(NodeStateEnum otherState) {
        return this.ordinal() <= otherState.ordinal();
    }

    public boolean isLessThan(NodeStateEnum otherState) {
        return this.ordinal() < otherState.ordinal();
    }

    public boolean isGreaterThanOrEqualTo(NodeStateEnum otherState) {
        return this.ordinal() >= otherState.ordinal();
    }

    public boolean isGreaterThan(NodeStateEnum otherState) {
        return this.ordinal() > otherState.ordinal();
    }
}
