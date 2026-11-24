package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 车辆人员变更信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(value="车辆人员变更信息表DTO", description="车辆人员变更信息表DTO")
public class YTSafetyCarPersonnelChangeInfoDTO {
    
	/**主键*/
    @ApiModelProperty(value = "主键")
	private Long id;
	/**流程表id*/
    @ApiModelProperty(value = "流程表id")
	private Long processId;
	/**车辆id*/
    @ApiModelProperty(value = "车辆id")
	private Integer employeeCarId;
	/**车牌号*/
    @ApiModelProperty(value = "车牌号")
	private String carNo;
	/**车辆类型*/
    @ApiModelProperty(value = "车辆类型")
	private String carType;
	/**车辆颜色*/
    @ApiModelProperty(value = "车辆颜色")
	private String carColor;
	/**当前所属人员*/
    @ApiModelProperty(value = "当前所属人员")
	private String currentAffiliatedPersonnel;
	/**是否更换人员*/
    @ApiModelProperty(value = "是否更换人员:1-是,0-否")
	private String isReplacePersonnel;
	/**是否更换人员*/
    @ApiModelProperty(value = "更换人员")
	private String changePersonnel;
}
