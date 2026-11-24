package com.yutong.safety.common.param.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-1-16 15:22
 */
@Data
public class BlackDepartAddParam implements Check {

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 身份证号
     */
    private String idCardNo;

    @ApiModelProperty("特批授权截止日期 yyyy-MM-dd")
    private String authorizationDate;

    @Override
    public void check() {
//        PreconditonsEx.checkArgument(Objects.nonNull(idCardNo), ResponseEnum.PARAM_ERROR.getCode(), "身份证号 idCardNo 不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(authorizationDate), ResponseEnum.PARAM_ERROR.getCode(), "特批授权截止日期 authorizationDate 不能为空！");
//        boolean validDateFormat = DateUtils.isValidDateFormat(authorizationDate, 1);
//        PreconditonsEx.checkArgument(validDateFormat, ResponseEnum.PARAM_ERROR.getCode(), "特批授权截止日期 authorizationDate 格式不正确！");
    }
}
