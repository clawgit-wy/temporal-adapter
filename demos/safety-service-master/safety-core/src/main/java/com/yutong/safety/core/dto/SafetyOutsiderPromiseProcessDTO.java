package com.yutong.safety.core.dto;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyOutsiderPromiseProcessDTO extends WebPageParam implements Serializable {

    @ApiModelProperty(value = "提交人")
    private String submitter;

    @ApiModelProperty(value = "提交人员工号")
    private String submitterId;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "外协人员")
    private String cooperativePerson;

    @ApiModelProperty(value = "外协人员员工号")
    private String cooperativePersonId;

    @ApiModelProperty(value = "外协组织")
    private String cooperativeOrganization;

    @ApiModelProperty(value = "已授权厂区")
    private String authorizedFacility;

    @ApiModelProperty(value = "变更授权厂区")
    private String changedFacility;

    @ApiModelProperty(value = "任职部门科长")
    private String departmentHead;

    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    @ApiModelProperty(value = "提交时间")
    private String submissionTime;

    @ApiModelProperty(value = "提交开始时间")
    private String submissionStartTime;

    @ApiModelProperty(value = "提交截止时间")
    private String submissionEndTime;

    @ApiModelProperty(value = "流程链接")
    private String bpmUrl;

    @ApiModelProperty(value = "流程链接前缀")
    private String bpmUrlPre;
    @ApiModelProperty(value = "变更前服务截止日期")
    private String beforeEndDate;
    @ApiModelProperty(value = "变更后服务截止日期")
    private String afterEndDate;

    private String flowId;
}
