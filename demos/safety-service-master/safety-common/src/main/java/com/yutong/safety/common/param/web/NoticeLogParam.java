package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author lukunpeng
 * @since 2024-04-11 10:47
 */
@Data
public class NoticeLogParam {

    private String carNo; //车牌
    private String staffNo; //员工号
    private String factory; //工厂
    private String noticeTimeStart; //通知时间 开始
    private String noticeTimeEnd; //通知时间 结束
    private String carStatus; //当前车辆是否在场
    private String noticeStatus; //通知状态
    private String noticeType; //通知类型
    private String remindType; //当前通知类型
    private String inTime; //入厂时间
    private int pageNum;
    private int pageSize;
}
