package com.yutong.modules.risk.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 在岗签到表查询参数
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class DutyCheckinListParam extends WebPageParam {

    @ApiModelProperty("厂区 ID")
    private Long factoryId;

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("姓名")
    private String empName;

    @ApiModelProperty(value = "人员类型 1：在岗人员 2：安保带班 3：消防值班",required = true)
    private String personType;

    @ApiModelProperty("岗位")
    private String position;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty("厂区")
    private String factory;

    @ApiModelProperty("值班日期")
    private String dutyDate;

    @ApiModelProperty("岗位状态：0-未到岗 1-在岗")
    private Integer dutyStatus;

    @ApiModelProperty("是否补签：0-否 1-是")
    private Integer isMakeup;

    /**是否查询当天（前端不传）*/
    @JsonIgnore
    private Boolean today;
}
