package com.yutong.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 对接BPM系统模块表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
@Data
@TableName("safety_process_abandon_config")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_process_abandon_config对象", description="对接BPM系统废弃关联表")
public class SafetyProcessAbandonConfig {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Long id;
	/**表单所属模块ID*/
    @ApiModelProperty(value = "表单所属模块ID")
	private String modelId;
	/**表单所属模块名称*/
    @ApiModelProperty(value = "表单所属模块名称")
	private String modelName;
	/**表单唯一标识*/
    @ApiModelProperty(value = "表单唯一标识")
	private String formId;
	/**表单名称*/
    @ApiModelProperty(value = "表单名称")
	private String formName;
	/**表单参数*/
    @ApiModelProperty(value = "表单参数")
	private String parameter;
	/**流程模板id*/
    @ApiModelProperty(value = "流程模板id")
	private String statusValue;
}
