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
package cn.boundivore.dl.plugin.kubesphere.config.utils;

/**
 * Description: KubeSphere 修改配置文件通用函数
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/9/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ClusterYamlUtil {
    // {{spec.roleGroups.etcd}}
    // {{spec.roleGroups.control-plane}}
    // {{spec.roleGroups.worker}}
    // {{spec.roleGroups.registry}}

    private final static String ROOT_PRIVATE_KEY_PATH = "/root/.ssh/id_rsa";

}
