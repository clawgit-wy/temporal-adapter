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
@ApiModel(value="修改新增对象", description="BPM模型配置")
public class SafetyBpmModelDTO {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 菜单主键
     */
    @NotNull
    @ApiModelProperty(value = "菜单主键")
    private Long permId;
    /**
     * 表单所属模块ID
     */
    @NotNull
    @ApiModelProperty(value = "表单所属模块ID")
    private String modelId;
    /**
     * 表单所属模块名称
     */
    @NotNull
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
    @NotNull
    @ApiModelProperty(value = "表单唯一标识")
    private String formId;

    /**
     * 详情表编码
     */
    @ApiModelProperty(value = "详情表编码")
    private String formDetail;
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

    /**是否显示*/
    @ApiModelProperty(value = "是否显示 0否 1是")
    private String isShow;
}
