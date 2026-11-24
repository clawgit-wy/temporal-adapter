package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccessRecordDTO {

    @ApiModelProperty(value = "员工号")
    private String empNo;

    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    @ApiModelProperty(value = "身份证号")
    private String IDNo;

    @ApiModelProperty(value = "车牌号")
    private String carNo;

}
