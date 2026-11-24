package com.yutong.safety.core.dto.build;

import com.yutong.safety.common.param.web.WebPageParam;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* 楼宇门禁管理流程表
* @TableName safety_build_manage_process
*/
@Data
public class SafetyBuildManageProcessDTO extends WebPageParam implements Serializable {

    /**
    * 主键
    */
    @ApiModelProperty("主键")
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
    * 流程状态
    */
    @ApiModelProperty("流程状态")
    private String processStatus;
    /**
    * 角色
    */
    @ApiModelProperty("角色")
    private String roleType;
    /**
    * 申请人员工号
    */
    @ApiModelProperty("申请人员工号")
    private String createEmployeeNo;
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
    * 申请人部门编码
    */
    @ApiModelProperty("申请人部门编码")
    private String createDeptCode;
    /**
    * 申请人部门名称
    */
    @ApiModelProperty("申请人部门名称")
    private String createDeptName;
    /**
    * 责任部门编码
    */
    @ApiModelProperty("责任部门编码")
    private String dutyDeptCode;
    private String dutyDeptCodePath;
    /**
    * 责任部门名称
    */
    @ApiModelProperty("责任部门名称")
    private String dutyDeptName;
    /**
    * 目前授权门禁编码
    */
    @ApiModelProperty("目前授权门禁编码")
    private String oldDeviceCode;
    /**
    * 目前授权门禁名称
    */
    @ApiModelProperty("目前授权门禁名称")
    private String oldDeviceName;
    /**
    * 变更后授权门禁编码
    */
    @ApiModelProperty("变更后授权门禁编码")
    private String newDeviceCode;
    /**
    * 变更后授权门禁名称
    */
    @ApiModelProperty("变更后授权门禁名称")
    private String newDeviceName;
    /**
    * 保密登录名
    */
    @ApiModelProperty("保密登录名")
    private String oldDepartEmployeeLogin;
    /**
    * 保密负责人姓名
    */
    @ApiModelProperty("保密负责人姓名")
    private String oldDepartEmployeeName;
    private String oldDepartEmployeeCode;
    /**
    * 变更后保密登录名
    */
    @ApiModelProperty("变更后保密登录名")
    private String neweDepartEmployeeLogin;
    /**
    * 变更后保密负责人姓名
    */
    @ApiModelProperty("变更后保密负责人姓名")
    private String newDepartEmployeeName;
    /**
    * 本部门部长登录名
    */
    @ApiModelProperty("本部门部长登录名")
    private String leaderEmployeeLogin;
    /**
    * 本部门部长姓名
    */
    @ApiModelProperty("本部门部长姓名")
    private String leaderEmployeeName;
    /**
    * 是否涉及绝密标记0-否 1-是
    */
    @ApiModelProperty("是否涉及绝密标记0-否 1-是")
    private String topSecretFlag;
    /**
    * 绝密区负责人登录名
    */
    @ApiModelProperty("绝密区负责人登录名")
    private String topSecretEmployeeLogin;
    /**
    * 绝密区负责人姓名
    */
    @ApiModelProperty("绝密区负责人姓名")
    private String topSecretEmployeeName;
    /**
    * 门禁巡逻组长登录名
    */
    @ApiModelProperty("门禁巡逻组长登录名")
    private String patrolEmployeeLogin;
    /**
    * 门禁巡逻组长
    */
    @ApiModelProperty("门禁巡逻组长")
    private String patrolEmployeeName;
    /**
    * 抄送本部门负责人登录名
    */
    @ApiModelProperty("抄送本部门负责人登录名")
    private String sendCopyEmployeeLogin;
    /**
    * 抄送本部门负责人
    */
    @ApiModelProperty("抄送本部门负责人")
    private String sendCopyEmployeeName;
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
    * 节点标识
    */
    @ApiModelProperty("节点标识")
    private String nodeFlag;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private String createTime;
    private String createStartTime;
    private String createEndTime;
    /**
    * 申请状态 1-通过 2-不通过
    */
    @ApiModelProperty("申请状态 1-通过 2-不通过")
    private String applyStatus;
    /**
    * 拒绝理由
    */
    @ApiModelProperty("拒绝理由")
    private String refuseReason;
    /**
    * 审批人
    */
    @ApiModelProperty("审批人")
    private String applyEmployee;
    /**
    * 审批角色
    */
    @ApiModelProperty("审批角色")
    private String applyRole;
    /**
    * 审批时间
    */
    @ApiModelProperty("审批时间")
    private String applyTime;
    /**
    * 门禁是否变动（0否1是）
    */
    @ApiModelProperty("门禁是否变动（0否1是）")
    private Integer devicedChange;

    @ApiModelProperty("工厂id")
    private String factoryId;

    @ApiModelProperty("变更后保密负责人Code")
    private String newDepartEmployeeCode;

    private String auditNote;

    private List<SafetyBuildPowerDO> buildPowerList;
    private List<SafetyBuildDeviceDO> buildList;

    @ApiModelProperty("可管理门禁数")
    private Integer buildNum;
    @ApiModelProperty("可授权白名单数")
    private Integer emposerNum;

    private String flowUrl;
    private String bpmUrlPre;
    private String building;
    private String combFactoryName;
    private String combBuild;

    private String buildSysManagerEip;

    private String buildSysManagerRole;

    private String buildSysManagerRoleCode;

    @ApiModelProperty("事由")
    private String remark;

    @ApiModelProperty("流程参数")
    private String fdParameterJson;
}
