package com.yutong.safety.core.db.entity.bpm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BpmCurNodeVO implements Serializable {

    @ApiModelProperty("流程实例ID")
    private String modelId;
    @ApiModelProperty("节点ID")
    private String nodeIds;
    @ApiModelProperty("节点名称")
    private String nodeNames;
    @ApiModelProperty("流程状态")
    private String fdStatus;
    @ApiModelProperty("当前审批人员工号")
    private String fdCurrNos;
    @ApiModelProperty("当前审批人姓名")
    private String fdCurrNames;
    @ApiModelProperty("当前审批人EIP用户名")
    private String fdCurrLoginNames;
    @ApiModelProperty("创建人")
    private String creatorName;
    @ApiModelProperty("创建人员工号")
    private String fdNo;
    @ApiModelProperty("创建时间")
    private String fdCreateTime;
}
