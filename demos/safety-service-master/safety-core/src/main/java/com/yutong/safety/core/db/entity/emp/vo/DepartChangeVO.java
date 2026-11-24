package com.yutong.safety.core.db.entity.emp.vo;

import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import lombok.Data;

@Data
public class DepartChangeVO extends SafetyDepartChangeListDO {

    private String userType;
    private String faceIssueFlag;

}
