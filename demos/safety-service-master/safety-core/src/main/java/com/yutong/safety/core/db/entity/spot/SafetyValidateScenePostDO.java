package com.yutong.safety.core.db.entity.spot;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyValidateScenePostDO implements Serializable {

    private Integer id;
    private Integer dataType;
    private String flowNo;
    private Integer factoryId;
    private String factory;
    private String teamId;
    private String teamName;
    private String personCategory;
    private String postName;
    private String empNo;
    private String empName;
    private String empDept;
    private Integer reachDuration;
    private String leaderEmpNo;

    private String leaderEmpName;

    private String validateTeam;

    private String validateDesc;

    private String validateBeginTime;

    private String validateEndTime;

    private String createEmpNo;

    private String createEmp;

    private String createDept;

    private Date createTime;

    private Integer isPass;

    private Integer isExamine;

    private String reformDesc;

    private String remark;

    private Integer isReform;

    private Date dealTime;

    private String updateEmpNo;

    private String updateEmp;

    private Date updateTime;

    private String nodeFlag;

    private static final long serialVersionUID = 1L;
}