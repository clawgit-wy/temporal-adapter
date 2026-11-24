package com.yutong.modules.vehicle.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 车辆违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(description="车辆违规上报新增入参")
public class YtsafetyVehicleViolationsReportAddParam {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
	/**车辆号码*/
    @ApiModelProperty(value = "车辆号码")
	private String vehicleNo;
	/**车辆类型*/
    @ApiModelProperty(value = "车辆类型")
	private String vehicleType;
    @ApiModelProperty(value = "车主姓名")
	private String vehicleOwner;
	/**车主员工号*/
    @ApiModelProperty(value = "车主员工号")
	private String vehicleOwnerNo;
	/**违规类型*/
    @ApiModelProperty(value = "违规类型")
	private String violationsType;
    @ApiModelProperty(value = "违规类型编码")
    private String violationsTypeCode;
	/**所属厂区id*/
    @ApiModelProperty(value = "所属厂区id")
	private String plantId;
	/**所属厂区名称*/
    @ApiModelProperty(value = "所属厂区名称")
	private String plantName;
	/**违规地点*/
    @ApiModelProperty(value = "违规地点")
	private String violationsLocation;
	/**违规描述*/
    @ApiModelProperty(value = "违规描述")
	private String violationsRemark;
    /**违规时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "违规时间")
    private Date violationsTime;
	/**附件图片id（文件服务回传id，多个用逗号分隔）*/
    @ApiModelProperty(value = "附件图片id（文件服务回传id，多个用逗号分隔）")
	private String attachmentIds;
    @ApiModelProperty(value = "0 保存草稿  1提交流程")
    private String opType;

}
