package com.yutong.safety.common.vo;

import lombok.Data;

@Data
public class NoticeLogVO {
    private String carNo; //车牌
    private String carType; //车辆类型
    private String carCate; //车辆型号
    private String staffName; //员工名称
    private String staffNo; //员工号
    private String phone; //电话
    private String factory; //厂区
    private String stopHours; //停车时长
    private int noticeNums; //通知次数
    private int retentionNums; //滞留次数
    private String noticeType; //应有通知方式
    private String noticeTime; //通知时间
    private String inTime; //车辆入厂时间
    private String remindType; //最后通知方式
}
