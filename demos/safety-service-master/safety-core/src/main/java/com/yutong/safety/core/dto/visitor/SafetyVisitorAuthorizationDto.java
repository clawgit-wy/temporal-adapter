package com.yutong.safety.core.dto.visitor;

import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDO;
import lombok.Data;

@Data
public class SafetyVisitorAuthorizationDto extends SafetyVisitorAuthorizationApprovalDO {


    private Integer authorizationApprovalId;


    private String granteeName;


    private String granteeNo;


    private String granteeOrgCode;


    private String granteeOrgName;


    private String granteePosition;


    private String granteeJobName;


    private String granteeEipLogin;


    private String cancelReason;


    private Integer status;
}
