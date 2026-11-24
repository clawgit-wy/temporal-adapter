package com.yutong.modules.vehicle.dto;


import com.baomidou.mybatisplus.annotation.*;
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
@ApiModel(value = "YtsafetyReleaseInfoDTO对象", description = "放行台账表DTO")
public class YtsafetyReleaseInfoDTO {

    @ApiModelProperty(value = "所属厂区ID")
    private Long factoryId;

    @ApiModelProperty(value = "位置ID")
    private Long locationId;

    @ApiModelProperty(value = "车辆类型")
    private String vehicleType;

    @ApiModelProperty(value = "车牌号")
    private String plateNumber;

    @ApiModelProperty(value = "进出类型（1-进场 2-出场）")
    private Integer inOutType;

    @ApiModelProperty(value = "放行人姓名")
    private String empName;

    @ApiModelProperty(value = "查询参数-放行日期-开始")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDateStart;

    @ApiModelProperty(value = "查询参数-放行日期-结束")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDateEnd;

    @ApiModelProperty(value = "查询参数-放行时间-开始")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Date releaseTimeStart;

    @ApiModelProperty(value = "查询参数-放行时间-结束")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private Date releaseTimeEnd;

    @ApiModelProperty(value = "selections")
    private String selections;

    @ApiModelProperty(value = "页码")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize = 10;

}
