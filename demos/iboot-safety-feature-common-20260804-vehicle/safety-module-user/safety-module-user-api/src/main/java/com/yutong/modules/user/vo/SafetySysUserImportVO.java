package com.yutong.modules.user.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class SafetySysUserImportVO {

    @Excel(name = "错误原因", width = 15)
    private String err;

    @Excel(name = "员工编号", width = 15)
    private String EmpNo;

    @Excel(name = "员工姓名", width = 15)
    private String EmpName;

    @Excel(name = "行数", width = 15)
    private Integer num;
}
