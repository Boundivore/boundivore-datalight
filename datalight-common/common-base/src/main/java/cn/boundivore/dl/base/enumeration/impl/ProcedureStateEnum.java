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
 * Description: 集群操作步骤状态枚举
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/12/29
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public enum ProcedureStateEnum implements IBaseEnum {
    PROCEDURE_PARSE_HOSTNAME("1", "解析节点主机名"),
    PROCEDURE_DETECT("2", "异步探测节点连通性"),
    PROCEDURE_CHECK("3", "异步检查节点初始化环境"),
    PROCEDURE_DISPATCH("4", "异步推送安装包"),
    PROCEDURE_ADD_DONE("5", "节点服役"),
    PROCEDURE_SELECT_SERVICE("6", "选择服务"),
    PROCEDURE_SELECT_COMPONENT("7", "选择组件"),
    PROCEDURE_PRE_CONFIG("8", "预配置"),
    PROCEDURE_DEPLOYING("9", "部署中"),
    PROCEDURE_DEPLOYED("10", "部署完成");


    private final String code;
    private final String message;

    ProcedureStateEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public ProcedureStateEnum getByCode(String code) {
        for (ProcedureStateEnum state : ProcedureStateEnum.values()) {
            if (state.getCode().equals(code)) {
                return state;
            }
        }
        return null;
    }

    public ProcedureStateEnum pre() {
        ProcedureStateEnum[] states = ProcedureStateEnum.values();
        int ordinal = this.ordinal();
        return ordinal > 0 ? states[ordinal - 1] : null;
    }

    public ProcedureStateEnum next() {
        ProcedureStateEnum[] states = ProcedureStateEnum.values();
        int ordinal = this.ordinal();
        return ordinal < states.length - 1 ? states[ordinal + 1] : null;
    }
}
