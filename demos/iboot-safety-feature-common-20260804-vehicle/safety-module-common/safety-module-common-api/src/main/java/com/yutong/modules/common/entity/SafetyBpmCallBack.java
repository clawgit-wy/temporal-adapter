package com.yutong.modules.common.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 对接BPM回调方法表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
@Data
@TableName("safety_bpm_call_back")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_bpm_call_back对象", description="对接BPM回调方法表")
public class SafetyBpmCallBack {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Long id;
	/**流程模板标识*/
    @ApiModelProperty(value = "流程模板标识")
	private String modelId;
	/**模块标识*/
    @ApiModelProperty(value = "调用方法标识")
	private String functionId;
	/**调用方法名称*/
    @ApiModelProperty(value = "调用方法名称")
	private String functionName;
	/**事件名称*/
    @ApiModelProperty(value = "事件名称")
	private String functionDesc;
	/**备注*/
    @ApiModelProperty(value = "备注")
	private String remarks;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
}
