package com.yutong.safety.core.db.entity.visitor.vo;

import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDetailDO;
import lombok.Data;

import java.util.List;

@Data
public class SafetyVisitorAuthorizationVO extends SafetyVisitorAuthorizationApprovalDO {

    private Integer status;

    private String title;

    private List<SafetyVisitorAuthorizationApprovalDetailDO> detailList;
}
