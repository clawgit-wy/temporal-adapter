package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PersonnelAccessVO {

    @ApiModelProperty(value = "进出类型")
    private String ioFlag;

    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;

    @ApiModelProperty(value = "位置名称")
    private String deviceName;

    @ApiModelProperty(value = "进出场时间")
    private String operTime;

}
