package com.yutong.safety.core.db.entity.visitor.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SafetyVisitorAuthorizationAddParam implements Check {

    @ApiModelProperty("数据行ID")
    private Integer id;

    @ApiModelProperty("被授权人工号")
    private String granteeNo;

    @ApiModelProperty("授权人工号")
    private String grantorNo;

    @ApiModelProperty("授权时间-始")
    private String authorizationBeginTime;

    @ApiModelProperty("授权时间-终")
    private String authorizationEndTime;

    @ApiModelProperty("授权原因")
    private String authorizationReason;


    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(granteeNo), ResponseEnum.FAIL.getCode(), "“被授权人工号”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(grantorNo), ResponseEnum.FAIL.getCode(), "“授权人工号”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(authorizationBeginTime), ResponseEnum.FAIL.getCode(), "“授权时间-始”不能为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(authorizationEndTime), ResponseEnum.FAIL.getCode(), "“授权时间-终”不能为空");
    }
}
