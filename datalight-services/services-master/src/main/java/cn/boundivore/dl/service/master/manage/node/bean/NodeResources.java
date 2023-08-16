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
package cn.boundivore.dl.service.master.manage.node.bean;

import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.ssh.tools.SshTool;
import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Description: 扫描得到的节点物理资源信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class NodeResources {

    private String nodeIp;

    private String cpuArch;

    private Long cpuCores;

    private Long ram;

    private Long diskTotal;

    private Long diskFree;

    private String osVersion;

    /**
     * Description: 将资源探测到的字符串内容映射为当前 Bean
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param execResult shell 的执行结果
     * @return NodeResources 当前 Bean
     */
    public static NodeResources fromString(SshTool.ExecResult execResult) {
        NodeResources nodeResources = new NodeResources();
        String[] lines = execResult.getOutput().split("\n");

        Assert.isTrue(
                lines.length >= 7,
                () -> new BException("扫描物理资源出现异常")
        );

        if (lines.length >= 1) {
            nodeResources.setNodeIp(lines[0].trim());
        }
        if (lines.length >= 2) {
            nodeResources.setCpuArch(lines[1].trim());
        }
        if (lines.length >= 3) {
            nodeResources.setCpuCores(Long.parseLong(lines[2].trim()));
        }
        if (lines.length >= 4) {
            nodeResources.setRam(Long.parseLong(lines[3].trim()));
        }
        if (lines.length >= 5) {
            nodeResources.setDiskTotal(Long.parseLong(lines[4].trim()));
        }
        if (lines.length >= 6) {
            nodeResources.setDiskFree(Long.parseLong(lines[5].trim()));
        }
        if (lines.length >= 7) {
            nodeResources.setOsVersion(lines[6].trim());
        }

        return nodeResources;
    }

}
