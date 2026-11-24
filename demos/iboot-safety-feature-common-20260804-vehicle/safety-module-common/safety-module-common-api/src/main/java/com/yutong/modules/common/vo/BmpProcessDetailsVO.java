package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="流程记录详情", description="流程记录详情")
public class BmpProcessDetailsVO {

    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "流程主键")
    private String flowId;

    @ApiModelProperty(value = "任务主键")
    private String taskId;

    @ApiModelProperty(value = "系统标识")
    private String sysId;

    @ApiModelProperty(value = "模型标识")
    private String modelFlag;

    @ApiModelProperty(value = "表单主键")
    private String formId;

    @ApiModelProperty(value = "表单详情")
    private String formDetail;

    @ApiModelProperty(value = "流程状态")
    private Integer flowStatus;

    @ApiModelProperty(value = "是否可审批")
    private Boolean isCanApprove;

    @ApiModelProperty(value = "是否可修改")
    private Boolean isCanEdit;

    @ApiModelProperty(value = "申请时间")
    private Date applyStartTime;

    @ApiModelProperty(value = "匿名临时token")
    private String temporaryToken;

    @ApiModelProperty(value = "匿名临时LRToken")
    private String LRToken;
}
