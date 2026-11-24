package com.yutong.safety.common.param.web;

import lombok.Data;

import java.util.List;

/**
 * create by gwcuiyx on 2023.07.10
 */
@Data
public class BlackMessageListParam extends WebPageParam {
    //公司
    private String companyName;
    //姓名
    private String empName;
    //卡号
    private String cardNo;
    //手机号
    private String phoneNo;
    //身份证号
    private String idCardNo;
    //车辆
    private String carNo;
    //操作员
    private String operator;
    //人员类型
    private String empType;
    //更新时间
    private String startTime;
    //更新时间
    private String endTime;
    //状态
    private Integer state;
    //数据来源
    private String source;

    private String lessAuthorizationDate;

    private Boolean authorizationDateIsNotNull;

    private List<String> cardNoList;

    private List<String> idCardNoList;

    private String positionType;
    /**
     * 用工类型
     */
    private List<String> positionTypes;
    private boolean isNullPositionType;

}
