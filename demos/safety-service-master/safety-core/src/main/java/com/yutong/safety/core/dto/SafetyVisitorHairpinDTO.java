package com.yutong.safety.core.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SafetyVisitorHairpinDTO {
    private Integer id;

    private String visitorName;

    private String visitorPhone;

    private String visitorIdCard;

    private String vendorFactory;

    private String vendorRegion;

    private String visitedName;

    private String visitedPhone;

    private String vendorMatter;

    private Date startTime;

    private Date endTime;

    private String vendorUnit;

    private String cardNo;

    private String photo;

    private Integer vendorState;

    private String hairpinNo;

    private String recoveryNo;

    private Date createTime;

    private Date updateTime;

    private Boolean deleteFalg;

    private String eipFlowId;

    private Integer secretState;

    private String secretRegion;

    private Date hairpinTime;

    private Date recoveryTime;

    private String factoryId;

    private String regionId;

    private String wxPhone;

    private String qrCode;

    private Integer rowCount;

    private Integer showNum;

    private String authFactoryNames;

    private Integer userId;

    private String visitedDept;

    //已授权厂区
    private String issueFactory;
    //未授权厂区
    private String unIssueFactory;
}
