package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PersonAndCarVO {

    @ApiModelProperty(value = "车辆信息")
    private List<CarInfoVO> vehicleListVO;

    @ApiModelProperty(value = "人员信息")
    private SafetyEmployeeMessageVO safetyEmployeeMessageVO;

}
