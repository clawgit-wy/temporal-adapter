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
 * @Description: 任务台账风险数据关系表
 * @Author: jeecg-boot
 * @Date:   2026-06-25
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_task_info_risk_data_rel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_task_info_risk_data_rel对象", description="任务台账风险数据关系表")
public class YTSafetyTaskInfoRiskDataRel {
    
	/**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	/**任务台账id*/
    @ApiModelProperty(value = "任务台账id")
	private Long taskInfoId;
	/**风险数据id*/
    @ApiModelProperty(value = "风险数据id")
	private Long riskDataId;

}
