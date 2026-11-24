package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.14
 */
@Data
public class VisitorHairpinListParam extends WebPageParam {
    //访客姓名
    private String visitorName;
    //访客手机号
    private String visitorPhone;
    //访客身份证号
    private String visitorIdCard;
    //被访人姓名
    private String visitedName;
    //被访人手机号
    private String visitedPhone;
    //访问状态
    private Integer vendorState;
    //访问厂区
    private String vendorFactory;
    //访问区域
    private String vendorRegion;
    //访问事由
    private String vendorMatter;
    //访客单位
    private String vendorUnit;
    //发卡时间
    private String hairpinTime;
    //退卡时间
    private String recoveryTime;
    //车牌号
    private String cardNo;
    //厂区id
    private Integer factoryId;
    //访问时间
    private String startTime;
    //访问结束时间
    private String endTime;
    //卡号ID
    private String hairpinNo;
    //进出权限状态
    private String inFactoryProcess;
    private String actInFactory;
    private String secretState;

    //授权厂区不等访问厂区
    private String equalFactory;
}
