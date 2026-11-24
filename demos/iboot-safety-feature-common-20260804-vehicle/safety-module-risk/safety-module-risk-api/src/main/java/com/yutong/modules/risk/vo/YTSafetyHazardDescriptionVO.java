package com.yutong.modules.risk.vo;

import com.yutong.modules.common.entity.YTSafetyFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Description: 隐患问题描述表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@ApiModel(value="隐患问题描述表对象", description="隐患问题描述表")
public class YTSafetyHazardDescriptionVO {

    @ApiModelProperty(value = "主键")
	private String id;
	@Excel(name = "隐患上报id")
    @ApiModelProperty(value = "隐患上报id")
	private String processId;
	@Excel(name = "问题描述", width = 500)
    @ApiModelProperty(value = "问题描述")
	private String problemDescription;
	@Excel(name = "附件图片id", width = 15)
    @ApiModelProperty(value = "附件图片id")
	private String attachmentIds;
    @ApiModelProperty(value = "附件图片对象列表")
    private List<YTSafetyFileInfo> attachmentList;
}
