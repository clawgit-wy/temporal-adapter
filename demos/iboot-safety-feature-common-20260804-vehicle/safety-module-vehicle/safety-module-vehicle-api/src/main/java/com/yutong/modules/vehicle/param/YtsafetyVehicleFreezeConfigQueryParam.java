package com.yutong.modules.vehicle.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="车辆冻结配置查询")
public class YtsafetyVehicleFreezeConfigQueryParam {

	/**违规类型*/
    @ApiModelProperty(value = "违规类型")
	private String violationsType;



}
