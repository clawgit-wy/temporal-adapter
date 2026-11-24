package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ScanRegisterDTO {

    @ApiModelProperty(value = "所属厂区名称")
    private String factoryName;

    @ApiModelProperty(value = "所属厂区ID")
    private Long factoryId;

    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @ApiModelProperty(value = "位置ID")
    private Long locationId;

    @ApiModelProperty(value = "所属部门名称")
    private String orgName;

    @ApiModelProperty(value = "所属部门code")
    private String orgCode;

    @ApiModelProperty(value = "姓名")
    private String empName;

    @ApiModelProperty(value = "员工号")
    private String empNo;

    @ApiModelProperty(value = "人脸照片")
    private String facePhoto;

    @ApiModelProperty(value = "进出类型（1-进场 2-出场）")
    private Integer inOutType;
}
