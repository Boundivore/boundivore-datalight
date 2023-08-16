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
import cn.hutool.core.lang.Assert;

/**
 * Description: SCStateEnum Service、Component 的当前状态
 * 无论是第一次部署还是增量部署，部署过程中，服务处于：CHANGING
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public enum SCStateEnum implements IBaseEnum {
    /**
     * 组件的枚举
     */
    DEPLOYING("0", "部署中"),

    STARTING("1", "启动中"),
    STARTED("2", "已启动"),

    STOPPING("3", "停止中"),
    STOPPED("4", "已停止"),

    BEING_DECOMMISSIONED("5", "退役中"),
    DECOMMISSIONED("6", "已退役"),

    /**
     * 服务的枚举
     */
    // 已部署的服务，在其下新增组件时，将处于 SELECTED_ADDITION 状态
    SELECTED_ADDITION("7", "选择增量部署"),
    // 无论是第一次部署还是增量部署，部署过程中，服务处于：CHANGING
    CHANGING("8", "变更中"),
    // 当服务部署完成时，处于该状态
    DEPLOYED("9", "已部署"),

    /**
     * 服务与组件共同使用的枚举
     */
    // 当所有组件均处于 REMOVED 状态时，对应服务也处于 REMOVED 状态，同时设计原则上，
    // 为了防止在长期频繁操作过程中垃圾数据过多 REMOVED 状态的记录将从数据库中移除，
    // 同时，审计操作记录将记录在另外的审计表中
    REMOVED("10", "已移除"),
    // 当服务、组件处于被选中，等待部署的状态时，处于该状态
    SELECTED("11", "选择部署"),
    // 当服务、组件在页面中反复修改，最终处于“不部署”的意图时，则由 SELECTED 转为 UNSELECTED 并，删除数据库该条记录
    UNSELECTED("12", "取消选择部署");

    private final String code;
    private final String message;

    SCStateEnum(String code, String message) {
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

    /**
     * Description: 判断服务当前状态是否处于部署状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 部署状态 true，未部署状态 false
     */
    public boolean isServiceDeployed() {
        return this != SCStateEnum.UNSELECTED && this != SCStateEnum.REMOVED;
    }

    /*
     * 以下为服务状态机
     */

    /**
     * Description: 选择服务 或 取消选择服务时，"服务选择时"状态机
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param operateSCStateEnum 本次执行的服务状态
     * @return SCStateEnum 基于当前服务状态，进行本次操作后，服务应该处于的状态
     */
    public SCStateEnum transitionSelectedServiceState(SCStateEnum operateSCStateEnum) {

        Assert.isTrue(
                operateSCStateEnum == SELECTED || operateSCStateEnum == UNSELECTED,
                () -> new IllegalArgumentException(
                        String.format(
                                "服务选择时状态机只能传递 %s 或 %s 状态切换意图",
                                SELECTED,
                                UNSELECTED)
                )
        );

        SCStateEnum resultSCSStateEnum;
        switch (this) {

            case REMOVED:
                resultSCSStateEnum = operateSCStateEnum == SELECTED ?
                        SELECTED :
                        REMOVED;
                break;

            case DEPLOYED:
            case SELECTED_ADDITION:
                resultSCSStateEnum = operateSCStateEnum == SELECTED ?
                        SELECTED_ADDITION :
                        DEPLOYED;
                break;

            case UNSELECTED:
            case SELECTED:
                resultSCSStateEnum = operateSCStateEnum == SELECTED ?
                        SELECTED :
                        UNSELECTED;
                break;

            default:
                throw new RuntimeException(
                        String.format(
                                "不支持的服务状态转换: %s => %s",
                                this,
                                operateSCStateEnum
                        )
                );
        }

        return resultSCSStateEnum;

    }

    /*
     * 以下为组件状态机
     */

    /**
     * Description: 选择组件 或 取消选择组件时，"组件选择时"状态机
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param operateSCStateEnum 本次执行的服务状态
     * @return SCStateEnum 基于当前组件状态，进行本次操作后，组件应该处于的状态
     */
    public SCStateEnum transitionSelectedComponentState(SCStateEnum operateSCStateEnum) {
        Assert.isTrue(
                operateSCStateEnum == SELECTED || operateSCStateEnum == UNSELECTED,
                () -> new IllegalArgumentException(
                        String.format(
                                "组件选择时状态机只能传递 %s 或 %s 状态切换意图",
                                SELECTED,
                                UNSELECTED
                        )
                )
        );

        SCStateEnum resultSCSStateEnum;
        switch (this) {

            case REMOVED:
                resultSCSStateEnum = operateSCStateEnum == SELECTED ?
                        SELECTED :
                        REMOVED;
                break;

            case UNSELECTED:
            case SELECTED:
                resultSCSStateEnum = operateSCStateEnum == SELECTED ?
                        SELECTED :
                        UNSELECTED;
                break;

            default:
                resultSCSStateEnum = this;
                break;
        }

        return resultSCSStateEnum;
    }

}
