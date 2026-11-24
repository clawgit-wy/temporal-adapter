package com.yutong.safety.core.db.entity.visitor;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyVisitorAuthorizationApprovalDO extends SafetyWebBaseEntity implements Serializable {

    @ApiModelProperty("数据ID")
    private Integer id;

    @ApiModelProperty("提交人名称")
    private String submitName;

    @ApiModelProperty("提交人工号")
    private String submitNo;

    @ApiModelProperty("提交人组织编码")
    private String submitOrgCode;

    @ApiModelProperty("提交人组织名称")
    private String submitOrgName;


    private String grantorName;


    private String grantorNo;


    private String grantorOrgCode;


    private String grantorOrgName;


    private String grantorPosition;


    private String grantorJobName;


    private String authorizationReason;


    private String authorizationBeginTime;


    private String authorizationEndTime;

    private String applyEmpNo;


    private String applyEmpLogin;


    private String applyEmpName;


    private Integer sectionChiefType;


    private String grantorEipLogin;


    private String bpmFlowId;

    @Override
    public boolean saveOrUpdate() {
        return id == null;
    }
}