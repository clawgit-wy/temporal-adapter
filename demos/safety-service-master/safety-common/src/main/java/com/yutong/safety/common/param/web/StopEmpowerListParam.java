package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * create by gwcuiyx on 2023.07.18
 */
@Data
public class StopEmpowerListParam extends WebPageParam {
    private String visitorName;
    private String visitorPhone;
    private String visitorIdCard;
    private String empowerNo;
    private String empowerFactory;
    private String cardNo;
    private String checkStartTime;
    private String checkEndTime;
    private String powerSartTime;
    private String powerEndTime;
}
