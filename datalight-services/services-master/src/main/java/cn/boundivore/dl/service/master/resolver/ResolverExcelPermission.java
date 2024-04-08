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
package cn.boundivore.dl.service.master.resolver;

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 解析权限配置模板 Excel
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/8
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public final class ResolverExcelPermission {

//    @Transactional(
//            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
//            rollbackFor = Exception.class
//    )
//    public void initPermissionTable() {
//        Long merchantId = Long.MIN_VALUE;
//
//        //Substring current excel version from filename
//        final String excelVersionString = tableInitializerPath.substring(
//                tableInitializerPath.lastIndexOf("-") + 1,
//                tableInitializerPath.lastIndexOf(".")
//        ).replace("v", "");
//
//        log.info(String.format("ExcelVersion: %s", excelVersionString));
//
//        final Long currentExcelVersion = Long.parseLong(excelVersionString);
//
//        //Determine whether the permission table needs to be updated
//        boolean exists = tHmmPermissionService.lambdaQuery()
//                .select()
//                .eq(THmmPermission::getMerchantId, Long.MIN_VALUE)
//                .ge(THmmPermission::getStaticVersion, currentExcelVersion)
//                .last("LIMIT 1")
//                .exists();
//        if (exists) {
//            log.info(String.format(
//                            "The database is already the latest version of Excel data, skip the update: %s",
//                            tableInitializerPath
//                    )
//            );
//            return;
//        }
//
//        //Find version
//        THmmPermission tHmmPermission = tHmmPermissionService.lambdaQuery()
//                .select()
//                .eq(THmmPermission::getMerchantId, Long.MIN_VALUE)
//                .last("LIMIT 1")
//                .one();
//        final Long version = tHmmPermission == null ? 0L : tHmmPermission.getVersion();
//
//        //Read Excel
//        final List<THmmPermission> tHmmPermissionList = CollUtil.newArrayList();
//        final List<THmmRuleInterface> tHmmRuleInterfaceList = CollUtil.newArrayList();
//        final List<THmmRuleDataRow> tHmmRuleDataRowList = CollUtil.newArrayList();
//        final List<THmmRuleDataColumn> tHmmRuleDataColumnList = CollUtil.newArrayList();
//
//        //Key: rule_static_code
//        final Map<String, THmmPermission> staticRuleCodeTHmmPermissionMap = new HashMap<>();
//        final Map<String, List<THmmRuleInterface>> staticRuleCodeTHmmRuleInterfaceListMap = new HashMap<>();
//        final Map<String, List<THmmRuleDataRow>> staticRuleCodeTHmmRuleDataRowListMap = new HashMap<>();
//        final Map<String, List<THmmRuleDataColumn>> staticRuleCodeTHmmRuleDataColumnListMap = new HashMap<>();
//
//        Excel07SaxReader reader = new Excel07SaxReader(
//                createRowHandler(
//                        merchantId,
//                        version,
//                        currentExcelVersion,
//                        staticRuleCodeTHmmPermissionMap,
//                        staticRuleCodeTHmmRuleInterfaceListMap,
//                        staticRuleCodeTHmmRuleDataRowListMap,
//                        staticRuleCodeTHmmRuleDataColumnListMap,
//                        tHmmPermissionList,
//                        tHmmRuleInterfaceList,
//                        tHmmRuleDataRowList,
//                        tHmmRuleDataColumnList
//                )
//        );
//        reader.read(tableInitializerPath, -1);
//
//        //Update table: t_hmm_permission
//        log.info(String.format("Prepare to update table of THmmPermission, rows: %s", tHmmPermissionList.size()));
//        if(tHmmPermissionList.size() > 0){
//            boolean saveOrUpdateTHmmPermissionList = tHmmPermissionService.saveBatch(tHmmPermissionList);
//            Assert.isTrue(
//                    saveOrUpdateTHmmPermissionList,
//                    () -> new DatabaseException("Save or update THmmPermissionList failed")
//            );
//        }
//
//        //Update table: t_hmm_rule_interface
//        log.info(String.format("Prepare to update table of THmmRuleInterface, rows: %s", tHmmRuleInterfaceList.size()));
//
//        if(tHmmRuleInterfaceList.size() > 0){
//            boolean saveOrUpdateTHmmRuleInterfaceList = tHmmRuleInterfaceService.saveBatch(tHmmRuleInterfaceList);
//            Assert.isTrue(
//                    saveOrUpdateTHmmRuleInterfaceList,
//                    () -> new DatabaseException("Save or update THmmRuleInterfaceList failed")
//            );
//        }
//
//
//        //Update table: t_hmm_rule_data_row
//        log.info(String.format("Prepare to update table of THmmRuleDataRow, rows: %s", tHmmRuleDataRowList.size()));
//        if(tHmmRuleDataRowList.size() > 0){
//            boolean saveOrUpdateTHmmRuleDataRowList = tHmmRuleDataRowService.saveBatch(tHmmRuleDataRowList);
//            Assert.isTrue(
//                    saveOrUpdateTHmmRuleDataRowList,
//                    () -> new DatabaseException("Save or update THmmRuleDataRowList failed")
//            );
//        }
//
//
//        //Update table: t_hmm_rule_data_column
//        log.info(String.format("Prepare to update table of THmmRuleDataColumn, rows: %s", tHmmRuleDataColumnList.size()));
//
//        if(tHmmRuleDataColumnList.size() > 0){
//            boolean saveOrUpdateTHmmRuleDataColumnList = tHmmRuleDataColumnService.saveBatch(tHmmRuleDataColumnList);
//            Assert.isTrue(
//                    saveOrUpdateTHmmRuleDataColumnList,
//                    () -> new DatabaseException("Save or update THmmRuleDataColumnList failed")
//            );
//        }
//
//        //Update table: t_hmm_permission_rule_relation
//        staticRuleCodeTHmmPermissionMap.forEach((key, value) -> {
//                    PermissionTypeEnum permissionType = value.getPermissionType();
//                    switch (permissionType) {
//                        case PERMISSION_INTERFACE:
//                            List<THmmRuleInterface> ruleInterfaceList = staticRuleCodeTHmmRuleInterfaceListMap.getOrDefault(
//                                    key,
//                                    CollUtil.newArrayList()
//                            );
//                            ruleInterfaceList
//                                    .forEach(i -> {
//                                        boolean e = tHmmPermissionRuleRelationService.lambdaQuery()
//                                                .select()
//                                                .eq(THmmPermissionRuleRelation::getMerchantId, merchantId)
//                                                .eq(THmmPermissionRuleRelation::getPermissionId, value.getId())
//                                                .eq(THmmPermissionRuleRelation::getRuleInterfaceId, i.getId())
//                                                .exists();
//
//                                        if (!e) {
//                                            THmmPermissionRuleRelation tHmmPermissionRuleRelation = new THmmPermissionRuleRelation();
//                                            tHmmPermissionRuleRelation.setPermissionId(value.getId());
//                                            tHmmPermissionRuleRelation.setMerchantId(merchantId);
//                                            tHmmPermissionRuleRelation.setRuleInterfaceId(i.getId());
//
//                                            tHmmPermissionRuleRelationService.save(tHmmPermissionRuleRelation);
//                                        }
//
//                                    });
//                            break;
//                        case PERMISSION_DATA_ROW:
//                            List<THmmRuleDataRow> ruleDataRowList = staticRuleCodeTHmmRuleDataRowListMap.getOrDefault(
//                                    key,
//                                    CollUtil.newArrayList()
//                            );
//                            ruleDataRowList
//                                    .forEach(i -> {
//                                        boolean e = tHmmPermissionRuleRelationService.lambdaQuery()
//                                                .select()
//                                                .eq(THmmPermissionRuleRelation::getMerchantId, merchantId)
//                                                .eq(THmmPermissionRuleRelation::getPermissionId, value.getId())
//                                                .eq(THmmPermissionRuleRelation::getRuleDataRowId, i.getId())
//                                                .exists();
//
//                                        if (!e) {
//                                            THmmPermissionRuleRelation tHmmPermissionRuleRelation = new THmmPermissionRuleRelation();
//                                            tHmmPermissionRuleRelation.setPermissionId(value.getId());
//                                            tHmmPermissionRuleRelation.setMerchantId(merchantId);
//                                            tHmmPermissionRuleRelation.setRuleDataRowId(i.getId());
//
//                                            tHmmPermissionRuleRelationService.save(tHmmPermissionRuleRelation);
//                                        }
//
//                                    });
//
//                            break;
//                        case PERMISSION_DATA_COLUMN:
//                            List<THmmRuleDataColumn> ruleDataColumnList = staticRuleCodeTHmmRuleDataColumnListMap.getOrDefault(
//                                    key,
//                                    CollUtil.newArrayList()
//                            );
//                            ruleDataColumnList
//                                    .forEach(i -> {
//                                        boolean e = tHmmPermissionRuleRelationService.lambdaQuery()
//                                                .select()
//                                                .eq(THmmPermissionRuleRelation::getMerchantId, merchantId)
//                                                .eq(THmmPermissionRuleRelation::getPermissionId, value.getId())
//                                                .eq(THmmPermissionRuleRelation::getRuleDataColumnId, i.getId())
//                                                .exists();
//
//                                        if (!e) {
//                                            THmmPermissionRuleRelation tHmmPermissionRuleRelation = new THmmPermissionRuleRelation();
//                                            tHmmPermissionRuleRelation.setPermissionId(value.getId());
//                                            tHmmPermissionRuleRelation.setMerchantId(merchantId);
//                                            tHmmPermissionRuleRelation.setRuleDataColumnId(i.getId());
//
//                                            tHmmPermissionRuleRelationService.save(tHmmPermissionRuleRelation);
//                                        }
//
//                                    });
//                            break;
//                        case PERMISSION_PAGE:
//                            log.info("Waiting for completion");
//                            break;
//                        default:
//                            throw new BusinessException(String.format("%s not supported", permissionType));
//                    }
//                }
//        );
//
//
//    }
}
