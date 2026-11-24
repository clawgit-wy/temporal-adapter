package com.yutong.modules.entity;

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
 * @Description: 人员信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Data
@TableName("safety_employee_message")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_employee_message对象", description="人员信息表")
public class SafetyEmployeeMessage {
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Integer id;
    @ApiModelProperty(value = "员工号")
	private String empNo;
    @ApiModelProperty(value = "姓名")
	private String empName;
    @ApiModelProperty(value = "手机号")
	private String phoneNo;
    @ApiModelProperty(value = "人员类型ZS:正式员工,LD:绿都员工,WX:外协员工")
	private String userType;
    @ApiModelProperty(value = "岗位编码")
	private String positionCode;
    @ApiModelProperty(value = "岗位名称")
	private String positionName;
    @ApiModelProperty(value = "类别")
	private String positionType;
    @ApiModelProperty(value = "部门路径")
	private String orgNamePath;
    @ApiModelProperty(value = "部门code路径")
	private String orgCodePath;
    @ApiModelProperty(value = "上级组织code")
	private String parentCode;
    @ApiModelProperty(value = "组织代码")
	private String orgCode;
    @ApiModelProperty(value = "组织名称")
	private String orgName;
    @ApiModelProperty(value = "电子邮箱")
	private String email;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private Date createTime;
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
    @ApiModelProperty(value = "备注")
	private String remark;
    @ApiModelProperty(value = "状态(正常:NORMAL,离职:DISABLED)")
	private String status;
    @ApiModelProperty(value = "员工登录名")
	private String empLogin;
    @ApiModelProperty(value = "人脸下发标记")
	private Integer faceIssueFlag;
    @ApiModelProperty(value = "卡下发标记")
	private Integer cardIssueFlag;
    @ApiModelProperty(value = "员工卡ID")
	private String employeeCardId;
    @ApiModelProperty(value = "性别")
	private String sex;
    @ApiModelProperty(value = "人员类型")
	private String empType;
    @ApiModelProperty(value = "人员类型编码")
	private String empTypeCode;
    @ApiModelProperty(value = "照片")
	private String photo;
    @ApiModelProperty(value = "职种(高层管理、科级管理、部级管理、处级管理、室级管理、班组级管理、模块这六种字段，其余所有字段均显示为员工)")
	private String jobTypeName;
}
