package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "行驶证识别结果", description = "行驶证OCR识别结果")
public class VehicleLicenseResultVO {

    @ApiModelProperty(value = "车主")
    private String owner;

    @ApiModelProperty(value = "车牌号")
    private String plateNo;

    @ApiModelProperty(value = "车型")
    private String model;

    @ApiModelProperty(value = "车辆识别代号(VIN)")
    private String vin;

    @ApiModelProperty(value = "发证日期")
    private String issueDate;

    @ApiModelProperty(value = "车辆类型")
    private String vehicleType;

    @ApiModelProperty(value = "注册日期")
    private String registerDate;
}
