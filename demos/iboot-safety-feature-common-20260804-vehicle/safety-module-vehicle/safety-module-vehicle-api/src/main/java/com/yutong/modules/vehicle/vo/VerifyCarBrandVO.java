package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VerifyCarBrandVO {

    @ApiModelProperty(value = "是否通过车辆品牌库校验")
    public boolean verifyPass;
}
