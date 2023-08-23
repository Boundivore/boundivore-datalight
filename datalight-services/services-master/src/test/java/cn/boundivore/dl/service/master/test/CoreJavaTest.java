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
package cn.boundivore.dl.service.master.test;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 临时 CoreJava 测试
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class CoreJavaTest {

    @Test
    public void javaPerformanceTest() {
        List<Integer> initList1 = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            initList1.add(i);
        }

        List<Integer> initList2 = new ArrayList<>();
        for (int i = 11; i <= 20; i++) {
            initList2.add(i);
        }

        List<Integer> resultList = new LinkedList<>(initList1);
        resultList.addAll(0, initList2);

        System.out.println(resultList);

    }
}
