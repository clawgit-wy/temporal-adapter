package com.yutong.modules.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * BPM回调参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BPM回调接口-获取异构系统表单模板记录", description="用于配置流程模板")
public class BpmTemplateFormDTO {


    /**
     * 系统标识
     */
    @ApiModelProperty(value = "系统标识")
    private String sysId;

    /**
     * 模块标识
     */
    @ApiModelProperty(value = "模块标识")
    private String modelId;

    /**
     * 表单模板标识
     */
    @ApiModelProperty(value = "表单模板标识")
    private String templateFormId;

    /**
     * 表单实例标识
     */
    @ApiModelProperty(value = "表单实例标识")
    private String formInstanceId;

    /**
     * 表单字段集合 可为空,多个使用英文逗号分隔
     */
    @ApiModelProperty(value = "表单字段集合")
    private String fieldIds;

    /**
     * 系统标识汇总
     */
    @ApiModelProperty(value = "系统标识汇总")
    private String formId;

    /**
     * 系统流程模板ID
     */
    @ApiModelProperty(value = "系统流程模板ID")
    private String flowDefinitionId;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型")
    private String operationType;

    /**
     * 函数标识
     */
    @ApiModelProperty(value = "函数标识")
    private String functionId;

    /**
     * BPM流程主键
     */
    @ApiModelProperty(value = "BPM流程主键")
    private String processId;

    /**
     * BPM流程数据
     */
    @ApiModelProperty(value = "BPM流程数据")
    private String processData;

    /**
     * BPM流程节点ID
     */
    @ApiModelProperty(value = "BPM流程节点ID")
    private String nodeId;

    /**
     * 语言
     */
    @ApiModelProperty(value = "语言")
    private String language;
}
