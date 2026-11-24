package com.yutong.modules.risk.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import lombok.Data;

/**
 * 监控岗位验证结果列表导出VO
 */
@Data
public class YTSafetyMonitorPostExportVO {

    @Excel(name = "事件编号", width = 18)
    private String flowNo;

    @Excel(name = "厂区", width = 15)
    private String factory;

    @Excel(name = "位置/岗位", width = 15)
    private String positionPost;

    @Excel(name = "验证时间", width = 15)
    private String checkTimeStart;

    @Excel(name = "异常事件类型", width = 15)
    private String abnormalType;

    @Excel(name = "异常事件描述", width = 25)
    private String abnormalDesc;

    @Excel(name = "异常事件接收人", width = 18)
    private String abnormalCreator;

    @Excel(name = "异常事件接收人部门班组", width = 20)
    private String abnormalDept;

    @Excel(name = "验证对象", width = 18)
    private String checkName;

    @Excel(name = "验证对象部门班组", width = 20)
    private String checkDept;

    @Excel(name = "是否异常", width = 10)
    private String checkException;

    @Excel(name = "是否考核兑现", width = 12)
    private String examFulfill;

    @Excel(name = "整改处理结果", width = 20)
    private String improveResult;

    @Excel(name = "镜头名称", width = 15)
    private String lensName;

    @Excel(name = "方式途径", width = 15)
    private String waysMeans;

    @Excel(name = "验证人姓名", width = 12)
    private String submitName;

    @Excel(name = "验证人部门班组", width = 20)
    private String submitDept;

    @Excel(name = "登记时间", width = 20)
    private String createTime;

    @Excel(name = "处理时间", width = 20)
    private String publishTime;

    @Excel(name = "备注", width = 20)
    private String remark;

    @Excel(name = "详情链接", width = 20)
    private String link;
}
