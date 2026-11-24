package com.yutong.safety.core.db.entity.hot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyHotWorkLensVO implements Serializable {

    @ApiModelProperty("监控设备id")
    private Long id;

    @ApiModelProperty("监控设备编号")
    private String lensNo;

    @ApiModelProperty("监控设备编码")
    private String lensNum;

    @ApiModelProperty("监控设备名称")
    private String lensName;

    @ApiModelProperty("监控设备SN号")
    private String lensSn;

    @ApiModelProperty("监控设备ip")
    private String lensIp;

    @ApiModelProperty("监控设备型号")
    private String lensModel;
}
