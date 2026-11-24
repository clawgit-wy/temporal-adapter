package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class VisitorHairpinParam implements Check {

    private String eipFlowId;

    private String factoryId;

    private String vendorFactory;

    private String regionId;

    private String vendorRegion;

    private String state;

    private String secretState;

    private String secretRegion;

    @ApiModelProperty("授权人员工号-被访人上级")
    private String authorizedNo;

    @ApiModelProperty("被授权人员工号")
    private String authorizedByNo;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(eipFlowId), ResponseEnum.PARAM_ERROR.getCode(), "eipFlowId不能为空");
    }
}
