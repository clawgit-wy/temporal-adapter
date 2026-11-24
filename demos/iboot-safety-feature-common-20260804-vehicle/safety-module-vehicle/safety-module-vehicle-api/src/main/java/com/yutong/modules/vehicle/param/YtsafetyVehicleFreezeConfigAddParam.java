package com.yutong.modules.vehicle.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="车辆冻结配置新增")
public class YtsafetyVehicleFreezeConfigAddParam {

	/**违规类型*/
    @ApiModelProperty(value = "违规类型")
	private String violationsType;

    /**违规类型编码*/
    @ApiModelProperty(value = "违规类型编码")
    private String violationsCode;

    @ApiModelProperty(value = "配置明細")
    private List<YtsafetyVehicleFreezeConfigInfoAddParam> ytsafetyVehicleFreezeConfigInfoAddParam;



}
