package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 今日在岗状态查询VO
 * @date 2026-08-10
 */
@Data
@ApiModel(value = "今日在岗状态查询VO")
public class DutyCheckinTodayVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "今日日期（格式：2026年08月10日 星期一）")
    private String todayDate;

    @ApiModelProperty(value = "上岗状态：0-未在岗 1-在岗")
    private Integer dutyStatus;

    @ApiModelProperty(value = "人员类型：1-在岗人员 2-安保带班 3-消防值班")
    private String personType;
}
