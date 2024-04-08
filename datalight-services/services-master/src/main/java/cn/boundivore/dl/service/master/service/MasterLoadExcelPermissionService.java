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

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.enumeration.impl.PermissionTypeEnum;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.*;
import cn.boundivore.dl.orm.service.single.impl.*;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
@Service
@RequiredArgsConstructor
public class MasterLoadExcelPermissionService {

    private final TDlPermissionTemplatedServiceImpl tDlPermissionTemplatedService;

    private final TDlRuleInterfaceTemplatedServiceImpl tDlRuleInterfaceTemplatedService;

    private final TDlRuleDataRowTemplatedServiceImpl tDlRuleDataRowTemplatedService;

    private final TDlRuleDataColumnTemplatedServiceImpl tDlRuleDataColumnTemplatedService;

    private final TDlPermissionRuleRelationTemplatedServiceImpl tDlPermissionRuleRelationTemplatedService;


    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = Exception.class
    )
    public void initPermissionTable(String confPath) {

        String tableInitializerPath = String.format(
                "%s/%s",
                confPath,
                "PermissionTableExcel-1.0.xlsx"
        );

        // 从权限模板配置文件名中截取模板版本号
        final String excelVersionString = tableInitializerPath.substring(
                tableInitializerPath.lastIndexOf("-") + 1,
                tableInitializerPath.lastIndexOf(".")
        ).replace("v", "");

        log.info(String.format("ExcelVersion: %s", excelVersionString));

        final Long currentExcelVersion = Long.parseLong(excelVersionString);

        //Determine whether the permission table needs to be updated
        boolean exists = this.tDlPermissionTemplatedService.lambdaQuery()
                .select()
                .ge(TDlPermissionTemplated::getStaticVersion, currentExcelVersion)
                .last("LIMIT 1")
                .exists();
        if (exists) {
            log.info(
                    String.format(
                            "数据库已经存在最新的权限模板: %s",
                            tableInitializerPath
                    )
            );
            return;
        }

        // 清除旧数据
        this.tDlPermissionTemplatedService.remove(new QueryWrapper<>());
        this.tDlRuleInterfaceTemplatedService.remove(new QueryWrapper<>());
        this.tDlRuleDataRowTemplatedService.remove(new QueryWrapper<>());
        this.tDlRuleDataColumnTemplatedService.remove(new QueryWrapper<>());
        this.tDlPermissionRuleRelationTemplatedService.remove(new QueryWrapper<>());

        // 读取 Excel
        final List<TDlPermissionTemplated> tDlPermissionTemplatedList = CollUtil.newArrayList();
        final List<TDlRuleInterfaceTemplated> tDlRuleInterfaceTemplatedList = CollUtil.newArrayList();
        final List<TDlRuleDataRowTemplated> tDlRuleDataRowTemplatedList = CollUtil.newArrayList();
        final List<TDlRuleDataColumnTemplated> tDlRuleDataColumnTemplatedList = CollUtil.newArrayList();

        // Key: rule_static_code
        final Map<String, TDlPermissionTemplated> staticRuleCodeTDlPermissionMap = new HashMap<>();
        final Map<String, List<TDlRuleInterfaceTemplated>> staticRuleCodeTDlRuleInterfaceListMap = new HashMap<>();
        final Map<String, List<TDlRuleDataRowTemplated>> staticRuleCodeTDlRuleDataRowListMap = new HashMap<>();
        final Map<String, List<TDlRuleDataColumnTemplated>> staticRuleCodeTDlRuleDataColumnListMap = new HashMap<>();

        Excel07SaxReader reader = new Excel07SaxReader(
                createRowHandler(
                        currentExcelVersion,
                        staticRuleCodeTDlPermissionMap,
                        staticRuleCodeTDlRuleInterfaceListMap,
                        staticRuleCodeTDlRuleDataRowListMap,
                        staticRuleCodeTDlRuleDataColumnListMap,
                        tDlPermissionTemplatedList,
                        tDlRuleInterfaceTemplatedList,
                        tDlRuleDataRowTemplatedList,
                        tDlRuleDataColumnTemplatedList
                )
        );
        reader.read(tableInitializerPath, -1);

        // 更新表: t_dl_permission_templated
        log.info(
                String.format(
                        "准备更新: t_dl_permission_templated, rows: %s",
                        tDlPermissionTemplatedList.size()
                )
        );
        if (!tDlPermissionTemplatedList.isEmpty()) {
            boolean isSaveSuccess = this.tDlPermissionTemplatedService.saveBatch(tDlPermissionTemplatedList);
            Assert.isTrue(
                    isSaveSuccess,
                    () -> new DatabaseException("保存 t_dl_permission_templated 表失败")
            );
        }

        //save table: t_dl_rule_interface_templated
        log.info(String.format(
                        "准备保存: t_dl_rule_interface_templated, rows: %s",
                        tDlRuleInterfaceTemplatedList.size()
                )
        );

        if (!tDlRuleInterfaceTemplatedList.isEmpty()) {
            boolean isSaveSuccess = this.tDlRuleInterfaceTemplatedService.saveBatch(tDlRuleInterfaceTemplatedList);
            Assert.isTrue(
                    isSaveSuccess,
                    () -> new DatabaseException("保存 t_dl_rule_interface_templated 表失败")
            );
        }


        // 保存表: t_dl_rule_data_row_templated
        log.info(
                String.format(
                        "准备保存表：t_dl_rule_data_row_templated, rows: %s",
                        tDlRuleDataRowTemplatedList.size()
                )
        );
        if (!tDlRuleDataRowTemplatedList.isEmpty()) {
            boolean isSaveSuccess = this.tDlRuleDataRowTemplatedService.saveBatch(tDlRuleDataRowTemplatedList);
            Assert.isTrue(
                    isSaveSuccess,
                    () -> new DatabaseException("保存 t_dl_rule_data_row_templated 表失败")
            );
        }


        // 保存表: t_dl_rule_data_column_templated
        log.info(
                String.format(
                        "准备保存表 t_dl_rule_data_column_templated, rows: %s",
                        tDlRuleDataColumnTemplatedList.size()
                )
        );

        if (!tDlRuleDataColumnTemplatedList.isEmpty()) {
            boolean isSaveSuccess = this.tDlRuleDataColumnTemplatedService.saveBatch(tDlRuleDataColumnTemplatedList);
            Assert.isTrue(
                    isSaveSuccess,
                    () -> new DatabaseException("Save or update THmmRuleDataColumnList failed")
            );
        }

        // 保存表: t_dl_permission_rule_relation_templated
        staticRuleCodeTDlPermissionMap.forEach((key, value) -> {
                    PermissionTypeEnum permissionType = value.getPermissionType();
                    switch (permissionType) {
                        case PERMISSION_INTERFACE:
                            List<TDlRuleInterfaceTemplated> interfaceTemplatedList = staticRuleCodeTDlRuleInterfaceListMap.getOrDefault(
                                    key,
                                    CollUtil.newArrayList()
                            );
                            interfaceTemplatedList
                                    .forEach(i -> {
                                        boolean e = this.tDlPermissionRuleRelationTemplatedService.lambdaQuery()
                                                .select()
                                                .eq(TDlPermissionRuleRelationTemplated::getPermissionId, value.getId())
                                                .eq(TDlPermissionRuleRelationTemplated::getRuleInterfaceId, i.getId())
                                                .exists();

                                        if (!e) {
                                            TDlPermissionRuleRelationTemplated tDlPermissionRuleRelationTemplated = new TDlPermissionRuleRelationTemplated();
                                            tDlPermissionRuleRelationTemplated.setPermissionId(value.getId());
                                            tDlPermissionRuleRelationTemplated.setRuleInterfaceId(i.getId());

                                            Assert.isTrue(
                                                    this.tDlPermissionRuleRelationTemplatedService.save(tDlPermissionRuleRelationTemplated),
                                                    () -> new DatabaseException("保存表 t_dl_permission_rule_relation_templated 失败")
                                            );
                                        }

                                    });
                            break;
                        case PERMISSION_DATA_ROW:
                            List<TDlRuleDataRowTemplated> ruleDataRowTemplatedList = staticRuleCodeTDlRuleDataRowListMap.getOrDefault(
                                    key,
                                    CollUtil.newArrayList()
                            );
                            ruleDataRowTemplatedList
                                    .forEach(i -> {
                                        boolean e = this.tDlPermissionRuleRelationTemplatedService.lambdaQuery()
                                                .select()
                                                .eq(TDlPermissionRuleRelationTemplated::getPermissionId, value.getId())
                                                .eq(TDlPermissionRuleRelationTemplated::getRuleDataRowId, i.getId())
                                                .exists();

                                        if (!e) {
                                            TDlPermissionRuleRelationTemplated tDlPermissionRuleRelationTemplated = new TDlPermissionRuleRelationTemplated();
                                            tDlPermissionRuleRelationTemplated.setPermissionId(value.getId());
                                            tDlPermissionRuleRelationTemplated.setRuleDataRowId(i.getId());

                                            Assert.isTrue(
                                                    this.tDlPermissionRuleRelationTemplatedService.save(tDlPermissionRuleRelationTemplated),
                                                    () -> new DatabaseException("保存表 t_dl_permission_rule_relation_templated 失败")
                                            );
                                        }

                                    });

                            break;
                        case PERMISSION_DATA_COLUMN:
                            List<TDlRuleDataColumnTemplated> ruleDataColumnList = staticRuleCodeTDlRuleDataColumnListMap.getOrDefault(
                                    key,
                                    CollUtil.newArrayList()
                            );
                            ruleDataColumnList
                                    .forEach(i -> {
                                        boolean e = this.tDlPermissionRuleRelationTemplatedService.lambdaQuery()
                                                .select()
                                                .eq(TDlPermissionRuleRelationTemplated::getPermissionId, value.getId())
                                                .eq(TDlPermissionRuleRelationTemplated::getRuleDataColumnId, i.getId())
                                                .exists();

                                        if (!e) {
                                            TDlPermissionRuleRelationTemplated tDlPermissionRuleRelationTemplated = new TDlPermissionRuleRelationTemplated();
                                            tDlPermissionRuleRelationTemplated.setPermissionId(value.getId());
                                            tDlPermissionRuleRelationTemplated.setRuleDataColumnId(i.getId());

                                            Assert.isTrue(
                                                    this.tDlPermissionRuleRelationTemplatedService.save(tDlPermissionRuleRelationTemplated),
                                                    () -> new DatabaseException("保存表 t_dl_permission_rule_relation_templated 失败")
                                            );
                                        }

                                    });
                            break;
                        case PERMISSION_PAGE:
                            log.info("Waiting for completion");
                            break;
                        default:
                            throw new BException(
                                    String.format(
                                            "不支持的权限类型: %s",
                                            permissionType
                                    )
                            );
                    }
                }
        );


    }

    /**
     * Description: Process each row of data in each Sheet.
     * Created by: liujingze
     * Creation time: 2022/11/29 9:35
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return RowHandler
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = Exception.class
    )
    public RowHandler createRowHandler(final Long staticVersion,
                                       final Map<String, TDlPermissionTemplated> staticRuleCodeTDlPermissionMap,
                                       final Map<String, List<TDlRuleInterfaceTemplated>> staticRuleCodeTDlRuleInterfaceListMap,
                                       final Map<String, List<TDlRuleDataRowTemplated>> staticRuleCodeTDlRuleDataRowListMap,
                                       final Map<String, List<TDlRuleDataColumnTemplated>> staticRuleCodeTDlRuleDataColumnListMap,
                                       final List<TDlPermissionTemplated> tHmmPermissionList,
                                       final List<TDlRuleInterfaceTemplated> tHmmRuleInterfaceList,
                                       final List<TDlRuleDataRowTemplated> tHmmRuleDataRowList,
                                       final List<TDlRuleDataColumnTemplated> tHmmRuleDataColumnList) {

        return (sheetIndex, rowIndex, rowCells) -> {

            switch (sheetIndex) {
                case 0:
                    log.info(String.format(
                                    "权限列表-SheetIndex: %s, RowIndex: %s, RowCells: %s",
                                    sheetIndex,
                                    rowIndex,
                                    rowCells
                            )
                    );

                    if (rowIndex >= 2) {
                        String permissionName = rowCells.get(2).toString();
                        String permissionCode = rowCells.get(3).toString();
                        PermissionTypeEnum permissionType = PermissionTypeEnum.valueOf(rowCells.get(4).toString());
                        Boolean isGlobal = Short.parseShort(rowCells.get(5).toString()) == 1;
                        Long permissionWeight = Long.valueOf(rowCells.get(6).toString());

                        Boolean isDeleted = Short.parseShort(rowCells.get(7).toString()) == 1;
                        Boolean enabled = Short.parseShort(rowCells.get(8).toString()) == 1;
                        String rejectPermissionCode = rowCells.get(9) != null ? rowCells.get(9).toString() : null;
                        String permissionComment = rowCells.get(10) != null ? rowCells.get(10).toString() : null;

                        TDlPermissionTemplated tDlPermissionTemplated = new TDlPermissionTemplated();
                        tDlPermissionTemplated.setPermissionName(permissionName);
                        tDlPermissionTemplated.setPermissionCode(permissionCode);
                        tDlPermissionTemplated.setPermissionType(permissionType);
                        tDlPermissionTemplated.setIsGlobal(isGlobal);
                        tDlPermissionTemplated.setPermissionWeight(permissionWeight);
                        tDlPermissionTemplated.setPermissionCode(rejectPermissionCode);
                        tDlPermissionTemplated.setPermissionComment(permissionComment);

                        tDlPermissionTemplated.setIsStatic(true);
                        tDlPermissionTemplated.setIsDeleted(isDeleted);
                        tDlPermissionTemplated.setEnabled(enabled);
                        tDlPermissionTemplated.setStaticVersion(staticVersion);

                        tHmmPermissionList.add(tDlPermissionTemplated);

                        // 接口规则编码
                        String staticRuleCode = rowCells.get(11) != null ? rowCells.get(11).toString() : null;
                        staticRuleCodeTDlPermissionMap.put(
                                staticRuleCode,
                                tDlPermissionTemplated
                        );

                    }
                    break;
                case 1: // Excel 第一标签页
                    log.info(String.format(
                            "接口资源规则表-SheetIndex: %s, RowIndex: %s, RowCells: %s",
                            sheetIndex,
                            rowIndex,
                            rowCells)
                    );

                    if (rowIndex >= 2) {
                        String ruleInterfaceUri = rowCells.get(1).toString();

                        TDlRuleInterfaceTemplated tDlRuleInterfaceTemplated = new TDlRuleInterfaceTemplated();
                        tDlRuleInterfaceTemplated.setRuleInterfaceUri(ruleInterfaceUri);

                        tHmmRuleInterfaceList.add(tDlRuleInterfaceTemplated);

                        //
                        String staticRuleCode = rowCells.get(2) != null ? rowCells.get(2).toString() : null;
                        List<TDlRuleInterfaceTemplated> tHmmRuleInterfaceListFromMap = staticRuleCodeTDlRuleInterfaceListMap.getOrDefault(
                                staticRuleCode,
                                new ArrayList<>()
                        );

                        tHmmRuleInterfaceListFromMap.add(tDlRuleInterfaceTemplated);
                        staticRuleCodeTDlRuleInterfaceListMap.put(
                                staticRuleCode,
                                tHmmRuleInterfaceListFromMap
                        );

                    }

                    break;
                case 2:
                    log.info(String.format(
                                    "数据行资源规则表-SheetIndex: %s, RowIndex: %s, RowCells: %s",
                                    sheetIndex,
                                    rowIndex,
                                    rowCells
                            )
                    );

                    if (rowIndex >= 2) {
                        String databaseName = rowCells.get(1).toString();
                        String tableName = rowCells.get(2).toString();
                        String columnName = rowCells.get(3).toString();
                        String ruleCondition = rowCells.get(4).toString();
                        String ruleConditionValue = rowCells.get(5).toString();

                        TDlRuleDataRowTemplated tDlRuleDataRowTemplated = new TDlRuleDataRowTemplated();
                        tDlRuleDataRowTemplated.setDatabaseName(databaseName);
                        tDlRuleDataRowTemplated.setTableName(tableName);
                        tDlRuleDataRowTemplated.setColumnName(columnName);
                        tDlRuleDataRowTemplated.setRuleCondition(ruleCondition);
                        tDlRuleDataRowTemplated.setRuleConditionValue(ruleConditionValue);

                        tHmmRuleDataRowList.add(tDlRuleDataRowTemplated);

                        //Rule relation
                        String staticRuleCode = rowCells.get(6).toString();
                        List<TDlRuleDataRowTemplated> tHmmRuleDataRowListFromMap = staticRuleCodeTDlRuleDataRowListMap.getOrDefault(
                                staticRuleCode,
                                CollUtil.newArrayList()
                        );

                        tHmmRuleDataRowListFromMap.add(tDlRuleDataRowTemplated);
                        staticRuleCodeTDlRuleDataRowListMap.put(
                                staticRuleCode,
                                tHmmRuleDataRowListFromMap
                        );
                    }

                    break;
                case 3:
                    log.info(String.format(
                                    "数据列资源规则表-SheetIndex: %s, RowIndex: %s, RowCells: %s",
                                    sheetIndex,
                                    rowIndex,
                                    rowCells
                            )
                    );

                    if (rowIndex >= 2) {
                        String databaseName = rowCells.get(1).toString();
                        String tableName = rowCells.get(2).toString();
                        String columnName = rowCells.get(3).toString();
                        Boolean isAllow = Short.parseShort(rowCells.get(4).toString()) == 1;

                        TDlRuleDataColumnTemplated tDlRuleDataColumnTemplated = new TDlRuleDataColumnTemplated();
                        tDlRuleDataColumnTemplated.setDatabaseName(databaseName);
                        tDlRuleDataColumnTemplated.setTableName(tableName);
                        tDlRuleDataColumnTemplated.setColumnName(columnName);
                        tDlRuleDataColumnTemplated.setIsAllow(isAllow);

                        tHmmRuleDataColumnList.add(tDlRuleDataColumnTemplated);

                        //Rule relation
                        String staticRuleCode = rowCells.get(5).toString();
                        List<TDlRuleDataColumnTemplated> tHmmRuleDataColumnListFromMap = staticRuleCodeTDlRuleDataColumnListMap.getOrDefault(
                                staticRuleCode,
                                CollUtil.newArrayList()
                        );

                        tHmmRuleDataColumnListFromMap.add(tDlRuleDataColumnTemplated);
                        staticRuleCodeTDlRuleDataColumnListMap.put(
                                staticRuleCode,
                                tHmmRuleDataColumnListFromMap
                        );
                    }

                    break;
                default:
                    throw new BException(
                            String.format(
                                    "Not supported, SheetIndex: %s",
                                    sheetIndex
                            )
                    );
            }


        };
    }
}
