package com.yutong.modules.vehicle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 车辆违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel( description="车辆违规上报Vo")
public class YtsafetyVehicleViolationsReportVo {
    
	/**主键*/
    @ApiModelProperty(value = "主键")
	private Long id;
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
	@Excel(name = "违规描述", width = 15)
    @ApiModelProperty(value = "违规描述")
	private String violationsRemark;
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
    private String violationsType;
    @ApiModelProperty(value = "违规类型编码")
    private String violationsTypeCode;
    /**违规时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "违规时间")
    private Date violationsTime;
	/**车辆冻结天数*/
    @ApiModelProperty(value = "车辆冻结天数")
	private Integer vehicleFreezeNumber;
    /**车辆冻结天数*/
    @ApiModelProperty(value = "车辆冻结规则")
    private String vehicleFreezeConfig;
    @ApiModelProperty(value = "草稿状态：1-是，0-否")
    private Integer draftStatus;
	/**是否复议：0-否，1-是*/
    @ApiModelProperty(value = "是否复议：0-否，1-是")
	private Integer isReconsider;
    /**说明*/
    @ApiModelProperty(value = "说明")
    private String statement;

    /**是否复议：0-否，1-是*/
    @ApiModelProperty(value = "是否同意：0-否不同意，1-同意")
    private Integer isAccept;
	/**附件图片id（文件服务回传id，多个用逗号分隔）*/
    @ApiModelProperty(value = "附件图片id（文件服务回传id，多个用逗号分隔）")
	private String attachmentIds;
    @ApiModelProperty(value = "附件图片对象列表")
    private List<YTSafetyFileInfo> attachmentList;
	/**状态：0-草稿，1-审批中，2-复议中，5-已完成，30-废弃*/
    @ApiModelProperty(value = "状态：0-草稿，1-审批中，2-复议，3-复议审批，4-处罚已完成，5-不处罚已完成，30-废弃a")
	private Integer status;
    @ApiModelProperty(value = "是否可审批")
    private Boolean isCanApprove;

    @ApiModelProperty(value = "流程主键")
    private String flowId;

    @ApiModelProperty(value = "是否可修改")
    private Boolean isCanEdit;
	/**创建人姓名*/
	@Excel(name = "创建人姓名", width = 15)
    @ApiModelProperty(value = "创建人姓名")
	private String createName;
	/**创建人工号*/
	@Excel(name = "创建人工号", width = 15)
    @ApiModelProperty(value = "创建人工号")
	private String createUser;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
}
