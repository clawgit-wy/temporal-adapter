package com.yutong.modules.risk.vo;

import lombok.Data;

/**
 * @Description: 在岗签到记录条目 VO
 * @Author: jeecg-boot
 * @Date: 2026-08-04
 * @Version: V1.0
 */
@Data
public class DutyCheckinRecordVO {

    /** 日期（格式化：07-09 星期二） */
    private String dutyDate;

    /** 员工号 */
    private String empNo;

    /** 姓名 */
    private String empName;

    /** 厂区 */
    private String factory;

    /** 岗位 */
    private String position;

    /** 位置 */
    private String location;

    /** 人员类型 */
    private String personType;

    /** 在岗状态：在岗 / 未到岗 */
    private String dutyStatusText;

    /** 是否补签：0-否 1-是 */
    private Integer isMakeup;

    /** 签到时间 */
    private String checkinTime;
}
