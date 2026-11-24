package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.excel.core.BaseImportData;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 排班表导入 VO
 * @Author: jeecg-boot
 * @Date: 2026-08-04
 * @Version: V1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShiftScheduleImportVO extends BaseImportData {

    @Excel(name = "排班日期*", width = 15)
    @NotBlank(message = "排班日期不能为空")
    @ApiModelProperty(value = "排班日期")
    private String scheduleDate;

    @Excel(name = "人员类型*", width = 15)
    @NotBlank(message = "人员类型不能为空")
    @ApiModelProperty(value = "人员类型：安保带班、消防值班")
    private String personType;

    @Excel(name = "员工号*", width = 15)
    @NotBlank(message = "员工号不能为空")
    @ApiModelProperty(value = "员工号")
    private String empNo;

    @Excel(name = "所属厂区*", width = 20)
    @NotBlank(message = "所属厂区不能为空")
    @ApiModelProperty(value = "所属厂区")
    private String factory;

    @Excel(name = "班次代码", width = 15)
    @ApiModelProperty(value = "班次代码")
    private String shiftCode;
}
