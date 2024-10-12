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
 * Description: 正则字符串验证
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/10/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class StringValidator {

    // 检查字符串是否符合要求
    public static boolean isValid(String input) {
        if (input == null) {
            return false;
        }
        return input.matches("^[a-zA-Z0-9-]+$");
    }

    // For test
    public static void main(String[] args) {
        System.out.println(isValid("Valid-String123")); // 输出: true
        System.out.println(isValid("Invalid_String!")); // 输出: false
        System.out.println(isValid("Invalid-String!")); // 输出: false
        System.out.println(isValid("测试")); // 输出: false
    }
}
