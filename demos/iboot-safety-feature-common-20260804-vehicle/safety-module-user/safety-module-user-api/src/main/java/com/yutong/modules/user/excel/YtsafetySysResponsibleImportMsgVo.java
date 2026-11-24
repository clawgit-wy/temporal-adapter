package com.yutong.modules.user.excel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtsafetySysResponsibleImportMsgVo {

    @Excel(name = "错误原因", width = 15)
    private String err;

    @Excel(name = "员工号", width = 15)
    private String EmpNo;

    @Excel(name = "员工名", width = 15)
    private String EmpName;

    @Excel(name = "行数", width = 15)
    private Integer num;
}
