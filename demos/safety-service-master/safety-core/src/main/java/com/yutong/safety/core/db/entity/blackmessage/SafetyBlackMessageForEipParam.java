package com.yutong.safety.core.db.entity.blackmessage;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * Eip 传参
 */
@Data
public class SafetyBlackMessageForEipParam  implements Check {

    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("特批授权截止日期 yyyy-MM-dd")
    private String authorizationDate;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(empNo), ResponseEnum.PARAM_ERROR.getCode(), "员工号 empNo 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(authorizationDate), ResponseEnum.PARAM_ERROR.getCode(), "特批授权截止日期 authorizationDate 不能为空！");
        boolean validDateFormat = DateUtils.isValidDateFormat(authorizationDate, 1);
        PreconditonsEx.checkArgument(validDateFormat, ResponseEnum.PARAM_ERROR.getCode(), "特批授权截止日期 authorizationDate 格式不正确！");
    }
}
