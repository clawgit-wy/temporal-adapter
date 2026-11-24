package com.yutong.safety.common.param.web;

import lombok.Data;

import java.util.Date;

/**
 * create by gwcuiyx on 2023.08.08
 */
@Data
public class DataSendLogAddParam {
    /**
     * 访客姓名
     */
    private String visitorName;
    /**
     * 访客身份证号
     */
    private String visitorIdCard;
    /**
     * 访问厂区
     */
    private String vendorFactory;
    /**
     * 访问区域
     */
    private String vendorRegion;
    /**
     * 厂区ID
     */
    private String factoryId;
    /**
     * 区域ID
     */
    private String regionId;
    /**
     * 入厂开始时间
     */
    private Date startTime;
    /**
     * 入厂结束时间
     */
    private Date endTime;
    /**
     * 照片
     */
    private String photo;
    /**
     * 发送状态
     */
    private Integer sendState;
    /**
     * 访客数据id
     */
    private Integer hairpinId;

    /**
     * 人脸相关数据接口返回值
     */
    private String jointResult;
    /**
     * 访客的手机号
     */
    private String visitorPhone;
    /**
     * eip流程创建时间
     */
    private Date eipCreateTime;


    private String idCardPhoto;

}
