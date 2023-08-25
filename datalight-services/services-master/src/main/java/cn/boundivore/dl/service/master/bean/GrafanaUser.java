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
package cn.boundivore.dl.service.master.bean;

import cn.boundivore.dl.base.enumeration.impl.GrafanaUserTypeEnum;
import cn.boundivore.dl.exception.BException;
import lombok.Getter;

/**
 * Description: Grafana 初始化用户信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Getter
public class GrafanaUser {

    private GrafanaUserTypeEnum grafanaUserTypeEnum;

    private String loginName;

    private String oldLoginPassword;

    private String newLoginPassword;

    /**
     * Description: 获取指定类型的 Grafana 用户
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgName         Grafana 的 org 名称（也是 集群名称，此处将 OrgName 作为 LoginName）
     * @param grafanaUserTypeEnum Grafana 用户角色类型
     * @return GrafanaUser 用户实例
     */
    public static GrafanaUser getGrafanaUser(String orgName,
                                             GrafanaUserTypeEnum grafanaUserTypeEnum) {
        GrafanaUser grafanaUser = new GrafanaUser();
        switch (grafanaUserTypeEnum) {
            case ADMIN:
                grafanaUser.grafanaUserTypeEnum = GrafanaUserTypeEnum.ADMIN;
                grafanaUser.loginName = "admin";
                grafanaUser.oldLoginPassword = "admin";
                grafanaUser.newLoginPassword = "adminadmin";
                break;
            case ADMIN_DATALIGHT:
                grafanaUser.grafanaUserTypeEnum = GrafanaUserTypeEnum.ADMIN_DATALIGHT;
                grafanaUser.loginName = String.format(
                        "admin-%s",
                        orgName
                );
                grafanaUser.oldLoginPassword = String.format(
                        "admin-%s",
                        orgName
                );
                grafanaUser.newLoginPassword = grafanaUser.oldLoginPassword;
                break;
            case EDITOR_DATALIGHT:
                grafanaUser.grafanaUserTypeEnum = GrafanaUserTypeEnum.EDITOR_DATALIGHT;
                grafanaUser.loginName = String.format(
                        "editor-%s",
                        orgName
                );
                grafanaUser.oldLoginPassword = String.format(
                        "editor-%s",
                        orgName
                );
                grafanaUser.newLoginPassword = grafanaUser.oldLoginPassword;
                break;
            case VIEWER_DATALIGHT:
                throw new BException(
                        String.format(
                                "暂未支持的 Grafana 角色: %s",
                                grafanaUserTypeEnum
                        )
                );
            default:
                throw new BException(
                        String.format(
                                "未知的 Grafana 角色: %s",
                                grafanaUserTypeEnum
                        )
                );
        }

        return grafanaUser;
    }
}
