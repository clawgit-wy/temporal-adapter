package com.yutong.safety.core.db.entity.build;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyBuildLongConfirmDO implements Serializable {
    private Integer id;

    @ApiModelProperty("员工号")
    private String employeeCode;

    @ApiModelProperty("员工姓名")
    private String employeeName;

    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("生效时间-开始")
    private String beginTime;

    @ApiModelProperty("生效时间-截止")
    private String endTime;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("责任部门编码")
    private String dutyDeptCode;

    @ApiModelProperty("责任部门")
    private String dutyDeptName;

    @ApiModelProperty("责任人EIP")
    private String dutyEmpLogin;

    @ApiModelProperty("责任人")
    private String dutyEmpName;

    @ApiModelProperty("责任人员工号")
    private String dutyEmpNo;

    private String bpmFlowId;

    @ApiModelProperty("流程类提交人角色 1-员工 2-管理员 3-保密负责人")
    private String roleType;

    private String operTime;

    @ApiModelProperty("是否跨部门 0-否 1-是")
    private String corresFlag;

    @ApiModelProperty("确认状态 0-待确认 1-保留授权 2-关闭，3-更新进出时间后删除")
    private String confirmFlag;

    private String confirmEmpNo;

    private String confirmEmpName;

    @ApiModelProperty("确认人角色 1-员工 2-管理员 3-保密负责人")
    private String confirmRoleType;

    @ApiModelProperty("未进出天数")
    private Integer diffDay;

    @ApiModelProperty("对接部门")
    private String corresDutyDeptCode;

    @ApiModelProperty("对接部门保密负责人")
    private String corresDutyEmpCode;

    /**
     * eip代办时间
     */
    private Date eipTodoDate;
    /**
     * eip代办状态 0：未发，1：已发
     */
    private Integer eipTodoStatus;

    private static final long serialVersionUID = 1L;
}