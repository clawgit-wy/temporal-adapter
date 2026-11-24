package com.yutong.modules.risk.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import lombok.Data;

/**
 * 岗位验证-模拟岗位验证结果导出VO（dataType=2）
 */
@Data
public class YTSafetyValidateScenePostSimulateExportVO {

    @Excel(name = "事件编号", width = 18)
    private String flowNo;

    @Excel(name = "厂区", width = 15)
    private String factory;

    @Excel(name = "验证项目", width = 15)
    private String teamName;

    @Excel(name = "验证时间", width = 20)
    private String validateTime;

    @Excel(name = "模拟验证位置", width = 15)
    private String postName;

    @Excel(name = "到达现场用时", width = 12)
    private String reachDuration;

    @Excel(name = "出警人", width = 12)
    private String empName;

    @Excel(name = "出警人部门班组", width = 20)
    private String empDept;

    @Excel(name = "验证过程动作及结构", width = 25)
    private String validateDesc;

    @Excel(name = "验证是否合格", width = 12)
    private String isPass;

    @Excel(name = "是否考核兑现", width = 12)
    private String isExamine;

    @Excel(name = "整改处理结果", width = 20)
    private String reformDesc;

    @Excel(name = "验证人姓名", width = 12)
    private String createEmp;

    @Excel(name = "验证人班组部门", width = 20)
    private String createDept;

    @Excel(name = "出警人直系领导", width = 18)
    private String leaderEmpName;

    @Excel(name = "登记时间", width = 20)
    private String createTime;

    @Excel(name = "处理时间", width = 20)
    private String dealTime;

    @Excel(name = "备注", width = 20)
    private String remark;

    @Excel(name = "详情链接", width = 20)
    private String link;
}
