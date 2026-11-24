package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 排班表导出 VO
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class ShiftScheduleExportVO {

    @Excel(name = "排班日期", width = 15)
    private String scheduleDate;

    @Excel(name = "员工号", width = 15)
    private String empNo;

    @Excel(name = "姓名", width = 12)
    private String empName;

    @Excel(name = "人员类型", width = 15)
    @ApiModelProperty(value = "人员类型 1：在岗人员 2：安保带班 3：消防值班")
    private String personType;

    @Excel(name = "岗位", width = 15)
    private String position;

    @Excel(name = "所属厂区", width = 20)
    private String factory;

    @Excel(name = "班次代码", width = 15)
    private String shiftCode;

}
