package com.yutong.safety.core.db.entity.build;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildProcessOpenDO implements Serializable {
    /**
     *
     */
    private String id;

    /**
     * 流程ID
     */
    private String processId;

    /**
     *  授权方式 1-按人员 2-按组织
     */
    private String empowerType;

    /**
     * 授权范围，员工号或组织编码
     */
    private String empowerAreaCode;

    /**
     * 授权范围，员工姓名拼接或组织名
     */
    private String empowerAreaName;

    /**
     * 门禁编码
     */
    private String deviceCode;

    /**
     * 门禁名称
     */
    private String deviceName;

    /**
     * 对接部门编码
     */
    private String dockingDeptCode;

    /**
     * 对接部门名称
     */
    private String dockingDeptName;

    /**
     * 夸跨部门负责人EIP登录名
     */
    private String acrossEmployeeLogin;

    /**
     * 夸跨部门负责人姓名
     */
    private String acrossEmployeeName;

    /**
     * 绝密区负责人登录名
     */
    private String topSecretEmployeeLogin;

    /**
     * 绝密区负责人姓名
     */
    private String topSecretEmployeeName;

    /**
     * 申请状态 1-通过 2-不通过
     */
    private String applyStatus;

    /**
     * 拒绝理由
     */
    private String refuseReason;

    /**
     * 审批人
     */
    private String applyEmployee;

    /**
     * 审批角色
     */
    private String applyRole;

    /**
     * 审批时间
     */
    private String applyTime;

    /**
     * 对应开通公共区域门禁编码拼接
     */
    private String publicDeviceCode;

    /**
     * 对应开通公共区域门禁名称拼接
     */
    private String publicDeviceName;

    private static final long serialVersionUID = 1L;

    /**
     * 厂区
     */
    private String anFactoryCode;

    private String anFactoryName;

    /**
     * 楼栋
     */
    private String anBuildingCode;

    private String anBuilding;

    private String createTime;
    /**
     * 生效开始时间
     */
    private String powerBeginTime;
    /**
     * 生效截止时间
     */
    private String powerEndTime;

    //EIP角色
    private String buildSysManagerRole;

    @ApiModelProperty("前端展示序号 不存库只用于校验提示用")
    private int rowNo;
}