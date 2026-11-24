package com.yutong.modules.vehicle.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 车辆人员变更流程表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_car_personnel_change_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_car_personnel_change_process对象", description="车辆人员变更流程表")
public class YTSafetyCarPersonnelChangeProcess {
    
	/**主键*/
    @ApiModelProperty(value = "主键")
	private Long id;
	/**车辆人员变更code*/
	@Excel(name = "车辆人员变更code", width = 15)
    @ApiModelProperty(value = "车辆人员变更code")
	private String carPersonnelChangeCode;
	/**人员变更类型*/
	@Excel(name = "人员变更类型", width = 15)
    @ApiModelProperty(value = "人员变更类型：DIMISSION-离职，TRANSFER_POSITION-调岗")
	private String personnelChangeType;
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
	/**调整人 多个以“,”隔开*/
	@Excel(name = "调整人 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "调整人 多个以“,”隔开")
	private String setupPersonEmpUser;
	/**调整人姓名 多个以“,”隔开*/
	@Excel(name = "调整人姓名 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "调整人姓名 多个以“,”隔开")
	private String setupPersonEmpName;
	/**审核人 多个以“,”隔开*/
	@Excel(name = "审核人 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "审核人 多个以“,”隔开")
	private String reviewPersonEmpUser;
	/**审核人姓名 多个以“,”隔开*/
	@Excel(name = "审核人姓名 多个以“,”隔开", width = 15)
    @ApiModelProperty(value = "审核人姓名 多个以“,”隔开")
	private String reviewPersonEmpName;
	/**是否更换人员*/
	@Excel(name = "是否更换人员", width = 15)
    @ApiModelProperty(value = "是否更换人员:1-是,0-否")
	private String isReplacePersonnel;
	/**状态：0-草稿，1-调整中，2-审核中，9-起草，30-废弃*/
	@Excel(name = "状态：0-草稿，1-调整中，2-审核中，9-起草，30-废弃", width = 15)
    @ApiModelProperty(value = "状态：0-草稿，1-调整中，2-审核中，9-起草，30-废弃")
	private Integer status;
	/**调整类型：adjustment-调整；uncorrected-未调整*/
	@Excel(name = "调整类型：adjustment-调整；uncorrected-未调整", width = 15)
    @ApiModelProperty(value = "调整类型：adjustment-调整；uncorrected-未调整")
	private String handlerType;
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
	/**修改人姓名*/
	@Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
	private String updateName;
	/**修改人工号*/
	@Excel(name = "修改人工号", width = 15)
    @ApiModelProperty(value = "修改人工号")
	private String updateUser;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**删除标记：0-未删除，1-已删除*/
	@Excel(name = "删除标记：0-未删除，1-已删除", width = 15)
    @ApiModelProperty(value = "删除标记：0-未删除，1-已删除")
    @TableLogic
	private Integer delFlag;
	/**修改版本（乐观锁）*/
	@Excel(name = "修改版本（乐观锁）", width = 15)
    @ApiModelProperty(value = "修改版本（乐观锁）")
    @Version
	private Integer version;
}
