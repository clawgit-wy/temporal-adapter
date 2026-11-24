package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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
@ApiModel(value="隐患上报修改历史表VO", description="隐患上报修改历史表")
public class YTSafetyHazardReportHistoryVO {

    @ApiModelProperty(value = "主键")
	private String id;
	@Excel(name = "隐患上报ID", width = 15)
    @ApiModelProperty(value = "隐患上报ID")
	private String processId;
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
    @ApiModelProperty(value = "修改前JSON快照", hidden = true)
	private String beforeSnapshot;
    @ApiModelProperty(value = "修改后JSON快照", hidden = true)
	private String afterSnapshot;

    @ApiModelProperty(value = "修改前数据")
    private String updateBeforeData;

    @ApiModelProperty(value = "修改后数据")
    private String updateAfterData;
}
