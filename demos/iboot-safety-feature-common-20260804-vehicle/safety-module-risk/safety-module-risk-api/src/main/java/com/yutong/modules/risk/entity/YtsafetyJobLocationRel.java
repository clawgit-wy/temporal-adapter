package com.yutong.modules.risk.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 作业环节位置关系表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_job_location_rel")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_job_location_rel对象", description="作业环节位置关系表")
public class YtsafetyJobLocationRel {
    
	/**位置id*/
	@Excel(name = "位置id", width = 15)
    @ApiModelProperty(value = "位置id")
	private Long locationId;
	/**作业环节id*/
	@Excel(name = "作业环节id", width = 15)
    @ApiModelProperty(value = "作业环节id")
	private Long jobStepId;
}
