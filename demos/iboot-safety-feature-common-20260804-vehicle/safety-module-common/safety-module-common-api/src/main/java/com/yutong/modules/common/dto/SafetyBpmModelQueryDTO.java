package com.yutong.modules.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="分页查询对象", description="BPM模型配置")
public class SafetyBpmModelQueryDTO {
    /**
     * 菜单主键
     */
    @ApiModelProperty(value = "菜单主键")
    private Integer permId;
    /**
     * 表单所属模块ID
     */
    @ApiModelProperty(value = "表单所属模块ID")
    private String modelId;
    /**
     * 表单所属模块名称
     */
    @ApiModelProperty(value = "表单所属模块名称")
    private String modelName;
    /**
     * 流程类型 0 安全巡检类
     */
    @ApiModelProperty(value = "流程类型 0 安全巡检类 ")
    private Integer modelType;
    /**
     * 表单唯一标识
     */
    @ApiModelProperty(value = "表单唯一标识")
    private String formId;
    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    private String formName;
    /**
     * 表单访问地址
     */
    @ApiModelProperty(value = "表单访问地址")
    private String formUrl;
    /**
     * 流程模板id
     */
    @ApiModelProperty(value = "流程模板id")
    private String templateId;
    /**
     * 流程模板类型查询模板唯一标识
     */
    @ApiModelProperty(value = "流程模板类型查询模板唯一标识")
    private String templateType;
}
