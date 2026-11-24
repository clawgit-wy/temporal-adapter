package com.yutong.modules.user.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: EHR员工信息
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("safety_employee_message")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_employee_message对象", description="EHR员工信息")
public class SafetyEmployeeMessage {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
	/**员工号*/
	@Excel(name = "员工号", width = 15)
    @ApiModelProperty(value = "员工号")
	private String empNo;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
	private String empName;
	/**手机号*/
	@Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
	private String phoneNo;
	/**人员类型ZS:正式员工,LD:绿都员工,WX:外协员工*/
	@Excel(name = "人员类型ZS:正式员工,LD:绿都员工,WX:外协员工", width = 15)
    @ApiModelProperty(value = "人员类型ZS:正式员工,LD:绿都员工,WX:外协员工")
	private String userType;
	/**岗位编码*/
	@Excel(name = "岗位编码", width = 15)
    @ApiModelProperty(value = "岗位编码")
	private String positionCode;
	/**岗位名称*/
	@Excel(name = "岗位名称", width = 15)
    @ApiModelProperty(value = "岗位名称")
	private String positionName;
	/**类别*/
	@Excel(name = "类别", width = 15)
    @ApiModelProperty(value = "类别")
	private String positionType;
	/**部门路径*/
	@Excel(name = "部门路径", width = 15)
    @ApiModelProperty(value = "部门路径")
	private String orgNamePath;
	/**部门code路径*/
	@Excel(name = "部门code路径", width = 15)
    @ApiModelProperty(value = "部门code路径")
	private String orgCodePath;
	/**上级组织code*/
	@Excel(name = "上级组织code", width = 15)
    @ApiModelProperty(value = "上级组织code")
	private String parentCode;
	/**组织代码*/
	@Excel(name = "组织代码", width = 15)
    @ApiModelProperty(value = "组织代码")
	private String orgCode;
	/**组织名称*/
	@Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "组织名称")
	private String orgName;
	/**电子邮箱*/
	@Excel(name = "电子邮箱", width = 15)
    @ApiModelProperty(value = "电子邮箱")
	private String email;
	/**createTime*/
	@Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private Date createTime;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private String remark;
	/**状态(正常:NORMAL,离职:DISABLED)*/
	@Excel(name = "状态(正常:NORMAL,离职:DISABLED)", width = 15)
    @ApiModelProperty(value = "状态(正常:NORMAL,离职:DISABLED)")
	private String status;
	/**员工登录名*/
	@Excel(name = "员工登录名", width = 15)
    @ApiModelProperty(value = "员工登录名")
	private String empLogin;
	/**人脸下发标记*/
	@Excel(name = "人脸下发标记", width = 15)
    @ApiModelProperty(value = "人脸下发标记")
	private Integer faceIssueFlag;
	/**卡下发标记*/
	@Excel(name = "卡下发标记", width = 15)
    @ApiModelProperty(value = "卡下发标记")
	private Integer cardIssueFlag;
	/**员工卡ID*/
	@Excel(name = "员工卡ID", width = 15)
    @ApiModelProperty(value = "员工卡ID")
	private String employeeCardId;
	/**性别*/
	@Excel(name = "性别", width = 15)
    @ApiModelProperty(value = "性别")
	private String sex;
	/**人员类型*/
	@Excel(name = "人员类型", width = 15)
    @ApiModelProperty(value = "人员类型")
	private String empType;
	/**人员类型编码*/
	@Excel(name = "人员类型编码", width = 15)
    @ApiModelProperty(value = "人员类型编码")
	private String empTypeCode;
	/**照片*/
	@Excel(name = "照片", width = 15)
    @ApiModelProperty(value = "照片")
	private String photo;
	/**职种(高层管理、科级管理、部级管理、处级管理、室级管理、班组级管理、模块这六种字段，其余所有字段均显示为员工)*/
	@Excel(name = "职种(高层管理、科级管理、部级管理、处级管理、室级管理、班组级管理、模块这六种字段，其余所有字段均显示为员工)", width = 15)
    @ApiModelProperty(value = "职种(高层管理、科级管理、部级管理、处级管理、室级管理、班组级管理、模块这六种字段，其余所有字段均显示为员工)")
	private String jobTypeName;
}
