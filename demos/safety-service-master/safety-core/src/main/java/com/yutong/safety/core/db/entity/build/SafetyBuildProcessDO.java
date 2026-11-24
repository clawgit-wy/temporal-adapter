package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildProcessDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String id;

    /**
     * BPM流程ID
     */
    private String bpmFlowId;

    /**
     * 流程编码 格式：LYMJSQ+YYYYMMDD+四位补齐序号
     */
    private String processCode;

    /**
     * 流程主题
     */
    private String processSubject;

    /**
     * 创建人员工号
     */
    private String createEmployeeNo;

    /**
     * 创建人EIP登录名
     */
    private String createEmployeeLogin;

    /**
     * 创建人姓名
     */
    private String createEmployeeName;

    /**
     * 创建人部门编码
     */
    private String createDeptCode;

    /**
     * 创建人部门
     */
    private String createDeptName;

    /**
     * 角色 1-员工 2-管理员 3-保密负责人
     */
    private String roleType;

    /**
     * 流程状态 00-废弃  10-草稿 20 审批  30-发布
     */
    private String status;

    /**
     * 本部门负责人登录名
     */
    private String departEmployeeLogin;

    /**
     * 本部门负责人
     */
    private String departEmployeeName;

    /**
     * 跨部门标记0-否 1-是
     */
    private String acrossFlag;

    /**
     * 夸跨部门负责人EIP登录名
     */
    private String acrossEmployeeLogin;

    /**
     * 夸跨部门负责人姓名
     */
    private String acrossEmployeeName;

    /**
     * 是否涉及绝密标记0-否 1-是
     */
    private String topSecretFlag;

    /**
     * 绝密区负责人登录名
     */
    private String topSecretEmployeeLogin;

    /**
     * 绝密区负责人姓名
     */
    private String topSecretEmployeeName;

    /**
     * 本部门科长登录名
     */
    private String chiefEmployeeLogin;

    /**
     * 本部门科长姓名
     */
    private String chiefEmployeeName;

    /**
     * 申请事由
     */
    private String applyReason;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 当前节点处理人-bpm
     */
    private String curNodeHandler;

    /**
     * 当前节点处理人姓名-bpm
     */
    private String curHandlerName;

    /**
     * 当前流程节点-bpm
     */
    private String curNodeId;

    /**
     * 当前流程节点名称-bpm
     */
    private String curNodeName;

    /**
     * 节点标识
     */
    private String nodeFlag;

    /**
     * 授权类型 1-开通 2-关闭
     */
    private String powerType;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 全部拒绝标记  0-否  1-是
     */
    private String refuseFlag;

    /**
     * 责任部门编码
     */
    private String dutyDeptCode;

    /**
     * 管理员审批状态
     */
    private String adminApprovalStatus;

    /**
     * 管理员审批状态
     */
    private String adminApprovalEipRole;

    /**
     * 责任部门
     */
    private String dutyDeptName;

    //EIP角色
    private String buildSysManagerRole;

    //跨部门EIP角色
    private String acrossBuildSysManagerRole;
}