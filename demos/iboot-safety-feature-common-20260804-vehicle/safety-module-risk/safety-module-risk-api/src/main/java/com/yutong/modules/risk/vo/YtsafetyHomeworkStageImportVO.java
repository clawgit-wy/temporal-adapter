package com.yutong.modules.risk.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.excel.core.BaseImportData;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 作业环节Excel导入模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YtsafetyHomeworkStageImportVO extends BaseImportData {

    @Excel(name = "作业环节编码")
    private String linkCode;

    @Excel(name = "作业环节名称*")
    private String linkName;

    @Excel(name = "所属厂区名称*")
    private String factoryName;

    @Excel(name = "所属区域名称*")
    private String areaName;

    @Excel(name = "所属组织名称*")
    private String orgName;

    @Excel(name = "位置名称")
    private String locationName;
}
