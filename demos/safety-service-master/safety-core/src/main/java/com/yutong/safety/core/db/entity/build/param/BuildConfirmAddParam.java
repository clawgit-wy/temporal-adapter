package com.yutong.safety.core.db.entity.build.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 门禁权限确认入参
 */
@Data
public class BuildConfirmAddParam implements Serializable {

    @ApiModelProperty("人员变更记录ID")
    private String changeId;

    @ApiModelProperty("员工号")
    private String employeeCode;

    @ApiModelProperty("所有已授权门禁ID拼接")
    private String allPowerIdStr;

    @ApiModelProperty("新部门已授权门禁ID拼接")
    private String localPowerIdStr;

    @ApiModelProperty("关闭授权ID拼接")
    private String closeIdStr;

    @ApiModelProperty("保留授权ID拼接")
    private String keepIdStr;
}
