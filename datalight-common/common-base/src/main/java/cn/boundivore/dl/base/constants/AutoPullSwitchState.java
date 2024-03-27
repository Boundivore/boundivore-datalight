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
package cn.boundivore.dl.base.constants;

/**
 * Description: 自动拉起开关状态
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class AutoPullSwitchState {
    /**
     * Worker
     */
    public static boolean AUTO_PULL_WORKER = true;
    public static long AUTO_CLOSE_DURATION_WORKER = 10 * 60 * 1000L;
    public static long AUTO_CLOSE_BEGIN_TIME_WORKER;
    public static long AUTO_CLOSE_END_TIME_WORKER;


    /**
     * Component
     */
    public static boolean AUTO_PULL_COMPONENT = true;
    public static long AUTO_CLOSE_DURATION_COMPONENT = 10 * 60 * 1000L;
    public static long AUTO_CLOSE_BEGIN_TIME_COMPONENT;
    public static long AUTO_CLOSE_END_TIME_COMPONENT;

    /**
     * Description: 设置关闭 Worker 进程自动拉起开关状态的持续时间，以及开关状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param autoPullWorker 开光状态
     * @param duration       关闭持续时间
     */
    public static void setCloseAutoPullWorker(boolean autoPullWorker, long duration) {
        AUTO_PULL_WORKER = autoPullWorker;

        AUTO_CLOSE_DURATION_WORKER = duration;
        AUTO_CLOSE_BEGIN_TIME_WORKER = System.currentTimeMillis();
        AUTO_CLOSE_END_TIME_WORKER = AUTO_CLOSE_BEGIN_TIME_WORKER + AUTO_CLOSE_DURATION_WORKER;
    }

    /**
     * Description: 设置关闭 Component 进程自动拉起开关状态的持续时间，以及开关状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param autoPullComponent 开光状态
     * @param duration          关闭持续时间
     */
    public static void setCloseAutoPullComponent(boolean autoPullComponent, long duration) {
        AUTO_PULL_COMPONENT = autoPullComponent;

        AUTO_CLOSE_DURATION_COMPONENT = duration;
        AUTO_CLOSE_BEGIN_TIME_COMPONENT = System.currentTimeMillis();
        AUTO_CLOSE_END_TIME_COMPONENT = AUTO_CLOSE_BEGIN_TIME_COMPONENT + AUTO_CLOSE_DURATION_COMPONENT;
    }
}
