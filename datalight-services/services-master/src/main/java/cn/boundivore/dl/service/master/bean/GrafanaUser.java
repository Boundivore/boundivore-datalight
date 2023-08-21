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

import cn.boundivore.dl.base.enumeration.impl.GrafanaRoleEnum;
import cn.boundivore.dl.exception.BException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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

    private GrafanaRoleEnum grafanaRoleEnum;

    private String loginName;

    private String loginPassword;

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
     * @param clusterName 集群名称（也是 Grafana 的 org 名称）
     * @param grafanaRoleEnum Grafana 用户角色类型
     * @return GrafanaUser 用户实例
     */
    public static GrafanaUser getGrafanaUser(String clusterName,
                                             GrafanaRoleEnum grafanaRoleEnum) {
        GrafanaUser grafanaUser = new GrafanaUser();
        switch (grafanaRoleEnum) {
            case ADMIN:
                grafanaUser.grafanaRoleEnum = GrafanaRoleEnum.ADMIN;
                grafanaUser.loginName = "admin";
                grafanaUser.loginPassword = "admin";
                break;
            case ADMIN_ORG:
                grafanaUser.grafanaRoleEnum = GrafanaRoleEnum.ADMIN_ORG;
                grafanaUser.loginName = String.format(
                        "admin-%s",
                        clusterName
                );
                grafanaUser.loginPassword = String.format(
                        "admin-%s",
                        clusterName
                );
                break;
            case EDITOR_ORG:
                grafanaUser.grafanaRoleEnum = GrafanaRoleEnum.EDITOR_ORG;
                grafanaUser.loginName = String.format(
                        "editor-%s",
                        clusterName
                );
                grafanaUser.loginPassword = String.format(
                        "editor-%s",
                        clusterName
                );
                break;
            case VIEWER_ORG:
                throw new BException(
                        String.format(
                                "暂未支持的 Grafana 角色: %s",
                                grafanaRoleEnum
                        )
                );
            default:
                throw new BException(
                        String.format(
                                "未知的 Grafana 角色: %s",
                                grafanaRoleEnum
                        )
                );
        }

        return grafanaUser;
    }
}
