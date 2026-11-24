package com.yutong.safety.core.db.entity.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEntryBlankProcessDO extends SafetyWebBaseEntity implements Serializable {
    @ApiModelProperty("Id")
    private Integer id;

    @ApiModelProperty("流程ID")
    private String bpmFlowId;

    @ApiModelProperty("创建人员工号")
    private String createEmpNo;

    @ApiModelProperty("创建人登录账号")
    private String createEmpLogin;

    @ApiModelProperty("创建人名称")
    private String createEmpName;

    @ApiModelProperty("审批人员工号")
    private String applyEmpNo;

    @ApiModelProperty("审批人登录账号")
    private String applyEmpLogin;

    @ApiModelProperty("审批人名称")
    private String applyEmpName;

    @ApiModelProperty("审批意见")
    private String applyAudit;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("批次号")
    private String versionNo;

    @ApiModelProperty("事由")
    private String reason;

    @ApiModelProperty("发布时间")
    private Date publishTime;

    @ApiModelProperty("创建人部门，任务详情时带出部门")
    private String department;

    @ApiModelProperty("审批状态 0：存审批通过 默认值； 1：全部审批拒绝")
    private Integer approvalStatus;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean saveOrUpdate() {
        return id == null;
    }
}