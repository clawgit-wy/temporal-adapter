package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * create by gwcuiyx on 2023.08.08
 */
@Data
public class DataSendLogListParam extends WebPageParam {
    /**
     * 访客姓名
     */
    private String visitorName;
    /**
     * 访客身份证号
     */
    private String visitorIdCard;
    /**
     * 厂区id
     */
    private Integer factoryId;
    /**
     * 区域id
     */
    private Integer regionId;
    /**
     * 下发状态
     */
    private Integer sendState;
    /**
     * 下发开始时间
     */
    private String startTime;
    /**
     * 下发结束时间
     */
    private String endTime;
    /**
     * 访客手机号
     */
    private String visitorPhone;
}
