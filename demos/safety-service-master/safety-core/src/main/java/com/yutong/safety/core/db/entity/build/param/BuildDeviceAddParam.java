package com.yutong.safety.core.db.entity.build.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuildDeviceAddParam implements Serializable {

    private String id;

    @ApiModelProperty("是否公告区域 0-否 1-是")
    private String isPublic;

    @ApiModelProperty("保密等级")
    private String securityLevel;

    @ApiModelProperty("绝密负责人员工号")
    private String topSecretEmpCode;

    @ApiModelProperty("绝密负责人登录名")
    private String topSecretEmpLogin;

    @ApiModelProperty("绝密负责人姓名")
    private String topSecretEmpName;
}
