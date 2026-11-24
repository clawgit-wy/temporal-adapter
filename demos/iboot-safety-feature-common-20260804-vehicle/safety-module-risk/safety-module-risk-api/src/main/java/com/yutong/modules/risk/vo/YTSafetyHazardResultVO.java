package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 隐患处理结果表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@ApiModel(value="隐患处理结果表VO", description="隐患处理结果表")
public class YTSafetyHazardResultVO {

    @ApiModelProperty(value = "主键")
	private String id;
	@Excel(name = "隐患上报id", width = 15)
    @ApiModelProperty(value = "隐患上报id")
	private String processId;
	@Excel(name = "处理人工号", width = 15)
    @ApiModelProperty(value = "处理人工号")
	private String handlerUserEmpNo;
    @ApiModelProperty(value = "处理人姓名")
    private String handlerUserEmpName;
	@Excel(name = "处理完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理完成时间")
	private Date handleCompleteTime;
	@Excel(name = "处理措施", width = 50)
    @ApiModelProperty(value = "处理措施")
	private String handleMeasure;
	@Excel(name = "附件图片id", width = 15)
    @ApiModelProperty(value = "附件图片id")
	private String attachmentIds;
    @Excel(name = "整改类型", width = 15)
    @ApiModelProperty(value = "整改类型：all-全部；part-部分")
	private String handlerType;
    @Excel(name = "状态：", width = 15)
    @ApiModelProperty(value = "状态：1-通过，2-驳回")
    private Integer status;
	@Excel(name = "审批结果", width = 15)
    @ApiModelProperty(value = "审批结果")
	private String approvalResult;
    @ApiModelProperty(value = "附件图片对象列表")
    private List<YTSafetyFileInfo> attachmentList;
}
