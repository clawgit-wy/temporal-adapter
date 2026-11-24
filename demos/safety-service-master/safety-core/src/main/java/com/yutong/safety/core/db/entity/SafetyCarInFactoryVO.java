package com.yutong.safety.core.db.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyCarInFactoryVO implements Serializable {

    private String ID;
    @ApiModelProperty("停车场设备ID")
    private String parkingId;
    @ApiModelProperty("车牌号")
    private String tokenNo;
    @ApiModelProperty("类型 0-进厂")
    private Integer tokenType;
    @ApiModelProperty("员工号")
    private String staffNo;
    @ApiModelProperty("员工姓名")
    private String staffName;
    @ApiModelProperty("车牌号")
    private String regPlate;
    @ApiModelProperty("进厂时间")
    private String inTime;
    @ApiModelProperty("最后更新时间")
    private String lastChargeTime;
    @ApiModelProperty("true-车辆在厂 false-车辆不在厂")
    private Boolean isSucess;
}
