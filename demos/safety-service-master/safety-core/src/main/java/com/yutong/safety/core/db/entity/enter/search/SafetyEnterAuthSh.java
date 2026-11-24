package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
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
public class SafetyEnterAuthSh extends Page implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 备案类型
     */
    private String fdFilingType;

    /**
     * 员工号
     */
    private String fdEmpNo;

    /**
     * 姓名
     */
    private String fdEmpName;

    /**
     * 电话
     */
    private String fdPhone;

    /**
     * 部门
     */
    private String fdEmpDepartment;

    /**
     * 权限
     */
    private String fdAuth;

    /**
     * 可备案厂区
     */
    private String fdFactory;

    /**
     * 人员类型
     */
    private String fdFilingUserType;

    /**
     * 是否允许备案长期车辆
     */
    private String fdLongTerm;

    /**
     * 是否允许黑名单司机备案
     */
    private String fdBlack;

    /**
     * 是否允许一键审批
     */
    private String fdApproval;

    @ApiModelProperty("配置类型 1-人员 2-组织")
    private String fdConfigType;
    @ApiModelProperty("是否到期提醒 0-否 1-是")
    private String fdNotification;

    private List<String> filingUserTypeList;
    private List<String> empNameList;
    private List<String> empNoList;
    private List<String> phoneList;
}
