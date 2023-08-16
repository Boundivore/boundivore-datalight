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
package cn.boundivore.dl.ssh.verifier;

import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 允许所有内部 SSH 请求验证器，用于验证主机密钥的空实现类，始终返回 true
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/3
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class NoneHostKeyVerifier implements HostKeyVerifier {

    @Override
    public boolean verify(String s, int i, PublicKey publicKey) {
        return true;
    }

    @Override
    public List<String> findExistingAlgorithms(String hostname, int port) {
        return new ArrayList<>();
    }
}
