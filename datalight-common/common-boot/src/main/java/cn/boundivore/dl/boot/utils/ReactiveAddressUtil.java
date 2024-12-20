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
package cn.boundivore.dl.boot.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 解析 IP 和 主机名 工具类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Slf4j
public class ReactiveAddressUtil {
    private final static String UNKNOWN_STR = "unknown";

    /**
     * Description: 获取远端 IP 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 当前请求
     * @return IP 地址
     */
    public static String getRemoteAddress(ServerHttpRequest request) {
        Map<String, String> headers = request.getHeaders().toSingleValueMap();
        String ip = headers.get("X-Forwarded-For");
        if (isEmptyIP(ip)) {
            String[] headerKeys = {
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "HTTP_CLIENT_IP",
                    "HTTP_X_FORWARDED_FOR"
            };
            for (String key : headerKeys) {
                ip = headers.get(key);
                if (!isEmptyIP(ip)) {
                    break;
                }
            }
            if (isEmptyIP(ip)) {
                ip = Objects.requireNonNull(request.getRemoteAddress())
                        .getAddress()
                        .getHostAddress();

                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    // 根据网卡取本机配置的IP
                    ip = getLocalAddress();
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!isEmptyIP(strIp)) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * Description: 获取远端 IP 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 当前请求
     * @return IP 地址
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = null;
        try {
            String[] headerKeys = {
                    "x-forwarded-for",
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "HTTP_CLIENT_IP",
                    "HTTP_X_FORWARDED_FOR"
            };
            for (String key : headerKeys) {
                ip = request.getHeader(key);
                if (ip != null && !isEmptyIP(ip)) {
                    break;
                }
            }
            if (isEmptyIP(ip)) {
                ip = request.getRemoteAddr();
            }
            // 针对多级代理情况，取第一个非 unknown 的 IP
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            // 处理本地回环地址
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }
        } catch (Exception e) {
            log.error("Invoke getRemoteIp() error: {}", ExceptionUtil.stacktraceToString(e));
        }
        return ip;
    }
    /**
     * Description: 判断 IP 地址是否为空或为unknown
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param ip IP地址字符串
     * @return 是否为空或unknown
     */
    private static boolean isEmptyIP(String ip) {
        return ip == null || ip.isEmpty() || UNKNOWN_STR.equalsIgnoreCase(ip);
    }

    /**
     * Description: 获取本地地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 本地IP地址
     */
    public static String getLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error", e);
        }
        return "";
    }

    /**
     * Description: 获取本机主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/12/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 返回本地主机名
     */
    public static String getLocalHostName() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            // UnknownHostException是InetAddress.getLocalHost()可能抛出的异常
            // 在这里可以处理异常，比如返回一个默认值或者自定义的错误信息
            return "Unknown Host";
        }
    }

    /**
     * Description: 获取内网IP地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 内网IP地址
     */
    public static String getInternalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Check if the interface name starts with 'en'
                if (networkInterface.getName().startsWith("en") || networkInterface.getName().startsWith("eth")) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        /*
                            isSiteLocalAddress()
                            10.0.0.0 ~ 10.255.255.255（10/8）
                            172.16.0.0 ~ 172.31.255.255（172.16/12）
                            192.168.0.0 ~ 192.168.255.255（192.168/16）
                         */
                        if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }

        return null;
    }
}
