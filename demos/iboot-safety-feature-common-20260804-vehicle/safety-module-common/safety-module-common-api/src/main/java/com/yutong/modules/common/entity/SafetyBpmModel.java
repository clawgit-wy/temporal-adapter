package com.yutong.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 对接BPM系统模块表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
@Data
@TableName("safety_bpm_model")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_bpm_model对象", description="对接BPM系统模块表")
public class SafetyBpmModel {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Long id;
    /**系统标识*/
    @ApiModelProperty(value = "系统标识")
    private String sysId;
	/**菜单主键*/
    @ApiModelProperty(value = "菜单主键")
	private Long permId;
	/**表单所属模块ID*/
    @ApiModelProperty(value = "表单所属模块ID")
	private String modelId;
	/**表单所属模块名称*/
    @ApiModelProperty(value = "表单所属模块名称")
	private String modelName;
    /**流程类型 0 安全巡检类 */
    @ApiModelProperty(value = "流程类型 0 安全巡检类 ")
    private Integer modelType;
	/**表单唯一标识*/
    @ApiModelProperty(value = "表单唯一标识")
	private String formId;
    /**详情表编码*/
    @ApiModelProperty(value = "详情表编码")
    private String formDetail;
	/**表单名称*/
    @ApiModelProperty(value = "表单名称")
	private String formName;
	/**表单访问地址*/
    @ApiModelProperty(value = "表单访问地址")
	private String formUrl;
	/**流程模板id*/
    @ApiModelProperty(value = "流程模板id")
	private String templateId;
    /**是否显示*/
    @ApiModelProperty(value = "是否显示 0否 1是")
    private String isShow;
	/**流程模板类型查询模板唯一标识*/
    @ApiModelProperty(value = "流程模板类型查询模板唯一标识")
	private String templateType;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String creator;
}
