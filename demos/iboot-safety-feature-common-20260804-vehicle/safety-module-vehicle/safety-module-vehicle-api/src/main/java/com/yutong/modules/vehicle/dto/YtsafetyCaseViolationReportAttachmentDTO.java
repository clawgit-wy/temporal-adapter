package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyCaseViolationReportAttachmentDTO对象", description="项违规上报附件DTO")
public class YtsafetyCaseViolationReportAttachmentDTO {

    @ApiModelProperty(value = "主键")
    private Long id;
    /**业务主键*/
    @Excel(name = "业务主键", width = 15)
    @ApiModelProperty(value = "业务主键")
    private Integer busId;
    /**文件类型 1 事项凭证 2 事项附件*/
    @Excel(name = "文件类型 1 事项凭证 2 事项附件", width = 15)
    @ApiModelProperty(value = "文件类型 1 事项凭证 2 事项附件")
    private Integer fileType;
    /**文件地址*/
    @Excel(name = "文件地址", width = 15)
    @ApiModelProperty(value = "文件地址")
    private String fileUrl;
}
