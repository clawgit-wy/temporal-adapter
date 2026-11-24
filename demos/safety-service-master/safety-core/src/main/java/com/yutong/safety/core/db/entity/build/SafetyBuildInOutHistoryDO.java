package com.yutong.safety.core.db.entity.build;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 楼宇门禁最新进出记录
 */
@Data
public class SafetyBuildInOutHistoryDO implements Serializable {

    private Integer id;

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("姓名")
    private String empName;

    @ApiModelProperty("门禁设备号")
    private String deviceCode;

    @ApiModelProperty("进出时间")
    private String operTime;

    @ApiModelProperty("进出类型 进/出")
    private String ioFlag;

    @ApiModelProperty("进出方式 刷卡/人脸识别")
    private String typeName;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("门禁系统唯一ID")
    private Long historyId;

    private static final long serialVersionUID = 1L;
}