package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 隐患上报修改历史表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_hazard_report_history")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_hazard_report_history对象", description="隐患上报修改历史表")
public class YTSafetyHazardReportHistory {

    @ApiModelProperty(value = "主键")
	private Long id;
	@Excel(name = "隐患上报ID", width = 15)
    @ApiModelProperty(value = "隐患上报ID")
	private Long processId;
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date changeTime;
	@Excel(name = "修改人工号", width = 15)
    @ApiModelProperty(value = "修改人工号")
	private String changeUserEmpNo;
	@Excel(name = "修改人姓名", width = 15)
    @ApiModelProperty(value = "修改人姓名")
	private String changeUserName;
    @ApiModelProperty(value = "修改前JSON快照")
	private String beforeSnapshot;
    @ApiModelProperty(value = "修改后JSON快照")
	private String afterSnapshot;
}
