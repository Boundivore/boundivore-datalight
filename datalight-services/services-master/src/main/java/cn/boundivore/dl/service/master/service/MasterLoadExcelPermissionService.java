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
import cn.hutool.core.io.FileUtil;
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


    /**
     * Description: 初始化权限配置模板
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param confPath Excel 权限模板配置文件所在目录
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = Exception.class
    )
    public void initPermissionTable(String confPath) {

        String tableInitializerPath = String.format(
                "%s/%s",
                confPath,
                "PermissionTableExcel-v1.xlsx"
        );

        if (!FileUtil.exist(tableInitializerPath)) {
            log.info(
                    String.format(
                            "未找到权限配置模板: %s",
                            tableInitializerPath
                    )
            );
            return;
        }

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
                    () -> new DatabaseException("保存 t_dl_rule_data_column_templated 失败")
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
     * Description: 处理每一个 Sheet 中的每一行数据
     * Created by: Boundivore
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return RowHandler 行处理器
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
                                       final List<TDlPermissionTemplated> tDlPermissionList,
                                       final List<TDlRuleInterfaceTemplated> tDlRuleInterfaceList,
                                       final List<TDlRuleDataRowTemplated> tDlRuleDataRowList,
                                       final List<TDlRuleDataColumnTemplated> tDlRuleDataColumnList) {

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

                    // 检查 rowCells 中索引 0 到 12 之间是否存在 null 值
                    for (int x = 0; x <= 12; x++) {
                        if (x == 9) {
                            continue;
                        }

                        if (rowCells.size() <= x || rowCells.get(x) == null) {
                            // 如果存在 null，则跳过本次循环
                            return;
                        }
                    }

                    if (rowIndex >= 2) {
                        String num = rowCells.get(0) != null ? rowCells.get(0).toString() : null;
                        String id = rowCells.get(1) != null ? rowCells.get(1).toString() : null;
                        String permissionName = rowCells.get(2) != null ? rowCells.get(2).toString() : null;
                        String permissionCode = rowCells.get(3) != null ? rowCells.get(3).toString() : null;
                        PermissionTypeEnum permissionType = rowCells.get(4) != null ? PermissionTypeEnum.valueOf(rowCells.get(4).toString()) : null;
                        Boolean isGlobal = rowCells.get(5) != null && Short.parseShort(rowCells.get(5).toString()) == 1;
                        Long permissionWeight = rowCells.get(6) != null ? Long.parseLong(rowCells.get(6).toString()) : -1L;

                        Boolean isDeleted = rowCells.get(7) != null && Short.parseShort(rowCells.get(7).toString()) == 1;
                        Boolean enabled = rowCells.get(8) != null && Short.parseShort(rowCells.get(8).toString()) == 1;
                        String rejectPermissionCode = rowCells.get(9) != null ? rowCells.get(9).toString() : null;
                        String permissionComment = rowCells.get(10) != null ? rowCells.get(10).toString() : null;

                        TDlPermissionTemplated tDlPermissionTemplated = new TDlPermissionTemplated();
                        tDlPermissionTemplated.setPermissionName(permissionName);
                        tDlPermissionTemplated.setPermissionCode(permissionCode);
                        tDlPermissionTemplated.setPermissionType(permissionType);
                        tDlPermissionTemplated.setIsGlobal(isGlobal);
                        tDlPermissionTemplated.setPermissionWeight(permissionWeight);
                        tDlPermissionTemplated.setRejectPermissionCode(rejectPermissionCode);
                        tDlPermissionTemplated.setPermissionComment(permissionComment);

                        tDlPermissionTemplated.setIsStatic(true);
                        tDlPermissionTemplated.setIsDeleted(isDeleted);
                        tDlPermissionTemplated.setEnabled(enabled);
                        tDlPermissionTemplated.setStaticVersion(staticVersion);

                        tDlPermissionList.add(tDlPermissionTemplated);

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

                    // 检查 rowCells 中索引 0 到 12 之间是否存在 null 值
                    for (int x = 0; x <= 2; x++) {
                        if (rowCells.size() <= x || rowCells.get(x) == null) {
                            // 如果存在 null，则跳过本次循环
                            return;
                        }
                    }

                    if (rowIndex >= 2) {
                        String ruleInterfaceUri = rowCells.get(1).toString();

                        TDlRuleInterfaceTemplated tDlRuleInterfaceTemplated = new TDlRuleInterfaceTemplated();
                        tDlRuleInterfaceTemplated.setRuleInterfaceUri(ruleInterfaceUri);

                        tDlRuleInterfaceList.add(tDlRuleInterfaceTemplated);

                        //
                        String staticRuleCode = rowCells.get(2) != null ? rowCells.get(2).toString() : null;
                        List<TDlRuleInterfaceTemplated> tDlRuleInterfaceListFromMap = staticRuleCodeTDlRuleInterfaceListMap.getOrDefault(
                                staticRuleCode,
                                new ArrayList<>()
                        );

                        tDlRuleInterfaceListFromMap.add(tDlRuleInterfaceTemplated);
                        staticRuleCodeTDlRuleInterfaceListMap.put(
                                staticRuleCode,
                                tDlRuleInterfaceListFromMap
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

                    // 检查 rowCells 中索引 0 到 12 之间是否存在 null 值
                    for (int x = 0; x <= 6; x++) {
                        if (rowCells.size() <= x || rowCells.get(x) == null) {
                            // 如果存在 null，则跳过本次循环
                            return;
                        }
                    }

                    if (rowIndex >= 2) {
                        String databaseName = rowCells.get(1) != null ? rowCells.get(1).toString() : null;
                        String tableName = rowCells.get(2) != null ? rowCells.get(2).toString() : null;
                        String columnName = rowCells.get(3) != null ? rowCells.get(3).toString() : null;
                        String ruleCondition = rowCells.get(4) != null ? rowCells.get(4).toString() : null;
                        String ruleConditionValue = rowCells.get(5) != null ? rowCells.get(5).toString() : null;

                        TDlRuleDataRowTemplated tDlRuleDataRowTemplated = new TDlRuleDataRowTemplated();
                        tDlRuleDataRowTemplated.setDatabaseName(databaseName);
                        tDlRuleDataRowTemplated.setTableName(tableName);
                        tDlRuleDataRowTemplated.setColumnName(columnName);
                        tDlRuleDataRowTemplated.setRuleCondition(ruleCondition);
                        tDlRuleDataRowTemplated.setRuleConditionValue(ruleConditionValue);

                        tDlRuleDataRowList.add(tDlRuleDataRowTemplated);

                        // 规则相关
                        String staticRuleCode = rowCells.get(6) != null ? rowCells.get(6).toString() : null;
                        List<TDlRuleDataRowTemplated> tDlRuleDataRowListFromMap = staticRuleCodeTDlRuleDataRowListMap.getOrDefault(
                                staticRuleCode,
                                CollUtil.newArrayList()
                        );

                        tDlRuleDataRowListFromMap.add(tDlRuleDataRowTemplated);
                        staticRuleCodeTDlRuleDataRowListMap.put(
                                staticRuleCode,
                                tDlRuleDataRowListFromMap
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

                    // 检查 rowCells 中索引 0 到 12 之间是否存在 null 值
                    for (int x = 0; x <= 5; x++) {
                        if (rowCells.size() <= x || rowCells.get(x) == null) {
                            // 如果存在 null，则跳过本次循环
                            return;
                        }
                    }

                    if (rowIndex >= 2) {
                        String databaseName = rowCells.get(1) != null ? rowCells.get(1).toString() : null;
                        String tableName = rowCells.get(2) != null ? rowCells.get(2).toString() : null;
                        String columnName = rowCells.get(3) != null ? rowCells.get(3).toString() : null;
                        Boolean isAllow = rowCells.get(4) != null && Short.parseShort(rowCells.get(4).toString()) == 1;

                        TDlRuleDataColumnTemplated tDlRuleDataColumnTemplated = new TDlRuleDataColumnTemplated();
                        tDlRuleDataColumnTemplated.setDatabaseName(databaseName);
                        tDlRuleDataColumnTemplated.setTableName(tableName);
                        tDlRuleDataColumnTemplated.setColumnName(columnName);
                        tDlRuleDataColumnTemplated.setIsAllow(isAllow);

                        tDlRuleDataColumnList.add(tDlRuleDataColumnTemplated);

                        //Rule relation
                        String staticRuleCode = rowCells.get(5) != null ? rowCells.get(5).toString() : null;
                        List<TDlRuleDataColumnTemplated> tDlRuleDataColumnListFromMap = staticRuleCodeTDlRuleDataColumnListMap.getOrDefault(
                                staticRuleCode,
                                CollUtil.newArrayList()
                        );

                        tDlRuleDataColumnListFromMap.add(tDlRuleDataColumnTemplated);
                        staticRuleCodeTDlRuleDataColumnListMap.put(
                                staticRuleCode,
                                tDlRuleDataColumnListFromMap
                        );
                    }

                    break;
                default:
                    throw new BException(
                            String.format(
                                    "尚未被定义支持的 Sheet 页面, SheetIndex: %s",
                                    sheetIndex
                            )
                    );
            }


        };
    }
}
