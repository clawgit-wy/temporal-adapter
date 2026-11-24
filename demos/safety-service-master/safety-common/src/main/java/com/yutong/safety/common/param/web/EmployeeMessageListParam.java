package com.yutong.safety.common.param.web;

import lombok.Data;

import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-4-3 13:13
 */
@Data
public class EmployeeMessageListParam extends WebPageParam {
    /**
     * 员工号
     */
    private String empNo;
    /**
     * 精确匹配员工号
     */
    private String fullEmpNo;
    /**
     * 姓名
     */
    private String empName;
    /**
     * 手机号
     */
    private String phoneNo;
    /**
     *  备注
     */
    private String remark;
    /**
     * 人员类型
     */
    private String userType;
    /**
     * 类别
     */
    private String positionType;
    /**
     * 岗位
     */
    private String positionName;
    /**
     * 组织编码
     */
    private String orgCode;

    private String orgPathCode;

    /**
     * 邮箱
     */
    private String email;
    private String empNoAndName;

    /* 员工号排除 */
    private String noEmpNoStart;

    private List<String> empNoList;

    private Boolean pageFlag;

    /**
     * 创建日期-起
     */
    private String createBegin;
    /**
     * 创建日期-止
     */
    private String createEnd;

    /**
     * 组织路径包含关键字
     */
    private String orgPatchName;

    /**
     * 组织路径不包含关键字
     */
    private String noOrgPatchName;

    private Short faceIssueFlag;

    private Short cardIssueFlag;

    private String noUserType;

    private String nullOrgPathName;

    private List<String> positionNames;

    /**
     * 职类
     */
    private String empTypes;
    /**
     * 职种
     */
    private String jobTypeNames;
}
