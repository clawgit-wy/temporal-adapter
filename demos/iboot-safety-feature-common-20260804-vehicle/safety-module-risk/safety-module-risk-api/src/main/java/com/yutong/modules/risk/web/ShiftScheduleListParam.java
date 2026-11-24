package com.yutong.modules.risk.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 排班表查询参数
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class ShiftScheduleListParam extends WebPageParam {

    @ApiModelProperty("排班日期开始")
    private String scheduleDateStart;

    @ApiModelProperty("排班日期结束")
    private String scheduleDateEnd;

    @ApiModelProperty("厂区 ID")
    private Long factoryId;

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("人员类型 1：在岗人员 2：安保带班 3：消防值班")
    private String personType;
}
