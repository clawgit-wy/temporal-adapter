package com.yutong.modules.risk.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 在岗签到参数
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class DutyCheckinAddParam {

    @ApiModelProperty("厂区 ID")
    private Long factoryId;

    @ApiModelProperty(value = "厂区", required = true)
    private String factory;

    @ApiModelProperty("位置")
    private String location;

    @ApiModelProperty(value = "位置 ID")
    private Long locationId;

    @ApiModelProperty(value = "人员类型")
    private String personType;

    @ApiModelProperty(value = "值班日期")
    private String dutyDate;
}
