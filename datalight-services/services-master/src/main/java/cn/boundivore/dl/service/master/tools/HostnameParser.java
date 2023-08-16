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
package cn.boundivore.dl.service.master.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class HostnameParser {
    public static void main(String[] args) {
        // 输入的主机名字符串
        String input = "node[1-3]\n" +
                "node[01-12]\n" +
                "[1-4]node\n" +
                "node[a-c]\n" +
                "linux[1-3].com\n" +
                "linux-admin[1-3].com\n" +
                "node109\n" +
                "linux103.com\n" +
                "hadoop202.com";

        Hostnames hostnames = parse(input);

        log.info("有效主机名: {}", hostnames.getValidHostnames());
        log.info("无效主机名: {}", hostnames.getInvalidHostnames());

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hostnames {
        //有效主机名
        List<String> validHostnames;

        //无效主机名
        List<String> invalidHostnames;
    }

    /**
     * Description: 解析主机名总入口，将字符串主机名解析为 有效主机名列表 与 无效主机名列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param input 可能包含正则的主机名字符串
     * @return Hostnames 解析后的主机名合集
     */
    public static Hostnames parse(String input) {

        // 展开主机名
        Set<String> allHostnames = expandNodes(input);
        // 查找无效的主机名
        Set<String> invalidHostnames = findInvalidHostnames(allHostnames);


        // 过滤有效的主机名并按自然顺序排序
        List<String> validHostnameList = allHostnames.stream()
                .filter(i -> !invalidHostnames.contains(i))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        // 过滤无效的主机名并按自然顺序排序
        List<String> invalidHostnameList = invalidHostnames.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        return new Hostnames(
                validHostnameList,
                invalidHostnameList
        );

    }

    /**
     * Description: 展开主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param input 当前项
     * @return input 返回展开后去重的所有主机名
     */
    private static Set<String> expandNodes(String input) {
        Set<String> expandedNodes = new HashSet<>();

        // 使用换行符、空格或制表符对输入进行分割
        String[] lines = input.split("\\r?\\n|\\s+|\t+");

        for (String line : lines) {
            if (line.contains("[")) {
                // 展开方括号表示的主机名
                List<String> expandedHostnames = expandBracketNotation(line);
                expandedNodes.addAll(expandedHostnames);
            } else {
                // 将非方括号表示的主机名添加到展开的主机名集合中
                expandedNodes.add(line);
            }
        }

        return expandedNodes;
    }

    /**
     * Description: 展开正则表示的主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param line 主机名当前正则项或主机名特称
     * @return List<String> 展开字符串，获取所有主机名
     */
    private static List<String> expandBracketNotation(String line) {
        List<String> expandedHostnames = new ArrayList<>();

        // 正则表达式模式，用于匹配包含范围的方括号表示
        Pattern pattern = Pattern.compile("^(.*?)\\[(.*?)\\](.*?)$");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            String prefix = matcher.group(1);
            String range = matcher.group(2);
            String suffix = matcher.group(3);

            if (range.matches("\\d+-\\d+")) {
                // 数字范围
                String[] rangeParts = range.split("-");
                int start = Integer.parseInt(rangeParts[0]);
                int end = Integer.parseInt(rangeParts[1]);
                for (int i = start; i <= end; i++) {
                    expandedHostnames.add(prefix + String.format("%0" + rangeParts[0].length() + "d", i) + suffix);
                }
            } else if (range.matches("[a-zA-Z]-[a-zA-Z]")) {
                // 字母范围
                char start = range.charAt(0);
                char end = range.charAt(2);
                for (char c = start; c <= end; c++) {
                    expandedHostnames.add(prefix + c + suffix);
                }
            }
        }

        return expandedHostnames;
    }

    /**
     * Description: 查找无效的主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param allHostnames 展开的所有主机名列表
     * @return 去重的无效主机名
     */
    private static Set<String> findInvalidHostnames(Set<String> allHostnames) {
        Set<String> invalidHostnames = new HashSet<>();
        Pattern pattern = Pattern.compile("[^a-zA-Z\\d@._]");

        for (String hostname : allHostnames) {
            Matcher matcher = pattern.matcher(hostname);

            if (matcher.find()) {
                invalidHostnames.add(hostname);
            }
        }

        return invalidHostnames;
    }
}
