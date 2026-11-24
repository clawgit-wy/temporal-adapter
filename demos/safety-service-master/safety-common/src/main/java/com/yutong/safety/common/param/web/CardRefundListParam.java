package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwcuiyx
 * @date 2024-1-18 10:16
 */
@Data
public class CardRefundListParam extends WebPageParam {
    //被访人部门
    private String visitedDept;
    //发卡厂区
    private String hairpinFactory;
    //遗失天数开始范围
    private Integer loseStartDay;
    //遗失天数结束范围
    private Integer loseEndDay;
    //访客信息
    private String visitorName;
    //访客手机号
    private String visitorPhone;
    //访客身份证号
    private String visitorIdCard;
    //发卡开始时间
    private String hairpinStartTime;
    //发卡结束时间
    private String hairpinEndTime;
    //发卡人姓名
    private String userName;
}
