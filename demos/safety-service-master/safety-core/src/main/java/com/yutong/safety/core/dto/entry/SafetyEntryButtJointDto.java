package com.yutong.safety.core.dto.entry;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 备案人员信息实体
 */
@Data
public class SafetyEntryButtJointDto {


    private Integer id;

    @ApiModelProperty("权限权限编码")
    private String fdAuthCode;
    @ApiModelProperty("权限")
    private String fdAuth;

    @ApiModelProperty("人员类型编码")
    private String fdFilingUserTypeCode;
    @ApiModelProperty("人员类型")
    private String fdFilingUserType;
    @ApiModelProperty("限制备案次数")
    private Integer fdLimit;
    @ApiModelProperty("限制备案时间（天） 是/否")
    private Integer fdLimitDay;
    @ApiModelProperty("是否允许备案长期车辆 是/否")
    private String fdLongTerm;
    @ApiModelProperty("是否允许黑名单司机备案 是/否")
    private String fdBlack;
    @ApiModelProperty("否允许一键审批 是/否")
    private String fdApproval;
    @ApiModelProperty("接口人员工号")
    private String fdEmpNo;
    @ApiModelProperty("接口人姓名")
    private String fdEmpName;
    @ApiModelProperty("接口人部门")
    private String fdEmpDepartment;
    @ApiModelProperty("接口人电话")
    private String fdPhone;
    @ApiModelProperty("配置类型 1-人员 2-组织")
    private String fdConfigType;

    @ApiModelProperty("备案类型")
    private Map<String, String> filingType = new HashMap<>();
    @ApiModelProperty("备案厂区")
    private Map<String, String> filingPark;
}
