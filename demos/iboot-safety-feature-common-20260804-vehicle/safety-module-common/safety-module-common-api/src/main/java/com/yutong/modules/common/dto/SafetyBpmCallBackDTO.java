package com.yutong.modules.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="新增修改对象", description="对接BPM回调方法表")
public class SafetyBpmCallBackDTO {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 流程模板主键
     */
    @NotNull
    @ApiModelProperty(value = "流程模板标识")
    private String modelId;
    /**
     * 调用方法标识
     */
    @NotNull
    @ApiModelProperty(value = "调用方法标识")
    private String functionId;
    /**
     * 调用方法名称
     */
    @NotNull
    @ApiModelProperty(value = "调用方法名称")
    private String functionName;
    /**
     * 事件名称
     */
    @ApiModelProperty(value = "事件名称")
    private String functionDesc;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;
}
