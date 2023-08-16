package cn.boundivore.dl.boot.utils;///**
// * Copyright (C) <2023> <Boundivore> <boundivore@foxmail.com>
// * <p>
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the Apache License, Version 2.0
// * as published by the Apache Software Foundation.
// * <p>
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * Apache License, Version 2.0 for more details.
// * <p>
// * You should have received a copy of the Apache License, Version 2.0
// * along with this program; if not, you can obtain a copy at
// * http://www.apache.org/licenses/LICENSE-2.0.
// */
//package cn.boundivore.dl.boot.utils;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.jinji.trans.common.result.Page;
//
///**
// * Description: PageUtil
// * Created by: Boundivore
// * E-mail: boundivore@foxmail.com
// * Creation time: 2023/5/28
// * Modification description:
// * Modified by:
// * Modification time:
// * Version: V1.0
// */
//public class PageUtil {
//    public static <T> Page iPage2Page(IPage<T> iPage) {
//        long current = iPage != null ? iPage.getCurrent() : 0;
//        long pages = iPage != null ? iPage.getPages() : 0;
//        long size = iPage != null ? iPage.getSize() : 0;
//        long total = iPage != null ? iPage.getTotal() : 0;
//
//        return new Page(
//                current,
//                pages,
//                size,
//                total
//        );
//    }
//
//}
