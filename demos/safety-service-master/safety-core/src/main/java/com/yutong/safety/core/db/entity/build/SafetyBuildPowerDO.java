package com.yutong.safety.core.db.entity.build;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyBuildPowerDO extends WebPageParam implements Serializable {

    private Integer id;

    private String employeeCode;

    private String employeeName;

    private String deviceCode;

    private String deviceName;

    private String dutyDeptCode;

    private String dutyDeptName;

    private String processId;

    private String processCode;

    private Integer emposerNum;

    private Integer buildNum;

    private String createTime;

    private String updateTime;

    private String isDelete;

    private String employeeLogin;

    private String buildSysManagerRole;

    private String remark;

    private static final long serialVersionUID = 1L;

}