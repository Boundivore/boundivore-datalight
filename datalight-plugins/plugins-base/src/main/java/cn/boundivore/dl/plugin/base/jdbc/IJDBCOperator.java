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
package cn.boundivore.dl.plugin.base.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Description: JDBC 操作接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/11/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public interface IJDBCOperator {
    /**
     * Description: 初始化数据库连接
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param dbUser 数据库用户名
     * @param dbPassword 数据库密码
     * @param ip 数据库 IP
     * @param port 数据库端口号
     * @param database 数据库名称
     * @return Connection JDBC 连接器
     */
    Connection initConnector(String dbUser,
                             String dbPassword,
                             String ip,
                             String port,
                             String database) throws SQLException;

    /**
     * Description: 添加 Fe Follower
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param connection JDBC 连接器
     * @param ip 对应 Doris 业务 IP 地址
     * @param port 对应 Doris 业务端口号
     * @return String 执行结果
     */
    String addFeFollower(Connection connection, String ip, String port);

    /**
     * Description: 添加 Fe Observer
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param connection JDBC 连接器
     * @param ip 对应 Doris 业务 IP 地址
     * @param port 对应 Doris 业务端口号
     * @return String 执行结果
     */
    String addFeObserver(Connection connection, String ip, String port);

    /**
     * Description: 添加 Be
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param connection JDBC 连接器
     * @param ip 对应 Doris 业务 IP 地址
     * @param port 对应 Doris 业务端口号
     * @return String 执行结果
     */
    String addBe(Connection connection, String ip, String port);
}
