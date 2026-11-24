package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SafetyEmployeePrivateCarPersonnelChangeDTO implements Serializable {

    @NotNull(message = "车辆id不能为空")
    @ApiModelProperty(value = "车辆id")
    private Integer id;

    @ApiModelProperty(value = "员工姓名")
    private String changeEmployeeName;

    @NotBlank(message = "员工号不能为空")
    @ApiModelProperty(value = "员工号")
    private String changeEmployeeNo;

}