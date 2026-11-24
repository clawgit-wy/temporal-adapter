package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 隐患问题描述表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_hazard_description")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_hazard_description对象", description="隐患问题描述表")
public class YTSafetyHazardDescription {

	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	@Excel(name = "隐患上报id")
    @ApiModelProperty(value = "隐患上报id")
	private Long processId;
	@Excel(name = "问题描述", width = 500)
    @ApiModelProperty(value = "问题描述")
	private String problemDescription;
	@Excel(name = "附件图片id", width = 15)
    @ApiModelProperty(value = "附件图片id")
	private String attachmentIds;


}
