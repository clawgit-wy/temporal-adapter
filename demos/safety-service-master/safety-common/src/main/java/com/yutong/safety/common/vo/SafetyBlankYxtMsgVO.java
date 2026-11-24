package com.yutong.safety.common.vo;

import com.yutong.safety.common.constants.SafetyYxtMsgEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
public class SafetyBlankYxtMsgVO {

    /**
     * 消息内容
     */
    private SafetyYxtMsgEnum yxtMsgEnum;

    /**
     * 员工工号(对接人)
     */
    private String workEmpNo;
    /**
     * 员工姓名(对接人)
     */
    private String workEmpName;
    /**
     * 黑名单人员
     */
    private String blankEmpName;
    /**
     * 身份证号
     */
    private String cardNo;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 提交时间
     */
    private Date currentDate;
    /**
     * 集团非友商负责人 名称+员工号
     */
    private String competitorInfo;
    /**
     * 供应商信息名称
     */
    private String supplierName;
    /**
     * 供应商信息手机号
     */
    private String supplierPhone;

    public SafetyBlankYxtMsgVO() {
    }

    public SafetyBlankYxtMsgVO(SafetyYxtMsgEnum yxtMsgEnum, String workEmpNo, String workEmpName, String blankEmpName, String cardNo, String phone, String competitorInfo) {
        this.yxtMsgEnum = yxtMsgEnum;
        this.workEmpNo = workEmpNo;
        this.workEmpName = workEmpName;
        this.blankEmpName = blankEmpName;
        this.cardNo = cardNo;
        this.phone = phone;
        this.currentDate = new Date();
        this.competitorInfo = competitorInfo;
    }

    public SafetyBlankYxtMsgVO(SafetyYxtMsgEnum yxtMsgEnum, String workEmpNo, String workEmpName, String blankEmpName, String cardNo, String phone, String competitorInfo, String supplierName, String supplierPhone) {
        this.yxtMsgEnum = yxtMsgEnum;
        this.workEmpNo = workEmpNo;
        this.workEmpName = workEmpName;
        this.blankEmpName = blankEmpName;
        this.cardNo = cardNo;
        this.phone = phone;
        this.currentDate = new Date();
        this.competitorInfo = competitorInfo;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
    }

    public void check() {
        if (StringUtils.isBlank(this.workEmpName)) {
            this.workEmpName = "无";
        }
        if (StringUtils.isBlank(this.blankEmpName)) {
            this.blankEmpName = "无";
        }
        if (StringUtils.isBlank(this.cardNo)) {
            this.cardNo = "无";
        }
        if (StringUtils.isBlank(this.phone)) {
            this.phone = "无";
        }
    }
}
