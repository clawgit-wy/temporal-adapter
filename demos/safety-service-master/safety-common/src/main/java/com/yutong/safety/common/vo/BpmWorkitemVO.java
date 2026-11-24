package com.yutong.safety.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * BPM当前节点审批人信息
 */
@Data
public class BpmWorkitemVO implements Serializable {
    @ApiModelProperty("节点审批人姓名")
    private String expecterName;
    @ApiModelProperty("节点审批人用户名")
    private String expecter;
    @ApiModelProperty("任务ID")
    private String taskId;
    @ApiModelProperty("节点开始时间")
    private String startDate;
}
