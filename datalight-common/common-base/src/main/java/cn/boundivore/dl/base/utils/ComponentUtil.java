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
package cn.boundivore.dl.base.utils;

/**
 * Description: 组件工具类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/29
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ComponentUtil {


    /**
     * Description: 修剪去除组件名称末尾的数字
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/29
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param componentName 可能带有数字结尾的组件名，如：NameNode1 NameNode2
     * @return 返回修剪后的组件名：如：NameNode
     */
    public static String clipComponentName(String componentName) {
        if (componentName == null || componentName.trim().isEmpty()) {
            throw new IllegalArgumentException("组件名称不能为空");
        }
        return componentName.replaceAll("\\d+$", "");
    }
}
