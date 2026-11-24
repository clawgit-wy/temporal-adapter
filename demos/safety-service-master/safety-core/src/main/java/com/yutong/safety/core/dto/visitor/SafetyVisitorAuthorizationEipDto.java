package com.yutong.safety.core.dto.visitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyVisitorAuthorizationEipDto {

    @ApiModelProperty("被授权人姓名")
    private String granteeName;

    @ApiModelProperty("被授权人员工号")
    private String granteeNo;

    @ApiModelProperty("被授权人组织编码")
    private String granteeOrgCode;

    @ApiModelProperty("被授权人组织名称")
    private String granteeOrgName;

    @ApiModelProperty("被授权人岗位")
    private String granteePosition;

    @ApiModelProperty("被授权人职种")
    private String granteeJobName;

    @ApiModelProperty("被授权人EIP登录账号")
    private String granteeEipLogin;
}
