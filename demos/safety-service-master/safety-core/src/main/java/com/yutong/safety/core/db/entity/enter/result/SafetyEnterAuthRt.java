package com.yutong.safety.core.db.entity.enter.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 备案权限管理
 * </p>
 *
 * @author 任昊翔
 * @since 2024-07-10
 */
@Data
public class SafetyEnterAuthRt implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @ApiModelProperty("备案类型编码")
    private String fdFilingTypeCode;
    @ApiModelProperty("备案类型")
    private String fdFilingType;
    @ApiModelProperty("接口人员工号")
    private String fdEmpNo;
    @ApiModelProperty("接口人姓名")
    private String fdEmpName;
    @ApiModelProperty("接口人部门")
    private String fdEmpDepartment;
    @ApiModelProperty("接口人部门编号")
    private String fdOrgCode;
    @ApiModelProperty("接口人电话")
    private String fdPhone;
    @ApiModelProperty("权限权限编码")
    private String fdAuthCode;
    @ApiModelProperty("权限")
    private String fdAuth;
    @ApiModelProperty("可备案厂区ID拼接")
    private String fdFactoryCode;
    @ApiModelProperty("可备案厂区")
    private String fdFactory;
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
    @ApiModelProperty("备注")
    private String fdRemark;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("创建人")
    private String createUser;
    @ApiModelProperty("更新时间")
    private String updateTime;
    @ApiModelProperty("更新人")
    private String updateUser;
    @ApiModelProperty("配置类型 1-人员 2-组织")
    private String fdConfigType;
    @ApiModelProperty("权限组织编码")
    private String fdPowerOrgCode;
    @ApiModelProperty("是否到期提醒 0-否 1-是")
    private String fdNotification;

    private List factoryList;
    private List perTypeList;
}
