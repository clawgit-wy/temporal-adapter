package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 车辆白名单查询参数
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Data
public class CarWhitelistListParam {

    @ApiModelProperty("白名单类型：1-人员白名单 2-车辆类型白名单")
    private Integer whitelistType;

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("姓名")
    private String empName;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("车辆类型")
    private String carType;

    @ApiModelProperty("状态：0-停用 1-启用")
    private Integer status;

    @ApiModelProperty("有效期开始时间-开始")
    private String validStartTimeStart;

    @ApiModelProperty("有效期开始时间-结束")
    private String validStartTimeEnd;

    @ApiModelProperty("有效期结束时间-开始")
    private String validEndTimeStart;

    @ApiModelProperty("有效期结束时间-结束")
    private String validEndTimeEnd;
    private Integer pageNo = 1;

    private Integer pageSize = 10;
}
