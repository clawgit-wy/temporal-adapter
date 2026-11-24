package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CarViolationVO {

    @ApiModelProperty(value = "违规类型")
    private String violationType;

    @ApiModelProperty(value = "违规时间")
    private String violationDate;

    @ApiModelProperty(value = "违规厂区名称")
    private String factoryName;

    @ApiModelProperty(value = "违规原因")
    private String reasonForViolation;

    @ApiModelProperty(value = "违规照片")
    private String ViolationPhoto;

    @ApiModelProperty(value = "处罚措施")
    private String penalties;

}
