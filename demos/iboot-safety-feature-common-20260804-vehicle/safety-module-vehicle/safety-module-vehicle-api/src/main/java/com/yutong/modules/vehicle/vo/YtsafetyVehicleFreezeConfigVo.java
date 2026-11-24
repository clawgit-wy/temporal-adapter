package com.yutong.modules.vehicle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="车辆冻结配vo")
public class YtsafetyVehicleFreezeConfigVo {
    
	/**主键*/
    @ApiModelProperty(value = "主键")
	private Long id;
	/**违规类型*/
    @ApiModelProperty(value = "违规类型")
	private String violationsType;
    /**违规类型编码*/
    @ApiModelProperty(value = "违规类型编码")
    private String violationsCode;
    /**违规类型*/
    @ApiModelProperty(value = "配置明细")
    private List<YtsafetyVehicleFreezeConfigInfoV0> VehicleFreezeConfigInfoList;
	/**创建人姓名*/
    @ApiModelProperty(value = "创建人姓名")
	private String createName;
	/**创建人工号*/
    @ApiModelProperty(value = "创建人工号")
	private String createUser;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
}
