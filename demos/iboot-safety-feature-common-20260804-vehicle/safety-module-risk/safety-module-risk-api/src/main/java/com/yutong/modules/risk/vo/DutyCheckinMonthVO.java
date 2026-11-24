package com.yutong.modules.risk.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 月度签到查询中间 VO（LEFT JOIN 员工表，含姓名和岗位）
 * @Author: jeecg-boot
 * @Date: 2026-08-10
 * @Version: V1.0
 */
@Data
public class DutyCheckinMonthVO {

    /** 员工号 */
    private String empNo;

    /** 姓名 */
    private String empName;

    /** 岗位 */
    private String position;

    /** 人员类型 */
    private String personType;

    /** 厂区 */
    private String factory;

    /** 位置 */
    private String location;

    private Long locationId;

    /** 值班日期 */
    private String dutyDate;

    /** 签到时间 */
    private Date checkinTime;

    /** 岗位状态：0-未到岗 1-在岗 */
    private Integer dutyStatus;

    /** 是否补签：0-否 1-是 */
    private Integer isMakeup;
}
