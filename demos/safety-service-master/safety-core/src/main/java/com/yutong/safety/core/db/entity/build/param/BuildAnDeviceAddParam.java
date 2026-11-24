package com.yutong.safety.core.db.entity.build.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuildAnDeviceAddParam implements Serializable {

    private String id;

    @ApiModelProperty("门禁名称-安保平台定义")
    private String anDeviceName;

    @ApiModelProperty("厂区名-安保平台定义")
    private String anFactoryName;

    @ApiModelProperty("厂区编码-安保平台定义")
    private String anFactoryCode;

    @ApiModelProperty("楼栋编码-安保平台定义")
    private String anBuildingCode;

    @ApiModelProperty("楼栋-安保平台定义")
    private String anBuilding;

    @ApiModelProperty("序号-安保平台定义")
    private Integer anShort;

    @ApiModelProperty("备注-安保平台定义")
    private String anRemark;
}
