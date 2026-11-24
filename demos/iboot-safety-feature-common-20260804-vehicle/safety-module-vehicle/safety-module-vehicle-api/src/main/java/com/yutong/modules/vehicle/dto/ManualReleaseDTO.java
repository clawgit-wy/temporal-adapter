package com.yutong.modules.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ManualReleaseDTO {

    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;

    @ApiModelProperty(value = "所属厂区ID")
    private Long factoryId;

    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @ApiModelProperty(value = "位置ID")
    private Long locationId;

    @ApiModelProperty(value = "进出类型（1-进场 2-出场）")
    private Integer inOutType;

    @ApiModelProperty(value = "车牌号")
    private String plateNumber;

    @ApiModelProperty(value = "放行人员工号")
    private String empNo;

    @ApiModelProperty(value = "放行人姓名")
    private String empName;

    @ApiModelProperty(value = "放行时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseDate;

}
