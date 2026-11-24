package com.yutong.modules.risk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 隐患问题描述表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
public class YTSafetyHazardDescriptionDTO {

    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "问题描述")
	private String problemDescription;
    @ApiModelProperty(value = "附件图片id")
	private String attachmentIds;
}
