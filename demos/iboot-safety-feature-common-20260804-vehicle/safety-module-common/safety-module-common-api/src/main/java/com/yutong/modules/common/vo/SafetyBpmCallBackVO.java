package com.yutong.modules.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="分页映射类对象", description="BPM回调方法")
public class SafetyBpmCallBackVO {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 流程模板标识
     */
    @ApiModelProperty(value = "流程模板标识")
    private String modelId;
    /**
     * 模块标识
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
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
