package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildAutoDeptDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuildAutoDeptVO extends SafetyBuildAutoDeptDO {

    private String anFactoryCode;

    private String anFactoryName;

    private String anBuildingCode;

    private String anBuilding;

    private String anDeviceName;

    @ApiModelProperty("责任部门编码")
    private String dutyDeptCode;

    @ApiModelProperty("责任部门名称")
    private String dutyDeptName;

    @ApiModelProperty("设备类型 1-新开普 2-富士")
    private String deviceType;
}
