package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 车辆冻结配置明细Vo
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="车辆冻结配置明细Vo")
public class YtsafetyVehicleFreezeConfigInfoV0 {

	/**车辆冻结配置id*/
    @ApiModelProperty(value = "车辆冻结配置id")
	private Long vehicleFreezeConfigId;
	/**车辆违规次数*/
    @ApiModelProperty(value = "车辆违规次数")
	private Integer vehicleViolationsNumber;
	/**车辆冻结天数*/
    @ApiModelProperty(value = "车辆冻结天数")
	private Integer vehicleFreezeNumber;

}
