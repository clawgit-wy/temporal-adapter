package com.yutong.safety.core.db.entity.park.vo;

import com.alibaba.fastjson.JSONArray;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ParkEmployeePowerVO extends SafetyEmployeeMessageDO {

    @ApiModelProperty("授权厂区编码-拼接")
    private String factoryCode;

    @ApiModelProperty("授权厂区-拼接")
    private String factoryName;

    @ApiModelProperty("授权门禁编码-拼接")
    private String deviceCode;

    @ApiModelProperty("授权门禁-拼接")
    private String deviceName;

    @ApiModelProperty("员工车辆")
    private String carInfos;

    @ApiModelProperty("锁定车辆")
    private String lockCars;

    @ApiModelProperty("人车权限备注-拼接")
    private String montageRemark;

    private String detailsStr;

    private JSONArray detailArray;
}
