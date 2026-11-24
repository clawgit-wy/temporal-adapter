package com.yutong.safety.core.db.entity.visitor.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyVisitorAuthorizationConfigQueryParam extends SafetyWebBaseParam {

    @ApiModelProperty("类型，必传 0：组织， 1：人员")
    private Integer configType;

    @ApiModelProperty("姓名/员工号")
    private String empInfo;

    @ApiModelProperty("员工号")
    private String empNo;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(configType != null, ResponseEnum.FAIL.getCode(), "“访客授权类型”不能为空");
    }
}
