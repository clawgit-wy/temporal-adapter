package com.yutong.modules.risk.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 在岗签到月度汇总 VO
 * @Author: jeecg-boot
 * @Date: 2026-08-04
 * @Version: V1.0
 */
@Data
public class DutyCheckinMonthlyVO {

    /** 已在岗数量 */
    private int inDutyCount;

    /** 未在岗数量 */
    private int notInDutyCount;

    /** 已补签数量 */
    private int makeupCount;

    /** 签到记录列表（按日期倒序） */
    private List<DutyCheckinRecordVO> records;
}
