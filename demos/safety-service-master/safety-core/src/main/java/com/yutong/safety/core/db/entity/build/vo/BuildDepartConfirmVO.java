package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildDepartConfirmDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildDepartConfirmVO extends SafetyBuildDepartConfirmDO {

    @ApiModelProperty("是否白名单 是/否")
    private String isWhite;

    private String createUserCode;

    @ApiModelProperty("添加人")
    private String createUserName;

    @ApiModelProperty("添加原因")
    private String createReason;

    @ApiModelProperty("白名单添加时间")
    private String whiteUpdateTime;

    @ApiModelProperty("拼接-员工已授权门禁")
    private String allPowerStr;

    @ApiModelProperty("拼接-员工新部门授权")
    private String localPowerStr;

    private String oldOrgNamePath;

    private String newOrgNamePath;

    //人员已授权门禁集合
    private List<SafetyBuildEmployeePowerDO> allPowerList;

    //人员新部门授权门禁集合
    private List<SafetyBuildEmployeePowerDO> localPowerList;
}
