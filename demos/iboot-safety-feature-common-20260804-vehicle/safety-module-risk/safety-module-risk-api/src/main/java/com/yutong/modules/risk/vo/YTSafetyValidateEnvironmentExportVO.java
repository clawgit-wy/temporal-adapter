package com.yutong.modules.risk.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 业务验证-货车环保标准抽查结果导出VO
 */
@Data
public class YTSafetyValidateEnvironmentExportVO {

    @Excel(name = "事件编号", width = 18)
    private String flowNo;

    @Excel(name = "厂区", width = 15)
    private String factory;

    @Excel(name = "位置", width = 15)
    private String location;

    @Excel(name = "抽查时间", width = 15)
    private String checkTime;

    @Excel(name = "车牌号", width = 15)
    private String carNumber;

    @Excel(name = "驾驶员姓名", width = 12)
    private String driverName;

    @Excel(name = "联系电话", width = 15)
    private String telephone;

    @Excel(name = "排放标准", width = 12)
    private String emissionStandard;

    @Excel(name = "是否加入黑名单", width = 12)
    private String isBlacklist;

    @Excel(name = "抽查人", width = 12)
    private String checkPerson;

    @Excel(name = "抽查人部门", width = 20)
    private String checkDept;

    @Excel(name = "加入黑名单时间", width = 20)
    private String blackTime;

    @Excel(name = "加入黑名单结果", width = 15)
    private String blackResult;

    @Excel(name = "出入口接口返回状态", width = 25)
    private String interfaceStatus;
}
