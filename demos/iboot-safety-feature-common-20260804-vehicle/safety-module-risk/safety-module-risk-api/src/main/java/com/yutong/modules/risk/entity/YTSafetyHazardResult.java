package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @Description: 隐患处理结果表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_hazard_result")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_hazard_result对象", description="隐患处理结果表")
public class YTSafetyHazardResult {

	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
	private Long id;
	@Excel(name = "隐患上报id", width = 15)
    @ApiModelProperty(value = "隐患上报id")
	private Long processId;
	@Excel(name = "处理人工号", width = 15)
    @ApiModelProperty(value = "处理人工号")
	private String handlerUserEmpNo;
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
    @ApiModelProperty(value = "状态：2-通过;3-驳回")
    private Integer status;
    @ApiModelProperty(value = "是否已审核：1-是")
    private Integer checkSign;
	@Excel(name = "审批结果", width = 15)
    @ApiModelProperty(value = "审批结果")
	private java.lang.String approvalResult;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
