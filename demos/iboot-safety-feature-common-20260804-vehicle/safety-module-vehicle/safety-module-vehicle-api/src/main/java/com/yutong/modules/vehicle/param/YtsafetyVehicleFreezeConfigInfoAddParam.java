package com.yutong.modules.vehicle.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 车辆冻结配置明细
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="车辆冻结配置明细新增")
public class YtsafetyVehicleFreezeConfigInfoAddParam {

	/**车辆违规次数*/
    @ApiModelProperty(value = "车辆违规次数")
	private Integer vehicleViolationsNumber;
	/**车辆冻结天数*/
    @ApiModelProperty(value = "车辆冻结天数")
	private Integer vehicleFreezeNumber;

}
