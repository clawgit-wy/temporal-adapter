package com.yutong.modules.vehicle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 车辆违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_vehicle_violations_report")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_vehicle_violations_report对象", description="车辆违规上报")
public class YtsafetyVehicleViolationsReport {
    
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**violationsCode*/
	@Excel(name = "violationsCode", width = 15)
    @ApiModelProperty(value = "violationsCode")
	private String violationsCode;
	/**模型主键*/
	@Excel(name = "模型主键", width = 15)
    @ApiModelProperty(value = "模型主键")
	private String modelFlag;
	/**流程编码*/
	@Excel(name = "流程编码", width = 15)
    @ApiModelProperty(value = "流程编码")
	private String flowCode;
	/**流程主题*/
	@Excel(name = "流程主题", width = 15)
    @ApiModelProperty(value = "流程主题")
	private String flowSubject;
	/**申请员工号*/
	@Excel(name = "申请员工号", width = 15)
    @ApiModelProperty(value = "申请员工号")
	private String applyNo;
	/**申请员工名*/
	@Excel(name = "申请员工名", width = 15)
    @ApiModelProperty(value = "申请员工名")
	private String applyName;
	/**所属部门编码*/
	@Excel(name = "所属部门编码", width = 15)
    @ApiModelProperty(value = "所属部门编码")
	private String applyDepartCode;
	/**所属部门名称*/
	@Excel(name = "所属部门名称", width = 15)
    @ApiModelProperty(value = "所属部门名称")
	private String applyDepartName;
	/**违规组长 多个以“,”隔开*/
	@Excel(name = "违规组长 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "违规组长 多个以“,”隔开")
	private String violationTeamLeader;
	/**违规组长名称“,”隔开*/
	@Excel(name = "违规组长名称“,”隔开", width = 15)
    @ApiModelProperty(value = "违规组长名称“,”隔开")
	private String violationTeamLeaderName;
	/**保卫组长 多个以“,”隔开*/
	@Excel(name = "审批人信息", width = 15)
    @ApiModelProperty(value = "审批人信息")
	private String securityTeamLeader;
	/**保卫组长姓名 多个以“,”隔开*/
	@Excel(name = "审批人信息", width = 15)
    @ApiModelProperty(value = "审批人信息")
	private String securityTeamLeaderName;
    /**复议人员*/
    @Excel(name = "复议人员信息", width = 15)
    @ApiModelProperty(value = "复议人员信息")
    private String reconsider;
    /**复议人员姓名*/
    @Excel(name = "复议人员姓名", width = 15)
    @ApiModelProperty(value = "复议人员姓名")
    private String reconsiderName;
	/**车辆号码*/
	@Excel(name = "车辆号码", width = 15)
    @ApiModelProperty(value = "车辆号码")
	private String vehicleNo;
	/**车辆类型*/
	@Excel(name = "车辆类型", width = 15)
    @ApiModelProperty(value = "车辆类型")
	private String vehicleType;
	/**车主姓名*/
	@Excel(name = "车主姓名", width = 15)
    @ApiModelProperty(value = "车主姓名")
	private String vehicleOwner;
	/**车主员工号*/
	@Excel(name = "车主员工号", width = 15)
    @ApiModelProperty(value = "车主员工号")
	private String vehicleOwnerNo;
	/**违规类型*/
	@Excel(name = "违规类型", width = 15)
    @ApiModelProperty(value = "违规类型")
	private String violationsType;
    /**违规类型*/
    @Excel(name = "违规类型编码", width = 15)
    @ApiModelProperty(value = "违规类型编码")
    private String violationsTypeCode;
	/**所属厂区id*/
	@Excel(name = "所属厂区id", width = 15)
    @ApiModelProperty(value = "所属厂区id")
	private String plantId;
	/**所属厂区名称*/
	@Excel(name = "所属厂区名称", width = 15)
    @ApiModelProperty(value = "所属厂区名称")
	private String plantName;
	/**违规地点*/
	@Excel(name = "违规地点", width = 15)
    @ApiModelProperty(value = "违规地点")
	private String violationsLocation;
	/**违规描述*/
	@Excel(name = "违规描述", width = 15)
    @ApiModelProperty(value = "违规描述")
	private String violationsRemark;
    /**违规时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "违规时间")
    private Date violationsTime;
	/**车辆冻结天数*/
	@Excel(name = "车辆冻结天数", width = 15)
    @ApiModelProperty(value = "车辆冻结天数")
	private Integer vehicleFreezeNumber;
    /**车辆冻结天数*/
    @ApiModelProperty(value = "车辆冻结规则")
    private String vehicleFreezeConfig;
    @Excel(name = "草稿状态：1-是，0-否", width = 15)
    @ApiModelProperty(value = "草稿状态：1-是，0-否")
    private Integer draftStatus;
	/**是否复议：0-否，1-是*/
	@Excel(name = "是否复议：0-否，1-是", width = 15)
    @ApiModelProperty(value = "是否复议：0-否，1-是")
	private Integer isReconsider;
	/**附件图片id（文件服务回传id，多个用逗号分隔）*/
	@Excel(name = "附件图片id（文件服务回传id，多个用逗号分隔）", width = 15)
    @ApiModelProperty(value = "附件图片id（文件服务回传id，多个用逗号分隔）")
	private String attachmentIds;
	/**状态：0-草稿，1-审批中，2-复议中，5-已完成，30-废弃*/
	@Excel(name = "状态：0-草稿，1-审批中，2-复议，3-复议审批，4-处罚已完成，5-不处罚已完成，30-废弃", width = 15)
    @ApiModelProperty(value = "状态：0-草稿，1-审批中，2-复议，3-复议审批，4-处罚已完成，5-不处罚已完成，30-废弃")
	private Integer status;
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
	/**删除标记：0-未删除，1-已删除*/
	@Excel(name = "删除标记：0-未删除，1-已删除", width = 15)
    @ApiModelProperty(value = "删除标记：0-未删除，1-已删除")
	private Integer delFlag;
    /**说明*/
    @ApiModelProperty(value = "说明")
    private String statement;

    /**是否复议：0-否，1-是*/
    @ApiModelProperty(value = "是否同意：0-不同意，1-同意")
    private Integer isAccept;
}
