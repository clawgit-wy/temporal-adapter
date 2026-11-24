package com.yutong.modules.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YtsafetyCaseViolationReportQueryDTO对象", description="事项违规上报查询DTO")
public class YtsafetyCaseViolationReportQueryDTO {

    @ApiModelProperty(value = "违规类别")
    private Integer violationCategoryId;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "违规日期")
    private Date violationTime;

    @ApiModelProperty(value = "当事人名称")
    private String personConcernedName;

    @ApiModelProperty(value = "责任部门")
    private Integer departId;


}
