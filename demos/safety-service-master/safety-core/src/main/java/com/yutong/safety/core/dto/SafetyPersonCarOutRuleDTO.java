package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.SafetyPersonCarOutRuleDO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gwcuiyx
 * @date 2024-10-15 14:17
 */
@Data
public class SafetyPersonCarOutRuleDTO extends SafetyPersonCarOutRuleDO implements Serializable {
    //员工号
    private String empNo;
    //员工姓名
    private String empName;
    //手机号
    private String phoneNo;
    //组织路径
    private String orgNamePath;
    //组织code路径
    private String orgCodePath;
    //岗位code
    private String positionCode;
    //岗位名称
    private String positionName;
    //组织代码
    private String orgCode;
    //组织名称
    private String orgName;
    //邮箱
    private String email;
}
