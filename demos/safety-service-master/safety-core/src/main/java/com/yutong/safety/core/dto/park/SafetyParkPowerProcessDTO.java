package com.yutong.safety.core.dto.park;

import com.yutong.safety.common.param.web.WebPageParam;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDetailDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class SafetyParkPowerProcessDTO extends WebPageParam implements Serializable {
    private String id;
    /**
     * BPM流程ID
     */
    @ApiModelProperty("BPM流程ID")
    private String bpmFlowId;
    /**
     * 流程编码
     */
    @ApiModelProperty("流程编码")
    private String processCode;
    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String processSubject;
    /**
     * 申请人员工号
     */
    @ApiModelProperty("申请人员工号")
    private String createEmployeeNo;
    private String curPerson;
    /**
     * 申请人登录名
     */
    @ApiModelProperty("申请人登录名")
    private String createEmployeeLogin;
    /**
     * 申请人姓名
     */
    @ApiModelProperty("申请人姓名")
    private String createEmployeeName;
    /**
     * 角色
     */
    @ApiModelProperty("角色 1：管理员；2：保密负责人；3：员工")
    private String roleType;
    /**
     * 权限类型 1-开通 2-关闭  1,2 都有
     */
    @ApiModelProperty("权限类型 1-开通 2-关闭 ")
    private String powerType;
    /**
     * 本部门科长登录名
     */
    @ApiModelProperty("本部门科长登录名")
    private String chiefEmployeeLogin;
    /**
     * 本部门科长姓名
     */
    @ApiModelProperty("本部门科长姓名")
    private String chiefEmployeeName;
    /**
     * 申请事由
     */
    @ApiModelProperty("申请事由")
    private String applyReason;
    /**
     * 审批结果: 1-同意 2-驳回
     */
    @ApiModelProperty("审批结果: 1-同意 2-驳回")
    private String resultStatus;
    /**
     * 审批结果描述
     */
    @ApiModelProperty("审批结果描述")
    private String resultRemark;
    /**
     * 流程状态 00-废弃  10-草稿 20 审批  30-发布
     */
    @ApiModelProperty("流程状态 00-废弃  10-草稿 20 审批  30-发布")
    private String status;
    /**
     * 当前节点处理人-bpm
     */
    @ApiModelProperty("当前节点处理人-bpm")
    private String curNodeHandler;
    /**
     * 当前节点处理人姓名-bpm
     */
    @ApiModelProperty("当前节点处理人姓名-bpm")
    private String curHandlerName;
    /**
     * 当前流程节点-bpm
     */
    @ApiModelProperty("当前流程节点-bpm")
    private String curNodeId;
    /**
     * 当前流程节点名称-bpm
     */
    @ApiModelProperty("当前流程节点名称-bpm")
    private String curNodeName;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;
    private String createStartTime;
    private String createEndTime;
    /**
     * 申请人部门
     */
    @ApiModelProperty("申请人部门")
    private String createEmployeeDept;
    /**
     * 申请人部门Code
     */
    @ApiModelProperty("申请人部门Code")
    private String createEmployeeDeptCode;
    /**
     * 明细字段拼接-厂区
     */
    @ApiModelProperty("明细字段拼接-厂区")
    private String montageFactoryName;
    private String montageFactoryCode;

    @ApiModelProperty("申请开始时间")
    private String applyStartTime;

    @ApiModelProperty("申请结束时间")
    private String applyEndTime;

    @ApiModelProperty("当前授权厂区")
    private List<SafetyParkEmployeePowerInfoDO> powerFactoryList;

    @ApiModelProperty("授权人信息")
    private List<SafetyParkPowerProcessDetailDO> userList;

    @ApiModelProperty("审批意见")
    private String auditNote;
    private String bpmUrlPre;
    private String bpmUrl;

    @ApiModelProperty("流程参数")
    private String fdParameterJson;
}