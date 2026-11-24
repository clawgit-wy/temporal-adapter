package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CarAccessVO {

    @ApiModelProperty(value = "入厂地点")
    private String inLaneName;

    @ApiModelProperty(value = "入厂大门")
    private String inOperatorName;

    @ApiModelProperty(value = "入厂时间")
    private String inTime;

    @ApiModelProperty(value = "出厂地点")
    private String outLaneName;

    @ApiModelProperty(value = "出厂大门")
    private String outOperatorName;

    @ApiModelProperty(value = "出厂时间")
    private String outTime;

    @ApiModelProperty(value = "停车时长")
    private String stayLasts;

    @ApiModelProperty(value = "车牌号")
    private String tokenNo;

}
