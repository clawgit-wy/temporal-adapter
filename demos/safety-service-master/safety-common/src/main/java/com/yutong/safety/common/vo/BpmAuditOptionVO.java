package com.yutong.safety.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * bpm审批记录VO
 * @author gwxiexj
 * @since 2024-07-15 16:07
 */
@Data
public class BpmAuditOptionVO implements Serializable {

    @ApiModelProperty("唯一ID")
    private String fdId;
    @ApiModelProperty("节点ID")
    private String factNodeId;
    @ApiModelProperty("节点名称")
    private String factNodeName;
    @ApiModelProperty("审批操作")
    private String actionName;
    @ApiModelProperty("审批明细")
    private String actionInfo;
    @ApiModelProperty("审批意见")
    private String auditNote;
    @ApiModelProperty("审批时间")
    private String createTime;
    @ApiModelProperty("NODEID")
    private String nodeId;
    @ApiModelProperty("")
    private String workItemId;
    @ApiModelProperty("审批人用户名")
    private String handler;
    @ApiModelProperty("审批人姓名")
    private String handlerName;
}
