package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessCloseDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessOpenDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class BuildProcessAddParam implements Check {
    @ApiModelProperty("主键ID-后台使用")
    private String id;

    @ApiModelProperty("权限类型 1-开通 2-关闭")
    private String powerType;

    @ApiModelProperty("流程编码")
    private String processCode;

    @ApiModelProperty("主题")
    private String processSubject;

    @ApiModelProperty("提交人员工号")
    private String createEmployeeNo;

    @ApiModelProperty("提交人登录名")
    private String createEmployeeLogin;

    @ApiModelProperty("提交人姓名")
    private String createEmployeeName;

    @ApiModelProperty("提交人部门编码-路径")
    private String createDeptCode;

    @ApiModelProperty("提交人部门-路径")
    private String createDeptName;

    @ApiModelProperty("提交人角色 1-员工 2-管理员 3-保密负责人")
    private String roleType;

    @ApiModelProperty("事由")
    private String applyReason;

    @ApiModelProperty("本部门科长登录名")
    private String chiefEmployeeLogin;

    @ApiModelProperty("本部门科长姓名")
    private String chiefEmployeeName;

    @ApiModelProperty("开通权限明细")
    private List<SafetyBuildProcessCloseDO> closeList;

    @ApiModelProperty("关闭权限明细")
    private List<SafetyBuildProcessOpenDO> openDOList;

    @ApiModelProperty("BPM流程ID")
    private String bpmFlowId;

    @ApiModelProperty("EIP角色")
    private String buildSysManagerRole;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(powerType), ResponseEnum.PARAM_ERROR.getCode(), "“权限类型”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(createEmployeeLogin), ResponseEnum.PARAM_ERROR.getCode(), "“提交人-EIP用户名”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(roleType), ResponseEnum.PARAM_ERROR.getCode(), "“提交人角色类型”不能为空！");
    }
}
