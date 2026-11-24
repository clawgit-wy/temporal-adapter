package com.yutong.modules.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="分页查询对象", description="BPM回调配置")
public class SafetyBpmCallBackQueryDTO {
    /**
     * 流程模板标识
     */
    @ApiModelProperty(value = "流程模板标识")
    private String modelId;
    /**
     * 调用方法标识
     */
    @ApiModelProperty(value = "调用方法标识")
    private String functionId;
    /**
     * 调用方法名称
     */
    @ApiModelProperty(value = "调用方法名称")
    private String functionName;
    /**
     * 事件名称
     */
    @ApiModelProperty(value = "事件名称")
    private String functionDesc;
}
