package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "驾驶证识别结果", description = "驾驶证OCR识别结果")
public class DriverLicenseResultVO {

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "国籍")
    private String nationality;

    @ApiModelProperty(value = "出生日期")
    private String birthDate;

    @ApiModelProperty(value = "驾驶证号/身份证号")
    private String idCard;

    @ApiModelProperty(value = "准驾车型")
    private String carType;

    @ApiModelProperty(value = "初次领证日期")
    private String issueDate;

    @ApiModelProperty(value = "驾驶证有效期开始日期")
    private String startDate;

    @ApiModelProperty(value = "驾驶证有效期结束日期")
    private String endDate;

    @ApiModelProperty(value = "发证机关")
    private String issueOrg;
}
