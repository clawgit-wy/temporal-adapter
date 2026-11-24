package com.yutong.modules.vehicle.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 车辆违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="车辆违规上报查询入参")
public class YtsafetyVehicleViolationsReportQueryParam {
	/**车辆号码*/
    @ApiModelProperty(value = "车辆号码")
	private String vehicleNo;
	/**车辆类型*/
    @ApiModelProperty(value = "车辆类型")
	private String vehicleType;
    @ApiModelProperty(value = "车主姓名")
	private String vehicleOwner;
	/**违规类型*/
    @ApiModelProperty(value = "违规类型")
	private String violationsType;
	/**所属厂区id*/
    @ApiModelProperty(value = "所属厂区id")
	private String plantId;
}
