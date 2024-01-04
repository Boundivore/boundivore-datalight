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
    ADD_NEW_CLUSTER_DONE("0", "新增集群完成"),
    PARSE_HOSTNAME_DONE("1", "解析节点主机名完成"),
    DETECT_DONE("2", "异步探测节点连通性完成"),
    CHECK_DONE("3", "异步检查节点初始化环境完成"),
    DISPATCH_DONE("4", "异步推送安装包完成"),
    ADD_DONE("5", "节点服役完成"),
    SELECT_SERVICE_DONE("6", "选择服务完成"),
    SELECT_COMPONENT_DONE("7", "选择组件完成"),
    PRE_CONFIG_DONE("8", "预配置完成"),
    DEPLOY_DONE("9", "部署完成");


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
