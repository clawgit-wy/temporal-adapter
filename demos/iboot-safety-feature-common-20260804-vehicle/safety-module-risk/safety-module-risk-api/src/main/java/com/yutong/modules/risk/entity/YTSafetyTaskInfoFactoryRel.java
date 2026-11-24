package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 任务台账厂区关系表
 * @Author: jeecg-boot
 * @Date:   2026-06-25
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_task_info_factory_rel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_task_info_factory_rel对象", description="任务台账厂区关系表")
public class YTSafetyTaskInfoFactoryRel {
    
	/**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**任务台账id*/
    @ApiModelProperty(value = "任务台账id")
	private Long taskInfoId;
	/**所属厂区id*/
    @ApiModelProperty(value = "所属厂区id")
	private Long factoryId;
	/**所属厂区名称*/
    @ApiModelProperty(value = "所属厂区名称")
	private String factoryName;

}
