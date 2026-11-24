package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyEmployeePrivateCarQueryDTO implements Serializable {

    @ApiModelProperty(value = "车牌号")
    private String carNo;

    @ApiModelProperty(value = "车辆颜色")
    private String carColor;

    @ApiModelProperty(value = "车辆型号")
    private String vehicleType;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "员工号")
    private String employeeNo;

    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    @ApiModelProperty(value = "所属部门")
    private String deptNo;

    @ApiModelProperty(value = "是否本人车辆")
    private String myselfCar;

    @ApiModelProperty(value = "与车主关系")
    private String relationship;

    @ApiModelProperty(value = "租赁到期时间")
    private String serverEndDate;

    @ApiModelProperty("页码")
    private Integer pageNo = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;

}