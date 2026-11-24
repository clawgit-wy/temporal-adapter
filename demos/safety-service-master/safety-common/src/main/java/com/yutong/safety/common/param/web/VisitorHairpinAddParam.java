package com.yutong.safety.common.param.web;

import com.yutong.safety.common.param.Check;
import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.14
 */
@Data
public class VisitorHairpinAddParam implements Check {
    /**
     * 被访人姓名
     */
    private String visitedName;
    /**
     * 被访人手机号
     */
    private String visitedPhone;
    /**
     * 访客姓名
     */
    private String visitorName;
    /**
     * 访客手机号
     */
    private String visitorPhone;
    /**
     * 访客身份证号
     */
    private String visitorIdCard;
    /**
     * 访客单位
     */
    private String vendorUnit;
    /**
     * 访问事由
     */
    private String vendorMatter;
    /**
     * 车牌号
     */
    private String cardNo;
    /**
     * 入厂开始时间
     */
    private String startTime;
    /**
     * 入厂结束时间
     */
    private String endTime;
    /**
     * 照片
     */
    private String photo;
    /**
     * eip流程id
     */
    private String eipId;

    /**
     * 是否是小程序触发
     */
    private String wxFlag;

    /**
     * 微信登录的电话号码
     */
    private String wxPhone;

    /**
     * 被访人部门
     */
    private String visitedDept;

    /**
     * eip流程创建时间
     */
    private String eipCreateTime;

    /**
     * 身份证照片
     */
    private String idCardPhoto;

    /**
     * 身份证照片是否保存，0： 否，1：是
     */
    private Integer isSaveCardPhoto;

    @Override
    public void check() {

    }
}
