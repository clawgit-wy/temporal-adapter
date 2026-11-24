package com.yutong.safety.core.db.entity.emp.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyEmployeeOrgChangeQueryParam extends WebPageParam {

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("组织编码")
    private String orgCode;

    @ApiModelProperty("状态： 正常:NORMAL, 离职:DISABLED")
    private String status;
}
