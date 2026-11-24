package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 安保带班人员信息VO
 * @date 2026-08-10
 */
@Data
@ApiModel(value = "安保带班人员信息VO")
public class SecurityDutyPersonVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工号")
    private String empNo;

    @ApiModelProperty(value = "姓名")
    private String empName;

    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    @ApiModelProperty(value = "厂区")
    private String factory;

    @ApiModelProperty(value = "值班日期")
    private String dutyDate;

    @ApiModelProperty(value = "人员类型")
    private String personType;
}
