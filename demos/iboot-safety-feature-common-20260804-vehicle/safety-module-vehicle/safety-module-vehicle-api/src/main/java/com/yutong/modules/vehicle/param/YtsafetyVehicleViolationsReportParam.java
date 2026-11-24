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
@ApiModel( description="车辆违规上报入參")
public class YtsafetyVehicleViolationsReportParam {
    

    @ApiModelProperty(value = "主键")
	private Long id;

    @ApiModelProperty(value = "violationsCode")
	private String violationsCode;
	/**模型主键*/
    @ApiModelProperty(value = "模型主键")
	private String modelFlag;
	/**流程编码*/
    @ApiModelProperty(value = "流程编码")
	private String flowCode;
	/**流程主题*/
    @ApiModelProperty(value = "流程主题")
	private String flowSubject;
	/**申请员工号*/
    @ApiModelProperty(value = "申请员工号")
	private String applyNo;
	/**申请员工名*/
    @ApiModelProperty(value = "申请员工名")
	private String applyName;
	/**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
	private String applyDepartCode;
	/**所属部门名称*/
    @ApiModelProperty(value = "所属部门名称")
	private String applyDepartName;
	/**车辆号码*/
    @ApiModelProperty(value = "车辆号码")
	private String vehicleNo;
	/**车辆类型*/
    @ApiModelProperty(value = "车辆类型")
	private String vehicleType;
	/**车主姓名*/
    @ApiModelProperty(value = "车主姓名")
	private String vehicleOwner;
	/**车主员工号*/
    @ApiModelProperty(value = "车主员工号")
	private String vehicleOwnerNo;
	/**违规类型*/
    @ApiModelProperty(value = "违规类型")
	private Integer violationsType;
	/**所属厂区id*/
    @ApiModelProperty(value = "所属厂区id")
	private Integer plantId;
	/**所属厂区名称*/
    @ApiModelProperty(value = "所属厂区名称")
	private String plantName;
	/**违规地点*/
    @ApiModelProperty(value = "违规地点")
	private String violationsLocation;
	/**违规描述*/
    @ApiModelProperty(value = "违规描述")
	private String violationsRemark;
	/**车辆冻结天数*/
    @ApiModelProperty(value = "车辆冻结天数")
	private Integer vehicleFreezeNumber;
	/**是否复议：0-否，1-是*/
    @ApiModelProperty(value = "是否复议：0-否，1-是")
	private Integer isReconsider;
	/**附件图片id（文件服务回传id，多个用逗号分隔）*/
    @ApiModelProperty(value = "附件图片id（文件服务回传id，多个用逗号分隔）")
	private String attachmentIds;
}
