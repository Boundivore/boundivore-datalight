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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlAlert;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerInterface;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerMail;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerRelation;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerInterfaceServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerMailServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerRelationServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description: 发送告警信息到指定为止相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAlertNoticeService {
    private final ObjectMapper objectMapper = new ObjectMapper();

//    private final JavaMailSenderImpl javaMailSender;

    private final TDlAlertHandlerRelationServiceImpl tDlAlertHandlerRelationService;

    private final TDlAlertHandlerInterfaceServiceImpl tDlAlertHandlerInterfaceService;

    private final TDlAlertHandlerMailServiceImpl tDlAlertHandlerMailService;

    private final RemoteInvokeHandlerInterfaceService remoteInvokeHandlerInterfaceService;

    @PostConstruct
    public void init() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Description: 发送告警信息到指定接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlAlert 告警自定义规则数据库实体
     * @param request  告警请求体
     */
    public void sendAlert(TDlAlert tDlAlert, AlertWebhookPayloadRequest.Alert request) {
        // 根据告警 ID 读取本次需要对告警处理的方式
        List<TDlAlertHandlerRelation> tDlAlertHandlerRelationList = this.tDlAlertHandlerRelationService.lambdaQuery()
                .select()
                .eq(TDlAlertHandlerRelation::getAlertId, tDlAlert.getId())
                .list();

        tDlAlertHandlerRelationList.parallelStream()
                .forEach(i -> {
                            switch (i.getHandlerType()) {
                                case ALERT_INTERFACE:
                                    this.sendToTargetInterface(tDlAlert, request);
                                    break;
                                case ALERT_MAIL:
                                    this.sendToEmail(tDlAlert, request);
                                    break;
                                case ALERT_LOG:
                                    log.warn("发生告警，日志记录: {}", request);
                                    break;
                                case ALERT_IGNORE:
                                    break;
                                default:
                                    break;
                            }
                        }
                );
    }

    /**
     * Description: 发送告警信息到指定接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlAlert 告警自定义规则数据库实体
     * @param request  告警请求体
     */
    public void sendToTargetInterface(TDlAlert tDlAlert, AlertWebhookPayloadRequest.Alert request) {
        List<Long> handlerIdList = this.getHandlerIdList(tDlAlert.getId());
        List<String> handlerInterfaceUriList = this.getHandlerInterfaceUriList(handlerIdList);

        try {
            handlerInterfaceUriList.forEach(uri -> {
                        try {
                            // Post Body
                            String preSendStr = this.objectMapper.writeValueAsString(request);

                            this.remoteInvokeHandlerInterfaceService
                                    .iThirdHandlerInterfaceAPI(uri)
                                        .sendPostRequest(preSendStr);
                        } catch (JsonProcessingException e) {
                            log.error(ExceptionUtil.stacktraceToString(e));
                        }
                    }
            );

        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }

    /**
     * Description: 发送告警信息到电子邮件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlAlert 告警自定义规则数据库实体
     * @param request  告警请求体
     */
    public void sendToEmail(TDlAlert tDlAlert, AlertWebhookPayloadRequest.Alert request) {

//        List<Long> handlerIdList = this.getHandlerIdList(tDlAlert.getId());
//        List<String> handlerMailAccountList = this.getHandlerMailAccountList(handlerIdList);
//        try {
//            handlerMailAccountList.forEach(mail -> {
//                        SimpleMailMessage message = new SimpleMailMessage();
//                        message.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
//                        message.setTo(mail);
//                        message.setSubject(tDlAlert.getAlertName());
//                        message.setText(request.getAnnotations().toString());
//
//                        javaMailSender.send(message);
//                    }
//            );
//        } catch (Exception e) {
//            log.error(ExceptionUtil.stacktraceToString(e));
//        }
    }

    /**
     * Description: 发送告警信息到微信
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlAlert 告警自定义规则数据库实体
     * @param request  告警请求体
     */
    public void sendToWeiChat(TDlAlert tDlAlert, AlertWebhookPayloadRequest request) {
    }

    /**
     * Description: 发送告警信息到钉钉
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlAlert 告警自定义规则数据库实体
     * @param request  告警请求体
     */
    public void sendToDingDing(TDlAlert tDlAlert, AlertWebhookPayloadRequest request) {
    }

    /**
     * Description: 发送告警信息到飞书
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlAlert 告警自定义规则数据库实体
     * @param request  告警请求体
     */
    public void sendToFeiShu(TDlAlert tDlAlert, AlertWebhookPayloadRequest request) {
    }

    /**
     * Description: 根据告警 ID 获取处理方式 ID 列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertId 告警 ID
     * @return List<Long> 处理方式 ID 列表
     */
    private List<Long> getHandlerIdList(Long alertId) {
        return this.tDlAlertHandlerRelationService.lambdaQuery()
                .select()
                .eq(TDlAlertHandlerRelation::getAlertId, alertId)
                .list()
                .stream()
                .map(TDlAlertHandlerRelation::getHandlerId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Description: 获取告警邮件列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param handlerIdList 处理方式 ID 列表
     * @return List<String> 邮箱地址列表
     */
    private List<String> getHandlerMailAccountList(List<Long> handlerIdList) {
        if (CollUtil.isEmpty(handlerIdList)) return new ArrayList<>();

        return this.tDlAlertHandlerMailService.lambdaQuery()
                .select()
                .in(TBasePo::getId, handlerIdList)
                .list()
                .stream()
                .map(TDlAlertHandlerMail::getMailAccount)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Description: 获取告警接口列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param handlerIdList 处理方式 ID 列表
     * @return List<String> 接口地址列表
     */
    private List<String> getHandlerInterfaceUriList(List<Long> handlerIdList) {
        if (CollUtil.isEmpty(handlerIdList)) return new ArrayList<>();

        return this.tDlAlertHandlerInterfaceService.lambdaQuery()
                .select()
                .in(TBasePo::getId, handlerIdList)
                .list()
                .stream()
                .map(TDlAlertHandlerInterface::getInterfaceUri)
                .distinct()
                .collect(Collectors.toList());
    }


}


