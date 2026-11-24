package com.yutong.modules.risk.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 在岗签到表导出 VO
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class DutyCheckinExportVO {

    @Excel(name = "人员类型", width = 15)
    private String personType;

    @Excel(name = "员工号", width = 15)
    private String empNo;

    @Excel(name = "姓名", width = 12)
    private String empName;

    @Excel(name = "手机号", width = 12)
    private String phone;

    @Excel(name = "岗位", width = 15)
    private String position;

    @Excel(name = "厂区", width = 20)
    private String factory;

    @Excel(name = "位置", width = 20)
    private String location;

    @Excel(name = "部门班组", width = 20)
    private String departmentTeam;

    @Excel(name = "日期", width = 15)
    private String dutyDate;

    @Excel(name = "班次", width = 15)
    private String shiftCode;

    @Excel(name = "岗位状态", width = 12)
    private String dutyStatus;

}
