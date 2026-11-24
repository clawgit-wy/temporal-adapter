package com.yutong.safety.core.db.entity.build;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildProcessCloseDO implements Serializable {
    /**
     *
     */
    private String id;

    /**
     * 主流程ID
     */
    private String processId;

    /**
     * 授权方式 1-按人员 2-按组织
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
     * 厂区
     */
    private String anFactoryCode;

    private String anFactoryName;
    /**
     * 楼栋
     */
    private String anBuildingCode;

    private String anBuilding;

    @ApiModelProperty("前端展示序号 不存库只用于校验提示用")
    private int rowNo;

    private static final long serialVersionUID = 1L;

}